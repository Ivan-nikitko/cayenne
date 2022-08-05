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
package org.apache.cayenne.modeler.editor;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.modeler.ProjectController;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class EjbqlQueryPropertiesPanel extends SelectPropertiesPanel {

    public EjbqlQueryPropertiesPanel(ProjectController mediator) {
        super(mediator);
    }


    protected void initView() {
        super.initView();
        this.setLayout(new BorderLayout());
        cacheGroupsLabel = new JLabel("Cache Group:");
        this.add(getPanel(), BorderLayout.CENTER);
    }

    private JPanel getPanel() {
        return FormBuilder.create()
                .columns("right:max(80dlu;pref), 3dlu, left:max(10dlu;pref), "
                        + "3dlu, left:max(37dlu;pref), 3dlu, fill:max(147dlu;pref)")
                .rows("8*(p, 3dlu)")
                .addSeparator("Select Properties").xywh(1, 1, 7, 1)
                .add("Result Caching:").xy(1, 3)
                .add(cacheStrategy).xyw(3, 3, 5)
                .add(cacheGroupsLabel).xy(1, 7)
                .add(cacheGroups.getComponent()).xyw(3, 7, 5)
                .addLabel("Fetch Offset, Rows:").xy(1, 9)
                .add(fetchOffset.getComponent()).xyw(3, 9, 3)
                .addLabel("Fetch Limit, Rows:").xy(1, 11)
                .add(fetchLimit.getComponent()).xyw(3, 11, 3)
                .addLabel("Page Size:").xy(1, 13)
                .add(pageSize.getComponent()).xyw(3, 13, 3)
                .padding(Paddings.DIALOG)
                .build();
    }
}
