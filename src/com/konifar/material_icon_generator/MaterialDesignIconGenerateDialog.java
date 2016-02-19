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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by family_lee on 2016/2/8.
 */
public class MaterialDesignIconGenerateDialog extends DialogWrapper {

    private static final String CONFIGS_FILE_PATH = "/meteriaConfig.conf";
    private static final String TITLE = "Material Icon Generator";
    private static final String URL_OVERVIEW = "http://google.github.io/material-design-icons";
    private static final String URL_REPOSITORY = "https://github.com/google/material-design-icons";

    private static final String ERROR_ICON_NOT_SELECTED           = "Please select icon.";
    private static final String ERROR_FILE_NAME_EMPTY             = "Please input file name.";
    private static final String ERROR_SIZE_CHECK_EMPTY            = "Please check icon size.";
    private static final String ERROR_RESOURCE_DIR_NOTHING_PREFIX = "Can not find resource dir: ";
    private static final String ERROR_CUSTOM_COLOR                = "Can not parse custom color. Please provide color in hex format (#FFFFFF).";

    private static final String DEFAULT_RES_DIR = "/app/src/main/res";

    private static final String FILE_ICON_COMBOBOX_XML     = "template.xml";

    private JPanel panelMain;
    private JTextField textFieldFileName;
    private JCheckBox checkBoxMdpi;
    private JCheckBox checkBoxHdpi;
    private JCheckBox checkBoxXhdpi;
    private JCheckBox checkBoxXxhdpi;
    private JCheckBox checkBoxXxxhdpi;
    private TextFieldWithBrowseButton resDirectoryName;
    private JTextField nameA;
    private JTextField colorA;
    private JLabel imageA;
    private JButton btnCreateA;
    private JButton btnCreateB;
    private JButton btnCreateC;
    private JLabel labelOverview;
    private JLabel labelRepository;
    private FilterComboBox comboBoxIcon;
    private JTextField nameB;
    private JTextField colorB;
    private JTextField nameC;
    private JTextField colorC;
    private JCheckBox cbIconA;
    private JCheckBox cbIconB;
    private JCheckBox cbIconC;
    private JLabel imageB;
    private JLabel imageC;
    private JComboBox comboBoxDp;
    private JCheckBox checkBoxAutoGenerateXml;

    private Project project;

    private Configs configs;

    private IconHandler[] iconHandlers;

    public MaterialDesignIconGenerateDialog(@Nullable final Project project) {
        super(project, true);

        this.project = project;
        configs=Configs.getConfigs(project.getBasePath()+CONFIGS_FILE_PATH);
        setTitle(TITLE);
        setResizable(true);
        initIconHanlders();

        initIconComboBox();
        initDpComboBox();
        initResDirectoryName();
        initSizeCheckBox();
        initIconsCheckBox();

        initLabelLink(labelOverview, URL_OVERVIEW);
        initLabelLink(labelRepository, URL_REPOSITORY);


        textFieldFileName.setText(configs.getLastIconName());
        changeIconsPreview();
        init();
    }

    private void initIconsCheckBox() {
        cbIconA.setSelected(configs.isIconA());
        cbIconB.setSelected(configs.isIconB());
        cbIconC.setSelected(configs.isIconC());
        checkBoxAutoGenerateXml.setSelected(configs.isAutoGenXml());

        cbIconA.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setIconA(cbIconA.isSelected());
            }
        });

        cbIconB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setIconB(cbIconB.isSelected());
            }
        });

        cbIconC.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setIconC(cbIconC.isSelected());
            }
        });

        checkBoxAutoGenerateXml.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setAutoGenXml(checkBoxAutoGenerateXml.isSelected());
            }
        });
    }

    private void initSizeCheckBox() {
        checkBoxMdpi.setSelected(configs.isMdpi());
        checkBoxHdpi.setSelected(configs.isHdpi());
        checkBoxXhdpi.setSelected(configs.isXhdpi());
        checkBoxXxhdpi.setSelected(configs.isXxhdpi());
        checkBoxXxxhdpi.setSelected(configs.isXxxhdpi());

        checkBoxMdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setMdpi(checkBoxMdpi.isSelected());
            }
        });

        checkBoxHdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setHdpi(checkBoxHdpi.isSelected());
            }
        });

        checkBoxXhdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setXhdpi(checkBoxXhdpi.isSelected());
            }
        });

        checkBoxXxhdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setXxhdpi(checkBoxXxhdpi.isSelected());
            }
        });

        checkBoxXxxhdpi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                configs.setXxxhdpi(checkBoxXxxhdpi.isSelected());
            }
        });
    }

    private void initResDirectoryName() {
        resDirectoryName.setText(project.getBasePath() + DEFAULT_RES_DIR);
        List<AndroidFacet> facets =  AndroidUtils.getApplicationFacets(project);
        // This code needs refined to support multiple facets and multiple resource directories
        if (facets.size() >= 1) {
            List<VirtualFile> allResourceDirectories = facets.get(0).getAllResourceDirectories();
            if (allResourceDirectories.size() >= 1) {
                resDirectoryName.setText(allResourceDirectories.get(0).getCanonicalPath());
            }
        }
        resDirectoryName.addBrowseFolderListener(new TextBrowseFolderListener(
                new FileChooserDescriptor(false, true, false, false, false, false), project));
    }

    private void initDpComboBox() {
        String dp=configs.getDp();
        if(dp!=null &&  dp.length()>0){
            comboBoxDp.setSelectedItem(dp);
        }else{
            comboBoxDp.setSelectedIndex(0);
        }


        comboBoxDp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                configs.setDp((String) comboBoxDp.getSelectedItem());
                changeIconsPreview();
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

    private void changeIconsPreview(){
        for(IconHandler handler:iconHandlers){
            handler.showIconPreview();
        }
    }

    private void initIconHanlders(){
        iconHandlers=new IconHandler[3];
        IconInfo[] iconInfos= configs.getIconInfos();
        iconHandlers[0]=new IconHandler(iconInfos[0],nameA,colorA,cbIconA,imageA,btnCreateA);
        iconHandlers[1]=new IconHandler(iconInfos[1],nameB,colorB,cbIconB,imageB,btnCreateB);
        iconHandlers[2]=new IconHandler(iconInfos[2],nameC,colorC,cbIconC,imageC,btnCreateC);
    }

    private void initIconComboBox() {
        Document doc;
        try {
            doc = JDOMUtil.loadDocument(getClass().getResourceAsStream(FILE_ICON_COMBOBOX_XML));

            java.util.List<Element> elements = doc.getRootElement().getChildren();
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
                configs.setLastChooseIcon((String) comboBoxIcon.getSelectedItem());
                    textFieldFileName.setText(configs.getLastIconName());
                    changeIconsPreview();
            }
        });

        if(StringUtils.isEmpty(configs.getLastChooseIcon())){
            comboBoxIcon.setSelectedIndex(0);
        }else{
            comboBoxIcon.setSelectedItem(configs.getLastChooseIcon());
        }
    }

    @Override
    protected void doOKAction() {
        if (!isConfirmed()) return;

        if (alreadyFileExists()) {
            Object[] options = {"Yes", "No"};
            int option = JOptionPane.showOptionDialog(panelMain,
                    "File already exists, overwrite this ?",
                    "File exists",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (option == JOptionPane.YES_OPTION) {
                createNewIcons();
            }else{
                return;
            }
        } else {
            createNewIcons();
        }
        configs.save();
        super.doOKAction();
    }

    private boolean alreadyFileExists() {
        for(IconHandler handler:iconHandlers){
            if(handler.alreadyFileExists()){
                return true;
            }
        }
        return false;
    }

    private void createNewIcons(){
        for(IconHandler handler:iconHandlers){
            if(handler.isIconEnable()){
                handler.createNewIcons();
            }
        }
        generateResXml();
        showSuccDialog();
    }

    private void generateResXml(){
        if(checkBoxAutoGenerateXml.isSelected()) {
            String xml = configs.generateResXml(textFieldFileName.getText());
            if (xml != null && xml.length() > 0) {
                try {
                    File file = new File(configs.getXmlFile(resDirectoryName.getText(), textFieldFileName.getText()));
                    new File(file.getParent()).mkdirs();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(xml.getBytes());
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showSuccDialog(){
        String[] options=new String[]{"OK"};
        JOptionPane.showOptionDialog(panelMain, "Icon created successfully.",
                "Material design icon created", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
    }



    public boolean isConfirmed() {
        Object[] options = {"Yes", "No"};
        int option = JOptionPane.showOptionDialog(panelMain,
                "Are you sure you want to generate '" + configs.getLastIconName() + "' ?",
                "Confirmation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        return option == JOptionPane.OK_OPTION;
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

        File resourcePath = new File(resDirectoryName.getText());
        if (!resourcePath.exists() || !resourcePath.isDirectory()) {
            return new ValidationInfo(ERROR_RESOURCE_DIR_NOTHING_PREFIX + resourcePath, panelMain);
        }

        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panelMain;
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

    private class IconHandler{
        private IconInfo iconInfo;
        private JTextField tvName;
        private JTextField tvColor;
        private JCheckBox cbIcon;
        private JLabel lbImage;
        private JButton btnCreate;

        public IconHandler(IconInfo iconInfo,JTextField tvName,JTextField tvColor,JCheckBox cbIcon,JLabel lbImage,JButton btnCreate){
            this.iconInfo=iconInfo;
            this.tvName=tvName;
            this.tvColor=tvColor;
            this.cbIcon=cbIcon;
            this.lbImage=lbImage;
            this.tvName.setText(iconInfo.getName());
            this.tvColor.setText(iconInfo.getColor());
            this.btnCreate=btnCreate;
            this.btnCreate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isConfirmed()) return;

                    if (alreadyFileExists()) {
                        Object[] options = {"Yes", "No"};
                        int option = JOptionPane.showOptionDialog(panelMain,
                                "File already exists, overwrite this ?",
                                "File exists",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                options,
                                options[0]);

                        if (option == JOptionPane.YES_OPTION) {
                            createNewIcons();
                            showSuccDialog();
                        }
                    } else {
                        createNewIcons();
                        showSuccDialog();
                    }
                    configs.save();
                }
            });
            initFileCustomColor();
            initFileCustomName();
        }



        private void initFileCustomColor() {
            tvColor.getDocument().addDocumentListener(new DocumentListener() {
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
                        if (StringUtils.isEmpty(tvColor.getText())) {
                            iconInfo.setColor(null);
                            showIconPreview();
                            return;
                        }
                        try {
                            decodeColor(tvColor.getText());
                            iconInfo.setColor(tvColor.getText());
                            showIconPreview();
                        } catch (NumberFormatException e) {
                            iconInfo.setColor(null);
                            showIconPreview();
                        }

                }
            });
        }

        private void initFileCustomName() {
            tvName.getDocument().addDocumentListener(new DocumentListener() {
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
                    iconInfo.setName(tvName.getText());
                }
            });
        }

        public boolean alreadyFileExists() {
            JCheckBox[] checkBoxes = {checkBoxMdpi, checkBoxHdpi, checkBoxXhdpi, checkBoxXxhdpi, checkBoxXxxhdpi};

            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String fileName=configs.getDestFile(resDirectoryName.getText(),iconInfo.getName(),textFieldFileName.getText(),checkBox.getText());
                            File copyFile = new File(fileName);
                    if (copyFile.exists() && copyFile.isFile()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isIconEnable(){
            return cbIcon.isSelected();
        }

        public void createNewIcons(){
            JCheckBox[] checkBoxes = {checkBoxMdpi, checkBoxHdpi, checkBoxXhdpi, checkBoxXxhdpi, checkBoxXxxhdpi};
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    createIcon(checkBox.getText());
                }
            }
        }

        private boolean createIcon(String size) {
            File copyFile = new File(configs.getDestFile(resDirectoryName.getText(),iconInfo.getName(),textFieldFileName.getText(),size));
            String path = configs.getIconLocalPath(size);
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
                BufferedImage img =generateColoredIcon(ImageIO.read(is), iconInfo);
                ImageIO.write(img, "png", destFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private void showIconPreview() {
            try {
                ImageIO.setUseCache(false);
                String size = checkBoxXxhdpi.getText();
                InputStream is = getClass().getResourceAsStream(configs.getIconLocalPath(size));
                BufferedImage img = generateColoredIcon(ImageIO.read(is),iconInfo);
                ImageIcon icon = new ImageIcon(img);
                lbImage.setIcon(icon);

            } catch (Exception e) {
                // Do nothing
            }
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

        private BufferedImage generateColoredIcon(BufferedImage image,IconInfo iconInfo) {
            Color color = null;
            if (configs != null) {
                String colorString = iconInfo.getColor();
                color = decodeColor(colorString);
            }
            if (color == null) return image;

            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            WritableRaster raster = newImage.getRaster();
            for (int xx = 0; xx < width; xx++) {
                for (int yy = 0; yy < height; yy++) {
                    Color originalColor = new Color(image.getRGB(xx, yy), true);
                    int[] pixels = new int[4];
                    pixels[0] = color.getRed();
                    pixels[1] = color.getGreen();
                    pixels[2] = color.getBlue();

                    //a hack for bug.
                    int blackHack=originalColor.getRGB() & 0x00ffffff;

                    pixels[3] = combineAlpha(blackHack==0?0:originalColor.getAlpha(), color.getAlpha());

                    raster.setPixel(xx, yy, pixels);
                }
            }
            return newImage;
        }

    }
}
