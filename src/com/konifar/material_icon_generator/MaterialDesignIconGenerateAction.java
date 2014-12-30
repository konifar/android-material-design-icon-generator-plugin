package com.konifar.material_icon_generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MaterialDesignIconGenerateAction extends AnAction {

    public void actionPerformed(AnActionEvent event) {
        MaterialDesignIconGenerateDialog dialog = new MaterialDesignIconGenerateDialog(getEventProject(event));
        dialog.show();
    }

}
