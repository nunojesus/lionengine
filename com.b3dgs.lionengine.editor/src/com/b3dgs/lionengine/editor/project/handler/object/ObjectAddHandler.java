/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.project.handler.object;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.game.object.ObjectConfig;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Add an object descriptor in the selected folder.
 */
public final class ObjectAddHandler
{
    /**
     * Create handler.
     */
    public ObjectAddHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param parent The shell parent.
     */
    @Execute
    public void execute(Shell parent)
    {
        InputValidator.getFile(parent, Messages.Title, Messages.Text, ObjectConfig.DEFAULT_FILENAME, file ->
        {
            final XmlNode root = Xml.create(UtilFile.removeExtension(ObjectConfig.NODE_OBJECT));
            root.add(ObjectConfig.exportClass(ObjectGame.class.getName()));
            root.add(ObjectConfig.exportSetup(Setup.class.getName()));
            Xml.save(root, Project.getActive().getResourceMedia(file));
        });
    }
}
