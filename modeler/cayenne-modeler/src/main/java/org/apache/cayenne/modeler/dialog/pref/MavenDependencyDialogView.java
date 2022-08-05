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
import org.apache.cayenne.modeler.util.CayenneDialog;
import org.apache.cayenne.modeler.util.ModelerUtil;
import org.apache.cayenne.modeler.util.PanelFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

public class MavenDependencyDialogView extends CayenneDialog {

    private JButton downloadButton;
    private JButton cancelButton;
    private JTextField groupId;
    private JTextField artifactId;
    private JTextField version;

    public MavenDependencyDialogView(Dialog parentDialog) {
        super(parentDialog, "Download artifact", true);
        this.initView();
        this.pack();
        ModelerUtil.centerWindow(parentDialog, this);
    }

    public MavenDependencyDialogView(Frame parentFrame) {
        super(parentFrame, "Download artifact", true);
        this.initView();
        this.pack();
        ModelerUtil.centerWindow(parentFrame, this);
    }

    private void initView() {
        getContentPane().setLayout(new BorderLayout());

        {
            groupId = new JTextField(25);
            artifactId = new JTextField(25);
            version = new JTextField(25);

            JPanel panel = FormBuilder.create()
                    .columns("right:max(50dlu;pref), 3dlu, fill:min(100dlu;pref)")
                    .rows("3*(p,3dlu)")
                    .addLabel("Group id:").xy(1, 1)
                    .add(groupId).xy(3, 1)
                    .addLabel("Artifact id:").xy(1, 3)
                    .add(artifactId).xy(3, 3)
                    .addLabel("Version:").xy(1, 5)
                    .add(version).xy(3, 5)
                    .padding(Paddings.DIALOG)
                    .build();

            getContentPane().add(panel, BorderLayout.NORTH);
        }

        {
            downloadButton = new JButton("Download");
            cancelButton = new JButton("Cancel");
            getRootPane().setDefaultButton(downloadButton);

            JButton[] buttons = {cancelButton, downloadButton};
            getContentPane().add(PanelFactory.createButtonPanel(buttons), BorderLayout.SOUTH);
        }
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getDownloadButton() {
        return downloadButton;
    }

    public JTextField getArtifactId() {
        return artifactId;
    }

    public JTextField getGroupId() {
        return groupId;
    }

    public JTextField getVersion() {
        return version;
    }
}