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

/**
 * Implementation provider for the {@link FactoryMedia}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryMediaProvider
        implements FactoryMedia
{
    /** Factory media implementation. */
    private static FactoryMedia factoryMedia;

    /**
     * Set the graphic factory used.
     * 
     * @param factoryMedia The media factory used.
     */
    public static void setFactoryMedia(FactoryMedia factoryMedia)
    {
        FactoryMediaProvider.factoryMedia = factoryMedia;
    }

    /*
     * FactoryMedia
     */

    @Override
    public Media create(String path)
    {
        return FactoryMediaProvider.factoryMedia.create(path);
    }

    @Override
    public Media create(String... path)
    {
        final StringBuilder fullPath = new StringBuilder();
        for (int i = 0; i < path.length; i++)
        {
            fullPath.append(path[i]);
            if (i < path.length - 1)
            {
                fullPath.append(getSeparator());
            }
        }
        return create(fullPath.toString());
    }

    @Override
    public String getSeparator()
    {
        return FactoryMediaProvider.factoryMedia.getSeparator();
    }

    @Override
    public void setSeparator(String separator)
    {
        FactoryMediaProvider.factoryMedia.setSeparator(separator);
    }
}