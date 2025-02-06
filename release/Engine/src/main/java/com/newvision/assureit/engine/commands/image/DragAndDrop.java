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
package com.newvision.assureit.engine.commands.image;

import com.newvision.assureit.datalib.or.common.ObjectGroup;
import com.newvision.assureit.datalib.or.image.ImageORObject;
import com.newvision.assureit.engine.core.CommandControl;
import com.newvision.assureit.engine.execution.exception.element.ElementException.ExceptionType;
import com.newvision.assureit.engine.support.Flag;
import com.newvision.assureit.engine.support.Status;
import com.newvision.assureit.engine.support.methodInf.Action;
import com.newvision.assureit.engine.support.methodInf.InputType;
import com.newvision.assureit.engine.support.methodInf.ObjectType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sikuli.script.Region;

public class DragAndDrop extends ImageCommand {

    public DragAndDrop(CommandControl cc) {
        super(cc);
    }

    /**
     * Find and drag the given Image
     */
    @Action(object = ObjectType.IMAGE, desc ="image [<Object>] is dragged.")
    public void imgDrag() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.drag(target) == 1) {
                    Report.updateTestLog(Action, "Dragged image " + ObjectName,
                            Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(),
                    Status.DEBUG);
            Logger.getLogger(DragAndDrop.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Drop the dragged image to the given image
     */
    
    @Action(object = ObjectType.IMAGE, desc ="Drop the dragged image on [<Object>]")
    public void imgDropAt() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.dropAt(target) == 1) {
                    Report.updateTestLog(Action, "Dropped the drageed on " + ObjectName,
                            Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action,
                    ex.getMessage(), Status.DEBUG);
            Logger.getLogger(DragAndDrop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Drag and drop the image to another image
     */
    
    @Action(object = ObjectType.IMAGE, desc ="Drag  image [<Object>]  and drop at image [<Data>] ", input =InputType.YES)
    public void imgDragandDrop() {

        try {
            String page = Data.split(",", -1)[0];
            String object = Data.split(",", -1)[1];
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            ObjectGroup<ImageORObject> dropObject = AObject.getImageObjects(page, object);

            droptarget = findTarget(dropObject, Flag.SET_OFFSET, Flag.MATCH_ONLY);

            if (target != null) {
                if (SCREEN.dragDrop(target, droptarget) == 1) {
                    Report.updateTestLog(Action,
                            "Drageed  image " + ObjectName + " and Dropped on " + object, Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(
                    Action,
                    target == null ? ObjectName : object
                            + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception e) {
            Report.updateTestLog(Action,
                    e.getMessage(), Status.DEBUG);
        }

    }

    /**
     * Drag and drop to the given location
     */
    @Action(object = ObjectType.IMAGE, 
    		desc =" Drag an image [<Object>] and drop it in a user-defined region [<Data>]", 
    		input =InputType.YES)
    public void imgDragandDropAt() {

        try {
            int x = Integer.valueOf(Data.split(",", -1)[0]), y = Integer
                    .valueOf(Data.split(",", -1)[1]), width = Integer
                    .valueOf(Data.split(",", -1)[2]), height = Integer
                    .valueOf(Data.split(",", -1)[3]);
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            droptarget = Region.create(x, y, width, height);

            if (target != null) {
                if (SCREEN.dragDrop(target, droptarget) == 1) {
                    Report.updateTestLog(Action,
                            "Draged image " + ObjectName + " and Dropped at given location", Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action,
                    ex.getMessage(), Status.DEBUG);
            Logger.getLogger(DragAndDrop.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
