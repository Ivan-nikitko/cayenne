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

package org.apache.cayenne.modeler.dialog.pref;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import com.jgoodies.forms.layout.CellConstraints;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 *
 */
public class GeneralPreferencesView extends JPanel {

    protected EncodingSelectorView encodingSelector;
    protected JLabel encodingSelectorLabel;
    protected JCheckBox autoLoadProjectBox;
    protected JCheckBox deletePromptBox;

    public GeneralPreferencesView() {
        this.encodingSelector = new EncodingSelectorView();
        this.encodingSelectorLabel = new JLabel("File Encoding:");
        this.autoLoadProjectBox = new JCheckBox("Automatically Load Last Opened Project");
        this.deletePromptBox = new JCheckBox("Always Delete Items Without Prompt");

        JPanel panel = FormBuilder.create()
                .columns("right:pref, 3dlu, 30dlu, 3dlu, fill:70dlu")
                .rows("p, 3dlu, p, 12dlu, p, 30dlu, p, 12dlu, p, 3dlu, p, fill:40dlu:grow")
                .addSeparator("General Preferences").xyw(1, 1, 5)
                .add(encodingSelector).xywh(1, 5, 3, 3)
                .add(autoLoadProjectBox).xy(1, 7, CellConstraints.LEFT, CellConstraints.DEFAULT)
                .addSeparator("Editor Preferences").xyw(1, 9, 5)
                .add(deletePromptBox).xy(1, 11, CellConstraints.LEFT, CellConstraints.DEFAULT)
                .padding(Paddings.DIALOG)
                .build();

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);

        encodingSelector.setEnabled(b);
        encodingSelectorLabel.setEnabled(b);
        autoLoadProjectBox.setEnabled(b);
        deletePromptBox.setEnabled(b);
    }

    public EncodingSelectorView getEncodingSelector() {
        return encodingSelector;
    }

    public JCheckBox getAutoLoadProject() {
        return autoLoadProjectBox;
    }

    public JCheckBox getDeletePrompt() {
        return deletePromptBox;
    }
}
