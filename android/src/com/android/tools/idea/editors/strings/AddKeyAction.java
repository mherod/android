/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.editors.strings;

import com.android.tools.idea.editors.strings.table.StringResourceTable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.containers.HashSet;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.NotNull;

final class AddKeyAction extends AnAction {
  private final StringResourceTable myTable;
  private final AndroidFacet myFacet;

  AddKeyAction(@NotNull StringResourceTable table, @NotNull AndroidFacet facet) {
    super("Add Key", null, AllIcons.ToolbarDecorator.Add);

    myTable = table;
    myFacet = facet;
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(myTable.getData() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    StringResourceData data = myTable.getData();
    assert data != null;

    NewStringKeyDialog dialog = new NewStringKeyDialog(myFacet, new HashSet<>(data.getKeys()));

    if (!dialog.showAndGet()) {
      return;
    }

    Project project = myFacet.getModule().getProject();
    StringResourceKey key = dialog.getKey();
    XmlFile file = StringPsiUtils.getDefaultStringResourceFile(project, key);

    if (file == null) {
      return;
    }

    WriteCommandAction.runWriteCommandAction(project, () -> StringPsiUtils.addString(file, key, dialog.getDefaultValue()));
  }
}
