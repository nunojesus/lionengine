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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the operating system class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class OperatingSystemTest
{
    /**
     * Test the operating system class.
     */
    @Test
    public void testOperatingSystem()
    {
        Assert.assertNotNull(OperatingSystem.getArchitecture());
        Assert.assertNotNull(OperatingSystem.getOperatingSystem());
        Assert.assertNotNull(OperatingSystem.MAC);
        Assert.assertNotNull(OperatingSystem.WINDOWS);
        Assert.assertNotNull(OperatingSystem.UNIX);
        Assert.assertNotNull(OperatingSystem.UNKNOWN);
        Assert.assertNotNull(OperatingSystem.SOLARIS);

        Assert.assertNotNull(OperatingSystem.values());
        Assert.assertEquals(OperatingSystem.WINDOWS, OperatingSystem.valueOf(OperatingSystem.WINDOWS.name()));
    }

    /**
     * Test the filter enum switch.
     */
    @Test
    public void testOperatingSystemEnumSwitch()
    {
        for (final OperatingSystem os : OperatingSystem.values())
        {
            switch (os)
            {
                case WINDOWS:
                    // Success
                    break;
                case UNIX:
                    // Success
                    break;
                case MAC:
                    // Success
                    break;
                case SOLARIS:
                    // Success
                    break;
                case UNKNOWN:
                    // Success
                    break;
                default:
                    Assert.fail();
            }
        }
    }
}