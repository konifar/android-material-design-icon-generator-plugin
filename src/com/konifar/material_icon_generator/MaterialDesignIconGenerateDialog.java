package com.konifar.material_icon_generator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.JDOMUtil;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.Nullable;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/*
 * Copyright 2014-2015 Material Design Icon Generator (Yusuke Konishi)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MaterialDesignIconGenerateDialog extends DialogWrapper {

    private static final String TITLE = "Material Icon Generator";
    private static final String FILE_ICON_COMBOBOX_XML = "template.xml";

    private static final String URL_OVERVIEW = "http://google.github.io/material-design-icons";
    private static final String URL_REPOSITORY = "https://github.com/google/material-design-icons";

    private static final String ERROR_ICON_NOT_SELECTED = "Please select icon.";
    private static final String ERROR_FILE_NAME_EMPTY = "Please input file name.";
    private static final String ERROR_SIZE_CHECK_EMPTY = "Please check icon size.";
    private static final String ERROR_RESOURCE_DIR_NOTHING = "Resource dir can not be found.";

    private static final String ICON_CONFIRM = "/icons/toggle/drawable-mdpi/ic_check_box_black_48dp.png";
    private static final String ICON_WARNING = "/icons/alert/drawable-mdpi/ic_error_black_48dp.png";
    private static final String ICON_DONE = "/icons/action/drawable-mdpi/ic_thumb_up_black_48dp.png";

    private Project project;
    private IconModel model;

    private JPanel panelMain;
    private JLabel imageLabel;
    private JComboBox comboBoxDp;
    private JComboBox comboBoxColor;
    private JCheckBox checkBoxMdpi;
    private FilterComboBox comboBoxIcon;
    private JTextField textFieldFileName;
    private JCheckBox checkBoxHdpi;
    private JCheckBox checkBoxXhdpi;
    private JCheckBox checkBoxXxhdpi;
    private JLabel labelOverview;
    private JLabel labelRepository;
    private JCheckBox checkBoxXxxhdpi;

    public MaterialDesignIconGenerateDialog(@Nullable final Project project) {
        super(project, true);

        this.project = project;

        setTitle(TITLE);
        setResizable(true);

        initIconComboBox();
        initColorComboBox();
        initDpComboBox();
        initFileName();
        initSizeCheckBox();

        initLabelLink(labelOverview, URL_OVERVIEW);
        initLabelLink(labelRepository, URL_REPOSITORY);

        model = createModel();

        model.setIconAndFileName((String) comboBoxIcon.getSelectedItem());
        textFieldFileName.setText(model.getFileName());
        showIconPreview();

        init();
    }

    private void initSizeCheckBox() {
        checkBoxMdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (model != null) model.setMdpi(checkBoxMdpi.isSelected());
            }
        });

        checkBoxHdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (model != null) model.setHdpi(checkBoxHdpi.isSelected());
            }
        });

        checkBoxXhdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (model != null) model.setXhdpi(checkBoxXhdpi.isSelected());
            }
        });

        checkBoxXxhdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (model != null) model.setXxhdpi(checkBoxXxhdpi.isSelected());
            }
        });

        checkBoxXxxhdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (model != null) model.setXxxhdpi(checkBoxXxxhdpi.isSelected());
            }
        });
    }

    private void initFileName() {
        textFieldFileName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                setText();
            }

            private void setText() {
                if (model != null) model.setFileName(textFieldFileName.getText());
            }
        });
    }

    private void initDpComboBox() {
        comboBoxDp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                model.setDpAndFileName((String) comboBoxDp.getSelectedItem());
                textFieldFileName.setText(model.getFileName());
                showIconPreview();
            }
        });

        comboBoxDp.getAccessibleContext().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())
                        && AccessibleState.FOCUSED.equals(event.getNewValue())
                        && comboBoxDp.getAccessibleContext().getAccessibleChild(0) instanceof ComboPopup) {
                    ComboPopup popup = (ComboPopup) comboBoxDp.getAccessibleContext().getAccessibleChild(0);
                    JList list = popup.getList();
                    comboBoxDp.setSelectedItem(String.valueOf(list.getSelectedValue()));
                }
            }
        });
    }

    private void initColorComboBox() {
        comboBoxColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                model.setColorAndFileName((String) comboBoxColor.getSelectedItem());
                textFieldFileName.setText(model.getFileName());
                showIconPreview();
            }
        });

        comboBoxColor.getAccessibleContext().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())
                        && AccessibleState.FOCUSED.equals(event.getNewValue())
                        && comboBoxColor.getAccessibleContext().getAccessibleChild(0) instanceof ComboPopup) {
                    ComboPopup popup = (ComboPopup) comboBoxColor.getAccessibleContext().getAccessibleChild(0);
                    JList list = popup.getList();
                    comboBoxColor.setSelectedItem(String.valueOf(list.getSelectedValue()));
                }
            }
        });
    }

    private IconModel createModel() {
        final String iconName = (String) comboBoxIcon.getSelectedItem();
        final String color = (String) comboBoxColor.getSelectedItem();
        final String dp = (String) comboBoxDp.getSelectedItem();
        final String fileName = textFieldFileName.getText();
        final boolean mdpi = checkBoxMdpi.isSelected();
        final boolean hdpi = checkBoxHdpi.isSelected();
        final boolean xdpi = checkBoxXhdpi.isSelected();
        final boolean xxdpi = checkBoxXxhdpi.isSelected();
        final boolean xxxdpi = checkBoxXxxhdpi.isSelected();
        return new IconModel(iconName, color, dp, fileName, mdpi, hdpi, xdpi, xxdpi, xxxdpi);
    }

    private void showIconPreview() {
        if (model == null) return;

        try {
            String size = checkBoxXxhdpi.getText();
            ImageIcon icon = new ImageIcon(getClass().getResource(model.getPath(size)));
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            // Do nothing
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panelMain;
    }

    private File getLocalFile(String fileName) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = getClass().getResourceAsStream(FILE_ICON_COMBOBOX_XML);
            File targetFile = new File(fileName);
            os = new FileOutputStream(targetFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return targetFile;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (os != null) try { os.close(); } catch (IOException e) { e.printStackTrace();}
            if (is != null) try { is.close(); } catch (IOException e) { e.printStackTrace();}
        }
    }

    private void initIconComboBox() {
        Document doc;
        try {
            File templateFile = getLocalFile(FILE_ICON_COMBOBOX_XML);
            doc = JDOMUtil.loadDocument(templateFile);

            List<Element> elements = doc.getRootElement().getChildren();
            for (org.jdom.Element element : elements) {
                comboBoxIcon.addItem(element.getText());
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        comboBoxIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (model != null) {
                    model.setIconAndFileName((String) comboBoxIcon.getSelectedItem());
                    textFieldFileName.setText(model.getFileName());
                    showIconPreview();
                }
            }
        });

        comboBoxIcon.setSelectedIndex(0);
    }

    @Override
    protected void doOKAction() {
        if (model == null) return;

        if (!isConfirmed()) return;

        if (alreadyFileExists()) {
            final int option = JOptionPane.showConfirmDialog(panelMain,
                    "File already exists, overwrite this ?",
                    "File exists",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon(getClass().getResource(ICON_WARNING)));

            if (option == JOptionPane.YES_OPTION) {
                create();
            }
        } else {
            create();
        }

    }

    private void create() {
        if (model.isMdpi()) createIcon(checkBoxMdpi.getText());
        if (model.isHdpi()) createIcon(checkBoxHdpi.getText());
        if (model.isXhdpi()) createIcon(checkBoxXhdpi.getText());
        if (model.isXxhdpi()) createIcon(checkBoxXxhdpi.getText());
        if (model.isXxxhdpi()) createIcon(checkBoxXxxhdpi.getText());

        JOptionPane.showConfirmDialog(panelMain,
                "Icon created successfully.",
                "Material design icon created",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                new ImageIcon(getClass().getResource(ICON_DONE)));
    }

    private boolean alreadyFileExists() {
        JCheckBox[] checkBoxes = {checkBoxMdpi, checkBoxHdpi, checkBoxXhdpi, checkBoxXxhdpi, checkBoxXxxhdpi};

        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                File copyFile = new File(model.getCopyPath(project, checkBox.getText()));
                if (copyFile.exists() && copyFile.isFile()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean createIcon(String size) {
        System.out.println(model.getCopyPath(project, size));
        File copyFile = new File(model.getCopyPath(project, size));
        String path = model.getPath(size);
        System.out.println(path);
        String originalFile = getClass().getResource(path).getFile();
        return copyIcon(copyFile, new File(originalFile));
    }

    private boolean copyIcon(File copyFile, File originalFile) {
        try {
            new File(copyFile.getParent()).mkdirs();
            copyFile(originalFile, copyFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void copyFile(File srcFile, File destFile) throws IOException {
        InputStream in = new FileInputStream(srcFile);
        OutputStream os = new FileOutputStream(destFile);

        int len = -1;
        byte[] b = new byte[1000 * 1024];
        try {
            while ((len = in.read(b, 0, b.length)) != -1) {
                os.write(b, 0, len);
            }
            os.flush();
        } finally {
            if (in != null) try { in.close(); } catch (IOException e) { e.printStackTrace(); }
            if (os != null) try { os.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void initLabelLink(JLabel label, final String url) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() > 0) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            URI uri = new URI(url);
                            desktop.browse(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isEmpty(comboBoxIcon.getInputText().trim())) {
            return new ValidationInfo(ERROR_ICON_NOT_SELECTED, comboBoxIcon);
        }

        if (StringUtils.isEmpty(textFieldFileName.getText().trim())) {
            return new ValidationInfo(ERROR_FILE_NAME_EMPTY, textFieldFileName);
        }

        if (!checkBoxMdpi.isSelected() && !checkBoxHdpi.isSelected() && !checkBoxXhdpi.isSelected()
                && !checkBoxXxhdpi.isSelected() && !checkBoxXxxhdpi.isSelected()) {
            return new ValidationInfo(ERROR_SIZE_CHECK_EMPTY, checkBoxMdpi);
        }

        File resourcePath = new File(model.getResourcePath(project));
        if (!resourcePath.exists() || !resourcePath.isDirectory()) {
            return new ValidationInfo(ERROR_RESOURCE_DIR_NOTHING, panelMain);
        }

        return null;
    }

    public boolean isConfirmed() {
        Object[] options = {"Yes", "No"};
        int option = JOptionPane.showOptionDialog(panelMain,
                "Are you sure you want to generate '" + model.getFileName() + "' ?",
                "Confirmation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                new ImageIcon(getClass().getResource(ICON_CONFIRM)),
                options,
                options[0]);

        return option == JOptionPane.OK_OPTION;
    }
}
