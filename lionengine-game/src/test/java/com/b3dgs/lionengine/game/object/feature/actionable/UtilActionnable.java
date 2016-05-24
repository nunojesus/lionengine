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
package com.b3dgs.lionengine.game.object.feature.actionable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Utilities dedicated to actionnable test.
 */
public class UtilActionnable
{
    /**
     * Create a default action.
     * 
     * @param description The description.
     * @param rectangle The button.
     * @return The temp media.
     */
    public static Media createAction(String description, Rectangle rectangle)
    {
        final Media media = Medias.create("action.xml");
        final String name = "name";
        final ActionConfig action = new ActionConfig(name,
                                                     description,
                                                     (int) rectangle.getX(),
                                                     (int) rectangle.getY(),
                                                     (int) rectangle.getWidth(),
                                                     (int) rectangle.getHeight());
        final XmlNode root = Xml.create("test");
        root.add(ActionConfig.exports(action));
        Xml.save(root, media);

        return media;
    }

    /**
     * Create the services.
     * 
     * @param clicked The click flag.
     * @param clickNumber The click number recorded.
     * @return The services.
     */
    public static Services createServices(final AtomicBoolean clicked, final AtomicInteger clickNumber)
    {
        final Services services = new Services();
        final Cursor cursor = new Cursor()
        {
            @Override
            public boolean hasClickedOnce(int click)
            {
                clickNumber.set(click);
                return clicked.get();
            }
        };
        cursor.setArea(0, 0, 32, 32);
        cursor.setLocation(0, 1);
        services.add(cursor);

        return services;
    }

    /**
     * Create the actionable.
     * 
     * @param media The media.
     * @param services The services.
     * @return The prepared actionable.
     */
    public static ActionableModel createActionable(Media media, Services services)
    {
        final Setup setup = new Setup(media);
        final ObjectGame object = new ObjectGame(setup);
        final ActionableModel actionable = new ActionableModel(setup);
        actionable.prepare(object, services);

        return actionable;
    }

    /**
     * Create a default action.
     * 
     * @param executed The execution flag.
     * @return The created action.
     */
    public static Action createAction(final AtomicBoolean executed)
    {
        return new Action()
        {
            @Override
            public void execute()
            {
                executed.set(true);
            }
        };
    }
}