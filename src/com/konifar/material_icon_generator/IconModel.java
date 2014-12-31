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

    private static final String PATH_ICONS = "icons";
    private static final String PATH_DRAWABLE_PREFIX = "drawable-";
    private static final String UNDERBAR = "_";
    private static final String PNG_SUFFIX = ".png";
    private static final String COPY_DIR = "/app/src/main/res";

    private String iconName;
    private String color;
    private String dp;
    private String fileName;

    private boolean mdpi;
    private boolean hdpi;
    private boolean xhdpi;
    private boolean xxhdpi;
    private boolean xxxhdpi;

    public IconModel(String iconName,
                     String color,
                     String dp,
                     String fileName,
                     boolean mdpi,
                     boolean hdpi,
                     boolean xhdpi,
                     boolean xxhdpi,
                     boolean xxxhdpi) {
        this.iconName = iconName;
        this.color = color.toLowerCase();
        this.dp = dp;
        this.fileName = fileName;
        this.mdpi = mdpi;
        this.hdpi = hdpi;
        this.xhdpi = xhdpi;
        this.xxhdpi = xxhdpi;
        this.xxxhdpi = xxxhdpi;
    }

    public String getLocalPath(String size) {
        if (iconName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_ICONS);

            String[] fileString = iconName.split(File.separator);
            sb.append(getLocalDrawabaleIconPath(getIconName(fileString[1]), size));

            return sb.toString();
        } else {
            return "";
        }
    }

    private String getLocalDrawabaleIconPath(String fileName, String size) {
        StringBuilder sb = new StringBuilder();
        sb.append(File.separator);
        String[] fileString = iconName.split(File.separator);
        sb.append(fileString[0]);
        sb.append(File.separator);
        sb.append(PATH_DRAWABLE_PREFIX);
        sb.append(size);
        sb.append(File.separator);
        sb.append(fileName);
        return sb.toString();
    }

    private String getIconName(String shortName) {
        StringBuilder sb = new StringBuilder();
        sb.append(shortName);
        sb.append(UNDERBAR);
        sb.append(color);
        sb.append(UNDERBAR);
        sb.append(dp);
        sb.append(PNG_SUFFIX);
        return sb.toString();
    }

    public String getResourcePath(Project project) {
        StringBuilder sb = new StringBuilder();
        sb.append(project.getBasePath());
        sb.append(COPY_DIR);
        return sb.toString();
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

    public void setIconAndFileName(String iconName) {
        if (iconName == null) {
            this.iconName = "";
            this.fileName = "";
        } else {
            this.iconName = iconName;
            String[] fileString = this.iconName.split(File.separator);
            if (fileString.length > 1) this.fileName = getIconName(fileString[1]);
        }
    }

    public void setColorAndFileName(String color) {
        this.color = color.toLowerCase();
        String[] fileString = iconName.split(File.separator);
        if (fileString.length > 1) this.fileName = getIconName(fileString[1]);
    }

    public void setDpAndFileName(String dp) {
        this.dp = dp;
        String[] fileString = iconName.split(File.separator);
        if (fileString.length > 1) this.fileName = getIconName(fileString[1]);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMdpi(boolean mdpi) {
        this.mdpi = mdpi;
    }

    public void setHdpi(boolean hdpi) {
        this.hdpi = hdpi;
    }

    public void setXhdpi(boolean xhdpi) {
        this.xhdpi = xhdpi;
    }

    public void setXxhdpi(boolean xxhdpi) {
        this.xxhdpi = xxhdpi;
    }

    public void setXxxhdpi(boolean xxxhdpi) {
        this.xxxhdpi = xxxhdpi;
    }

    public boolean isMdpi() {
        return mdpi;
    }

    public boolean isHdpi() {
        return hdpi;
    }

    public boolean isXhdpi() {
        return xhdpi;
    }

    public boolean isXxhdpi() {
        return xxhdpi;
    }

    public boolean isXxxhdpi() {
        return xxxhdpi;
    }

    public String getFileName() {
        return fileName;
    }
}
