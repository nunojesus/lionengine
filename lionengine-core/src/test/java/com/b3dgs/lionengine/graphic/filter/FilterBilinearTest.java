/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.filter;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Test {@link FilterBilinear}.
 */
public final class FilterBilinearTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setLoadFromJar(FilterBilinearTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test bilinear filter
     */
    @Test
    public void testBilinear()
    {
        final Media media = Medias.create("image.png");
        final ImageBuffer image = Graphics.getImageBuffer(media);
        int i = 0;
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                i++;
                if (y < 10)
                {
                    image.setRgb(x, y, i % 2);
                }
                else if (y < 20)
                {
                    image.setRgb(x, y, i % 3);
                }
                else if (y < 32)
                {
                    image.setRgb(x, y, i % 5);
                }
            }
        }
        final FilterBilinear bilinear = new FilterBilinear();
        final ImageBuffer filtered = bilinear.filter(image);

        assertNotEquals(image, filtered);
        assertNotNull(bilinear.getTransform(1.0, 1.0));
        assertEquals(image.getWidth(), filtered.getWidth());
        assertEquals(image.getHeight(), filtered.getHeight());

        image.dispose();
        filtered.dispose();
    }
}
