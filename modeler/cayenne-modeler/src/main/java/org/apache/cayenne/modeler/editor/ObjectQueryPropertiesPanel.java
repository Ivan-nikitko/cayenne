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
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.query.QueryMetadata;
import org.apache.cayenne.swing.components.JCayenneCheckBox;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * A panel that supports editing the properties of a query based on ObjEntity.
 */
public class ObjectQueryPropertiesPanel extends SelectPropertiesPanel {

    protected JCheckBox dataRows;

    public ObjectQueryPropertiesPanel(ProjectController mediator) {
        super(mediator);
    }

    @Override
    protected void initView() {
        super.initView();
        // create widgets

        dataRows = new JCayenneCheckBox();
        cacheGroupsLabel = new JLabel("Cache Group:");

        JPanel panel = FormBuilder.create()
                .columns("right:max(80dlu;pref), 3dlu, left:max(50dlu;pref), fill:max(150dlu;pref)")
                .rows("9*(p, 3dlu)")
                .addSeparator("").xyw(1, 1, 4)
                .addLabel("Result Caching:").xy(1, 3)
                .add(cacheStrategy).xywh(3, 3, 2, 1)
                .add(cacheGroupsLabel).xy(1, 7)
                .add(cacheGroups.getComponent()).xywh(3, 7, 2, 1)
                .add("Fetch Data Rows:").xy(1, 9)
                .add(dataRows).xy(3, 9)
                .add("Fetch Offset, Rows:").xy(1, 11)
                .add(fetchOffset.getComponent()).xy(3, 11)
                .add("Fetch Limit, Rows:").xy(1, 13)
                .add(fetchLimit.getComponent()).xy(3, 13)
                .add("Page Size:").xy(1, 15)
                .add(pageSize.getComponent()).xy(3, 15)
                .padding(Paddings.DIALOG)
                .build();

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void initController() {
        super.initController();

        dataRows.addItemListener(e -> {
            Boolean b = dataRows.isSelected() ? Boolean.TRUE : Boolean.FALSE;
            setQueryProperty(QueryMetadata.FETCHING_DATA_ROWS_PROPERTY, String.valueOf(b));
        });
    }

    /**
     * Updates the view from the current model state. Invoked when a currently displayed
     * query is changed.
     */
    @Override
    public void initFromModel(QueryDescriptor query) {
        super.initFromModel(query);

        dataRows.setSelected(Boolean.valueOf(query.getProperty(QueryMetadata.FETCHING_DATA_ROWS_PROPERTY)));
    }
}
