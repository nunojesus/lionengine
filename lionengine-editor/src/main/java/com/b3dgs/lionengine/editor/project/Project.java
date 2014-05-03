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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents a project and its data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Project
{
    /** Properties file. */
    public static final String PROPERTIES_FILE = ".lionengine";
    /** Properties file description. */
    public static final String PROPERTIES_FILE_DESCRIPTION = "LionEngine project properties";
    /** Property project sources folder. */
    public static final String PROPERTY_PROJECT_SOURCES = "SourcesFolder";
    /** Property project resources folder. */
    public static final String PROPERTY_PROJECT_RESOURCES = "ResourcesFolder";
    /** Create project error. */
    private static final String ERROR_CREATE_PROJECT = "Unable to create the project: ";

    /**
     * Open a project from its path.
     * 
     * @param projectPath The project path.
     * @return The created project.
     * @throws LionEngineException If not able to create the project.
     */
    public static Project create(File projectPath) throws LionEngineException
    {
        try (InputStream inputStream = new FileInputStream(new File(projectPath, Project.PROPERTIES_FILE));)
        {
            final Properties properties = new Properties();
            properties.load(inputStream);

            final String sources = properties.getProperty(Project.PROPERTY_PROJECT_SOURCES);
            final String resources = properties.getProperty(Project.PROPERTY_PROJECT_RESOURCES);

            final Project project = new Project(projectPath);
            project.setName(projectPath.getName());
            project.setSources(sources);
            project.setResources(resources);

            return project;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, Project.ERROR_CREATE_PROJECT, projectPath.getPath());
        }
    }

    /** Project path. */
    private final File path;
    /** Project name. */
    private String name;
    /** Source folder (represents the main source folder, such as <code>src/</code>). */
    private String sources;
    /** Resources folder (represents the main resources folder, such as <code>resources/</code>. */
    private String resources;
    /** Opened state. */
    private boolean opened;

    /**
     * Constructor.
     * 
     * @param path The project path.
     */
    private Project(File path)
    {
        this.path = path;
        opened = true;
    }

    /**
     * Open the project.
     */
    public void open()
    {
        opened = true;
    }

    /**
     * Close the project.
     */
    public void close()
    {
        opened = false;
    }

    /**
     * Set the project name.
     * 
     * @param name The project name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set the sources folder.
     * 
     * @param folder The source folder.
     */
    public void setSources(String folder)
    {
        sources = folder;
    }

    /**
     * Set the resources folder.
     * 
     * @param folder The resource folder.
     */
    public void setResources(String folder)
    {
        resources = folder;
    }

    /**
     * Get the project path.
     * 
     * @return The project path.
     */
    public File getPath()
    {
        return path;
    }

    /**
     * Get the project name.
     * 
     * @return The project name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the sources folder.
     * 
     * @return The sources folder.
     */
    public String getSources()
    {
        return sources;
    }

    /**
     * Get the resources folder.
     * 
     * @return The resources folder.
     */
    public String getResources()
    {
        return resources;
    }

    /**
     * Check if the project is opened.
     * 
     * @return <code>true</code> if opened, <code>false</code> else.
     */
    public boolean isOpened()
    {
        return opened;
    }
}
