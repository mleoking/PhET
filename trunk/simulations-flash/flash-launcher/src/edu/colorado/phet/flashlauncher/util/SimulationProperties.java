package edu.colorado.phet.flashlauncher.util;

import java.io.IOException;
import java.util.Properties;

/**
 * This encapsulates a .properties file named "simulation.properties" that contains information used by FlashLauncher and TranslationUtility for launching Java and Flash sims.
 *
 * @author Sam Reid
 * @author Jon Olson
 */
public class SimulationProperties {
    public static final String SIMULATION_PROPRTIES_FILENAME = "simulation.properties";

    public static final String KEY_PROJECT = "project";
    public static final String KEY_SIMULATION = "simulation";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_TYPE = "type";

    private String project;
    private String simulation;
    private String language;
    private String country;
    private String type;

    public SimulationProperties() {
        // read sim and language from args file (JAR resource)        
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(SIMULATION_PROPRTIES_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.project = properties.getProperty(KEY_PROJECT);
        this.simulation = properties.getProperty(KEY_SIMULATION);
        this.language = properties.getProperty(KEY_LANGUAGE);
        this.country = properties.getProperty(KEY_COUNTRY);
        this.type = properties.getProperty(KEY_TYPE);
    }

    //TODO: This is currently a stub for which we may drop or add support, not sure now
    public boolean isDevelopment() {
        return false;
    }

    public String getSimulation() {
        return simulation;
    }

    public String getProject() {
        return project;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getType() {
        return type;
    }
}
