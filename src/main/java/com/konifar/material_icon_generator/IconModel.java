package com.konifar.material_icon_generator;

import com.intellij.openapi.project.Project;

import java.io.File;

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
public class IconModel {

    private static final String PATH_ICONS = "/icons";
    private static final String PATH_DRAWABLE_PREFIX = "drawable-";
    private static final String VECTOR_SIZE_NAME = "anydpi-v21";
    private static final String VECTOR_DEFAULT_DP = "24dp";
    private static final String VECTOR_VIEWPORT_SIZE = "24.0";
    private static final String UNDERBAR = "_";
    private static final String PNG_SUFFIX = ".png";
    private static final String XML_SUFFIX = ".xml";
    private static final String BLACK = "black";
    private static final String DP = "dp";

    private String iconName;
    private String displayColorName;
    private String colorCode;
    private String dp;
    private String fileName;
    private String resDir;

    private boolean mdpi;
    private boolean hdpi;
    private boolean xhdpi;
    private boolean xxhdpi;
    private boolean xxxhdpi;

    private boolean isVectorType;
    private boolean drawable;
    private boolean drawableV21;

    public IconModel(String iconName,
                     String displayColorName,
                     String colorCode,
                     String dp,
                     String fileName,
                     String resDir,
                     boolean mdpi,
                     boolean hdpi,
                     boolean xhdpi,
                     boolean xxhdpi,
                     boolean xxxhdpi,
                     boolean isVectorType,
                     boolean drawable,
                     boolean drawableV21) {
        this.iconName = iconName;
        this.displayColorName = displayColorName;
        this.colorCode = colorCode;
        this.dp = dp;
        this.fileName = fileName;
        this.resDir = resDir;
        this.mdpi = mdpi;
        this.hdpi = hdpi;
        this.xhdpi = xhdpi;
        this.xxhdpi = xxhdpi;
        this.xxxhdpi = xxxhdpi;
        this.isVectorType = isVectorType;
        this.drawable = drawable;
        this.drawableV21 = drawableV21;
    }

    public String getLocalPath(String size, boolean shouldForcePng) {
        if (iconName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_ICONS);

            String[] fileString = iconName.split("/");
            String iconName = isVectorType && !shouldForcePng
                    ? getVectorIconName(fileString[1])
                    : getImageIconName(fileString[1]);
            sb.append(getLocalDrawabaleIconPath(iconName, size));

            return sb.toString();
        } else {
            return "";
        }
    }

    public String getLocalPath(String size) {
        return getLocalPath(size, false);
    }

    public String getVectorLocalPath() {
        return getLocalPath(VECTOR_SIZE_NAME);
    }

    private String getLocalDrawabaleIconPath(String fileName, String size) {
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        String[] fileString = iconName.split("/");
        sb.append(fileString[0]);
        sb.append("/");
        sb.append(PATH_DRAWABLE_PREFIX);
        sb.append(size);
        sb.append("/");
        sb.append(fileName);
        return sb.toString();
    }

    private String getImageIconName(String shortName) {
        return getIconName(shortName, BLACK, dp, PNG_SUFFIX);
    }

    private String getVectorIconName(String shortName) {
        return getIconName(shortName, BLACK, VECTOR_DEFAULT_DP, XML_SUFFIX);
    }

    private String getIconName(String shortName, String colorName) {
        String suffix = isVectorType ? XML_SUFFIX : PNG_SUFFIX;
        return getIconName(shortName, colorName, this.dp, suffix);
    }

    private String getIconName(String shortName, String colorName, String dp, String suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(shortName);
        sb.append(UNDERBAR);
        sb.append(colorName);
        sb.append(UNDERBAR);
        sb.append(dp);
        sb.append(suffix);
        return sb.toString();
    }

    public String getResourcePath(Project project) {
        return resDir;
    }

    public String getCopyPath(Project project, String size) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResourcePath(project));
        sb.append(File.separator);
        sb.append(PATH_DRAWABLE_PREFIX);
        sb.append(size);
        sb.append(File.separator);
        sb.append(fileName);

        return sb.toString();
    }

    public String getVectorCopyPath(Project project, String dir) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResourcePath(project));
        sb.append(File.separator);
        sb.append(dir);
        sb.append(File.separator);
        sb.append(fileName);

        return sb.toString();
    }

    public void setIconAndFileName(String iconName) {
        if (iconName == null) {
            this.iconName = "";
            this.fileName = "";
        } else {
            this.iconName = iconName;
            String[] fileString = this.iconName.split("/");
            if (fileString.length > 1) this.fileName = getIconName(fileString[1], displayColorName);
        }
    }

    public void setDpAndFileName(String dp) {
        this.dp = dp;
        String[] fileString = iconName.split("/");
        if (fileString.length > 1) this.fileName = getIconName(fileString[1], displayColorName);
    }

    public void setDisplayColorName(String displayColorName) {
        this.displayColorName = displayColorName;
        String[] fileString = iconName.split("/");
        if (fileString.length > 1) this.fileName = getIconName(fileString[1], displayColorName);
    }

    public void setVectorTypeAndFileName(boolean vectorType) {
        isVectorType = vectorType;
        String[] fileString = iconName.split("/");
        if (fileString.length > 1) this.fileName = getIconName(fileString[1], displayColorName);
    }

    public void setResDir(String resDir) {
        this.resDir = resDir;
    }

    public boolean isMdpi() {
        return mdpi;
    }

    public void setMdpi(boolean mdpi) {
        this.mdpi = mdpi;
    }

    public boolean isHdpi() {
        return hdpi;
    }

    public void setHdpi(boolean hdpi) {
        this.hdpi = hdpi;
    }

    public boolean isXhdpi() {
        return xhdpi;
    }

    public void setXhdpi(boolean xhdpi) {
        this.xhdpi = xhdpi;
    }

    public boolean isXxhdpi() {
        return xxhdpi;
    }

    public void setXxhdpi(boolean xxhdpi) {
        this.xxhdpi = xxhdpi;
    }

    public boolean isXxxhdpi() {
        return xxxhdpi;
    }

    public void setXxxhdpi(boolean xxxhdpi) {
        this.xxxhdpi = xxxhdpi;
    }

    public String getDp() {
        return dp;
    }

    public String getViewportSize() {
        return VECTOR_VIEWPORT_SIZE;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isVectorType() {
        return isVectorType;
    }

    public boolean isDrawable() {
        return drawable;
    }

    public void setDrawable(boolean drawable) {
        this.drawable = drawable;
    }

    public boolean isDrawableV21() {
        return drawableV21;
    }

    public void setDrawableV21(boolean drawableV21) {
        this.drawableV21 = drawableV21;
    }

}
