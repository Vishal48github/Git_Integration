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
package com.newvision.assureit.engine.core;

import com.newvision.assureit.datalib.component.ExecutionStep;
import com.newvision.assureit.datalib.component.TestSet;
import com.newvision.assureit.engine.cli.LookUp;
import com.newvision.assureit.engine.constants.FilePath;
import com.newvision.assureit.engine.drivers.WebDriverFactory.Browser;
import com.newvision.assureit.engine.settings.GlobalSettings;
import com.newvision.assureit.datalib.model.Tags;
import java.io.BufferedReader;
import org.apache.commons.lang.ArrayUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openqa.selenium.Platform;

public class RunManager {

    private static GlobalSettings globalSettings;

    private static Queue<RunContext> runQ;

    public static GlobalSettings getGlobalSettings() {
        if (globalSettings == null) {
            globalSettings = new GlobalSettings(FilePath.getConfigurationPath());
        }
        return globalSettings;
    }

    public static String getRunName() {
        if (globalSettings.isTestRun()) {
            return "TestCase - " + globalSettings.getScenario() + " - " + globalSettings.getTestCase();
        }
        return "TestSet - " + globalSettings.getRelease() + " - " + globalSettings.getTestSet();
    }

    public static void init() {
        upadteProperties();
    }

    private static void upadteProperties() {
        File appSettings = new File(FilePath.getAppSettings());
        if (appSettings.exists()) {
            try {
                Properties appSett = new Properties();
                appSett.load(new FileReader(appSettings));
                System.getProperties().putAll(appSett);
            } catch (IOException ex) {
                Logger.getLogger(RunManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void loadRunManager() throws Exception {
        if (globalSettings.isTestRun()) {
            runQ = getTestCaseRunManager();
        } else {
            runQ = getTestSetRunManager();
        }
    }

    public static Queue<RunContext> queue() throws Exception {
        return runQ;
    }

    static Queue<RunContext> getTestCaseRunManager() {
        Queue<RunContext> execQ = new LinkedList<>();
        RunContext exe = new RunContext();
        exe.Scenario = globalSettings.getScenario();
        exe.TestCase = globalSettings.getTestCase();
        exe.Description = "Test Run";
        exe.BrowserName = globalSettings.getBrowser();
        exe.Browser = Browser.fromString(exe.BrowserName);
        exe.Platform = Platform.ANY;
        exe.BrowserVersion = "default";
        exe.Iteration = "Single";
        exe.print();
        execQ.add(exe);
        return execQ;
    }

    static Queue<RunContext> getTestSetRunManager() throws Exception {
        Queue<RunContext> execQ = new LinkedList<>();
        TestSet testSet = Control.exe.getTestSet();
        int count = 0;
        try {
            testSet.loadTableModel();	
			int tagsMatched=0;
			if (!testSet.getExecutableSteps().isEmpty()) {
				for (ExecutionStep step : testSet.getExecutableSteps()) {
					Tags tags = step.getProject().getInfo().getData()
							.findOrCreate(step.getTestCaseName(), step.getTestScenarioName()).getTags();
					if (globalSettings.getTags()!=null) {
						String tagsArr[] = globalSettings.getTags().split(",");
						for (String tag : tagsArr) {
							
							if (tags.contains(tag)) {
								tagsMatched = ArrayUtils.indexOf(tagsArr, tag)+1;
								count=1;
								break;
							}
							else {
								
								count=0;
							}
						}
						if (count != 0) {
							addRunContext(step, execQ);
						}
					}
					else
						addRunContext(step, execQ);
						
				}
				if(tagsMatched==0 && globalSettings.getTags()!=null)
					System.out.println("----------------------------------------------------------\n"
				            +"[Error] No testcase in the selected test set contain the tag(s) ["
							+ globalSettings.getTags() + "]");
			} else {
				    throw new Exception("No testcases are selected for execution in - " + testSet.getName());
			}
        } catch (Exception ex) {
            throw new Exception(String.format(
                    "Not able to load TestSet [%s/%s]\n[%S]",
                    testSet.getRelease().getName(), testSet.getName(), ex.getMessage()));
        }
        System.out.println("----------------------------------------------------------");
        System.out.println(String.format(
                "[%s] TestCase%s selected for execution from [//%s/%s]",
                execQ.size(), execQ.size() > 1 ? "s" : "", testSet.getRelease().getName(),
                testSet.getName()));
        System.out.println("----------------------------------------------------------");
        return execQ;
    }

    private static void addRunContext(ExecutionStep step, Queue<RunContext> execQ) {
        RunContext exe = new RunContext();
        exe.Scenario = step.getTestScenarioName();
        exe.TestCase = step.getTestCaseName();
        exe.Description = exe.TestCase;
        exe.PlatformValue = step.getPlatform();
        exe.Platform = getPlatform(exe.PlatformValue);
        String browser = RunManager.getGlobalSettings().getBrowser();
        if (browser != null && !(browser.equals("")) && LookUp.cliflag == true) {
            exe.BrowserName = browser;
        } else {
            exe.BrowserName = step.getBrowser();
        }
        exe.Browser = Browser.fromString(exe.BrowserName);
        exe.BrowserVersionValue = step.getBrowserVersion();
        exe.BrowserVersion = getBrowserVersion(exe.BrowserVersionValue);
        exe.Iteration = step.getIteration();
        
        // Store browser names in an array
        String[] knownBrowsers = {"Chrome", "Firefox", "IE", "Edge", "Safari", "No Browser"};

        // Check if BrowserName is not null and doesn't contain values from the array
        if (exe.BrowserName != null
                && Arrays.stream(knownBrowsers).noneMatch(browserName -> browserName.equalsIgnoreCase(exe.BrowserName))) {
            System.out.println("Emulator detected in the step. Checking and launching if necessary.");
            handleEmulatorLaunch(exe.BrowserName);
        } else {
            System.out.println("Browser detected or not specified: " + exe.BrowserName);
        }
        exe.print();
        execQ.add(exe);
    }

    public static int getThreadCount(String threadCount) {
        switch (threadCount.toLowerCase()) {
            case "single":
                return 1;
            case "2 threads":
                return 2;
            case "3 threads":
                return 3;
            case "4 threads":
                return 4;
            case "5 threads":
                return 5;
            default:
                return getThread(threadCount);
        }
    }

    static int getThread(String threadCount) {
        try {
            int x = Integer.parseInt(threadCount);
            return Math.max(1, x);
        } catch (NumberFormatException ex) {
            System.out.println("Error Converting value[" + threadCount + "] Resetting thread to 1");
            return 1;
        }
    }

    static long getExecutionTimeout(String exeTimeout) {
        try {
            int l = Integer.parseInt(exeTimeout);
            return Math.abs(l);
        } catch (NumberFormatException ex) {
            return 300L;
        }
    }

    static String getBrowserVersion(String bVersion) {
        if (bVersion == null || bVersion.isEmpty()) {
            return "default";
        }
        return bVersion;
    }

    static Platform getPlatform(String platform) {
        if (platform != null && !platform.trim().isEmpty()) {
            if (platform.contains("WIN8_1")) {
                return Platform.fromString("WIN8.1");
            }
            if (platform.contains("_")) {
                platform = platform.replace("_", " ");
                return Platform.fromString(platform.toUpperCase());
            } else {
                return Platform.fromString(platform.toUpperCase());
            }
        }
        return Platform.ANY;
    }

    public static void clear() {
        runQ.clear();
        runQ = null;
    }
    
      public static void handleEmulatorLaunch(String browserName) {
        try {
            String emulatorName = browserName; // Use the passed browserName dynamically
            if (!isEmulatorRunning("emulator-" + emulatorName)) { // Use emulator prefix for identification
                System.out.println("Emulator '" + emulatorName + "' is not running. Launching it now.");
                launchEmulator(emulatorName);
            } else {
                System.out.println("Emulator '" + emulatorName + "' is already running.");
            }
        } catch (Exception e) {
            System.err.println("Error while handling emulator launch: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
  private static boolean isEmulatorRunning(String emulatorName) {
    try {
        Process process = Runtime.getRuntime().exec("adb devices");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("ADB Output: " + line);
            if (line.startsWith(emulatorName)) {
                System.out.println("Emulator '" + emulatorName + "' is running.");
                return true;
            }
        }
    } catch (IOException e) {
        System.err.println("Error checking emulator status: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

    public static boolean launchEmulator(String emulatorName) {
        try {
            // Get the path to the Android SDK
            String androidHome = System.getenv("ANDROID_HOME");
            if (androidHome != null) {
                // Determine the operating system
                String os = System.getProperty("os.name").toLowerCase();
                String emulatorPath;
                String adbPath;

                // Set paths based on the OS
                if (os.contains("win")) {
                    emulatorPath = androidHome + "\\emulator\\emulator.exe";
                    adbPath = androidHome + "\\platform-tools\\adb.exe";
                } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                    emulatorPath = androidHome + "/emulator/emulator";
                    adbPath = androidHome + "/platform-tools/adb";
                } else {
                    System.err.println("Unsupported operating system: " + os);
                    return false; // Unsupported OS
                }

                // Step 1: List connected devices
                ProcessBuilder listDevicesBuilder = new ProcessBuilder(adbPath, "devices");
                Process listDevicesProcess = listDevicesBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(listDevicesProcess.getInputStream()));
                String line;
                boolean realDeviceConnected = false;
                String runningEmulatorId = null;

                while ((line = reader.readLine()) != null) {
                    if (line.endsWith("device") && !line.startsWith("emulator-")) {
                        realDeviceConnected = true;
                        break;
                    } else if (line.startsWith("emulator-")) {
                        runningEmulatorId = line.split("\t")[0]; // Capture the running emulator ID
                    }
                }

                if (realDeviceConnected) {
                    System.out.println("Testcases running on Real device");
                    return false; // Abort the emulator launch
                }

                // Step 3: Terminate any running emulators
                if (runningEmulatorId != null) {
                    ProcessBuilder killEmulatorBuilder = new ProcessBuilder(adbPath, "-s", runningEmulatorId, "emu", "kill");
                    killEmulatorBuilder.start().waitFor();
                    System.out.println("Terminated running emulator: " + runningEmulatorId);
                }

                // Step 4: Launch the new emulator
                ProcessBuilder processBuilder = new ProcessBuilder(emulatorPath, "-avd", emulatorName);
                processBuilder.start();
                return true; // Emulator launched successfully
            } else {
                System.err.println("ANDROID_HOME environment variable is not set.");
                return false; // Failed to launch emulator due to missing environment variable
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false; // Failed to launch emulator
        }
    }
}
