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
package com.b3dgs.lionengine.editor.properties.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderTile;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.WorldPart;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileConfig;
import com.b3dgs.lionengine.game.tile.TileFeature;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 */
public class PropertiesTile implements PropertiesProviderTile
{
    /** Tile group icon. */
    private static final Image ICON_GROUP = UtilIcon.get(FOLDER, "tilegroup.png");
    /** Tile sheet icon. */
    private static final Image ICON_SHEET = UtilIcon.get(FOLDER, "tilesheet.png");
    /** Tile number icon. */
    private static final Image ICON_NUMBER = UtilIcon.get(FOLDER, "tilenumber.png");
    /** Tile size icon. */
    private static final Image ICON_SIZE = UtilIcon.get(FOLDER, "tilesize.png");
    /** Tile features icon. */
    private static final Image ICON_FEATURES = UtilIcon.get(FOLDER, "tilefeatures.png");
    /** Tile feature icon. */
    private static final Image ICON_FEATURE = UtilIcon.get(FOLDER, "tilefeature.png");

    /**
     * Change tile group.
     * 
     * @param map The map reference.
     * @param oldGroup The old group name.
     * @param newGroup The new group name (empty to remove it).
     * @param tile The tile reference.
     */
    public static void changeTileGroup(MapTileGroup map, String oldGroup, String newGroup, Tile tile)
    {
        final Media config = map.getGroupsConfig();
        final XmlNode root = Xml.load(config);
        changeTileGroup(root, oldGroup, newGroup, tile);
        Xml.save(root, config);
        map.loadGroups(config);
    }

    /**
     * Change tile group.
     * 
     * @param root The root reference.
     * @param oldGroup The old group name.
     * @param newGroup The new group name (empty to remove it).
     * @param tile The tile reference.
     */
    public static void changeTileGroup(XmlNode root, String oldGroup, String newGroup, Tile tile)
    {
        final Collection<Point> toAdd = new HashSet<>();
        for (final XmlNode nodeGroup : root.getChildren(TileGroupsConfig.NODE_GROUP))
        {
            removeOldGroup(nodeGroup, oldGroup, tile);
            if (CollisionGroup.same(nodeGroup.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME), newGroup))
            {
                final Point point = Geom.createPoint(tile.getSheet().intValue(), tile.getNumber());
                if (!toAdd.contains(point))
                {
                    toAdd.add(point);
                }
            }

        }
        if (!TileGroupsConfig.REMOVE_GROUP_NAME.equals(newGroup))
        {
            final XmlNode newNode = getNewNode(root, newGroup);
            for (final Point current : toAdd)
            {
                final XmlNode node = newNode.createChild(TileConfig.NODE_TILE);
                node.writeInteger(TileConfig.ATT_TILE_SHEET, current.getX());
                node.writeInteger(TileConfig.ATT_TILE_NUMBER, current.getY());
            }
        }
        toAdd.clear();
    }

    /**
     * Remove old tile group.
     * 
     * @param nodeGroup The current node group.
     * @param oldGroup The old group name.
     * @param tile The current tile.
     */
    private static void removeOldGroup(XmlNode nodeGroup, String oldGroup, Tile tile)
    {
        final Collection<XmlNode> toRemove = new ArrayList<>();
        if (CollisionGroup.same(nodeGroup.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME), oldGroup))
        {
            for (final XmlNode nodeTile : nodeGroup.getChildren(TileConfig.NODE_TILE))
            {
                if (nodeTile.readInteger(TileConfig.ATT_TILE_SHEET) == tile.getSheet().intValue()
                    && nodeTile.readInteger(TileConfig.ATT_TILE_NUMBER) == tile.getNumber())
                {
                    toRemove.add(nodeTile);
                }
            }
            for (final XmlNode remove : toRemove)
            {
                nodeGroup.removeChild(remove);
            }
        }
        toRemove.clear();
    }

    /**
     * Called on double click.
     * 
     * @param properties The tree properties.
     * @param selection The selected item.
     * @param tile The selected tile.
     */
    private static void onDoubleClick(Tree properties, TreeItem selection, Tile tile)
    {
        final MapTileGroup mapGroup = WorldModel.INSTANCE.getMap().getFeature(MapTileGroup.class);
        final Collection<String> values = new ArrayList<>();
        for (final String group : mapGroup.getGroups())
        {
            values.add(group);
        }
        if (!values.contains(TileGroupsConfig.REMOVE_GROUP_NAME))
        {
            values.add(TileGroupsConfig.REMOVE_GROUP_NAME);
        }
        final GroupChooser chooser = new GroupChooser(properties.getShell(), values);
        chooser.open();
        final String oldGroup = mapGroup.getGroup(tile);
        final String newGroup = chooser.getChoice();
        if (newGroup != null)
        {
            changeTileGroup(mapGroup, oldGroup, newGroup, tile);
            selection.setText(PropertiesPart.COLUMN_VALUE, newGroup);

            final WorldPart part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
            part.update();
        }
    }

    /**
     * Get the new node group.
     * 
     * @param node The node root.
     * @param newGroup The new group name.
     * @return The node found or created.
     */
    private static XmlNode getNewNode(XmlNode node, String newGroup)
    {
        for (final XmlNode nodeGroup : node.getChildren(TileGroupsConfig.NODE_GROUP))
        {
            if (newGroup.equals(nodeGroup.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME)))
            {
                return nodeGroup;
            }
        }
        final XmlNode newGroupNode = node.createChild(TileGroupsConfig.NODE_GROUP);
        newGroupNode.writeString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME, newGroup);

        return newGroupNode;
    }

    /**
     * Create the attribute group.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileGroup(final Tree properties, final Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        final MapTileGroup mapGroup = WorldModel.INSTANCE.getMap().getFeature(MapTileGroup.class);
        PropertiesPart.createLine(item, Messages.Properties_TileGroup, mapGroup.getGroup(tile));
        item.setData(TileGroupsConfig.NODE_GROUP);
        item.setImage(PropertiesTile.ICON_GROUP);

        properties.addListener(SWT.MouseDoubleClick, event ->
        {
            final org.eclipse.swt.graphics.Point point = new org.eclipse.swt.graphics.Point(event.x, event.y);
            final TreeItem selection = properties.getItem(point);
            if (item.equals(selection))
            {
                onDoubleClick(properties, selection, tile);
            }
        });
    }

    /**
     * Create the attribute sheet number.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileSheet(Tree properties, Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(item, Messages.Properties_TileSheet, String.valueOf(tile.getSheet()));
        item.setData(TileConfig.ATT_TILE_SHEET);
        item.setImage(PropertiesTile.ICON_SHEET);
    }

    /**
     * Create the attribute tile number.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileNumber(Tree properties, Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(item, Messages.Properties_TileNumber, String.valueOf(tile.getNumber()));
        item.setData(TileConfig.ATT_TILE_NUMBER);
        item.setImage(PropertiesTile.ICON_NUMBER);
    }

    /**
     * Create the attribute tile size.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileSize(Tree properties, Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(item, Messages.Properties_TileSize, tile.getWidth() + " * " + tile.getHeight());
        item.setData(TileConfig.NODE_TILE);
        item.setImage(PropertiesTile.ICON_SIZE);
    }

    /**
     * Create the attribute tile features.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileFeatures(Tree properties, Tile tile)
    {
        final TreeItem features = new TreeItem(properties, SWT.NONE);
        features.setText(Messages.Properties_TileFeatures);
        features.setImage(PropertiesTile.ICON_FEATURES);

        for (final TileFeature feature : tile.getFeatures())
        {
            final TreeItem item = new TreeItem(features, SWT.NONE);
            item.setText(Messages.Properties_TileFeature);
            item.setImage(PropertiesTile.ICON_FEATURE);

            final Class<?> clazz = feature.getClass();
            for (final Class<?> type : clazz.getInterfaces())
            {
                if (TileFeature.class.isAssignableFrom(type))
                {
                    PropertiesPart.createLine(item, type.getSimpleName(), clazz.getName());
                }
            }
        }
    }

    /**
     * Create properties.
     */
    public PropertiesTile()
    {
        // Nothing to do
    }

    /*
     * PropertiesProviderTile
     */

    @Override
    public void setInput(Tree properties, Tile tile)
    {
        createAttributeTileGroup(properties, tile);
        createAttributeTileSheet(properties, tile);
        createAttributeTileNumber(properties, tile);
        createAttributeTileSize(properties, tile);
        createAttributeTileFeatures(properties, tile);
    }
}
