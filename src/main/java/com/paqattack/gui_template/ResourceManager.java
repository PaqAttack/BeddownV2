package com.paqattack.gui_template;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceManager {
    private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());

    private final String extractedLocationPath;
    private final String rssPath;

    public ResourceManager(String extractedLocationPath, String rssPath) {
        this.extractedLocationPath = extractedLocationPath;
        this.rssPath = rssPath;
    }

    /**
     * Loads the requested file from the extracted files location if available and then from resources if it wasn't found.
     * @param path The path to the resource.
     * @return The string reference to the located resource.
     * @throws IOException If the resource could not be extracted.
     */
    public String getResourceAbsoluteFilePath(String path) throws IOException {
        // check if file exists in extracted resourcePath
        File rssDir = new File(extractedLocationPath + path);
        if (rssDir.exists()) {
            logger.log(Level.INFO, "Resource found at extracted location: {0}", extractedLocationPath + path);
            return extractedLocationPath + path;
        } else {
            logger.log(Level.INFO, "Resource not found at extracted location: {0}", extractedLocationPath + path);
        }

        // check if file exists in rssRoot
        try (InputStream is = getClass().getResourceAsStream(rssPath + path)) {
            if (is != null) {
                logger.log(Level.INFO, "Resource found at resources location: {0}", rssPath + path);
                return rssPath + path;
            } else {
                logger.log(Level.INFO, "Resource not found at resources location: {0}", rssPath + path);
            }
        }

        logger.log(Level.WARNING, "Resource not found anywhere: {0}", path);
        throw new IOException("Resource not found anywhere: " + path);
    }

    /**
     * Extracts resource to path in file system allowing user to override used file as extracted files are loaded over unextracted versions.
     * @param path String showing path from resource root of a resource
     * @throws IOException We dont have about exceptions! no no no (Or Bruno)
     */
    public void extractResource(String path) throws IOException {
        File rssDir = new File(extractedLocationPath + path);
        if (rssDir.exists()) {
            logger.log(Level.INFO, "Resource already extracted: {0}", extractedLocationPath + path);
            return;
        }

        logger.log(Level.INFO, "Extracting resource: {0}", extractedLocationPath + path);
        InputStream is = getClass().getResourceAsStream(rssPath + path);
        if (is == null) {
            logger.log(Level.WARNING, "Resource not found: {0}", rssPath + path);
            throw new IOException("Resource not found: " + rssPath + path);
        }

        if (rssDir.getParentFile().mkdirs()) {
            Files.copy(is, Paths.get(extractedLocationPath + path));
        } else {
            logger.log(Level.WARNING, "Error creating directory: {0}", rssDir.getParentFile().getAbsolutePath());
            throw new IOException("Error creating directory: " + rssDir.getParentFile().getAbsolutePath());
        }
    }


}
