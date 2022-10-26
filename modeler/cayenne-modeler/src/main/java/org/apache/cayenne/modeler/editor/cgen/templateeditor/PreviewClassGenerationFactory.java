/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.editor.cgen.templateeditor;

import org.apache.cayenne.di.Inject;
import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.gen.ClassGenerationAction;
import org.apache.cayenne.gen.ClassGenerationActionFactory;
import org.apache.cayenne.gen.MetadataUtils;
import org.apache.cayenne.gen.ToolsUtilsFactory;

import java.io.StringWriter;


/**
 * @since 4.3
 */
public class PreviewClassGenerationFactory implements ClassGenerationActionFactory {

    @Inject
    private ToolsUtilsFactory utilsFactory;

    @Inject
    private MetadataUtils metadataUtils;

    @Inject(PreviewActionConfigurator.TEMPLATE_EDITOR_WRITER)
    private StringWriter writer;

    @Override
    public ClassGenerationAction createAction(CgenConfiguration cgenConfiguration) {
        PreviewGenerationAction action = new PreviewGenerationAction(cgenConfiguration);
        action.setUtilsFactory(utilsFactory);
        action.setMetadataUtils(metadataUtils);
        action.setWriter(writer);
        return action;
    }

}
