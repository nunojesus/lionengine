/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.editor.handlers;

import java.io.File;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.editor.project.ProjectsPart;

/**
 * Open project handler implementation.
 * 
 * @author Pierre-Alexandre
 */
public class OpenProjectHandler
{
    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     * @param activePart The active part.
     */
    @Execute
    public void execute(Shell shell, @Active MPart activePart)
    {
        final DirectoryDialog dialog = new DirectoryDialog(shell);
        final String path = dialog.open();
        if (path != null)
        {
            final File projectPath = new File(path);
            final Object part = activePart.getObject();
            if (part instanceof ProjectsPart)
            {
                final Project project = Project.create(projectPath);
                ProjectsModel.INSTANCE.setRoot(project.getPath());
                ((ProjectsPart) part).setInput(project);
            }
        }
    }
}
