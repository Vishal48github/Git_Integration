
/*
 * Copyright 2014 - 2017 newvision Software Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.newvision.assureit.engine.drivers;

import com.newvision.assureit.datalib.settings.ProjectSettings;
import com.newvision.assureit.datalib.settings.emulators.Emulator;
import com.newvision.assureit.engine.constants.FilePath;
import com.newvision.assureit.engine.constants.SystemDefaults;
import com.newvision.assureit.engine.core.Control;
import com.newvision.assureit.engine.core.RunContext;
import com.newvision.assureit.engine.drivers.customWebDriver.EmptyDriver;
import com.newvision.assureit.engine.drivers.findObjectBy.support.ByObjectProp;
import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.google.gson.Gson;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebDriverFactory {

    private static final Logger LOGGER = Logger.getLogger(WebDriverFactory.class.getName());
    public static FirefoxOptions fOptions;
    public static FirefoxProfile fProfile;
    public static EdgeOptions Eoptions;
    private static AppiumDriverLocalService service;

    public enum Browser {

//        Chrome("Chrome"), FireFox("Firefox"), IE("IE"), Edge("Edge"), Safari("Safari"),
//        ChromeHeadless("Chrome Headless"), HtmlUnit("HtmlUnit"), Empty("No Browser"),
//        Emulator("Emulator");
        Chrome("Chrome"), FireFox("Firefox"), IE("IE"), Edge("Edge"), Safari("Safari"),
        Empty("No Browser"), Emulator("Emulator"), RealDevice("Real Device");

        private static String emulatorName = "Emulator";
        private static String realDevice = "Real Device";

        public static void setEmulatorName(String name) {
            emulatorName = name;
        }

        public static String getEmulatorName() {
            return emulatorName;
        }

        public static void setRealDeviceName(String name) {
            realDevice = name;
        }

        public static String getRealDeviceName() {
            return realDevice;
        }

        private final String browserValue;

        Browser(String value) {
            browserValue = value;
        }

//        public String getBrowserValue() {
//            return this == Emulator ? emulatorName : browserValue;
//        }
        public String getBrowserValue() {
            return this == Emulator ? emulatorName : (this == RealDevice ? realDevice : browserValue);
        }

        @Override
        public String toString() {
            return getBrowserValue();
        }

        public static Browser fromString(String browserName) {
            for (Browser browser : values()) {
                if (browser.browserValue.equalsIgnoreCase(browserName)) {
                    return browser;
                }
            }
            return Emulator;
        }

        public static ArrayList<String> getValuesAsList() {
            ArrayList<String> browserList = new ArrayList<>();
            for (Browser browser : values()) {
                browserList.add(browser.getBrowserValue());
            }
            return browserList;
        }
    }

    public static ArrayList<String> getPlatFormList() {
        ArrayList<String> platFormList = new ArrayList<>();
        for (Platform platForm : Platform.values()) {
            platFormList.add(platForm.name());
        }
        return platFormList;
    }

    public static void initDriverLocation(ProjectSettings settings) {
        ByObjectProp.load();
//        System.setProperty("webdriver.chrome.driver", resolve(settings.getDriverSettings().getChromeDriverPath()));
        GalenConfig.getConfig().setProperty(GalenProperty.SCREENSHOT_FULLPAGE,
                String.valueOf(Control.exe.getExecSettings().getRunSettings().getTakeFullPageScreenShot()));
        GalenConfig.getConfig().setProperty(GalenProperty.SCREENSHOT_AUTORESIZE, "false");

        GalenConfig.getConfig().setProperty(GalenProperty.SCREENSHOT_FULLPAGE_SCROLLWAIT, "200");
    }

    private static String resolve(String location) {
        if (location.startsWith(".")) {
            return new File(FilePath.getAppRoot() + File.separator + location.substring(1)).getAbsolutePath();
        }
        return location;
    }

    public static WebDriver createRemote(RunContext context, ProjectSettings settings) {
        String url = Control.exe.getExecSettings().getRunSettings().getRemoteGridURL();
        String selectedEmulator = Browser.getEmulatorName();
        String realDevice = Browser.getRealDeviceName();
        return create(context, settings, true, url, selectedEmulator, realDevice);
    }

    public static WebDriver create(RunContext context, ProjectSettings settings) {
        String selectedEmulator = Browser.getEmulatorName();
        String realDevice = Browser.getRealDeviceName();
        return create(context, settings, false, null, selectedEmulator, realDevice);
    }

    private static String getRealDevice() {
        DeviceUtils deviceUtils = new DeviceUtils();
        return deviceUtils.getConnectedDevice(); // Fetch the connected device name dynamically
    }

    private static WebDriver create(RunContext context, ProjectSettings settings, Boolean isGrid, String remoteUrl, String selectedEmulator, String realDevice) {
        fOptions = new FirefoxOptions();
        Eoptions = new EdgeOptions();
        ChromeOptions options = new ChromeOptions();
        options.setCapability("platformName", context.Platform);

        if (!context.BrowserVersion.equalsIgnoreCase("default")) {
            options.setBrowserVersion(context.BrowserVersion);
        }
        if (!getCapability(context.BrowserName, settings).isEmpty()) {
            for (String cap : getCapability(context.BrowserName, settings)) {
                if (cap.toLowerCase().contains("browserversion")) {
                    options.setBrowserVersion(cap.split("=")[1]);
                } else {
                    options.setCapability(cap.split("=")[0], getPropertyValueAsDesiredType(cap.split("=")[1]));
                }
            }
        }

        // for firefox
        if (context.BrowserName.contains("Firefox") && !context.BrowserVersion.equalsIgnoreCase("default")) {
            fOptions.setBrowserVersion(context.BrowserVersion);
        }
        if (context.BrowserName.contains("Firefox") && !getCapability(context.BrowserName, settings).isEmpty()) {
            for (String cap : getCapability(context.BrowserName, settings)) {
                if (cap.toLowerCase().contains("browserversion")) {
                    fOptions.setBrowserVersion(cap.split("=")[1]);
                } else {
                    fOptions.setCapability(cap.split("=")[0], getPropertyValueAsDesiredType(cap.split("=")[1]));
                }
            }
        }

        System.out.println("brower name:--------" + context.BrowserName);
        System.out.println("setting name:--------" + settings);
        System.out.println("getoption name:--------" + getOption(context.BrowserName, settings));
        if (!getOption(context.BrowserName, settings).isEmpty()) {

            System.out.println("webdriver create if condition list is not empty");
            for (String option : getOption(context.BrowserName, settings)) {
                System.out.println("webdriver create for option :- " + option);
                options.addArguments(option);
		options.addArguments("--remote-allow-origins=*");
                options.addArguments("--start-maximized");
            }

        }

        //For firefox
        if (context.BrowserName.contains("Firefox") && !getOption(context.BrowserName, settings).isEmpty()) {

            System.out.println("webdriver create if condition list is not empty");
            for (String fOption : getOption(context.BrowserName, settings)) {
                System.out.println("webdriver create for fOption :- " + fOption);
                // fOptions.addArguments(fOptions);
                fOptions.addArguments(fOption);
		fOptions.addArguments("--remote-allow-origins=*");
                fOptions.addArguments("--start-maximized");
            }

        }
        //For Edge
        if (context.BrowserName.contains("Edge") && !context.BrowserVersion.equalsIgnoreCase("default")) {
            Eoptions.setBrowserVersion(context.BrowserVersion);
        }
        if (!getCapability(context.BrowserName, settings).isEmpty()) {
            for (String cap : getCapability(context.BrowserName, settings)) {
                if (cap.toLowerCase().contains("browserversion")) {
                    Eoptions.setBrowserVersion(cap.split("=")[1]);
                } else {
                    Eoptions.setCapability(cap.split("=")[0], getPropertyValueAsDesiredType(cap.split("=")[1]));
                }
            }
        }
        //Fore Edge
        if (context.BrowserName.contains("Edge") && !getOption(context.BrowserName, settings).isEmpty()) {

            System.out.println("Edge webdriver create if condition list is not empty");
            for (String option : getOption(context.BrowserName, settings)) {
                System.out.println("webdriver create for option :- " + option);
                Eoptions.addArguments(option);
		Eoptions.addArguments("--remote-allow-origins=*");
                Eoptions.addArguments("--start-maximized");
            }

        }

        return create(context.BrowserName, fOptions, options, Eoptions, settings, isGrid, remoteUrl, selectedEmulator, realDevice);
    }

    private static WebDriver create(String browserName, FirefoxOptions fopts, ChromeOptions opts, EdgeOptions eopts, ProjectSettings settings, Boolean isGrid, String remoteUrl, String selectedEmulator, String realDevice) {
        Browser browser = Browser.fromString(browserName);
        Boolean maximize = true;
        WebDriver driver = null;
        switch (browser) {
            case FireFox:
                if (!isGrid) {
                    driver = new FirefoxDriver(fOptions);
                    System.out.println("firefox driver:-" + driver);
                } else {
                    // Grid execution:
                    try {
                        driver = new RemoteWebDriver(new URL(remoteUrl), fOptions);
                    } catch (MalformedURLException e) {
                        // Handle exception appropriately
                        LOGGER.log(Level.WARNING, "Error during create connection with remote selenium grid where remote url is : ", remoteUrl);
                    }
                }
                break;
            case Chrome:
                if (!isGrid) {
                    driver = new ChromeDriver(withChromeOptions(opts));
                } else {
                    try {
                        driver = new RemoteWebDriver(new URL(remoteUrl), opts);
                    } catch (MalformedURLException e) {
                        // Handle exception appropriately
                        LOGGER.log(Level.WARNING, "Error during create connection with remote selenium grid where remote url is : ", remoteUrl);
                    }
                }
                break;
            case IE:
                if (!isGrid) {
                    driver = new InternetExplorerDriver();
                } else {
                    try {
                        driver = new RemoteWebDriver(new URL(remoteUrl), new InternetExplorerOptions());
                    } catch (MalformedURLException e) {
                        // Handle exception appropriately
                        LOGGER.log(Level.WARNING, "Error during create connection with remote selenium grid where remote url is : ", remoteUrl);
                    }
                }
                break;
            case Edge:
                if (!isGrid) {
                    driver = new EdgeDriver(withEgdeOptions(eopts));
                } else {
                    try {
                        driver = new RemoteWebDriver(new URL(remoteUrl), eopts);
                    } catch (MalformedURLException e) {
                        LOGGER.log(Level.WARNING, "Error during create connection with remote selenium grid where remote url is : ", remoteUrl);
                    }

                }
                break;
            case Safari:
                SafariOptions options = new SafariOptions();
                if (!isGrid) {

                    driver = new SafariDriver(options);
                } else {
                    try {
                        driver = new RemoteWebDriver(new URL(remoteUrl), options);
                    } catch (MalformedURLException e) {
                        LOGGER.log(Level.WARNING, "Error during create connection with remote selenium grid where remote url is : ", remoteUrl);
                    }
                }
                break;
            case Empty:
                return new EmptyDriver();

            case Emulator:
                if (selectedEmulator != null && !selectedEmulator.isEmpty()) {
                    System.out.println("Entering Emulator case");
                  try {
                   if (checkDeviceStatus()) {
                   startServer();
                   System.out.println("=== Appium Logs ===");
                   if (isServerRunning()) {   
                    DesiredCapabilities capabilities = new DesiredCapabilities();
                    capabilities.setCapability("platformName", "Android");
                    capabilities.setCapability("deviceName", selectedEmulator);
                    capabilities.setCapability("automationName", "UiAutomator2");
                    capabilities.setCapability("noReset", true);
                    capabilities.setCapability("uiautomator2ServerInstallTimeout", 60000);
                    capabilities.setCapability("appiumServerStartupTimeout", 60000);
                    capabilities.setCapability("ignoreHiddenApiPolicyError", true);
                    capabilities.setCapability("autoGrantPermissions", true);
                    try {
                        driver = new AndroidDriver(new URL("http://0.0.0.0:4723"), capabilities);
                        System.out.println("Emulator driver: " + driver);
                    } catch (MalformedURLException e) {
                        LOGGER.log(Level.WARNING, "Invalid URL for Android driver: ", e);
                    } catch (WebDriverException e) {
                        LOGGER.log(Level.WARNING, "WebDriverException occurred: ", e);
                    }
                    try {
                             // Connect to the Appium server using the server URL
                             URL serverUrl = getServerUrl();
                             driver = new AndroidDriver(serverUrl, capabilities);
                             System.out.println("Emulator driver: " + driver);
 
                             // Your test code here
                            } catch (WebDriverException e) {
                                LOGGER.log(Level.WARNING, "WebDriverException occurred: ", e);
                            }
                        } else {
                            System.out.println("Failed to start Appium server.");
                        }
                    } else {
                            System.out.println("Device is offline. Waiting for it to come online...");
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error occurred while starting the Appium server: ", e);
                    }
                } else {
                    throw new IllegalArgumentException("Emulator name is not specified.");
                }
                break;

            case RealDevice:
                DesiredCapabilities realCapabilities = new DesiredCapabilities();
                realCapabilities.setCapability("platformName", "Android");
                realCapabilities.setCapability("automationName", "UIAutomator2");

                // Fetch connected device details dynamically
                String deviceName = DeviceUtils.getConnectedDevice();
                String udid = DeviceUtils.getDeviceUdid(deviceName);
                String platformVersion = DeviceUtils.getPlatformVersion(deviceName);
                realCapabilities.setCapability("platformVersion", platformVersion);
                realCapabilities.setCapability("deviceName", deviceName);
                realCapabilities.setCapability("udid", udid);
                realCapabilities.setCapability("uiautomator2ServerInstallTimeout", 60000);
                realCapabilities.setCapability("appiumServerStartupTimeout", 60000);
                realCapabilities.setCapability("noReset", true);
                realCapabilities.setCapability("ignoreHiddenApiPolicyError", true);
                realCapabilities.setCapability("autoGrantPermissions", true);


                try {
                    URL serverUrl = getServerUrl();
                    driver = new AndroidDriver(serverUrl, realCapabilities);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                    System.out.println("Real Device driver: " + driver);
                    // Your test code here
                } catch (WebDriverException e) {
                    LOGGER.log(Level.WARNING, "WebDriverException occurred: ", e);
                }
                break;
            default:
                throw new AssertionError(browser.name());
        }
        if (driver != null && maximize && !(driver instanceof AndroidDriver)) {
            driver.manage().window().maximize();
        }
        return driver;
    }
    
    private static boolean checkDeviceStatus() {
        try {
            while (true) {
                Process process = Runtime.getRuntime().exec("adb devices");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean deviceFound = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("device") && !line.contains("List of devices attached")) {
                        deviceFound = true;
                        break;
                    } else if (line.contains("offline")) {
                        deviceFound = false;
                        break;
                    }
                }
                if (deviceFound) {
                    return true;
                } else {
                    System.out.println("Device is offline. Waiting for it to come online...");
                    Thread.sleep(5000); // Wait for 5 seconds before checking again
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while checking device status: ", e);
        }
        return false;
    }
    
    public static void startServer() {
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        String appiumJsPath = getAppiumJsPath();
        builder.withAppiumJS(new File(appiumJsPath));
        builder.usingAnyFreePort();
        builder.withArgument(() -> "--base-path", "/wd/hub");
        builder.withArgument(() -> "--allow-insecure", "adb_shell");
        builder.withArgument(() -> "--relaxed-security");
        service = AppiumDriverLocalService.buildService(builder);
        service.start();
        System.out.println("=== Appium Logs ===");
        System.out.println("Appium server URL: " + service.getUrl().toString());
    } 
    
    private static String getAppiumJsPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String appiumJsPath = System.getenv("APPIUM_JS_PATH");

        if (appiumJsPath == null || appiumJsPath.isEmpty()) {
            if (os.contains("win")) {
                // Windows path
                appiumJsPath = new File(System.getProperty("user.home"), "AppData/Roaming/npm/node_modules/appium/build/lib/main.js").getAbsolutePath();
            } else if (os.contains("linux")) {
            // Linux path (dynamic for nvm or global installations)
            String nodeVersion = getNodeVersion();
            String userHome = System.getProperty("user.home");

            // Assuming Node.js is installed via nvm
            appiumJsPath = new File(userHome, ".nvm/versions/node/" + nodeVersion + "/lib/node_modules/appium/build/lib/main.js").getAbsolutePath();

            // Fallback if nvm is not used
            if (!new File(appiumJsPath).exists()) {
                appiumJsPath = "/usr/lib/node_modules/appium/build/lib/main.js"; // Example for global installation path
            }
            }
        }
        return appiumJsPath;
    }
    // Helper method to get the Node.js version dynamically
    private static String getNodeVersion() {
    try {
        Process process = Runtime.getRuntime().exec("node -v");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String version = reader.readLine().replace("v", "").trim();
        return version;
    } catch (IOException e) {
        e.printStackTrace();
    }
    // Fallback version in case dynamic detection fails
    return "default";
}
 
    public static void stopServer() {
        if (service != null) {
            service.stop();
        }
    }
 
    public static boolean isServerRunning() {
        return service != null && service.isRunning();
    }
 
    public static URL getServerUrl() {
        return service.getUrl();
    }

    private static String toLString(Object o) {
        return Objects.toString(o, "").toLowerCase();
    }

    private static boolean isNullOrEmpty(Object o) {
        return Objects.isNull(o) || o.toString().isEmpty();
    }

    public static boolean isChromeEmulator(Emulator emulator) {
        return emulator != null && "Chrome Emulator".equalsIgnoreCase(emulator.getType());
    }
    private static boolean isAppiumNative(String remoteUrl, Map props) {
        return toLString(remoteUrl).matches(".*/wd/hub.*") && props != null && props.containsKey("platformName")
                && toLString(props.get("platformName")).matches("android|ios")
                && (!props.containsKey("browserName") || isNullOrEmpty(props.get("browserName")));
    }

    private static boolean isAndroidNative(Map props) {
        return toLString(props.get("platformName")).matches("android");
    }

    private static boolean isIOSNative(Map props) {
        return toLString(props.get("platformName")).matches("ios");
    }

//    private static final Logger LOGGER = Logger.getLogger(WebDriverFactory.class.getName());
    private static WebDriver createRemoteDriver(String url, DesiredCapabilities caps, Boolean checkForProxy, Properties props) {
        try {
            if (isAppiumNative(url, caps.asMap())) {
                if (isAndroidNative(caps.asMap())) {
                    return new io.appium.java_client.android.AndroidDriver(new URL(url), caps);
                } else if (isIOSNative(caps.asMap())) {
                    return new io.appium.java_client.ios.IOSDriver(new URL(url), caps);
                }
            }
            if (url == null) {
                return new RemoteWebDriver(caps);
            }

           
            return new RemoteWebDriver(new URL(url), caps);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    private static ChromeOptions getChromeEmulatorCaps(DesiredCapabilities caps, String deviceName) {
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", deviceName);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        chromeOptions.merge(caps);
        // caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return chromeOptions;
    }

    private static ChromeOptions getChromeUAECaps(DesiredCapabilities caps, Emulator emulator) {
        ChromeOptions chromeOptions = new ChromeOptions();
        if (!emulator.getUserAgent().trim().isEmpty()) {
            chromeOptions.addArguments("--user-agent=" + emulator.getUserAgent());
        }
        chromeOptions.merge(caps);
        return chromeOptions;
    }
    private static WebDriver checkAndSetSize(WebDriver driver, String size) {
        if (driver != null) {
            if (size.matches("[0-9]+ x [0-9]+")) {
                int w = Integer.valueOf(size.split("x")[0].trim());
                int h = Integer.valueOf(size.split("x")[1].trim());
                driver.manage().window().setSize(new Dimension(w, h));
            }
        }
        return driver;
    }

    
    private static FirefoxProfile addFFProfile(FirefoxProfile fProfile) {
        // Do your FirefoxProfile Settings over here
        fOptions.setProfile(fProfile);
        return fProfile;
    }

    private static ChromeOptions addChromeOptions(ChromeOptions chromeOptions) {
        // Do your ChromeOptions Settings over here
        //latency and other things as per user req.
        return chromeOptions;
    }

    private static EdgeOptions addEdgeOptions(EdgeOptions edgeOptions) {
        // Do your ChromeOptions Settings over here
        //latency and other things as per user req.
        return edgeOptions;
    }

    /**
     * Patch for https://github.com/newvisionQAHub/assureit/issues/7 Based on
     * https://github.com/mozilla/geckodriver/issues/759#issuecomment-308522851
     *
     * @param fDriver FirefoxDriver
     */
    private static void addGeckoDriverAddon(FirefoxDriver fDriver) {
        if (SystemDefaults.getClassesFromJar.get() && SystemDefaults.debugMode.get()) {
            if (FilePath.getFireFoxAddOnPath().exists()) {
                HttpCommandExecutor ce = (HttpCommandExecutor) fDriver.getCommandExecutor();
                String url = ce.getAddressOfRemoteServer() + "/session/" + fDriver.getSessionId()
                        + "/moz/addon/install";
                addGeckoDriverAddon(FilePath.getFireFoxAddOnPath(), url);
            }
        }
    }

    private static Boolean addGeckoDriverAddon(File addonLoc, String url) {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            Map<String, Object> addonInfo = new HashMap<>();
            addonInfo.put("temporary", true);
            addonInfo.put("path", addonLoc.getAbsolutePath());
            String json = new Gson().toJson(addonInfo);
            StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);
            return client.execute(post).getStatusLine().getStatusCode() == 200;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static List<String> getCapability(String browserName, ProjectSettings settings) {

        Properties prop = settings.getCapabilities().getCapabiltiesFor(browserName);
        List<String> caps = new ArrayList<>();

        if (prop != null) {
            prop.keySet().stream().forEach((key) -> {
                System.out.println("caps" + caps);
                if (prop.getProperty(key.toString()) == null || prop.getProperty(key.toString()).trim().isEmpty()) {

                } else {
                    caps.add(key.toString() + "=" + prop.getProperty(key.toString()));

                }
            });
        }

        return caps;
    }

    private static List<String> getOption(String browserName, ProjectSettings settings) {

        Properties prop = settings.getCapabilities().getCapabiltiesFor(browserName);
        List<String> options = new ArrayList<>();
        if (prop != null) {
            prop.keySet().stream().forEach((key) -> {
                System.out.println("Capbility loop options" + prop.keySet());
                if (prop.getProperty(key.toString()) == null || prop.getProperty(key.toString()).trim().isEmpty()) {
                    options.add(key.toString());
                    System.out.println("optionsif" + key.toString());
                }
            });
        }
        return options;
    }

    private static ChromeOptions withChromeOptions(ChromeOptions options) {

        if (!SystemDefaults.debugMode.get()) {

            options.addArguments("--disable-notifications");

        }

        if (SystemDefaults.getClassesFromJar.get() && SystemDefaults.debugMode.get()) {

            if (FilePath.getChromeAddOnPath().exists()) {

                options.addExtensions(FilePath.getChromeAddOnPath());

            }

        }

        options.addArguments("--start-maximized");

        options = addChromeOptions(options);

        System.out.println("=========================================================\n");

        System.out.println("Chrome launching with the following options/capabilities :" + "\n" + options + "\n");

        System.out.println("=========================================================\n");

        return options;

    }

    private static EdgeOptions withEgdeOptions(EdgeOptions Eoptions) {

        if (!SystemDefaults.debugMode.get()) {

            Eoptions.addArguments("--disable-notifications");

        }

        if (SystemDefaults.getClassesFromJar.get() && SystemDefaults.debugMode.get()) {

            if (FilePath.getChromeAddOnPath().exists()) {

                Eoptions.addExtensions(FilePath.getChromeAddOnPath());

            }

        }

        Eoptions.addArguments("--start-maximized");

        Eoptions = addEdgeOptions(Eoptions);

        System.out.println("=========================================================\n");

        System.out.println("Edge launching with the following options/capabilities :" + "\n" + Eoptions + "\n");

        System.out.println("=========================================================\n");

        return Eoptions;

    }

    private static Object getPropertyValueAsDesiredType(String value) {

        if (value != null && !value.isEmpty()) {

            if (value.toLowerCase().matches("(true|false)")) {

                return Boolean.valueOf(value);

            }

            if (value.matches("\\d+")) {

                return Integer.valueOf(value);

            } else {
                return value;
            }

        }

        return value;

    }

    private static FirefoxOptions withFirefoxOption(FirefoxOptions fOptions) {
        // Disable notifications only in non-debug mode:
        if (!SystemDefaults.debugMode.get()) {

            fOptions.addArguments("--disable-notifications");

        }
// Add extensions only if both conditions are met:
        if (SystemDefaults.getClassesFromJar.get() && SystemDefaults.debugMode.get()) {
            File extensionFile = new File(FilePath.getFireFoxAddOnPath().toString());  // Use .toString() to get the path as a String
            if (extensionFile.exists()) {
                try {
                    fProfile.addExtension(FilePath.getFireFoxAddOnPath());
                    System.out.println("firefox extesion added");
                    //  fOptions.addExtension(extensionFile);
                } catch (WebDriverException e) {
                    System.err.println("Error adding Firefox extension: " + e.getMessage());
                }
            } else {
                System.err.println("Warning: Firefox extension path not found: " + FilePath.getFireFoxAddOnPath());
            }

        }

        fOptions.addArguments("--start-maximized");

        fOptions = addFirefoxOptions(fOptions);

        System.out.println("=========================================================\n");

        System.out.println("Firefox launching with the following options/capabilities :" + "\n" + fOptions + "\n");

        System.out.println("=========================================================\n");

        return fOptions;

    }

    private static FirefoxOptions addFirefoxOptions(FirefoxOptions fOptions) {
        // Do your FirefoxOptions Settings over here
        //latency and other things as per user req.
        return fOptions;
    }

    private static FirefoxOptions withFirefoxProfile(FirefoxOptions fOptions) {
        // Directly retrieve or create FirefoxProfile within FirefoxOptions
        fProfile = fOptions.getProfile();

        // Set binary path if provided
        String binPath = System.getProperty("firefox.bin.path");
        if (binPath != null && !binPath.isEmpty()) {
            fOptions.setBinary(binPath);
        }

        return fOptions; // Return the configured FirefoxOptions
    }

}
