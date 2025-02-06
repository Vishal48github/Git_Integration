/*
 * Copyright 2014 - 2019 newvision Technology Solutions
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

import com.newvision.assureit.datalib.component.Project;
import com.newvision.assureit.datalib.component.Scenario;
import com.newvision.assureit.datalib.component.TestCase;
import com.newvision.assureit.datalib.settings.RunSettings;
import com.newvision.assureit.engine.constants.SystemDefaults;
import com.newvision.assureit.engine.drivers.SeleniumDriver;
import com.newvision.assureit.engine.execution.data.Parameter;
import com.newvision.assureit.engine.execution.data.UserDataAccess;
import com.newvision.assureit.engine.execution.exception.DriverClosedException;
import com.newvision.assureit.engine.execution.exception.TestFailedException;
import com.newvision.assureit.engine.execution.exception.UnCaughtException;
import com.newvision.assureit.engine.execution.run.TestCaseRunner;
import com.newvision.assureit.engine.reporting.TestCaseReport;
import com.newvision.assureit.engine.reporting.util.DateTimeUtils;
import com.newvision.assureit.engine.support.Status;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Task implements Runnable {

    TestCaseReport report;
    RunContext runContext;
    SeleniumDriver seleniumDriver;
    DateTimeUtils runTime;
    UserDataAccess userData;
    TestCaseRunner runner;

    public Task(RunContext RC) {
        runContext = RC;
    }

    public Project project() {
        return Control.exe.getProject();
    }

    private static RunSettings getRunSettings() {
        return Control.exe.getExecSettings().getRunSettings();
    }

    @Override
    public void run() {
        runTime = new DateTimeUtils();
        report = new TestCaseReport();
        TestCase stc = getTestCase();
        if (stc != null) {
            runner = new TestCaseRunner(Control.exe, stc);
        } else {
            runner = new TestCaseRunner(Control.exe, runContext.Scenario,
                    runContext.TestCase);
        }
        report.createReport(runContext, DateTimeUtils.DateTimeNow());
        int iter = 1;
        Date startexecDate = new Date();
        if (RunManager.getGlobalSettings().isTestRun()) {
            runner.setMaxIter(1);
        } else {
            runner.setMaxIter(Parameter.resolveMaxIter(runContext.Iteration));
            iter = Parameter.resolveStartIter(runContext.Iteration);
        }

        while (!SystemDefaults.stopExecution.get() && iter <= runner.getMaxIter()) {
            try {
                System.out.println("Running Iteration " + iter);
                runIteration(iter++);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        Date endEexcDate = new Date();
        
        if (report != null) {
            Status s = report.finalizeReport();
            Control.ReportManager.startDate = startexecDate;
            Control.ReportManager.endDate = endEexcDate;
            Control.ReportManager.updateTestCaseResults(runContext, report, s, runTime.timeRun());
            SystemDefaults.reportComplete.set(false);
        }
    }

    private TestCase getTestCase() {
        try {
            Scenario scn = project().getScenarioByName(runContext.Scenario);
            if (scn != null) {
                TestCase stc = scn.getTestCaseByName(runContext.TestCase);
                if (stc != null) {
                    return stc;
                } else {
                    LOG.log(Level.WARNING, "Testcase [{0}] not found", runContext.Scenario);
                }
            } else {
                LOG.log(Level.WARNING, "Scenario [{0}] not found", runContext.Scenario);
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Unable to load TestaCase", ex);
        }
        return null;
    }
    private static final Logger LOG = Logger.getLogger(Task.class.getName());

    public boolean runIteration(int iter) {
        boolean success = false;
        seleniumDriver = getSeDriver();
        try {
            SystemDefaults.reportComplete.set(true);
            report.startIteration(iter);
            launchBrowser();
            SystemDefaults.stopCurrentIteration.set(false);
            runner.run(createControl(), iter);
            success = true;
        } catch (DriverClosedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            report.updateTestLog("DriverClosedException", ex.getMessage(), Status.FAILNS);
        } catch (TestFailedException ex) {
            onFail(ex, ex.getMessage(), Status.DEBUG);
        } catch (UnCaughtException ex) {
            onError(ex, "Unhandled Error", ex.getMessage());
        } catch (Throwable ex) {
            onError(ex, "Error", ex.getMessage());
        } finally {
            if (seleniumDriver != null && !getRunSettings().useExistingDriver()) {
                try {
                    seleniumDriver.closeBrowser();
                } catch (Exception ex) {
                    System.out.println("Driver Closed Unexpectedly");
                    onError(ex, "Driver Error", ex.getMessage());
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            report.endIteration(iter);
        }
        return success;
    }

    private void launchBrowser() throws UnCaughtException {
        if (!getRunSettings().useExistingDriver() || seleniumDriver.driver == null) {
            seleniumDriver.launchDriver(runContext);
        }
        report.setDriver(seleniumDriver);
    }

    private CommandControl createControl() {
        return new CommandControl(seleniumDriver, report) {
            @Override
            public void execute(String com, int sub) {
                runner.runTestCase(com, sub);
            }

            @Override
            public void executeAction(String action) {
                runner.runAction(action);
            }

            @Override
            public Object context() {
                return runner;
            }
        };
    }

    private void onError(Throwable ex, String err, String desc) {
        onError(ex, err, desc, Status.DEBUG);
    }

    private void onFail(Throwable ex, String desc, Status s) {
        onError(ex, "[Breaking execution!]", desc, s);
    }

    private void onError(Throwable ex, String err, String desc, Status s) {
        if (ex != null) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        if (report != null) {
            report.updateTestLog(err, desc, s);
        }
    }

    private SeleniumDriver getSeDriver() {
        SeleniumDriver seDriver;
        if (!getRunSettings().useExistingDriver()
                || Control.getSeDriver() == null) {
            seDriver = new SeleniumDriver();
            Control.setSeDriver(seDriver);
        } else {
            seDriver = Control.getSeDriver();
        }
        return seDriver;
    }

}
