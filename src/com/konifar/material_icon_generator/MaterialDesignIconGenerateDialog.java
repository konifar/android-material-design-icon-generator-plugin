package com.konifar.material_icon_generator;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;
import org.jetbrains.annotations.Nullable;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String COLOR_PALETTE_COMBOBOX_XML = "palette.xml";

    private static final String URL_OVERVIEW = "http://google.github.io/material-design-icons";
    private static final String URL_REPOSITORY = "https://github.com/google/material-design-icons";
    private static final String ERROR_ICON_NOT_SELECTED = "Please select icon.";
    private static final String ERROR_FILE_NAME_EMPTY = "Please input file name.";
    private static final String ERROR_SIZE_CHECK_EMPTY = "Please check icon size.";
    private static final String ERROR_RESOURCE_DIR_NOTHING_PREFIX = "Can not find resource dir: ";
    private static final String ERROR_CUSTOM_COLOR = "Can not parse custom color. Please provide color in hex format (#FFFFFF).";

    private static final String PACKAGE = "/com/konifar/material_icon_generator/";
    private static final String ICON_CONFIRM = PACKAGE + "icons/toggle/drawable-mdpi/ic_check_box_white_48dp.png";
    private static final String ICON_WARNING = PACKAGE + "icons/alert/drawable-mdpi/ic_error_white_48dp.png";
    private static final String ICON_DONE = PACKAGE + "icons/action/drawable-mdpi/ic_thumb_up_white_48dp.png";

    private static final String DEFAULT_RES_DIR = "/app/src/main/res";

    private Project project;
    private IconModel model;
    private Map<String, String> colorPaletteMap;

    private JPanel panelMain;
    private JLabel imageLabel;
    private JComboBox comboBoxDp;
    private JComboBox comboBoxColor;
    private JTextField textFieldColorCode;
    private FilterComboBox comboBoxIcon;
    private JTextField textFieldFileName;
    private JLabel labelOverview;
    private JLabel labelRepository;
    private JCheckBox checkBoxXxxhdpi;
    private TextFieldWithBrowseButton resDirectoryName;

    private JRadioButton radioImage;
    private JPanel panelImageSize;
    private JCheckBox checkBoxMdpi;
    private JCheckBox checkBoxHdpi;
    private JCheckBox checkBoxXhdpi;
    private JCheckBox checkBoxXxhdpi;

    private JRadioButton radioVector;
    private JPanel panelVector;
    private JCheckBox checkBoxDrawable;
    private JCheckBox checkBoxDrawableV21;
    private JCheckBox checkBoxDrawableNoDpi;

    public MaterialDesignIconGenerateDialog(@Nullable final Project project) {
        super(project, true);

        this.project = project;

        setTitle(TITLE);
        setResizable(true);

        initIconComboBox();
        initColorComboBox();
        initDpComboBox();
        initFileName();
        initResDirectoryName();
        initSizeCheckBox();
        initFileCustomColor();

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

    private void initFileCustomColor() {
        textFieldColorCode.getDocument().addDocumentListener(new DocumentListener() {
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
                if (model != null) {
                    if (StringUtils.isEmpty(textFieldColorCode.getText())) {
                        model.setColorCode(null);
                        showIconPreview();
                        comboBoxColor.setSelectedItem("");
                        return;
                    }
                    try {
                        decodeColor(textFieldColorCode.getText());
                        model.setColorCode(textFieldColorCode.getText());
                        showIconPreview();
                    } catch (NumberFormatException e) {
                        model.setColorCode(null);
                        comboBoxColor.setSelectedItem("");
                        showIconPreview();
                    }
                }
            }
        });
    }

    private void initResDirectoryName() {
        resDirectoryName.setText(project.getBasePath() + DEFAULT_RES_DIR);
        List<AndroidFacet> facets = AndroidUtils.getApplicationFacets(project);
        // This code needs refined to support multiple facets and multiple resource directories
        if (facets.size() >= 1) {
            List<VirtualFile> allResourceDirectories = facets.get(0).getAllResourceDirectories();
            if (allResourceDirectories.size() >= 1) {
                resDirectoryName.setText(allResourceDirectories.get(0).getCanonicalPath());
            }
        }
        resDirectoryName.addBrowseFolderListener(new TextBrowseFolderListener(
                new FileChooserDescriptor(false, true, false, false, false, false), project));
        resDirectoryName.getTextField().getDocument().addDocumentListener(new DocumentListener() {
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
                if (model != null) model.setResDir(resDirectoryName.getText());
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
        colorPaletteMap = new HashMap<String, String>();

        Document doc;
        try {
            doc = JDOMUtil.loadDocument(getClass().getResourceAsStream(COLOR_PALETTE_COMBOBOX_XML));

            List<Element> elements = doc.getRootElement().getChildren();
            for (org.jdom.Element element : elements) {
                String key = element.getAttributeValue("id");
                colorPaletteMap.put(key, element.getText());
                comboBoxColor.addItem(key);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        comboBoxColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (model != null) {
                    model.setDisplayColorName((String) comboBoxColor.getSelectedItem());
                    String value = colorPaletteMap.get(comboBoxColor.getSelectedItem());
                    textFieldColorCode.setText(value);
                    textFieldFileName.setText(model.getFileName());
                }
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

        comboBoxColor.setSelectedIndex(0);
        String value = colorPaletteMap.get(comboBoxColor.getSelectedItem());
        textFieldColorCode.setText(value);
    }

    private IconModel createModel() {
        final String iconName = (String) comboBoxIcon.getSelectedItem();
        final String displayColorName = (String) comboBoxColor.getSelectedItem();
        final String colorCode = textFieldColorCode.getText();
        final String dp = (String) comboBoxDp.getSelectedItem();
        final String fileName = textFieldFileName.getText();
        final String resDir = resDirectoryName.getText();
        final boolean mdpi = checkBoxMdpi.isSelected();
        final boolean hdpi = checkBoxHdpi.isSelected();
        final boolean xdpi = checkBoxXhdpi.isSelected();
        final boolean xxdpi = checkBoxXxhdpi.isSelected();
        final boolean xxxdpi = checkBoxXxxhdpi.isSelected();
        return new IconModel(iconName, displayColorName, colorCode, dp, fileName, resDir, mdpi, hdpi, xdpi, xxdpi, xxxdpi);
    }

    private void showIconPreview() {
        if (model == null) return;

        try {
            String size = checkBoxXxhdpi.getText();
            InputStream is = getClass().getResourceAsStream(model.getLocalPath(size));
            BufferedImage img = generateColoredIcon(ImageIO.read(is));
            ImageIcon icon = new ImageIcon(img);
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

    private void initIconComboBox() {
        Document doc;
        try {
            doc = JDOMUtil.loadDocument(getClass().getResourceAsStream(FILE_ICON_COMBOBOX_XML));

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
        File copyFile = new File(model.getCopyPath(project, size));
        String path = model.getLocalPath(size);
        try {
            new File(copyFile.getParent()).mkdirs();
            copyFile(path, copyFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private void copyFile(String originalPath, File destFile) throws IOException {
        try {
            InputStream is = getClass().getResourceAsStream(originalPath);
            BufferedImage img = generateColoredIcon(ImageIO.read(is));
            ImageIO.write(img, "png", destFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private BufferedImage generateColoredIcon(BufferedImage image) {
        Color color = null;
        if (model.getColorCode() != null) {
            String colorString = model.getColorCode();
            color = decodeColor(colorString);
        }
        if (color == null) return image;

        int width = image.getWidth();
        int height = image.getHeight();
        boolean hasAlpha = image.getColorModel().hasAlpha();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = newImage.getRaster();
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int originalPixel = image.getRGB(xx, yy);
                int originalAlpha;
                if (hasAlpha) {
                    originalAlpha = new Color(originalPixel, true).getAlpha();
                } else {
                    // Due to ImageIO's issue, `hasAlpha` is assigned `false` although PNG file has alpha channel.
                    // Regarding PNG files of Material Icon, in this case, the file is 1bit depth binary(BLACK or WHITE).
                    // Therefore BLACK is `alpha = 0` and WHITE is `alpha = 255`
                    originalAlpha = originalPixel == 0xFF000000 ? 0 : 255;
                }

                int[] pixels = new int[4];
                pixels[0] = color.getRed();
                pixels[1] = color.getGreen();
                pixels[2] = color.getBlue();
                pixels[3] = combineAlpha(originalAlpha, color.getAlpha());
                raster.setPixel(xx, yy, pixels);
            }
        }
        return newImage;
    }

    /**
     * {@link Color#decode} only supports opaque colors. This replicates that code but adds support
     * for alpha stored as the highest byte.
     */
    private Color decodeColor(String argbColor) throws NumberFormatException {
        long colorBytes = Long.decode(argbColor);
        if (argbColor.length() < 8) {
            colorBytes |= 0xFF << 24;
        }
        // Must cast to int otherwise java chooses the float constructor instead of the int constructor
        return new Color((int) (colorBytes >> 16) & 0xFF, (int) (colorBytes >> 8) & 0xFF, (int) colorBytes & 0xFF, (int) (colorBytes >> 24) & 0xFF);
    }

    private int combineAlpha(int first, int second) {
        return (first * second) / 255;
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
            return new ValidationInfo(ERROR_RESOURCE_DIR_NOTHING_PREFIX + resourcePath, panelMain);
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
