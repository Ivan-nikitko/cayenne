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

package org.apache.cayenne.modeler.util;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

/**
 * A dialog rendering a progress bar. It is normally controlled by a subclass of
 * LongRunningTask.
 */
public class ProgressDialog extends JDialog {

    protected JProgressBar progressBar;
    protected JLabel statusLabel;
    protected JButton cancelButton;
    private JLabel messageLabel;

    public ProgressDialog(JFrame parent, String title, String message) {
        super(parent, title);
        init(message);
    }

    private void init(String message) {
        this.progressBar = new JProgressBar();
        this.statusLabel = new JLabel(message, SwingConstants.LEFT);
        this.messageLabel = new JLabel(message, SwingConstants.LEFT);
        this.cancelButton = new JButton("Cancel");

        // assemble

        getRootPane().setDefaultButton(cancelButton);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);

        Container root = getContentPane();
        root.setLayout(new BorderLayout(5, 5));

        root.add(getPanel(), BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setResizable(false);
        pack();
        ModelerUtil.centerWindow(getOwner(), this);
    }

    private JPanel getPanel() {
        return FormBuilder.create()
                .columns("fill:max(250dlu;pref)")
                .rows("3*(p, 3dlu)")
                .add(messageLabel).xy(1, 1)
                .add(progressBar).xy(1, 3)
                .add(statusLabel).xy(1, 5)
                .padding(Paddings.DIALOG)
                .build();
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }
}
