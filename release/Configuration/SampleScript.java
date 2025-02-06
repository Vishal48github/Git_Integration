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


import com.newvision.assureit.engine.commands.General;
import com.newvision.assureit.engine.core.CommandControl;

import com.newvision.assureit.engine.support.Status;
import com.newvision.assureit.engine.support.methodInf.Action;
import com.newvision.assureit.engine.support.methodInf.ObjectType;
import com.newvision.assureit.engine.support.methodInf.InputType;

public class SampleScript extends General {

    public SampleScript(CommandControl cc) {
        super(cc);
    }

    @Action(desc = "Description of the method", input = InputType.NO)
    public void yourCustomMethod() {
        //To Do. Put your code here
        Report.updateTestLog(Action, Description, Status.DONE);
    }

    @Action(object = ObjectType.SELENIUM)
    public void youCustomMethod2() {
        if (Element.getText().equals("Something")) {
            Report.updateTestLog(Action, "Element text matched with Something", Status.PASS);
        } else {
            Report.updateTestLog(Action, "Error in action", Status.FAIL);
        }
    }
}
