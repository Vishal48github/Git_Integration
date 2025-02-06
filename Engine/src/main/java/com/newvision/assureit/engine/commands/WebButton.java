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

import com.newvision.assureit.engine.core.CommandControl;
import com.newvision.assureit.engine.execution.exception.element.ElementException;
import com.newvision.assureit.engine.support.Status;
import com.newvision.assureit.engine.support.methodInf.Action;
import com.newvision.assureit.engine.support.methodInf.ObjectType;

final class WebButton extends General {

    public WebButton(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Object [<Object> is enabled]")
    public void isEnabled() {
        if (elementEnabled()) {
            Report.updateTestLog(Action, "Web Element is enabled", Status.PASS);
        } else {
            throw new ElementException(ElementException.ExceptionType.Element_Not_Enabled, Condition);
        }
    }

}
