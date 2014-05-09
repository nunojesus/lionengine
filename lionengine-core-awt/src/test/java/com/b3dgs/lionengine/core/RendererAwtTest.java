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
package com.b3dgs.lionengine.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.mock.ScreenMock;

/**
 * Test the renderer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class RendererAwtTest
        extends RendererTest
{
    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Engine.start("Test", Version.create(1, 0, 0), Verbose.CRITICAL, UtilityFile.getPath("src", "test", "resources"));
        System.out.println("*********************************** SEQUENCE VERBOSE ***********************************");
        System.out.flush();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        ScreenMock.wait = false;
        Engine.terminate();
        System.out.println("****************************************************************************************");
        System.out.flush();
    }
}