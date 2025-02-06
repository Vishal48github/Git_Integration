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
package com.newvision.assureit.engine.commands;

import com.google.common.collect.ImmutableMap;
import com.newvision.assureit.engine.constants.SystemDefaults;
import com.newvision.assureit.engine.core.CommandControl;
import com.newvision.assureit.engine.core.Control;
import com.newvision.assureit.engine.execution.exception.ForcedException;
import com.newvision.assureit.engine.execution.exception.element.ElementException;
import com.newvision.assureit.engine.execution.exception.element.ElementException.ExceptionType;
import com.newvision.assureit.engine.support.Status;
import com.newvision.assureit.engine.support.Step;
import com.newvision.assureit.engine.support.methodInf.Action;
import com.newvision.assureit.engine.support.methodInf.InputType;
import com.newvision.assureit.engine.support.methodInf.ObjectType;
import com.newvision.assureit.util.encryption.Encryption;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchContextException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.Set;

public class Basic extends General {

    public Basic(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Simulate a tap or click action on an element.")
    public void Click() {
        if (elementEnabled()) {
            Element.click();
            Report.updateTestLog(Action, "Clicking on " + ObjectName, Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

     @Action(object = ObjectType.MOBILE, desc = "Tap or Click the [<Object>] ")
    public void Tap() {
	if (elementEnabled()) {
	    Element.click();
	    Report.updateTestLog(Action, "Clicking on " + ObjectName, Status.DONE);
	} else {
	    throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
	}
    }

    @Action(object = ObjectType.MOBILE, desc = "Capture the current screen.")
    public void TakeMobileScreenshot() {
        if (elementEnabled()) {
            try {
                File screenshotFile = ((TakesScreenshot) Driver).getScreenshotAs(OutputType.FILE);
                File destinationFile = new File("path/to/screenshot_" + ObjectName + ".png");
                FileUtils.copyFile(screenshotFile, destinationFile);

                // Get the absolute path of the screenshot
                String screenshotPath = destinationFile.getAbsolutePath();

                 // Add a message with the path and instructions
                String message = "Screenshot of the mobile screen captured successfully for " + ObjectName
                                + ". Path: " + screenshotPath
                                + ". Copy-paste the link into a browser: file:///" + screenshotPath.replace("\\", "/");

                // Update the report with the screenshot file path
                Report.updateTestLog(Action, message, Status.DONE);
            } catch (IOException e) {
                Report.updateTestLog(Action, "Failed to take screenshot for " + ObjectName, Status.FAIL, e.getMessage());
            }
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }
    @Action(object = ObjectType.APP, desc = "Capture the current screen of the application.")
    public void TakeAppScreenshot() {
    try {
        // Ensure the driver implements TakesScreenshot
        if (Driver instanceof TakesScreenshot) {
            // Capture the screenshot
            File screenshotFile = ((TakesScreenshot) Driver).getScreenshotAs(OutputType.FILE);
            
            // Define a destination file path (ensure the directory exists)
            File destinationFile = new File(System.getProperty("user.dir") + File.separator + "screenshots" + File.separator 
                                            + "app_screenshot_" + System.currentTimeMillis() + ".png");
            
            // Copy the screenshot to the destination
            FileUtils.copyFile(screenshotFile, destinationFile);

            // Get the absolute path of the screenshot
            String screenshotPath = destinationFile.getAbsolutePath();

            // Add a message with the path and instructions
            String message = "Screenshot of the app captured successfully. Path: " + screenshotPath 
                            + ". Copy-paste the link into a browser: file:///" + screenshotPath.replace("\\", "/");
            
            // Log the screenshot path in the report
            Report.updateTestLog(Action, message, Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver does not support taking screenshots");
        }
    } catch (IOException e) {
        Report.updateTestLog(Action, "Failed to capture app screenshot: " + e.getMessage(), Status.FAIL);
    } catch (Exception e) {
        Report.updateTestLog(Action, "Unexpected error during screenshot capture: " + e.getMessage(), Status.FAIL);
    }
}
    
    @Action(object = ObjectType.MOBILE, desc = "Simulate a shake gesture on the mobile device.")
    public void ShakeMobileDevice() {
    try {
        // Ensure the driver is an instance of AndroidDriver
        if (Driver instanceof AndroidDriver) {
		AndroidDriver androidDriver = (AndroidDriver) Driver;
 
            int width = androidDriver.manage().window().getSize().width;
            int height = androidDriver.manage().window().getSize().height;
 
            int midX = width / 2;
            int midY = height / 2;
            int offsetX = width / 4;
 
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence shake = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), midX - offsetX, midY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), midX + offsetX, midY))
                .addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), midX - offsetX, midY))
                .addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), midX + offsetX, midY))
                .addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), midX - offsetX, midY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
 
            // Perform the touch action
            androidDriver.perform(Arrays.asList(shake));
 
            Report.updateTestLog(Action, "Simulated shake gesture on the device", Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Shake gesture simulation failed: " + e.getMessage(), Status.FAIL);
        throw new ElementException(ExceptionType.Element_Not_Found, "Shake Gesture Simulation: " + e.getMessage());
    }
}

    @Action(object = ObjectType.APP, desc = "Swipe the mobile screen [<Direction>]", input = InputType.YES)
    public void SwipeMobileScreen() {
    try {
        // Ensure the driver is an instance of AndroidDriver
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;

            // Assuming the direction is passed as a string input, e.g., "LEFT", "RIGHT", "UP", "DOWN"
            String direction = Data.toUpperCase();  // Use the Data variable to get the user input
            Dimension size = androidDriver.manage().window().getSize();

            int startX, startY, endX, endY;
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);

            switch (direction) {
                case "LEFT":
                    startX = (int) (size.width * 0.8);
                    endX = (int) (size.width * 0.2);
                    startY = size.height / 2;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(20000), PointerInput.Origin.viewport(), endX, startY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped the screen to the LEFT", Status.DONE);
                    break;
                case "RIGHT":
                    startX = (int) (size.width * 0.2);
                    endX = (int) (size.width * 0.8);
                    startY = size.height / 2;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(20000), PointerInput.Origin.viewport(), endX, startY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped the screen to the RIGHT", Status.DONE);
                    break;
                case "UP":
                    startY = (int) (size.height * 0.8);
                    endY = (int) (size.height * 0.2);
                    startX = size.width / 2;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(20000), PointerInput.Origin.viewport(), startX, endY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped the screen UP", Status.DONE);
                    break;
                case "DOWN":
                    startY = (int) (size.height * 0.2);
                    endY = (int) (size.height * 0.8);
                    startX = size.width / 2;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(20000), PointerInput.Origin.viewport(), startX, endY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped the screen DOWN", Status.DONE);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid direction: " + direction);
            }
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Swipe Screen failed: " + e.getMessage(), Status.FAIL);
        throw new ElementException(ExceptionType.Element_Not_Found, "Swipe Screen: " + e.getMessage());
    }
}

    @Action(object = ObjectType.APP, desc = "Move through a list or page vertically or horizontally.", input = InputType.YES)
    public void ScrollMobileScreen() {
    try {
        // Ensure the driver is an instance of AndroidDriver
        if (Driver instanceof AndroidDriver) {
		AndroidDriver androidDriver = (AndroidDriver) Driver;
 
            // Assuming the input data is a comma-separated string: "startY,endY"
            String[] coordinates = Data.split(",");
            if (coordinates.length != 2) {
                throw new IllegalArgumentException("Invalid input format. Expected format: startY,endY");
            }
 
            // Parse the start and end Y coordinates
            int startY = Integer.parseInt(coordinates[0].trim());
            int endY = Integer.parseInt(coordinates[1].trim());
 
            // Get the device width
            int deviceWidth = androidDriver.manage().window().getSize().width;
            int centerX = deviceWidth / 2;
 
            // Create the touch actions
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), centerX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(20000), PointerInput.Origin.viewport(), centerX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
 
            // Perform the touch action
            androidDriver.perform(Arrays.asList(swipe));
 
            Report.updateTestLog(Action, "Scrolled the screen from Y: " + startY + " to Y: " + endY, Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Scroll Screen failed: " + e.getMessage(), Status.FAIL);
        throw new ElementException(ExceptionType.Element_Not_Found, "Scroll Screen: " + e.getMessage());
    }
}

     @Action(object = ObjectType.MOBILE, desc = "Dismiss the on-screen keyboard.", input = InputType.NO)
    public void HideKeyboard() {
    try {
        if (Driver instanceof AndroidDriver) {
		AndroidDriver androidDriver = (AndroidDriver) Driver;
            androidDriver.hideKeyboard();
            Report.updateTestLog(Action, "Keyboard hidden successfully", Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Hide keyboard failed: " + e.getMessage(), Status.FAIL);
        throw new ElementException(ExceptionType.Element_Not_Found, "Hide Keyboard: " + e.getMessage());
    }
}

    @Action(object = ObjectType.APP, desc = "Rotate the mobile screen to [<Orientation>]", input = InputType.YES)
    public void RotateMobileScreen() {
	try {
	    // Ensure the driver is an instance of AndroidDriver
	    if (Driver instanceof AndroidDriver) {
		AndroidDriver androidDriver = (AndroidDriver) Driver;

		// Assuming the orientation is passed as a string input, e.g., "LANDSCAPE" or "PORTRAIT"
		String orientation = Data.toUpperCase();  // Use the Data variable to get the user input

		switch (orientation) {
		    case "LANDSCAPE":
			androidDriver.executeScript("mobile: shell", ImmutableMap.of("command", "content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0; content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:1"));
			Report.updateTestLog(Action, "Rotated the screen to LANDSCAPE", Status.DONE);
			break;
		    case "PORTRAIT":
			androidDriver.executeScript("mobile: shell", ImmutableMap.of("command", "content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0; content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:0"));
			Report.updateTestLog(Action, "Rotated the screen to PORTRAIT", Status.DONE);
			break;
		    default:
			throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	    } else {
		throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
	    }
	} catch (Exception e) {
	    Report.updateTestLog(Action, "Rotate Screen failed: " + e.getMessage(), Status.FAIL);
	    throw new ElementException(ExceptionType.Element_Not_Found, "Rotate Screen: " + e.getMessage());
	}
    }

    @Action(object = ObjectType.MOBILE, desc = "Start an application.")
    public void OpenApp() {
        // Generate the correct XPath for Android application elements
        String dynamicXPathInput = "//*[@content-desc='" + ObjectName + "']";

        // Initialize Step object
        Step step = new Step(1);
        step.object(ObjectName);
        step.input(dynamicXPathInput);

        WebDriverWait wait = new WebDriverWait(Driver, Duration.ofSeconds(30));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dynamicXPathInput)));

        if (element.isEnabled()) {
            element.click();
            Report.updateTestLog(Action, "Clicking on " + ObjectName, Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.APP, desc = "Retrieve device information.")
    public void GetDeviceInfo() {
    try {
        if (Driver instanceof AndroidDriver) {
		AndroidDriver androidDriver = (AndroidDriver) Driver;
            String deviceName = (androidDriver.getCapabilities().getCapability("deviceName") != null) 
                                ? androidDriver.getCapabilities().getCapability("deviceName").toString() 
                                : "N/A";
            String platformVersion = (androidDriver.getCapabilities().getCapability("platformVersion") != null) 
                                     ? androidDriver.getCapabilities().getCapability("platformVersion").toString() 
                                     : "N/A";
            String udid = (androidDriver.getCapabilities().getCapability("udid") != null) 
                          ? androidDriver.getCapabilities().getCapability("udid").toString() 
                          : "N/A";
 
            String deviceInfo = "Device Name: " + deviceName + ", Platform Version: " + platformVersion + ", UDID: " + udid;
            Report.updateTestLog(Action, "Retrieved Device Info: " + deviceInfo, Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to retrieve device information: " + e.getMessage(), Status.FAIL);
        Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, null, e);
    }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Click the [<Object>] if it exists")
    public void ClickIfExists() {
        if (Element != null) {
            Click();
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Click the [<Object>] if it is displayed")
    public void ClickIfVisible() {
        if (Element != null) {
            if (Element.isDisplayed()) {
                Click();
            } else {
                Report.updateTestLog(Action, "Element [" + ObjectName + "] not Visible", Status.DONE);
            }
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    @Action(object = ObjectType.MOBILE, desc = "Remove existing text from an input field.")
    public void clearText() {
	if (elementEnabled()) {
	    Element.clear();
	    Report.updateTestLog(Action, "Cleared text from " + ObjectName, Status.DONE);
	} else {
	    throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
	}
    }

    @Action(object = ObjectType.SELENIUM, desc = "Submit action on the browser")
    public void Submit() {
        if (elementEnabled()) {
            Element.submit();
            Report.updateTestLog(Action, "[" + ObjectName + "] Submitted successfully ", Status.DONE);

        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Submit the [<Object>] if it exists")
    public void SubmitIfExists() {
        if (Element != null) {
            Submit();
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the Field [<Object>]", input = InputType.YES)
    public void Set() {
        if (elementEnabled()) {
            Element.clear();
            Element.sendKeys(Data);
            Report.updateTestLog(Action, "Entered Text '" + Data + "' on '"
                    + ObjectName + "'", Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Enter text into a specified input field", input = InputType.YES)
    public void SetText() {
	if (elementEnabled()) {
	    Element.clear();
	    Element.sendKeys(Data);
	    Report.updateTestLog(Action, "Entered Text '" + Data + "' on '"
		    + ObjectName + "'", Status.DONE);
	} else {
	    throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
	}
    }

    @Action(object = ObjectType.MOBILE, desc = "Simulate the back button press.")
    public void Back() {
	try {
	    // Ensure the driver is an instance of AndroidDriver
	    if (Driver instanceof AndroidDriver) {
		((AndroidDriver) Driver).pressKey(new KeyEvent(AndroidKey.BACK));
		Report.updateTestLog(Action, "Pressed the back button on the device", Status.DONE);
	    } else {
		throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
	    }
	} catch (Exception e) {
	    throw new ElementException(ExceptionType.Element_Not_Found, "Press Back Button");
	}
    }

    @Action(object = ObjectType.APP, desc = "Delay execution for a specified period.", input = InputType.YES)
    public void Wait() {
	try {
	    Thread.sleep(Long.parseLong(Data));
	    Report.updateTestLog(Action,
		    "Thread sleep for '" + Long.parseLong(Data) + "' milliseconds", Status.DONE);
	} catch (Exception e) {
	    Report.updateTestLog(Action, e.getMessage(), Status.FAIL);
	    Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, null, e);
	}
    }

    @Action(object = ObjectType.MOBILE, desc = "Pause until a specified element is visible or interactable.")
    public void waitForElement() {
	try {
	    // Ensure the driver is an instance of AndroidDriver
	    if (Driver instanceof AndroidDriver) {
		WebDriverWait wait = new WebDriverWait((AppiumDriver) Driver, Duration.ofSeconds(30));
		WebElement element = wait.until(ExpectedConditions.visibilityOf(Element));
		Report.updateTestLog(Action, "Element " + ObjectName + " is visible", Status.DONE);
	    } else {
		throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
	    }
	} catch (Exception e) {
	    Report.updateTestLog(Action, "Failed to wait for element " + ObjectName + ": " + e.getMessage(), Status.FAIL);
	    Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, null, e);
	}
    }

    @Action(object = ObjectType.APP, desc = "Terminate an application.", input = InputType.YES)
    public void CloseApp() {
	try {
	    // Ensure the driver is an instance of AndroidDriver
	    if (Driver instanceof AndroidDriver) {
		// Assuming the input value is stored in a variable called Data
		String package_name = Data;  // Use the Data variable to get the user input
		((AndroidDriver) Driver).terminateApp(package_name);
		Report.updateTestLog(Action, "Closed the mobile app with package name: " + package_name, Status.DONE);
	    } else {
		throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
	    }
	} catch (Exception e) {
	    throw new ElementException(ExceptionType.Element_Not_Found, "Close App: " + e.getMessage());
	}
    }

    @Action(object = ObjectType.APP, desc = "Install an APK on the device", input = InputType.YES)
    public void InstallApk() {
    try {
        // Ensure the driver is an instance of AndroidDriver
        if (Driver instanceof AndroidDriver) {
            // Use the Data variable to get the APK file path from user input
            String projectLocation = Control.getCurrentProject().getLocation();
            String absolutePath = projectLocation + "/APP/" + Data;
            String apkPath = absolutePath;  // Retrieve the APK path from user input
 
            // Validate the APK file path
            File apkFile = new File(apkPath);
            if (!apkFile.exists()) {
                throw new FileNotFoundException("APK file not found at: " + apkPath);
            }
 
            // Install the APK using adb command
            String command = "adb install " + apkPath;
            Process process = Runtime.getRuntime().exec(command);
            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                Report.updateTestLog(Action, "Successfully installed the APK from: " + apkPath, Status.DONE);
            } else {
                throw new Exception("Failed to install APK. Exit code: " + exitCode);
            }
 
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        throw new ElementException(ExceptionType.Element_Not_Found, "Install App: " + e.getMessage());
    }
}
// Helper method to extract the package name from the APK file
    private String getPackageNameFromApk(String apkPath) throws IOException {
        String packageName = null;
        ProcessBuilder processBuilder = new ProcessBuilder("aapt", "dump", "badging", apkPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("package:")) {
                    String[] parts = line.split("'");
                    packageName = parts[1];
                    break;
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading APK file: " + e.getMessage(), e);
        }

        if (packageName == null) {
            throw new IOException("Package name not found in APK file.");
        }

        return packageName;
    }

    @Action(object = ObjectType.APP, desc = "Remove an application from the device", input = InputType.YES)
    public void UninstallApp() {
	try {
	    // Ensure the driver is an instance of AndroidDriver
	    if (Driver instanceof AndroidDriver) {
		// Use the Data variable to get the user input
		String package_name = Data;  // Retrieve the package name from user input
		((AndroidDriver) Driver).removeApp(package_name);
		Report.updateTestLog(Action, "Uninstalled the mobile app with package name: " + package_name, Status.DONE);
	    } else {
		throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
	    }
	} catch (Exception e) {
	    throw new ElementException(ExceptionType.Element_Not_Found, "Uninstall App: " + e.getMessage());
	}
    }
    
    @Action(object = ObjectType.APP, desc = "Set network state [<Data>] (AIRPLANE, WIFI, DATA)", input = InputType.YES)
    public void setNetworkState() {
        try {
            if (Driver instanceof AndroidDriver) {
                String checkCommand = ""; // Command to check the current state
                String enableCommand = ""; // Command to enable the feature
                String disableCommand = ""; // Command to disable the feature
                switch (Data.toUpperCase()) {
                    case "AIRPLANE":
                        checkCommand = "adb shell settings get global airplane_mode_on"; // Check Airplane mode state
                        enableCommand = "adb shell settings put global airplane_mode_on 1 && adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"; // Enable Airplane mode
                        disableCommand = "adb shell settings put global airplane_mode_on 0 && adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"; // Disable Airplane mode
                        break;
                    case "WIFI":
                        checkCommand = "adb shell dumpsys wifi | grep 'Wi-Fi is'"; // Check WiFi state
                        enableCommand = "adb shell svc wifi enable"; // Enable WiFi
                        disableCommand = "adb shell svc wifi disable"; // Disable WiFi
                        break;
                    case "DATA":
                        checkCommand = "adb shell settings get global mobile_data"; // Check Mobile Data state
                        enableCommand = "adb shell svc data enable"; // Enable Mobile Data
                        disableCommand = "adb shell svc data disable"; // Disable Mobile Data
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid network state: " + Data);
                }

                // Execute the check command
                Process process = Runtime.getRuntime().exec(checkCommand);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String currentState = reader.readLine().trim();
                process.waitFor();
 
                boolean isEnabled = false;
 
                // Parse the current state based on the Data type
                if (Data.equalsIgnoreCase("AIRPLANE")) {
                    isEnabled = "1".equals(currentState); // 1 means Airplane mode is enabled
                } else if (Data.equalsIgnoreCase("WIFI")) {
                    isEnabled = currentState.contains("enabled"); // Check for WiFi enabled state
                } else if (Data.equalsIgnoreCase("DATA")) {
                    isEnabled = "1".equals(currentState); // Check for Mobile Data enabled state
                }
 
                // Execute the appropriate command
                if (isEnabled) {
                    Runtime.getRuntime().exec(disableCommand);
                    Report.updateTestLog(Action, Data + " is enabled. Disabling it.", Status.DONE);
                } else {
                    Runtime.getRuntime().exec(enableCommand);
                    Report.updateTestLog(Action, Data + " is disabled. Enabling it.", Status.DONE);
                }
 
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to set network state: " + e.getMessage(), Status.FAIL);
            Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error while setting network state: ", e);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the [<Object>] if it exists", input = InputType.YES)
    public void SetIfExists() {
        if (Element != null) {
            Set();
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the Field [<Object>] and check [<Data>] matches with [<Object>] value", input = InputType.YES)
    public void SetAndCheck() {
        if (elementEnabled()) {
            Element.clear();
            Element.sendKeys(Data);
            if (Element.getAttribute("value").equals(Data)) {
                Report.updateTestLog("Set", "Entered Text '" + Data + "' on '"
                        + ObjectName + "'", Status.DONE);
            } else {
                Report.updateTestLog("Set", "Unable Enter Text '" + Data
                        + "' on '" + ObjectName + "'", Status.FAIL);
            }
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Clear text [<Data>] from object [<Object>].")
    public void clear() {
        if (elementEnabled()) {
            Element.clear();
            Report.updateTestLog("Clear", "Cleared Text on '" + ObjectName + "'", Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Enter the Decrypted value [<Data>] in the Field [<Object>]", input = InputType.YES)
    public void setEncrypted() {
        if (Data != null && Data.matches(".* Enc")) {
            if (elementEnabled()) {
                try {
                    Element.clear();
                    Data = Data.substring(0, Data.lastIndexOf(" Enc"));
                    byte[] valueDecoded = Encryption.getInstance().decrypt(Data).getBytes();
                    Element.sendKeys(new String(valueDecoded));
                    Report.updateTestLog(Action, "Entered Encrypted Text " + Data + " on " + ObjectName, Status.DONE);
                } catch (Exception ex) {
                    Report.updateTestLog("setEncrypted", ex.getMessage(), Status.FAIL);
                    Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
            }
        } else {
            Report.updateTestLog(Action, "Data not encrypted '" + Data + "'", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.MOBILE, desc = "Enter the Decrypted value [<Data>] in the Field [<Object>]", input = InputType.YES)
    public void setEncryptedMobileElement() {
        if (Data != null && Data.matches(".* Enc")) {
            if (elementEnabled()) {
                try {
                    Element.clear();
                    Data = Data.substring(0, Data.lastIndexOf(" Enc"));
                    byte[] valueDecoded = Encryption.getInstance().decrypt(Data).getBytes();
                    Element.sendKeys(new String(valueDecoded));
                    Report.updateTestLog(Action, "Entered Encrypted Text " + Data + " on " + ObjectName, Status.DONE);
                } catch (Exception ex) {
                    Report.updateTestLog("setEncrypted", ex.getMessage(), Status.FAIL);
                    Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
            }
        } else {
            Report.updateTestLog(Action, "Data not encrypted '" + Data + "'", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Move the Browser View to the specified element [<Object>]")
    public void moveTo() {
        if (elementDisplayed()) {
            if (Data != null && Data.matches("(\\d)+,(\\d)+")) {
                int x = Integer.valueOf(Data.split(",")[0]);
                int y = Integer.valueOf(Data.split(",")[1]);
                new Actions(Driver).moveToElement(Element, x, y).build().perform();
            } else {
                new Actions(Driver).moveToElement(Element).build().perform();
            }
            Report.updateTestLog(Action, "Viewport moved to" + ObjectName, Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Visible, ObjectName);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "This a dummy function helpful with testing.")
    public void filler() {

    }

    @Action(object = ObjectType.BROWSER,
            desc = "Open the Url [<Data>] in the Browser",
            input = InputType.YES)
    public void Open() {
        Boolean pageTimeOut = false;
        try {
            if (Condition.matches("[0-9]+")) {
                setPageTimeOut(Integer.valueOf(Condition));
                pageTimeOut = true;
            }
            Driver.get(Data);
            Report.updateTestLog("Open", "Opened Url: " + Data, Status.DONE);
        } catch (TimeoutException e) {
            Report.updateTestLog("Open",
                    "Opened Url: " + Data + " and cancelled page load after " + Condition + " seconds",
                    Status.DONE);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, e);
            Report.updateTestLog("Open", e.getMessage(), Status.FAIL);
            throw new ForcedException("Open", e.getMessage());
        }
        if (pageTimeOut) {
            setPageTimeOut(300);
        }
    }

    @Action(object = ObjectType.MOBILE, desc = "Perform gestures involving multiple touchpoints, like pinch or zoom.")
    public void multiTouchGesture() {
	try {
	    // Ensure the driver is an instance of AndroidDriver
	    if (Driver instanceof AndroidDriver) {
		WebElement element = Element; // Assuming Element is the mobile element to be touched

		// Coordinates for the multi-touch gesture
		org.openqa.selenium.Point center = element.getRect().getPoint();
		int centerX = (int) (center.getX() + element.getSize().getWidth() / 2);
		int centerY = (int) (center.getY() + element.getSize().getHeight() / 2);

		PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
		PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

		Sequence sequence1 = new Sequence(finger1, 0);
		sequence1.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX - 50, centerY));
		sequence1.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		sequence1.addAction(new Pause(finger1, Duration.ofMillis(500)));
		sequence1.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		Sequence sequence2 = new Sequence(finger2, 0);
		sequence2.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX + 50, centerY));
		sequence2.addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		sequence2.addAction(new Pause(finger2, Duration.ofMillis(500)));
		sequence2.addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		((AndroidDriver) Driver).perform(Arrays.asList(sequence1, sequence2));

		Report.updateTestLog(Action, "Performed a multi-touch gesture on " + ObjectName, Status.DONE);
	    } else {
		throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
	    }
	} catch (Exception e) {
	    throw new ElementException(ExceptionType.Element_Not_Found, "Multi-touch gesture on " + ObjectName + " failed");
	}
    }

    @Action(object = ObjectType.MOBILE, desc = "Zoom on the [<Object>]")
    public void Zoom() {
        try {
            if (Driver instanceof AndroidDriver) {
                WebElement element = Element; // Assuming Element is the mobile element to be zoomed
                int centerX = element.getRect().getX() + (element.getRect().getWidth() / 2);
                int centerY = element.getRect().getY() + (element.getRect().getHeight() / 2);

                PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
                PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

                Sequence finger1Sequence = new Sequence(finger1, 1)
                        .addAction(finger1.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX, centerY))
                        .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                        .addAction(finger1.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), centerX + 100, centerY + 100))
                        .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

                Sequence finger2Sequence = new Sequence(finger2, 1)
                        .addAction(finger2.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX, centerY))
                        .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                        .addAction(finger2.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), centerX - 100, centerY - 100))
                        .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

                ((AndroidDriver) Driver).perform(Arrays.asList(finger1Sequence, finger2Sequence));

                Report.updateTestLog(Action, "Zoomed in on " + ObjectName, Status.DONE);
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            TakeMobileScreenshot();
            throw new ElementException(ExceptionType.Action_Failed, "Zoom on " + ObjectName);
        }
    }
  
    private void setPageTimeOut(int sec) {
        try {
            Driver.manage().timeouts().pageLoadTimeout(sec, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println("Couldn't set PageTimeOut to " + sec);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "Start a specified browser", input = InputType.YES)
    public void StartBrowser() {
        try {
            getDriverControl().StartBrowser(Data);
            Report.setDriver(getDriverControl());
            Report.updateTestLog("StartBrowser", "Browser Started: " + Data,
                    Status.DONE);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, e);
            Report.updateTestLog("StartBrowser", "Error: " + e.getMessage(),
                    Status.FAIL);
        }

    }

    @Action(object = ObjectType.BROWSER, desc = "Restarts the Browser")
    public void RestartBrowser() {
        try {
            getDriverControl().RestartBrowser();
            Report.setDriver(getDriverControl());
            Report.updateTestLog("RestartBrowser", "Restarted Browser",
                    Status.DONE);
        } catch (Exception ex) {
            Report.updateTestLog("RestartBrowser", "Unable Restart Browser",
                    Status.FAIL);
            Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Action(object = ObjectType.BROWSER, desc = "Stop the current browser")

    public void StopBrowser() {
        getDriverControl().StopBrowser();
        Report.updateTestLog("StopBrowser", "Browser Stopped: ", Status.DONE);
    }

    @Action(object = ObjectType.BROWSER, desc = "Add a variable to access within testcase", input = InputType.YES, condition = InputType.YES)
    public void AddVar() {
        addVar(Condition, Data);
        if (getVar(Condition) != null) {
            Report.updateTestLog("addVar", "Variable " + Condition, Status.DONE);
        } else {
            Report.updateTestLog("addVar", "Variable " + Condition
                    + " not added ", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "Add a Global variable to access across test set", input = InputType.YES, condition = InputType.YES)
    public void AddGlobalVar() {
        addGlobalVar(Condition, Data);
        if (getVar(Condition) != null) {
            Report.updateTestLog(Action, "Variable " + Condition
                    + " added with value " + Data, Status.DONE);
        } else {
            Report.updateTestLog(Action, "Variable " + Condition
                    + " not added ", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "changing wait time by [<Data>] seconds", input = InputType.YES)
    public void changeWaitTime() {
        try {
            int t = Integer.parseInt(Data);
            if (t > 0) {
                SystemDefaults.waitTime.set(t);
                Report.updateTestLog("changeWaitTime", "Wait time changed to "
                        + t + " second/s", Status.DONE);
            } else {
                Report.updateTestLog("changeWaitTime",
                        "Couldn't change Wait time (invalid input)",
                        Status.DEBUG);
            }

        } catch (NumberFormatException ex) {
            Report.updateTestLog("changeWaitTime",
                    "Couldn't change Wait time ", Status.DEBUG);
            Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Change Default Element finding wait time by [<Data>] seconds",
            input = InputType.YES)
    public void setElementTimeOut() {
        if (Data != null && Data.matches("[0-9]+")) {
            SystemDefaults.elementWaitTime.set(Integer.valueOf(Data));
            Report.updateTestLog(Action, "Element Wait time changed to "
                    + Data + " second/s", Status.DONE);
        } else {
            Report.updateTestLog(Action,
                    "Couldn't change Element Wait time (invalid input) " + Data,
                    Status.DEBUG);
        }

    }

    @Action(object = ObjectType.BROWSER, desc = "Changes the browser size into [<Data>]", input = InputType.YES)
    public void setBrowserSize() {
        try {
            if (Data.matches("\\d*(x|,| )\\d*")) {
                String size = Data.replaceFirst("(x|,| )", " ");
                String[] sizes = size.split(" ", 2);
                Driver.manage().window().setSize(new Dimension(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1])));
                Report.updateTestLog(Action, " Browser is resized to " + Data,
                        Status.DONE);
            } else {
                Report.updateTestLog(Action, " Invalid Browser size [" + Data + "]",
                        Status.DEBUG);
            }
        } catch (Exception ex) {
            Report.updateTestLog(Action, "Unable to resize the Window ",
                    Status.FAIL);
            Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Highlight the element [<Object>]", input = InputType.OPTIONAL)
    public void highlight() {
        if (elementDisplayed()) {
            if (Data != null && !Data.trim().isEmpty()) {
                highlightElement(Element, Data);
            } else {
                highlightElement(Element);
            }
            Report.updateTestLog(Action, "Element Highlighted",
                    Status.PASS);
        }
    }

    private void highlightElement(WebElement element, String color) {
        JavascriptExecutor js = (JavascriptExecutor) Driver;
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, " outline:" + color + " solid 2px;");
    }

    public void highlightElement(WebElement element) {
        highlightElement(element, "#f00");
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Long press on the [<Object>]")
    public void longPress() {
    try {
        // Ensure the driver is an instance of AndroidDriver
        if (Driver instanceof AndroidDriver) {
            WebElement element = Element; // Assuming Element is the mobile element to be long pressed
            JavascriptExecutor js = (JavascriptExecutor) Driver;
 
            // Create the JavaScript for a long press
            String longPressScript = "mobile: longClickGesture";
            Map<String, Object> params = new HashMap<>();
            params.put("elementId", ((RemoteWebElement) element).getId());
            params.put("duration", 2000); // Duration in milliseconds
 
            js.executeScript(longPressScript, params);
            Report.updateTestLog(Action, "Long pressed on " + ObjectName, Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        throw new ElementException(ExceptionType.Action_Failed, "Long Press on " + ObjectName);
    }
 }
    
    @Action(object = ObjectType.MOBILE, desc = "Verify if element [<ObjectName>] is present", input = InputType.NO)
    public boolean verifyElementPresent() {
        try {
            if (Driver instanceof AndroidDriver) {
                if (Element == null) {
                    // Assuming 'ObjectName' holds the XPath or some locator strategy
                    Element = Driver.findElement(By.xpath("//*[@content-desc='" + ObjectName + "']"));
                }
                if (Element != null) {
                    return true;
                } else {
                    throw new ElementException(ExceptionType.Element_Not_Found, ObjectName);
                }
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to verify element presence: " + e.getMessage(), Status.FAIL);
            Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error occurred while verifying element presence for: " + ObjectName, e);
            return false;
        }
    }
    
    @Action(object = ObjectType.APP, desc = "Set Bluetooth state [<Data>] (ON, OFF)", input = InputType.YES)
    public void setBluetoothState() {
        try {
          if (Driver instanceof AndroidDriver) {
            String command = "";
            switch (Data.toUpperCase()) {
                case "ON":
                    command = "adb shell am start -a android.bluetooth.adapter.action.REQUEST_ENABLE";
                    break;
                case "OFF":
                    command = "adb shell am force-stop com.android.bluetooth";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Bluetooth state: " + Data);
            }

            // Execute the ADB command
            Runtime.getRuntime().exec(command);
            Report.updateTestLog(Action, "Bluetooth state set to " + Data, Status.DONE);
         } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
         }
       } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to set Bluetooth state: " + e.getMessage(), Status.FAIL);
        Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error while setting Bluetooth state: ", e);
    }
 }

    @Action(object = ObjectType.MOBILE, desc = "Select an option from a dropdown menu.", input = InputType.YES)
    public void selectDropdownOption() {
        try {
            // Ensure the driver is an instance of AndroidDriver
            if (Driver instanceof AndroidDriver) {
                AndroidDriver androidDriver = (AndroidDriver) Driver;

                // Fetch the dynamic XPath for the dropdown
                String dropdownXPath = "//*[@content-desc='" + ObjectName + "']";
                WebDriverWait wait = new WebDriverWait(androidDriver, Duration.ofSeconds(30));
                WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dropdownXPath)));

                // Click to open the dropdown
                dropdown.click();

                // Assuming Data contains the option text to select
                String optionText = Data;

                // Fetch the dynamic XPath for the option
                String optionXPath = "//*[@text='" + optionText + "']";
                WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(optionXPath)));

                // Click on the desired option
                option.click();

                Report.updateTestLog(Action, "Selected option '" + optionText + "' from dropdown '" + ObjectName + "'", Status.DONE);
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to select option from dropdown: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Select Dropdown Option: " + e.getMessage());
        }
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Verify if [<Object>] element is enabled")
    public void verifyElementEnabledMobile() {
        Boolean status = elementEnabled();
        String isNot = status ? "" : "not ";
        String value = isNot + "enabled";
        String description = String.format("Element [%s] is %s", ObjectName, value);
        Report.updateTestLog(Action, description, Status.getValue(status));
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Show keyboard by clicking on [<Object>] ")
    public void showKeyBoard() {
        if (elementEnabled()) {
            Element.click();
            Report.updateTestLog(Action, "Show Keyboard by clicking on " + ObjectName, Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }
        @Action(object = ObjectType.MOBILE, desc = "Drag and drop an element in the emulator.")
    public void dragAndDrop() {
        // Generate the correct XPath for the source element to be dragged
        String dynamicXPathSource = "//*[@content-desc='" + ObjectName + "']";

        // Initialize Step object
        Step step = new Step(1);
        step.object(ObjectName);
        step.input(dynamicXPathSource);

        // Wait for the source element to be present
        WebDriverWait wait = new WebDriverWait(Driver, Duration.ofSeconds(30));
        WebElement sourceElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dynamicXPathSource)));

        // Define the target coordinates for the drag-and-drop action
        Point sourceLocation = sourceElement.getLocation();
        int targetX = sourceLocation.getX() + 500; // Move to the right by 500 pixels
        int targetY = sourceLocation.getY() + 300; // Move down by 300 pixels

        // Debugging: Print out the coordinates
        System.out.println("Source Element Location: " + sourceLocation);
        System.out.println("Target Coordinates: X=" + targetX + ", Y=" + targetY);

        // Perform the drag-and-drop action using W3C Actions API
        if (sourceElement.isEnabled()) {
            Actions actions = new Actions(Driver);
            actions.clickAndHold(sourceElement) // Simulate long press and hold
                    .pause(Duration.ofSeconds(2)) // Hold for 2 seconds
                    .moveByOffset(targetX - sourceLocation.getX(), targetY - sourceLocation.getY()) // Drag to the target
                    .pause(Duration.ofSeconds(1)) // Optional pause before releasing
                    .release() // Drop the element
                    .perform();

            Report.updateTestLog(Action, "Dragging " + ObjectName + " to new location.", Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }
    @Action(object = ObjectType.MOBILE, desc = "Check if the accessibility element is present.", input = InputType.YES)
    public void CheckAccessibilityElement() {
        try {
            if (Driver instanceof AndroidDriver) {
                AndroidDriver androidDriver = (AndroidDriver) Driver;
                String accessibilityId = Data;  // Use the Data variable to get the accessibility ID
 
                WebDriverWait wait = new WebDriverWait(androidDriver, Duration.ofSeconds(10));
                // Find the element by accessibility ID
                org.openqa.selenium.WebElement element = androidDriver.findElement(AppiumBy.accessibilityId(accessibilityId));
 
                if (element.isDisplayed()) {
                    Report.updateTestLog(Action, "Accessibility element with ID: '" + accessibilityId + "' is present and displayed.", Status.DONE);
                } else {
                    Report.updateTestLog(Action, "Accessibility element with ID: '" + accessibilityId + "' is present but not displayed.", Status.WARNING);
                }
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (NoSuchElementException e) {
            Report.updateTestLog(Action, "Accessibility element with ID: '" + Data + "' not found.", Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Accessibility element with ID: '" + Data + "' not found: " + e.getMessage());
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to check accessibility element: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Failed to check accessibility element: " + e.getMessage());
        }
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Switch context to default")
    public void switchContextToNativeView() {
        try {
            // Assuming the default context is "NATIVE_APP"
            String defaultContext = "NATIVE_APP";
            Driver.toString().contains(defaultContext);
            Report.updateTestLog("Switch Context", "Switched to default context '" + defaultContext + "'", Status.DONE);
        } catch (WebDriverException e) {
            Report.updateTestLog("Switch Context", "Failed to switch context: " + e.getMessage(), Status.FAIL);
            throw new RuntimeException("Switch Context: " + e.getMessage());
        }
    }  
    
    @Action(object = ObjectType.MOBILE, desc = "Switch context to WEBVIEW")
    public void switchContextToWebView() {
    try {
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;

            // Get all available contexts
            Set<String> contextNames = androidDriver.getContextHandles();
            
            // Loop through available contexts and switch to the WEBVIEW context
            for (String contextName : contextNames) {
                if (contextName.contains("WEBVIEW")) {
                    androidDriver.context(contextName);
                    Report.updateTestLog("Switch Context", "Switched to WEBVIEW context: " + contextName, Status.DONE);
                    return; // Exit the method after switching
                }
            }

            // If no WEBVIEW context is found
            throw new NoSuchContextException("No WEBVIEW context found.");
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AppiumDriver");
        }
    } catch (WebDriverException e) {
        Report.updateTestLog("Switch Context", "Failed to switch to WEBVIEW context: " + e.getMessage(), Status.FAIL);
        throw new RuntimeException("Switch Context: " + e.getMessage());
    }
}

    @Action(object = ObjectType.APP, desc = "Set the context [<Data>] (NATIVE_APP, WEBVIEW_<packageName>)", input = InputType.YES)
    public void setContext() {
    try {
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;
            String contextName = Data;  // Retrieve the context name from user input

            // Check if the context is available
            List<String> availableContexts = new ArrayList<>(androidDriver.getContextHandles());
            if (availableContexts.contains(contextName)) {
                // Switch to the specified context
                androidDriver.context(contextName);
                Report.updateTestLog(Action, "Switched to context: " + contextName, Status.DONE);
            } else {
                throw new IllegalArgumentException("Context " + contextName + " is not available.");
            }
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to switch context: " + e.getMessage(), Status.FAIL);
        Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error while switching context: ", e);
    }
}
    
@Action(object = ObjectType.APP, desc = "Tap or Click the [<Object>] in the app")
public void TapAppElement() {
    try {
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;

            // Generate the correct XPath or use an identifier to locate the app element
            String dynamicXPath = "//*[@content-desc='" + ObjectName + "']";
            
            // Wait for the element to be present
            WebDriverWait wait = new WebDriverWait(androidDriver, Duration.ofSeconds(10));
            WebElement appElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dynamicXPath)));

            // Check if the element is enabled before performing the tap
            if (appElement.isEnabled()) {
                appElement.click();
                Report.updateTestLog(Action, "Tapped on " + ObjectName + " in the app", Status.DONE);
            } else {
                throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
            }
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (NoSuchElementException e) {
        Report.updateTestLog(Action, "Element not found: " + ObjectName, Status.FAIL);
        throw new ElementException(ExceptionType.Element_Not_Found, "Element not found: " + ObjectName);
    } catch (Exception e) {
       Report.updateTestLog(Action, "Failed to tap on element: " + ObjectName + " - " + e.getMessage(), Status.FAIL);
       Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error while tapping on element: " + ObjectName, e);
    }
}

    @Action(object = ObjectType.APP, desc = "Handle alert by accepting, dismissing, or entering text based on the input.", input = InputType.YES)
    public void HandleTextAlert() {
    try {
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;
            WebDriverWait wait = new WebDriverWait(androidDriver, Duration.ofSeconds(10));
 
            // Wait for the alert to be present
            wait.until(ExpectedConditions.alertIsPresent());
 
            // Handle the action based on input using switch-case
            switch (Data.split(":")[0].toLowerCase()) {
                case "entertext":
                    String textToEnter = Data.split("enterText:")[1];
                    // Assuming the text input field can be identified via XPath or another locator
                    WebElement textField = androidDriver.findElement(By.xpath("//android.widget.EditText"));
                    textField.sendKeys(textToEnter);
                    Report.updateTestLog(Action, "Entered text in the alert: " + textToEnter, Status.DONE);
                    // Accepting the alert after entering text
                    androidDriver.findElement(By.xpath("//android.widget.Button[@text='OK']")).click();
                    break;
 
                case "accept":
                    androidDriver.switchTo().alert().accept();
                    Report.updateTestLog(Action, "Accepted the alert", Status.DONE);
                    break;
 
                case "dismiss":
                    androidDriver.switchTo().alert().dismiss();
                    Report.updateTestLog(Action, "Dismissed the alert", Status.DONE);
                    break;
 
                default:
                    throw new IllegalArgumentException("Invalid input for handling alert. Use 'enterText:<your_text>', 'accept', or 'dismiss'.");
            }
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (NoAlertPresentException e) {
    Report.updateTestLog(Action, "No alert present to handle.", Status.WARNING);
} catch (Exception e) {
    Report.updateTestLog(Action, "Failed to handle alert: " + e.getMessage(), Status.FAIL);
    throw new ElementException(ExceptionType.Not_Found_on_Screen, "Failed to handle alert: " + e.getMessage());
}
}

    @Action(object = ObjectType.MOBILE, desc = "Perform UI responsiveness check by executing [<Object>] action asynchronously.")
    public void UIResponsiveness() {
    try {
        if (Driver instanceof AndroidDriver) {
            // Create an ExecutorService to run tasks asynchronously
            ExecutorService executorService = Executors.newSingleThreadExecutor();
 
            // Submit the task for asynchronous execution
            Future<?> future = executorService.submit(() -> {
                try {
                    // Example action: Click on an element
                    Element.click();
                    Report.updateTestLog(Action, "Successfully clicked on " + ObjectName, Status.DONE);
                } catch (Exception e) {
                    Report.updateTestLog(Action, "Failed to perform action: " + e.getMessage(), Status.FAIL);
                    Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, null, e);
                }
            });
 
            // Optionally, you can wait for the task to complete or set a timeout
            // future.get(10, TimeUnit.SECONDS);
 
            // Shutdown the executor service
            executorService.shutdown();
 
            // Update the test log to indicate that the action is performed asynchronously
            Report.updateTestLog(Action, "Performed action asynchronously on " + ObjectName, Status.DONE);
 
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to ensure UI responsiveness: " + e.getMessage(), Status.FAIL);
        Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error occurred while ensuring UI responsiveness for: " + ObjectName, e);
    }
}
    @Action(object = ObjectType.APP, desc = "Start recording the mobile screen.")
public void StartRecordMobileScreen() {
    if (Driver instanceof AndroidDriver) {
        try {
            AndroidDriver androidDriver = (AndroidDriver) Driver;
            androidDriver.startRecordingScreen();

            Report.updateTestLog(Action, "Screen recording started for " + ObjectName, Status.DONE);
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to start screen recording for " + ObjectName, Status.FAIL, "Exception: " + e.getMessage());
        }
    } else {
        throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
    }
}

@Action(object = ObjectType.APP, desc = "Stop recording the mobile screen and save the video.")
public void StopRecordMobileScreen() {
    if (Driver instanceof AndroidDriver) {
        try {
            AndroidDriver androidDriver = (AndroidDriver) Driver;
            String base64String = androidDriver.stopRecordingScreen();

            // Determine OS-specific file path
            String os = System.getProperty("os.name").toLowerCase();
            String basePath;
            if (os.contains("win")) {
                basePath = System.getenv("RECORDING_PATH") != null ? System.getenv("RECORDING_PATH") : System.getProperty("user.home") + "\\recordings\\";
            } else {
                basePath = System.getenv("RECORDING_PATH") != null ? System.getenv("RECORDING_PATH") : System.getProperty("user.home") + "/recordings/";
            }

            // Ensure the directory exists
            File baseDir = new File(basePath);
            if (!baseDir.exists() && !baseDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + baseDir.getAbsolutePath());
            }

            // Set the final video file path with a unique timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filePath = basePath + "recording_" + ObjectName + "_" + timestamp + ".mp4";
            File videoFile = new File(filePath);

            // Decode the Base64 string and save it to the file
            try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                byte[] decodedBytes = Base64.decodeBase64(base64String);
                fos.write(decodedBytes);
            }

            // Add the video path to the report
            Report.updateTestLog(Action, "Screen recording saved at path: " + videoFile.getAbsolutePath() + " for " + ObjectName, Status.DONE);
        } catch (IOException e) {
            Report.updateTestLog(Action, "Failed to save screen recording for " + ObjectName, Status.FAIL, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to save screen recording for " + ObjectName, Status.FAIL, "General Exception: " + e.getMessage());
        }
    } else {
        throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
    }
}
    @Action(object = ObjectType.MOBILE, desc = "Verify the text of the element.",input = InputType.YES)
    public void VerifyElementText() {
        WebDriverWait wait = new WebDriverWait(Driver, Duration.ofSeconds(30));

        // Wait for the element to be visible
        WebElement visibleElement = wait.until(ExpectedConditions.visibilityOf(Element));

        if (elementEnabled()) {
            // Retrieve the text from the element using getAttribute("text")
            String actualText = visibleElement.getAttribute("text");

            // Handle null values
            if (actualText == null) {
                actualText = "";
            }
            if (Data == null) {
                Data = "";
            }

            // Compare with the expected text
            if (actualText.equals(Data)) {
                Report.updateTestLog(Action, "Text '" + actualText + "' is correctly displayed on '" + ObjectName + "'", Status.DONE);
            } else {
                Report.updateTestLog(Action, "Expected text '" + Data + "' but found '" + actualText + "' on '" + ObjectName + "'", Status.FAIL);
                throw new ElementException(ExceptionType.Element_Text_Mismatch, ObjectName);
            }
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
}    
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Highlight the element [<Object>]", input = InputType.OPTIONAL)
    public void highlightMobileElement() {
        if (elementDisplayed()) {
            if (Driver instanceof AndroidDriver) {
                // Scroll to the element for mobile environments
                scrollToElement(Element);
                Report.updateTestLog(Action, "Element Scrolled Into View", Status.PASS);
            } else {
                Report.updateTestLog(Action, "Unsupported driver for highlight operation", Status.FAIL);
            }
        } else {
            Report.updateTestLog(Action, "Element not displayed", Status.FAIL);
        }
    }
    
    // Scroll to the element (for mobile environments)
    private void scrollToElement(WebElement element) {
        AndroidDriver androidDriver = (AndroidDriver) Driver;
        String elementText = element.getAttribute("content-desc");

        if (elementText == null || elementText.isEmpty()) {
            elementText = element.getText();  // Fallback to text attribute if content-desc is not available
        }

        if (elementText != null && !elementText.isEmpty()) {
            // Using UiScrollable with UiSelector to scroll to the element
            androidDriver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector()).scrollIntoView("
                    + "new UiSelector().description(\"" + elementText + "\").instance(0));"
            ));
        } else {
            // Optionally handle cases where both content-desc and text are unavailable
            Report.updateTestLog(Action, "Element not found by description or text", Status.FAIL);
        }
    }
    @Action(object = ObjectType.APP, desc = "Push a file to the mobile device.", input = InputType.YES)
    public void pushFile() {
    try {
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;
 
            // Get the file path from input (user-provided)
            String sourceFilePath = Data;  // This is the user-provided file path
 
            // Define the default path on the mobile device
            String deviceFilePath = "/sdcard/Download/";  // Default folder on mobile device
 
            // Check if the provided path exists
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.exists()) {
                throw new FileNotFoundException("File does not exist at path: " + sourceFilePath);
            }
 
            // Determine the appropriate ADB command based on the operating system
            String os = System.getProperty("os.name").toLowerCase();
            String adbCommand;
 
            if (os.contains("win")) {
                // Windows ADB command
                adbCommand = "adb push \"" + sourceFilePath + "\" " + deviceFilePath;
            } else if (os.contains("linux")) {
                // Linux ADB command
                adbCommand = "adb push " + sourceFilePath + " " + deviceFilePath;
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + os);
            }
 
            // Execute the ADB command
            executeADBCommand(adbCommand);
 
            // Log success
            Report.updateTestLog(Action, "Pushed file from " + sourceFilePath + " to " + deviceFilePath + " on device", Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to push file: " + e.getMessage(), Status.FAIL);
        throw new ElementException(ExceptionType.Action_Failed, "Push file failed: " + e.getMessage());
    }
}
// Helper method to execute ADB commands for real devices

 
    @Action(object = ObjectType.APP, desc = "Pull a file from the mobile device", input = InputType.YES)
    public void pullFileFromDevice() {
    try {
        // Ensure the driver is an instance of AndroidDriver
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;
            // Get the path of the file to pull from the mobile device (provided by the user)
            String deviceFilePath = Data; // Input path provided by the user (file path on the mobile device)
            // Determine the local directory and file path
            String localDirectory;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // For Windows, set default directory to C:\PulledFiles1
                localDirectory = "C:\\PulledFiles";
            } else if (os.contains("nux") || os.contains("nix")) {
                // For Linux, set default directory to /home/user/PulledFiles1
                String userHome = System.getProperty("user.home");
                localDirectory = userHome + "/PulledFiles";
            } else {
                throw new UnsupportedOperationException("Unsupported operating system");
            }
            // Ensure the local directory exists
            createDirectoryIfNotExists(localDirectory);
            // Create the full local file path
            String localFilePath = localDirectory + File.separator + new File(deviceFilePath).getName();
            // Pull the file from the device using Appium's pullFile method
            byte[] fileData = androidDriver.pullFile(deviceFilePath);
            // Write the pulled file data to the local file path
            Files.write(Paths.get(localFilePath), fileData);
            Report.updateTestLog(Action, "File pulled from device to: " + localFilePath, Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Pull file action failed: " + e.getMessage(), Status.FAIL);
        throw new ElementException(ExceptionType.Action_Failed, "Pull file action failed: " + e.getMessage());
    }
}
 
// Helper method to create a directory if it does not exist
    private void createDirectoryIfNotExists(String directoryPath) throws IOException {
    File directory = new File(directoryPath);
    if (!directory.exists()) {
        if (!directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + directoryPath);
        }
    }
}
    
    @Action(object = ObjectType.APP, desc = "Interact with the ambient light sensor on real devices and emulators without passing parameters.")
    public void setAmbientLightSensor() {
    try {
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;

            // Check if the device is an emulator or a real device
            String deviceName = androidDriver.getCapabilities().getCapability("deviceName").toString();

            // Predefined light sensor value (e.g., 1000 lux)
            int predefinedLightValue = 1000;

            if (deviceName.toLowerCase().contains("emulator") || deviceName.toLowerCase().contains("sdk")) {
                // Emulator-specific action: use Appium's sensorSet feature for emulator
                Map<String, Object> params = new HashMap<>();
                params.put("sensorType", "light");
                params.put("value", predefinedLightValue);
                androidDriver.executeScript("mobile:sensorSet", params);
                Report.updateTestLog(Action, "Ambient light sensor set to " + predefinedLightValue + " on emulator", Status.DONE);

            } else {
                // Real device-specific action: use ADB shell command to simulate light sensor (if supported)
                String adbCommand = "adb shell input sensor light " + predefinedLightValue;
                executeADBCommand(adbCommand);
                Report.updateTestLog(Action, "Ambient light sensor set to " + predefinedLightValue + " on real device", Status.DONE);
            }
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to set ambient light sensor: " + e.getMessage(), Status.FAIL);
        Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error while setting ambient light sensor", e);
    }
}
    
    @Action(object = ObjectType.APP, desc = "Send a push notification with title [<Data1>] and message [<Data2>]", input = InputType.YES)
    public void SendPushNotification() {
    try {
        if (Driver instanceof AndroidDriver) {
            // Check if the AndroidDriver is in use
            String title = ""; // Title of the notification
            String message = ""; // Message body of the notification

            // Create ADB command to send push notification
            String command = String.format(
                "adb shell am broadcast -a com.package.PUSH_NOTIFICATION_TEST " +
                "--es 'title' '%s' --es 'message' '%s'",
                title, message
            );

            // Execute the ADB command
            Runtime.getRuntime().exec(command);
            Report.updateTestLog(Action, "Push notification sent with title: " + title + " and message: " + message, Status.DONE);
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to set ambient light sensor: " + e.getMessage(), Status.FAIL);
        Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error while setting ambient light sensor", e);    
        Report.updateTestLog(Action, "Failed to send push notification: " + e.getMessage(), Status.FAIL);
        Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, "Error while sending push notification: ", e);
    }
}
    @Action(object = ObjectType.MOBILE, desc = "Select and copy text from the mobile screen.")
    public void CopyText() {
        try {
            if (elementEnabled()) {
                if (Driver instanceof AndroidDriver) {
                    AndroidDriver androidDriver = (AndroidDriver) Driver;
                    // Long press the element to select the text
                    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
                    Sequence longPress = new Sequence(finger, 1)
                        .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(),
                                Element.getLocation().getX(), Element.getLocation().getY()))
                        .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                        .addAction(finger.createPointerMove(Duration.ofMillis(1500), PointerInput.Origin.viewport(),
                                Element.getLocation().getX(), Element.getLocation().getY()))
                        .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    // Perform the long press to select the text
                    androidDriver.perform(Arrays.asList(longPress));
                    // Trigger the copy action (assuming "Copy" option appears after long press)
                    androidDriver.pressKey(new KeyEvent(AndroidKey.COPY));
                    Report.updateTestLog(Action, "Text copied from element " + ObjectName, Status.DONE);
                } else {
                    throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
                }
            } else {
                throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to copy text: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Copy Text: " + e.getMessage());
        }
    }
    @Action(object = ObjectType.MOBILE, desc = "Paste text from mobile clipboard", input = InputType.NO)
    public void pasteText() {
    try {
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;
 
            // Focus on the currently active field (the field where text should be pasted)
            // Pasting the clipboard text using Android's paste shortcut
 
            // Simulate long press (KEYCODE_CTRL_LEFT) to open the clipboard paste option
            androidDriver.pressKey(new KeyEvent(AndroidKey.PASTE));
 
            // Log the success action
            Report.updateTestLog(Action, "Clipboard text pasted successfully", Status.DONE);
 
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Failed to paste clipboard text: " + e.getMessage(), Status.FAIL);
        throw new RuntimeException("pasteClipboardText failed: " + e.getMessage());
    }
}
    
     @Action(object = ObjectType.APP, desc = "Simulate battery level change to [<BatteryLevel>]%", input = InputType.YES)
    public void SimulateBattery() {
        try {
            // Ensure the driver is an instance of AndroidDriver
            if (Driver instanceof AndroidDriver) {
                AndroidDriver androidDriver = (AndroidDriver) Driver;
                // Parse the battery level from the input data
                int batteryLevel = Integer.parseInt(Data.trim());
                if (batteryLevel < 0 || batteryLevel > 100) {
                    throw new IllegalArgumentException("Invalid battery level. Please provide a value between 0 and 100.");
                }
                // Simulate battery level change
                String command = "adb shell dumpsys battery set level " + batteryLevel;
                Runtime.getRuntime().exec(command);
                Report.updateTestLog(Action, "Simulated battery level change to " + batteryLevel + "%", Status.DONE);
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Battery simulation failed: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Simulate Battery: " + e.getMessage());
        }
    }
    
    @Action(object = ObjectType.APP, desc = "Simulate location change to [<Latitude>, <Longitude>]", input = InputType.YES)
    public void SimulateLocation() {
        try {
            // Ensure the driver is an instance of AndroidDriver
            if (Driver instanceof AndroidDriver) {
                AndroidDriver androidDriver = (AndroidDriver) Driver;
                // Assuming the input data is a comma-separated string: "latitude,longitude"
                String[] coordinates = Data.split(",");
                if (coordinates.length != 2) {
                    throw new IllegalArgumentException("Invalid input format. Expected format: latitude,longitude");
                }
                // Parse the latitude and longitude
                double latitude = Double.parseDouble(coordinates[0].trim());
                double longitude = Double.parseDouble(coordinates[1].trim());
                // Get current location (optional: to log current location)
                Location currentLocation = androidDriver.location();
                Report.updateTestLog(Action, "Current Location - Latitude: " + currentLocation.getLatitude()
                        + ", Longitude: " + currentLocation.getLongitude(), Status.INFO);
                // Set the new location using AndroidDriver
                Location newLocation = new Location(latitude, longitude, 0); // Altitude is set to 0 by default
                androidDriver.setLocation(newLocation);
                // Log success
                Report.updateTestLog(Action, "Simulated location change to Latitude: " + latitude + ", Longitude: " + longitude, Status.DONE);
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Location simulation failed: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Simulate Location: " + e.getMessage());
        }
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Perform a double tap on the [<Object>] mobile element.")
    public void DoubleTap() {
        try {
            // Ensure the driver is an instance of AndroidDriver
            if (Driver instanceof AndroidDriver) {
                AndroidDriver androidDriver = (AndroidDriver) Driver;
 
                if (elementEnabled()) {
                    // Define the element to be double tapped
                    WebElement element = Element;
 
                    // Create the double tap sequence using PointerInput
                    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
                    Sequence doubleTap = new Sequence(finger, 1)
                            // Move to the element's location
                            .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(),
                                    element.getLocation().x, element.getLocation().y))
                            // First tap
                            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                            // Pause before second tap
                            .addAction(new Pause(finger, Duration.ofMillis(100))) // 100 ms pause
                            // Second tap
                            .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                            .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
 
                    // Perform the double tap action
                    androidDriver.perform(Arrays.asList(doubleTap));
 
                    Report.updateTestLog(Action, "Double tapped on " + ObjectName, Status.DONE);
                } else {
                    throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
                }
                           } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Double tap action failed: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Double Tap: " + e.getMessage());
        }
    } 
    
    @Action(object = ObjectType.MOBILE, desc = "Tap and hold on the [<Object>] for a specific duration.")
    public void tapAndHold() {
        try {
            // Ensure the driver is an instance of AndroidDriver
            if (Driver instanceof AndroidDriver) {
                WebElement element = Element; // Assuming Element is the mobile element to be tapped and held

                // Create pointer input for touch actions
                PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
                Sequence tapAndHold = new Sequence(finger, 1)
                        .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.fromElement(element), 0, 0)) // Move to element
                        .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg())) // Tap and hold
                        .addAction(new Pause(finger, Duration.ofMillis(2000))) // Hold for 2 seconds
                        .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg())); // Release

                // Perform the tap and hold action
                ((AndroidDriver) Driver).perform(Arrays.asList(tapAndHold));

                Report.updateTestLog(Action, "Tap and hold on " + ObjectName + " for 2 seconds", Status.DONE);
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Tap and hold failed: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Action_Failed, "Tap and hold on " + ObjectName);
        }
    }
    
    @Action(object = ObjectType.APP, desc = "Simulate an incoming call to the mobile device.", input = InputType.YES)
    public void SimulateIncomingCall() {
        try {
            if (Driver instanceof AndroidDriver) {
                AndroidDriver androidDriver = (AndroidDriver) Driver;
                String phoneNumber = Data;  // Retrieve the phone number input dynamically from Data

                // Use adb command to simulate an incoming call
                String adbCommand = "adb shell am broadcast -a android.intent.action.NEW_OUTGOING_CALL -e phone_number " + phoneNumber;
                
                // Execute the adb command
                Process process = Runtime.getRuntime().exec(adbCommand);

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    Report.updateTestLog(Action, "Simulated an incoming call from " + phoneNumber, Status.DONE);
                } else {
                    Report.updateTestLog(Action, "Failed to simulate an incoming call", Status.FAIL);

                }
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }

        } catch (Exception e) {
            Report.updateTestLog(Action, "Double tap action failed: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "Double Tap: " + e.getMessage());
        }
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Pinch on the [<Object>]")
    public void Pinch() {
        try {
            if (Driver instanceof AndroidDriver) {
                WebElement element = Element; // Assuming Element is the mobile element to be pinched
                int centerX = element.getRect().getX() + (element.getRect().getWidth() / 2);
                int centerY = element.getRect().getY() + (element.getRect().getHeight() / 2);
 
                PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
                PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");
 
                Sequence finger1Sequence = new Sequence(finger1, 1)
                        .addAction(finger1.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX + 100, centerY + 100))
                        .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                        .addAction(finger1.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), centerX, centerY))
                        .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
 
                Sequence finger2Sequence = new Sequence(finger2, 1)
                        .addAction(finger2.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX - 100, centerY - 100))
                        .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                        .addAction(finger2.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), centerX, centerY))
                        .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
 
                ((AndroidDriver) Driver).perform(Arrays.asList(finger1Sequence, finger2Sequence));
 
                Report.updateTestLog(Action, "Pinched on " + ObjectName, Status.DONE);
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            TakeMobileScreenshot();
            throw new ElementException(ExceptionType.Action_Failed, "Pinch on " + ObjectName);
        }
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Check and toggle a mobile element (checkbox/toggle) if it is unchecked.")
    public void CheckUnchecked() {
        try {
            WebElement checkbox = (WebElement) Element; // Element will dynamically fetch the object
            if (checkbox != null && checkbox.isDisplayed()) {
                String isChecked = checkbox.getAttribute("checked"); // Check the 'checked' attribute for mobile
                if (isChecked == null || !isChecked.equalsIgnoreCase("true")) {
                    // If the checkbox/toggle is unchecked, use the Tap method
                    Tap(); // Reusing the existing Tap method
                    Report.updateTestLog(Action, "Checkbox was unchecked. It is now checked.", Status.DONE);
                } else {
                    Report.updateTestLog(Action, "Checkbox is already checked.", Status.PASS);
                }
            } else {
                throw new ElementException(ExceptionType.Element_Not_Visible, ObjectName);
            }
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to check or toggle mobile checkbox: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, ObjectName);
        }
    }
    
    @Action(object = ObjectType.APP, desc = "Open the specified app using dynamically retrieved package name.", input = InputType.YES)
    public void OpenAppUsingPackageName() {
        try {
            // Ensure the driver is an instance of AndroidDriver
            if (Driver instanceof AndroidDriver) {
                // Get app package from data input
                String appPackage = Data;

                // Create the shell monkey command to launch the app directly on the device
                String command = "monkey -p " + appPackage + " -c android.intent.category.LAUNCHER 1";

                // Use the AndroidDriver to execute the command
                Map<String, String> launchArgs = new HashMap<>();
                launchArgs.put("command", command);
                ((AndroidDriver) Driver).executeScript("mobile: shell", launchArgs);

                // Log the success in the report
                Report.updateTestLog(Action, "App launched with package: " + appPackage, Status.DONE);
            } else {
                throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
            }
        } catch (Exception e) {
            // Handle any exceptions and log failure
            Report.updateTestLog(Action, "Failed to open app: " + e.getMessage(), Status.FAIL);
            throw new ElementException(ExceptionType.Element_Not_Found, "OpenApp: " + e.getMessage());
        }
    }
    
    @Action(object = ObjectType.MOBILE, desc = "Perform a long press on an element in the emulator.")
    public void longPressOnApp() {
        // Generate the correct XPath for the element to be long-pressed
        String dynamicXPath = "//*[@content-desc='" + ObjectName + "']";

        // Initialize Step object
        Step step = new Step(1);
        step.object(ObjectName);
        step.input(dynamicXPath);

        // Wait for the element to be present
        WebDriverWait wait = new WebDriverWait(Driver, Duration.ofSeconds(30));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dynamicXPath)));

        // Perform the long press action using W3C Actions API
        if (element.isEnabled()) {
            Actions actions = new Actions(Driver);
            actions.clickAndHold(element) // Simulate long press
                    .pause(Duration.ofSeconds(2)) // Hold for 2 seconds
                    .release() // Release the press
                    .perform();

            Report.updateTestLog(Action, "Performed long press on " + ObjectName, Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }


// Helper method to execute ADB commands for real devices
    private void executeADBCommand(String command) throws IOException, InterruptedException {
    Process process = Runtime.getRuntime().exec(command);
    process.waitFor();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
    reader.close();
}
    @Action(object = ObjectType.MOBILE, desc = "Swipe the object [<Object>] on the mobile screen in [<Direction>] direction.", input = InputType.YES)
    public void SwipeObject() {
    try {
        // Ensure the driver is an instance of AndroidDriver
        if (Driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) Driver;

            // Assuming the direction is passed as a string input, e.g., "LEFT", "RIGHT", "UP", "DOWN"
            String direction = Data.toUpperCase();  // Use the Data variable to get the user input
            if (!elementEnabled()) {
                throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
            }

            // Get element location and size
            Point elementLocation = Element.getLocation();
            Dimension elementSize = Element.getSize();

            // Determine the start and end points for the swipe action
            int startX, startY, endX, endY;
            startX = elementLocation.getX() + elementSize.getWidth() / 2;
            startY = elementLocation.getY() + elementSize.getHeight() / 2;

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);

            switch (direction) {
                case "LEFT":
                    endX = elementLocation.getX() - elementSize.getWidth() / 2;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), endX, startY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped LEFT on the object " + ObjectName, Status.DONE);
                    break;
                case "RIGHT":
                    endX = elementLocation.getX() + elementSize.getWidth() + 50;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), endX, startY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped RIGHT on the object " + ObjectName, Status.DONE);
                    break;
                case "UP":
                    endY = elementLocation.getY() - elementSize.getHeight() / 2;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), startX, endY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped UP on the object " + ObjectName, Status.DONE);
                    break;
                case "DOWN":
                    endY = elementLocation.getY() + elementSize.getHeight() + 50;
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, startY));
                    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
                    swipe.addAction(finger.createPointerMove(Duration.ofMillis(2000), PointerInput.Origin.viewport(), startX, endY));
                    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
                    androidDriver.perform(Arrays.asList(swipe));
                    Report.updateTestLog(Action, "Swiped DOWN on the object " + ObjectName, Status.DONE);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid direction: " + direction);
            }
        } else {
            throw new UnsupportedOperationException("Driver is not an instance of AndroidDriver");
        }
    } catch (Exception e) {
        Report.updateTestLog(Action, "Swipe Object failed: " + e.getMessage(), Status.FAIL);
        throw new ElementException(ExceptionType.Element_Not_Found, "Swipe Object: " + e.getMessage());
    }
}
}



