/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.newvision.assureit.engine.drivers;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author tejasvi.mali
 */
public class DeviceUtils {
   public static String getConnectedDevice() {
            String deviceName = null;
            try {
                Process process = Runtime.getRuntime().exec("adb devices");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith("device")) {
                        deviceName = line.split("\t")[0];
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return deviceName;
        }

        public static String getDeviceUdid(String deviceName) {
            String udid = null;
            try {
                Process process = Runtime.getRuntime().exec("adb -s " + deviceName + " get-serialno");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                udid = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return udid;
        } 
        
    // New method to get the platform version
    public static String getPlatformVersion(String deviceName) {
        String platformVersion = null;
        try {
            Process process = Runtime.getRuntime().exec("adb -s " + deviceName + " shell getprop ro.build.version.release");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            platformVersion = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return platformVersion;
    }
}
