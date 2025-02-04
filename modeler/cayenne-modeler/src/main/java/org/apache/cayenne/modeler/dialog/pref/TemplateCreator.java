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

import java.awt.Component;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.cayenne.modeler.CodeTemplateManager;
import org.apache.cayenne.modeler.pref.FSPath;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.pref.CayennePreferenceEditor;
import org.apache.cayenne.pref.PreferenceEditor;
import org.apache.cayenne.swing.BindingBuilder;
import org.apache.cayenne.swing.control.FileChooser;
import org.apache.cayenne.util.Util;

public class TemplateCreator extends CayenneController {

    protected TemplateCreatorView view;
    protected boolean canceled;
    protected Set<String> existingNames;
    protected CayennePreferenceEditor editor;
    protected Preferences preferences;
    private static final String ERROR = "Error";

    public TemplateCreator(TemplatePreferences parent) {
        super(parent);

        JDialog parentDialog = (JDialog) SwingUtilities.getAncestorOfClass(
                JDialog.class,
                parent.getView());
        this.view = new TemplateCreatorView(parentDialog);
        this.existingNames = new HashSet<>();
        PreferenceEditor editor = parent.getEditor();
        if (editor instanceof CayennePreferenceEditor) {
            this.editor = (CayennePreferenceEditor) editor;
        }
        this.preferences = parent.getTemplatePreferences();

        for (FSPath path : parent.getTemplateEntries()) {
            existingNames.add(path.getKey());
        }
        initBindings();
    }

    public Component getView() {
        return view;
    }

    FSPath getLastTemplateDirectory() {
        // find start directory in preferences

        FSPath path = new FSPath(application.getPreferencesNode(
                CodeTemplateManager.class,
                "lastTemplate"));

        if (path.getPath() == null) {
            path.setPath(getLastDirectory().getPath());
        }

        return path;
    }

    protected void initBindings() {
        BindingBuilder builder = new BindingBuilder(
                getApplication().getBindingFactory(),
                this);

        builder.bindToAction(view.getCancelButton(), "cancelAction()");
        builder.bindToAction(view.getOkButton(), "okAction()");

        final FSPath path = getLastTemplateDirectory();
        view.getTemplateChooser().setCurrentDirectory(path.getExistingDirectory(false));
        view.getTemplateChooser().addPropertyChangeListener(
                FileChooser.CURRENT_DIRECTORY_PROPERTY,
                evt -> {
                    File directory = view.getTemplateChooser().getCurrentDirectory();
                    path.setDirectory(directory);
                });
    }

    public void okAction() {
        String templateName = view.getTemplateName().getText();

        if (Util.isEmptyString(templateName)) {
            JOptionPane.showMessageDialog(
                    view,
                    "Enter Template Name",
                    ERROR,
                    JOptionPane.WARNING_MESSAGE);
        } else if (existingNames.contains(templateName)) {
            JOptionPane.showMessageDialog(
                    view,
                    "'" + templateName + "' is already taken, enter a different name",
                    ERROR,
                    JOptionPane.WARNING_MESSAGE);
        } else if (view.getTemplateChooser().getFile() == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Must select an existing template file",
                    ERROR,
                    JOptionPane.WARNING_MESSAGE);
        } else {
            canceled = false;
            view.dispose();
        }
    }

    public void cancelAction() {
        canceled = true;
        view.dispose();
    }

    /**
     * Pops up a dialog and blocks current thread until the dialog is closed.
     */
    public FSPath startupAction() {
        // this should handle closing via ESC
        canceled = true;

        view.setModal(true);
        view.pack();
        view.setResizable(false);
        makeCloseableOnEscape();
        centerView();

        view.setVisible(true);
        return createTemplate();
    }

    protected FSPath createTemplate() {
        if (canceled) {
            return null;
        }
        String name = view.getTemplateName().getText();
        File file = view.getTemplateChooser().getFile();
        String path = file != null ? file.getAbsolutePath() : null;
        FSPath fsPath = application.getCodeTemplateManager().addTemplate(path, name);
        editor.getAddedNode().add(fsPath.getCurrentPreference());
        return fsPath;
    }
}
