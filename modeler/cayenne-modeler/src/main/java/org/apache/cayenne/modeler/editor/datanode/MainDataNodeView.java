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

package org.apache.cayenne.modeler.editor.datanode;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.util.JTextFieldUndoable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;

/**
 * A view for the main DataNode editor tab.
 */
public class MainDataNodeView extends JPanel {

    protected JTextField dataNodeName;
    protected JComboBox<String> factories;
    protected JPanel dataSourceDetail;
    protected CardLayout dataSourceDetailLayout;
    protected JTextFieldUndoable customAdapter;
    protected JComboBox<String> localDataSources;
    protected JButton configLocalDataSources;
    protected JComboBox<String> schemaUpdateStrategy;
    JLabel tipsLabel;

    public MainDataNodeView() {

        // create widgets
        this.dataNodeName = new JTextFieldUndoable();

        this.factories = Application.getWidgetFactory().createUndoableComboBox();

        this.localDataSources = Application.getWidgetFactory().createUndoableComboBox();

        this.schemaUpdateStrategy = Application.getWidgetFactory().createUndoableComboBox();
        this.dataSourceDetailLayout = new CardLayout();
        this.dataSourceDetail = new JPanel(dataSourceDetailLayout);

        this.customAdapter = new JTextFieldUndoable();

        this.configLocalDataSources = new JButton("...");
        this.configLocalDataSources.setToolTipText("configure local DataSource");

        this.tipsLabel = new JLabel("You can enter custom class implementing SchemaUpdateStrategy");
        this.tipsLabel.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize() - 2));

        // assemble
        JPanel panel = FormBuilder.create()
                .columns("right:80dlu, 3dlu, fill:200dlu, 3dlu, fill:20dlu")
                .rows("7*(p,3dlu)")
                .addSeparator("DataNode Configuration").xyw(1, 1, 5)
                .add("DataNode Name:").xy(1, 3)
                .add(getDataNodeName()).xyw(3, 3, 3)
                .add("Schema Update Strategy:").xy(1, 5)
                .add(schemaUpdateStrategy).xyw(3, 5, 3)
                .add(tipsLabel).xy(3,7)
                .add("Custom Adapter (opt.):").xy(1, 9)
                .add(customAdapter).xyw(3, 9, 3)
                .add("Local DataSource (opt.):").xy(1, 11)
                .add(localDataSources).xy(3, 11)
                .add(configLocalDataSources).xy(5, 11)
                .add("DataSource Factory:").xy(1, 13)
                .add(factories).xyw(3, 13, 3)
                .padding(Paddings.DIALOG)
                .build();

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(dataSourceDetail, BorderLayout.CENTER);
    }

    public JComboBox<String> getSchemaUpdateStrategy() {
        return schemaUpdateStrategy;
    }

    public JTextField getDataNodeName() {
        return dataNodeName;
    }

    public JPanel getDataSourceDetail() {
        return dataSourceDetail;
    }

    public JComboBox<String> getLocalDataSources() {
        return localDataSources;
    }

    public CardLayout getDataSourceDetailLayout() {
        return dataSourceDetailLayout;
    }

    public JComboBox<String> getFactories() {
        return factories;
    }

    public JButton getConfigLocalDataSources() {
        return configLocalDataSources;
    }

    public JTextFieldUndoable getCustomAdapter() {
        return customAdapter;
    }
}
