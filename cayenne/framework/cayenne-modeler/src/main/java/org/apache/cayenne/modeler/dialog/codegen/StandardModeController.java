/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.dialog.codegen;

import java.awt.Component;

import org.apache.cayenne.gen.ClassGenerationAction;
import org.apache.cayenne.gen.ArtifactsGenerationMode;
import org.apache.cayenne.modeler.pref.DataMapDefaults;
import org.apache.cayenne.swing.BindingBuilder;

public class StandardModeController extends GeneratorController {

    protected StandardModePanel view;

    public StandardModeController(CodeGeneratorControllerBase parent) {
        super(parent);

        BindingBuilder builder = new BindingBuilder(
                getApplication().getBindingFactory(),
                this);

        builder.bindToTextField(
                view.getSuperclassPackage(),
                "preferences.superclassPackage").updateView();
    }

    protected DataMapDefaults createDefaults() {
        DataMapDefaults prefs = getApplication()
                .getFrameController()
                .getProjectController()
                .getDataMapPreferences("");

        prefs.updateSuperclassPackage(getParentController().getDataMap(), false);
        this.preferences = prefs;
        return prefs;
    }

    protected GeneratorControllerPanel createView() {
        this.view = new StandardModePanel();
        return view;
    }

    public Component getView() {
        return view;
    }
    
    @Override
    protected ClassGenerationAction newGenerator() {
        return new ClassGenerationAction();
    }
    
     @Override
    public ClassGenerationAction createGenerator() {
        mode = ArtifactsGenerationMode.ALL.getLabel();
        return super.createGenerator();
    }
}
