// Source code is decompiled from a .class file using FernFlower decompiler.
package com.newvision.assureit.engine.CustomMethods;

import com.newvision.assureit.engine.commands.CommonMethods;
import com.newvision.assureit.engine.commands.General;
import com.newvision.assureit.engine.commands.JSCommands;
import com.newvision.assureit.engine.core.CommandControl;
import com.newvision.assureit.engine.core.Control;
import com.newvision.assureit.engine.execution.exception.element.ElementException;
import com.newvision.assureit.engine.execution.exception.element.ElementException.ExceptionType;
import com.newvision.assureit.engine.support.Status;
import com.newvision.assureit.engine.support.methodInf.Action;
import com.newvision.assureit.engine.support.methodInf.InputType;
import com.newvision.assureit.engine.support.methodInf.ObjectType;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Custom extends General {
   WebDriverWait wait;

   public Custom(CommandControl cc) {
      super(cc);
   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "Print Input and Condition",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void PrintInputCondition() {
      System.out.println("Input is " + this.Input);
      System.out.println("Input is " + this.Condition);
      System.out.println("Data is " + this.Data);
      this.Report.updateTestLog(this.Action, "[" + this.Input + "],[" + this.Data + "],[" + this.Condition + "]", Status.DONE);
   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "Returns Absolute Path %absPath%",
      input = InputType.NO,
      condition = InputType.NO
   )
   public void returnAbsolutePath() {
      String projectLocation = Control.getCurrentProject().getLocation();
      String absolutePath = projectLocation + "/Uploads/";
      this.addVar("%absPath%", absolutePath);
      this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "store  value [<Data>] in Variable [<Condition>]",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void updateUserDefine() {
      if (this.Condition.startsWith("%") && this.Condition.endsWith("%")) {
         this.addGlobalVar(this.Condition, this.Data);
         this.Report.updateTestLog(this.Action, "Value" + this.Data + "' is stored in Variable '" + this.Condition + "'", Status.DONE);
      } else {
         this.Report.updateTestLog(this.Action, "Variable format is not correct", Status.DEBUG);
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "fn before storeDataFromPreviousTestCase AddVar input<@(TCName)>Condition<%testCase%>,In fn input<DestinationSheet:DestnColumn> condition<fromSheetName:FromsheetColoum>",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void storeDataFromPreviousTestCase() {
      try {
         String sheetName = "";
         String columName = "";
         String inputData = this.Condition;
         String inputSheetName = inputData.split(":", 2)[0];
         String inputcolumnName = inputData.split(":", 2)[1];
         String inputtestcase = this.getVar("%testcase%");
         String scenario = this.userData.getScenario();
         String reportDescription = "";
         System.out.println("Input Test Case is:" + inputtestcase);
         String value = this.userData.getData(inputSheetName, inputcolumnName, scenario, inputtestcase, this.userData.getIteration(), this.userData.getSubIteration());
         if (this.Input.matches("%.*%")) {
            this.addVar(this.Input, value);
            reportDescription = this.Input.replaceAll("%", "");
         } else {
            String strObj = this.Input;
            sheetName = strObj.split(":", 2)[0];
            columName = strObj.split(":", 2)[1];
            reportDescription = columName;
            this.userData.putData(sheetName, columName, value, scenario, this.userData.getTestCase(), this.userData.getIteration(), this.userData.getSubIteration());
         }

         this.Report.updateTestLog(reportDescription, "Value : [" + value + "] is sucessfully stored", Status.DONE);
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "update the NPI in the sheet input<NPI to be update>condition<fileName.xlxs>",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void updateNPIExcel() {
      String projectLocation = Control.getCurrentProject().getLocation();
      String absolutePath = projectLocation + "/Uploads/" + this.Condition;

      try {
         FileInputStream fis = new FileInputStream(absolutePath);

         try {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            sheet = wb.getSheetAt(0);
            Cell cell2Update = sheet.getRow(1).getCell(0);
            cell2Update.setCellValue(this.Data);
            FileOutputStream outputStream = new FileOutputStream(absolutePath);
            wb.write(outputStream);
            wb.close();
            outputStream.close();
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         } catch (Exception var8) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, var8.getMessage(), var8);
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         }
      } catch (Exception var9) {
         Logger.getLogger(this.getClass().getName()).log(Level.OFF, var9.getMessage(), var9);
         this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "update the D1 in the sheet input<NPI to be update>condition<fileName.xlxs>",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void updateDate1Excel() {
      String projectLocation = Control.getCurrentProject().getLocation();
      String absolutePath = projectLocation + "/Uploads/" + this.Condition;

      try {
         FileInputStream fis = new FileInputStream(absolutePath);

         try {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            sheet = wb.getSheetAt(0);
            Cell cellD1Update = sheet.getRow(0).getCell(3);
            cellD1Update.setCellValue(this.Data);
            FileOutputStream outputStream = new FileOutputStream(absolutePath);
            wb.write(outputStream);
            wb.close();
            outputStream.close();
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         } catch (Exception var8) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, var8.getMessage(), var8);
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         }
      } catch (Exception var9) {
         Logger.getLogger(this.getClass().getName()).log(Level.OFF, var9.getMessage(), var9);
         this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "update the D2 in the sheet input<NPI to be update>condition<fileName.xlxs>",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void updateDate2Excel() {
      String projectLocation = Control.getCurrentProject().getLocation();
      String absolutePath = projectLocation + "/Uploads/" + this.Condition;

      try {
         FileInputStream fis = new FileInputStream(absolutePath);

         try {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            sheet = wb.getSheetAt(0);
            Cell cellD2Update = sheet.getRow(0).getCell(4);
            cellD2Update.setCellValue(this.Data);
            FileOutputStream outputStream = new FileOutputStream(absolutePath);
            wb.write(outputStream);
            wb.close();
            outputStream.close();
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         } catch (Exception var8) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, var8.getMessage(), var8);
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         }
      } catch (Exception var9) {
         Logger.getLogger(this.getClass().getName()).log(Level.OFF, var9.getMessage(), var9);
         this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "update the D3 in the sheet input<NPI to be update>condition<fileName.xlxs>",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void updateDate3Excel() {
      String projectLocation = Control.getCurrentProject().getLocation();
      String absolutePath = projectLocation + "/Uploads/" + this.Condition;

      try {
         FileInputStream fis = new FileInputStream(absolutePath);

         try {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            sheet = wb.getSheetAt(0);
            Cell cellD3Update = sheet.getRow(0).getCell(5);
            cellD3Update.setCellValue(this.Data);
            FileOutputStream outputStream = new FileOutputStream(absolutePath);
            wb.write(outputStream);
            wb.close();
            outputStream.close();
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         } catch (Exception var8) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, var8.getMessage(), var8);
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         }
      } catch (Exception var9) {
         Logger.getLogger(this.getClass().getName()).log(Level.OFF, var9.getMessage(), var9);
         this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
      }

   }

   @Action(
      object = ObjectType.SELENIUM,
      desc = "Enter the value [<Data>] in the Field [<Object>]",
      input = InputType.YES
   )
   public void SetAndTab() {
      if (this.elementEnabled()) {
         this.Element.clear();
         this.Element.sendKeys(new CharSequence[]{this.Data});
         this.Element.sendKeys(new CharSequence[]{Keys.TAB});
         this.Report.updateTestLog(this.Action, "Entered Text '" + this.Data + "' on '" + this.ObjectName + "'", Status.DONE);
      } else {
         throw new ElementException(ExceptionType.Element_Not_Enabled, this.ObjectName);
      }
   }

   @Action(
      object = ObjectType.SELENIUM,
      desc = "Enter the value [<Data>] in the Field [<Object>]",
      input = InputType.YES
   )
   public void SetOnly() {
      if (this.elementEnabled()) {
         this.Element.sendKeys(new CharSequence[]{this.Data});
         this.Report.updateTestLog(this.Action, "Entered Text '" + this.Data + "' on '" + this.ObjectName + "'", Status.DONE);
      } else {
         throw new ElementException(ExceptionType.Element_Not_Enabled, this.ObjectName);
      }
   }

   @Action(
      object = ObjectType.SELENIUM,
      desc = "Set [<Data>] on [<Object>]",
      input = InputType.YES,
      condition = InputType.OPTIONAL
   )
   public void findElementAndSendkey() {
      if (this.elementPresent()) {
         try {
            this.Element.sendKeys(new CharSequence[]{this.Data});
            this.Report.updateTestLog(this.Action, "Entered Text '" + this.Data + "' on '" + this.ObjectName + "'", Status.DONE);
         } catch (Exception var2) {
            Logger.getLogger(JSCommands.class.getName()).log(Level.SEVERE, (String)null, var2);
            this.Report.updateTestLog(this.Action, "Couldn't set value on " + this.ObjectName + " - Exception " + var2.getMessage(), Status.FAIL);
         }

      } else {
         throw new ElementException(ExceptionType.Element_Not_Found, this.ObjectName);
      }
   }

   @Action(
      object = ObjectType.SELENIUM,
      desc = "Enter the value [<Data>] in the Field [<Object>]",
      input = InputType.YES
   )
   public void Upload() {
      try {
         try {
            this.Element.getAttribute("style").replace("display: none", "display: enable");
            Thread.sleep(200L);
            this.Element.sendKeys(new CharSequence[]{this.Data});
            Thread.sleep(1000L);
            this.Element.getAttribute("style").replace("display: enable", "display: none");
            Thread.sleep(200L);
         } catch (StaleElementReferenceException var2) {
            var2.printStackTrace();
         }
      } catch (Exception var3) {
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "+1",
      input = InputType.YES
   )
   public void increment() {
      String strObj = this.Input;
      if (strObj.startsWith("%") && strObj.endsWith("%")) {
         int i = Integer.parseInt(strObj);
         ++i;
         String j = String.valueOf(i);
         this.addVar(strObj, j);
         this.Report.updateTestLog(this.Action, "Current URL '" + j + "' is stored in variable '" + strObj + "'", Status.PASS);
      } else {
         this.Report.updateTestLog(this.Action, "Variable format is not correct", Status.FAIL);
      }

   }

   @Action(
      object = ObjectType.SELENIUM,
      desc = "Simple sendKeys",
      input = InputType.YES
   )
   public void simpleSet() {
      this.Element.sendKeys(new CharSequence[]{this.Data});
      this.Report.updateTestLog(this.Action, "Entered Text '" + this.Data + "' on '" + this.ObjectName + "'", Status.DONE);
   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "Accept the alert present"
   )
   public void IfacceptAlert() {
      try {
         this.Driver.switchTo().alert().accept();
         this.Report.updateTestLog(this.Action, "Alert is accepted", Status.DONE);
      } catch (Exception var2) {
         Logger.getLogger(CommonMethods.class.getName()).log(Level.SEVERE, (String)null, var2);
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "update the D3 in the sheet input<NPI to be update>condition<fileName.xlxs>",
      input = InputType.OPTIONAL,
      condition = InputType.YES
   )
   public void loopLimit() {
      String projectLocation = Control.getCurrentProject().getLocation();
      String absolutePath = projectLocation + "/TestData/" + this.Condition;
      int data = 5;
      if (!this.Data.isEmpty()) {
         data = Integer.parseInt(this.Data);
      }

      try {
         FileInputStream fis = new FileInputStream(absolutePath);

         try {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);

            for(int i = 0; i <= data; ++i) {
               String ScenarioName = sheet.getRow(1).getCell(0).getStringCellValue();
               Cell UpdateScenarioName = sheet.getRow(i + 2).getCell(0);
               UpdateScenarioName.setCellValue(ScenarioName);
               String Flow = sheet.getRow(1).getCell(1).getStringCellValue();
               Cell UpdateFlow = sheet.getRow(i + 2).getCell(1);
               UpdateFlow.setCellValue(Flow);
               String Iteration = sheet.getRow(1).getCell(2).getStringCellValue();
               Cell UpdateIteration = sheet.getRow(i + 2).getCell(2);
               UpdateIteration.setCellValue(Iteration);
               Cell SubIteration = sheet.getRow(i + 1).getCell(2);
               SubIteration.setCellValue((double)(i + 1));
            }

            FileOutputStream outputStream = new FileOutputStream(absolutePath);
            wb.write(outputStream);
            wb.close();
            outputStream.close();
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         } catch (Exception var15) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, var15.getMessage(), var15);
            this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
         }
      } catch (Exception var16) {
         Logger.getLogger(this.getClass().getName()).log(Level.OFF, var16.getMessage(), var16);
         this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
      }

   }

   @Action(
      object = ObjectType.BROWSER,
      desc = "update filenamewithextension at condition and input no. of subiternation>condition<fileName.csv>",
      input = InputType.YES,
      condition = InputType.YES
   )
   public void UpdateSubiternation() throws Throwable {
      String projectLocation = Control.getCurrentProject().getLocation();
      String absolutePath = projectLocation + "/TestData/" + this.Condition;
      int count = Integer.parseInt(this.Data);

      try {
         String[] headers = readCsvHeaders(absolutePath);
         if (headers != null) {
            String[] data = readCsvRow(absolutePath,1);
            generateExcelData(absolutePath,headers,data,1,count);
         } else {
            System.err.println("Error reading headers from CSV file");
         }

         this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
      } catch (Exception var8) {
         Logger.getLogger(this.getClass().getName()).log(Level.OFF, var8.getMessage(), var8);
         this.Report.updateTestLog(this.Action, "Path :[" + absolutePath + "] generated", Status.DONE);
      }

   }
   
   

   private static String[] readCsvHeaders(String filePath) throws Throwable {
      try {
         Throwable var1 = null;
         Object var2 = null;

         try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String[] var10000;
            try {
               String line = reader.readLine();
               if (line == null) {
                  return null;
               }

               var10000 = line.split(",");
            } finally {
               if (reader != null) {
                  reader.close();
               }

            }

            return var10000;
         } catch (Throwable var12) {
            if (var1 == null) {
               var1 = var12;
            } else if (var1 != var12) {
               var1.addSuppressed(var12);
            }

            throw var1;
         }
      } catch (IOException var13) {
         var13.printStackTrace();
         return null;
      }
   }

   private static String[] readCsvRow(String filePath, int rowIndex) throws Throwable {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            try {
               for(int i = 0; i < rowIndex; ++i) {
                  reader.readLine();
               }

               String line = reader.readLine();
               if (line == null) {
                  return null;
               } else {
                  String[] var10000 = line.split(",");
                  return var10000;
               }
            } finally {
               if (reader != null) {
                  reader.close();
               }

            }
         } catch (Throwable var13) {
            if (var2 == null) {
               var2 = var13;
            } else if (var2 != var13) {
               var2.addSuppressed(var13);
            }

            throw var2;
         }
      } catch (IOException var14) {
         var14.printStackTrace();
         return null;
      }
   }

  /* private static void generateExcelData(String filePath, String[] headers, String[] data, int startSubIteration, int endSubIteration) throws Throwable {
      try {
         Throwable var5 = null;
         Object var6 = null;

         try {
            PrintWriter writer = new PrintWriter(new FileWriter(filePath));

            try {
               writer.println(String.join(",", headers));

               for(int subIteration = startSubIteration; subIteration <= endSubIteration; ++subIteration) {
                  data[data.length - 1] = Integer.toString(subIteration);
                  writer.println(String.join(",", data));
               }

               System.out.println("Excel data generated successfully!");
            } finally {
               if (writer != null) {
                  writer.close();
               }

            }
         } catch (Throwable var16) {
            if (var5 == null) {
               var5 = var16;
            } else if (var5 != var16) {
               var5.addSuppressed(var16);
            }

            throw var5;
         }
      } catch (IOException var17) {
         var17.printStackTrace();
         System.err.println("Error generating Excel data: " + var17.getMessage());
      }

   }*/
   
   public static void generateExcelData(String filePath, String[] headers, String[] data, int startSubIteration, int endSubIteration) {
       try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
           writer.println(String.join(",", headers));

           for (int subIteration = startSubIteration; subIteration <= endSubIteration; ++subIteration) {
              // data[data.length - 1] = Integer.toString(subIteration);
        	   if (data.length > 0) {
        		    data[3] = Integer.toString(subIteration);
        		} else {
        		    // Handle the case where the array is empty
        		    System.err.println("Error: The 'data' array is empty.");
        		    // You might choose to throw an exception, log an error, or take other appropriate actions.
        		}

               writer.println(String.join(",", data));
           }

           System.out.println("Excel data generated successfully!");
       } catch (IOException e) {
           e.printStackTrace();
           System.err.println("Error generating Excel data: " + e.getMessage());
       }
   }
}
