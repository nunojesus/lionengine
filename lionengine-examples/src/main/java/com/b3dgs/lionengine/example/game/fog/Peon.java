/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.fog;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.fovable.Fovable;
import com.b3dgs.lionengine.game.trait.fovable.FovableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Peon entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Peon
        extends ObjectGame
        implements Updatable, Renderable
{
    /** Setup reference. */
    public static final Media MEDIA = Core.MEDIA.create("Peon.xml");

    /** Random timer. */
    private final Timing timing = new Timing();
    /** Surface reference. */
    private final SpriteAnimated surface;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Transformable model. */
    private Transformable transformable;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Peon(SetupSurface setup, Services services)
    {
        super(setup, services);

        surface = Drawable.loadSpriteAnimated(setup.surface, 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        viewer = services.get(Viewer.class);

        addTrait(TransformableModel.class);
        addTrait(FovableModel.class);
    }

    @Override
    protected void prepareTraits()
    {
        transformable = getTrait(Transformable.class);
        transformable.teleport(64, 64);

        final Fovable fovable = getTrait(Fovable.class);
        fovable.setFov(4);
        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        surface.setLocation(viewer, transformable);
        if (timing.elapsed(1000))
        {
            transformable.teleport(UtilRandom.getRandomInteger(19) * 16, UtilRandom.getRandomInteger(14) * 16);
            timing.restart();
        }
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }
}
