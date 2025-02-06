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
package com.newvision.assureit.engine.commands.aXe;

import com.newvision.assureit.engine.commands.General;
import com.newvision.assureit.engine.core.CommandControl;
import com.newvision.assureit.engine.execution.exception.element.ElementException;
import com.newvision.assureit.engine.reporting.aXe.AXE;
import com.newvision.assureit.engine.support.Status;
import com.newvision.assureit.engine.support.methodInf.Action;
import com.newvision.assureit.engine.support.methodInf.InputType;
import com.newvision.assureit.engine.support.methodInf.ObjectType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 
 */
public class Accessibility extends General {

    private static final Pattern IPATTERN = Pattern.compile("(?<var>@(\\S)+) (?<val>[^@]+)");

    public Accessibility(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.BROWSER,
            input = InputType.YES,
            desc = "To Test the Accessibility of the Page using aXe")
    public void testAccessibility() {
        testAccessibility(false);
    }

    @Action(object = ObjectType.WEB,
            input = InputType.OPTIONAL,
            desc = "To Test the Accessibility of the Element using aXe")
    public void testAccessibilityElement() {
        if (elementPresent()) {
            testAccessibility(true);
        } else {
            throw new ElementException(ElementException.ExceptionType.Element_Not_Found, ObjectName);
        }
    }

    private void testAccessibility(Boolean element) {
        Map<String, String> m = loadVals();
        AXE.Builder axe = new AXE.Builder(Driver)
                .options(m.get("@options"))
                .include(m.get("@include"))
                .exclude(m.get("@exclude"));
        String result;
        if (element) {
            result = axe.analyze(Element);
        } else {
            result = axe.analyze();
        }
        String name;
        if (m.isEmpty()) {
            name = Data;
        } else {
            name = Data.substring(0, Data.indexOf("@") - 1);
        }
        if (name.isEmpty()) {
            name = element ? ObjectName : "AxeReport";
        }
        updateStatus(name, result);
    }

    private Map<String, String> loadVals() {
        Matcher matcher = IPATTERN.matcher(Data);
        Map<String, String> m = new HashMap<>();
        while (matcher.find()) {
            m.put(matcher.group("var"), matcher.group("val"));
        }
        return m;
    }

    private void updateStatus(String name, String result) {
        AXE.add(name, result);
        int count = AXE.getViolationCount(result);
        if (count == 0) {
            Report.updateTestLog(Action, name + " - Passed all the recommendations by aXe", Status.DONE);
        } else {
            Report.updateTestLog(Action, name + " - Failed " + count + " violations found ", Status.WARNING);
        }
    }

}
