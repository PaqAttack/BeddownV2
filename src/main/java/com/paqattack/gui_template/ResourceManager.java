package com.paqattack.gui_template;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceManager {
    private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());

    private String rootPath;

    public ResourceManager(String rootPath) {
        if (!rootPath.endsWith("/")) {
            rootPath += "/";
        }
        this.rootPath = rootPath;
    }

    public BufferedReader getBufferedReaderResource(String path) {
        logger.log(Level.INFO, "Loading resource: {0}", rootPath + path);
        //TODO: check if resource has been extracted
        //TODO: If so, load that resource

        // check if file exists in resources
        try (InputStream is = getClass().getResourceAsStream(rootPath + path)) {
            logger.log(Level.INFO, "Resource loaded successfully: {0}", rootPath + path);
            if (is == null) {
                logger.log(Level.WARNING, "Resource not found: {0}", rootPath + path);
                return null;
            }
            return new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading resource: {0}", e.getMessage());
        }
        return null;
    }


}
