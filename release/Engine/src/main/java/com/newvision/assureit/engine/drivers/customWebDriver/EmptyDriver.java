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
package com.newvision.assureit.engine.drivers.customWebDriver;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * 
 */
public class EmptyDriver implements WebDriver, TakesScreenshot {

    @Override
    public void get(String string) {
        //No Need
    }

    @Override
    public String getCurrentUrl() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return new ArrayList<>();
    }

    @Override
    public WebElement findElement(By by) {
        return null;
    }

    @Override
    public String getPageSource() {
        return null;
    }

    @Override
    public void close() {
        //No Need
    }

    @Override
    public void quit() {
        //No Need
    }

    @Override
    public Set<String> getWindowHandles() {
        return new HashSet<>();
    }

    @Override
    public String getWindowHandle() {
        return null;
    }

    @Override
    public TargetLocator switchTo() {
        return null;
    }

    @Override
    public Navigation navigate() {
        return null;
    }

    @Override
    public Options manage() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle rectangle = new Rectangle(0, 0, screenSize.width, screenSize.height);
        try {
            File ss = new File("image");
            ImageIO.write(new Robot().createScreenCapture(rectangle), "png", ss);
            return ((X) ss);
        } catch (AWTException | IOException ex) {
            Logger.getLogger(EmptyDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
        public static void chormeExtension() {
           String currentPath = System.getProperty("user.dir");
//         if(SystemUtils.IS_OS_WINDOWS){
//             System.setProperty("webdriver.chrome.driver", "lib/Drivers/chromedriver.exe");    
//         }else if(SystemUtils.IS_OS_LINUX){
//        File file = new File("lib/Drivers/chromedriver");
//        file.setExecutable(true);
//          System.setProperty("webdriver.chrome.driver", "lib/Drivers/chromedriver");    
//        } else{
//         System.err.println("OS Not Supported");
//        }
//       
        System.out.println("Current Directory: "+currentPath);
       String extensionPath = currentPath+File.separator+"Engine"+File.separator+"assureit_ChromeExtention";
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--load-extension=" + extensionPath);


        options.addArguments("--enable-extensions");
        options.addArguments("--disable-infobars");
        // options.addArguments("--disable-extensions-except=hganjjjeeplpdlbfjibjbooojcfhapnc");

        // options.addArguments("https://localhost:8887");
        // Launch Chrome with the extension loaded
        WebDriver driver = new ChromeDriver(options);
        // driver.switchTo().newWindow(TAB);
        driver.get("https://localhost:8887");
        driver.manage().window().maximize();
       
    }

}
