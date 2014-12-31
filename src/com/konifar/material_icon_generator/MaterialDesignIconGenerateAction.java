package com.konifar.material_icon_generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

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
public class MaterialDesignIconGenerateAction extends AnAction {

    /**
     * @param event Action event
     */
    public void actionPerformed(AnActionEvent event) {
        MaterialDesignIconGenerateDialog dialog = new MaterialDesignIconGenerateDialog(getEventProject(event));
        dialog.show();
    }

}
