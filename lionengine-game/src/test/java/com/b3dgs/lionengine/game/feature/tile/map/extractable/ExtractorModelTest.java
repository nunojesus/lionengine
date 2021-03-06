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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilEnum;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Test {@link ExtractorModel}.
 */
public final class ExtractorModelTest
{
    /** Hack enum. */
    private static final UtilEnum<ExtractorState> HACK = new UtilEnum<>(ExtractorState.class, ExtractorModel.class);

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        HACK.restore();
    }

    private final Services services = new Services();

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return 0;
            }

            @Override
            public int getHeight()
            {
                return 0;
            }

            @Override
            public int getRate()
            {
                return 50;
            }
        });
        services.add(new MapTileGame());
    }

    /**
     * Test the extractor config.
     */
    @Test
    public void testConfig()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(5);
        extractor.setExtractionPerSecond(1.0);
        extractor.setDropOffPerSecond(2.0);
        extractor.prepare(object);

        assertEquals(5, extractor.getExtractionCapacity());
        assertEquals(1.0, extractor.getExtractionPerSecond());
        assertEquals(2.0, extractor.getDropOffPerSecond());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor.
     */
    @Test
    public void testExtractor()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setChecker(object);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<>();
        final AtomicReference<Enum<?>> startExtract = new AtomicReference<>();
        final AtomicReference<Enum<?>> extracted = new AtomicReference<>();
        final AtomicReference<Enum<?>> carry = new AtomicReference<>();
        final AtomicReference<Enum<?>> startDrop = new AtomicReference<>();
        final AtomicReference<Enum<?>> endDrop = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(goTo, startExtract, extracted, carry, startDrop, endDrop));

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);

        final Tiled location = extractor.getResourceLocation();
        assertEquals(1.0, location.getInTileX());
        assertEquals(2.0, location.getInTileY());
        assertEquals(1.0, location.getInTileWidth());
        assertEquals(1.0, location.getInTileHeight());
        assertEquals(ResourceType.WOOD, extractor.getResourceType());
        assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, goTo.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, startExtract.get());
        assertNotEquals(ResourceType.WOOD, extracted.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, extracted.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, carry.get());
        assertNotEquals(ResourceType.WOOD, startDrop.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, startDrop.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, endDrop.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor cannot extract.
     */
    @Test
    public void testCannotExtract()
    {
        final ObjectExtractor object = new ObjectExtractor(false, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(1);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(50.0);
        extractor.prepare(object);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<>();
        final AtomicReference<Enum<?>> skip = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(goTo, skip, skip, skip, skip, skip));
        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertNotNull(goTo.get());

        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertNotNull(goTo.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor cannot carry.
     */
    @Test
    public void testCannotCarry()
    {
        final ObjectExtractor object = new ObjectExtractor(true, false);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(1);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(50.0);
        extractor.prepare(object);

        final AtomicReference<Enum<?>> drop = new AtomicReference<>();
        final AtomicReference<Enum<?>> skip = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(skip, skip, skip, skip, drop, skip));
        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());

        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertNull(drop.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor with extractable.
     */
    @Test
    public void testExtractorExtractable()
    {
        final ObjectExtractorSelf object = new ObjectExtractorSelf();
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(2);
        extractor.setExtractionPerSecond(25.0);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object);
        extractor.addListener(object);

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        final Extractable extractable = UtilExtractable.createExtractable();
        extractor.setResource(extractable);

        assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals(1, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(3, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(4, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(5, object.flag.get());

        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);
        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertEquals(6, object.flag.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extractor with extractable without resource.
     */
    @Test
    public void testExtractorExtractableNoResource()
    {
        final ObjectExtractorSelf object = new ObjectExtractorSelf();
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(100.0);
        extractor.prepare(object);
        extractor.addListener(object);

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        final Extractable extractable = UtilExtractable.createExtractable();
        extractable.setResourcesQuantity(0);
        extractor.setResource(extractable);

        assertFalse(extractor.isExtracting());

        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals(1, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(2, object.flag.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the stop extraction.
     */
    @Test
    public void testStopExtraction()
    {
        final ObjectExtractor object = new ObjectExtractor(true, true);
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.prepare(object);
        extractor.setCapacity(6);
        extractor.setExtractionPerSecond(50.0);
        extractor.setDropOffPerSecond(100.0);

        final AtomicReference<Enum<?>> goTo = new AtomicReference<>();
        final AtomicReference<Enum<?>> startExtract = new AtomicReference<>();
        final AtomicReference<Enum<?>> empty = new AtomicReference<>();
        final AtomicReference<Enum<?>> extracted = new AtomicReference<>();
        extractor.addListener(UtilExtractable.createListener(goTo, startExtract, extracted, empty, empty, empty));

        assertNull(extractor.getResourceLocation());
        assertNull(extractor.getResourceType());

        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();

        assertFalse(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, goTo.get());

        extractor.update(1.0);

        assertTrue(extractor.isExtracting());
        assertEquals(ResourceType.WOOD, startExtract.get());
        assertNotEquals(ResourceType.WOOD, extracted.get());

        extractor.stopExtraction();
        extractor.update(1.0);

        assertFalse(extractor.isExtracting());
        assertNotEquals(ResourceType.WOOD, extracted.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the auto add listener.
     */
    @Test
    public void testListenerAutoAdd()
    {
        final ObjectExtractorSelf object = new ObjectExtractorSelf();
        object.addFeature(new TransformableModel());

        final Extractor extractor = new ExtractorModel(services);
        extractor.prepare(object);
        extractor.checkListener(object);
        extractor.setResource(ResourceType.WOOD, 1, 2, 1, 1);
        extractor.startExtraction();
        extractor.update(1.0);

        assertEquals(2, object.flag.get());

        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test with enum fail.
     * 
     * @throws NoSuchFieldException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     */
    @Test
    public void testEnumFail() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        final ExtractorModel extractor = new ExtractorModel(services);
        final Field field = extractor.getClass().getDeclaredField("state");
        UtilReflection.setAccessible(field, true);
        field.set(extractor, ExtractorState.values()[5]);

        assertThrows(() -> extractor.update(1.0), "Unknown enum: FAIL");
    }
}
