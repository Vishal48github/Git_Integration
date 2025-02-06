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
package com.newvision.assureit.engine.reporting.impl.sync;

import com.newvision.assureit.datalib.settings.RunSettings;
import com.newvision.assureit.engine.constants.FilePath;
import com.newvision.assureit.engine.core.Control;
import com.newvision.assureit.engine.core.RunContext;
import com.newvision.assureit.engine.reporting.SummaryReport;
import com.newvision.assureit.engine.reporting.TestCaseReport;
import com.newvision.assureit.engine.reporting.impl.handlers.SummaryHandler;
import com.newvision.assureit.engine.reporting.sync.sapi.ApiLink;
import com.newvision.assureit.engine.support.Status;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * 
 */
public class SAPISummaryHandler extends SummaryHandler {

    public ApiLink sapi_Link;
    private int PassedTestCases;
    private int FailedTestCases;

    public SAPISummaryHandler(SummaryReport report) {
        super(report);
        try {
            sapi_Link = new ApiLink();
        } catch (MalformedURLException ex) {
        }
    }

    /**
     * initialize the report data file.
     *
     * @param runTime
     * @param size
     */
    @Override
    public synchronized void createReport(String runTime, int size) {

        try {
            if (sapi_Link != null) {
                sapi_Link.setThreads(getRunSettings().getThreadCount());
                sapi_Link.setStartTime(runTime);
                sapi_Link.setIterMode(getRunSettings().getIterationMode());
                sapi_Link.setExeMode(getRunSettings().getExecutionMode());
                sapi_Link.setNoTests(size);
                sapi_Link.setRunName(FilePath.getCurrentReportFolderName());
                sapi_Link.clientData((JSONObject) report.pHandler.getData());
                sapi_Link.update();
            } else {
                System.out.println(String.format("ApiLink not available!!"));
            }
        } catch (Exception ex) {
            Logger.getLogger(SAPISummaryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static RunSettings getRunSettings() {
        return Control.exe.getExecSettings().getRunSettings();
    }

    /**
     * update the result of each test case result
     *
     * @param runContext
     * @param report
     * @param state
     * @param executionTime
     */
    @Override
    public synchronized void updateTestCaseResults(RunContext runContext,
            TestCaseReport report, Status state, String executionTime) {
        if (sapi_Link != null) {
            if (state.equals(Status.PASS)) {
                PassedTestCases++;
            } else {
                FailedTestCases++;
            }
            System.out.println(String.format("Updating results to SAPI, Passed : [%s] : Failed [%s]",
                    PassedTestCases, FailedTestCases));
            sapi_Link.update(PassedTestCases, FailedTestCases);
        }

    }

}
