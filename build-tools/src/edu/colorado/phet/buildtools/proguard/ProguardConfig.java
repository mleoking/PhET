/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.proguard;

import java.io.File;

/**
 * Describes a ProGuard configuration.
 * <p/>
 * It's impossible to construct an instance of an invalid configuration object,
 * and because the object is immutable, impossible to transition an instance
 * from a valid to an invalid state. So clients that have a reference to a
 * ProGuard configuration object know it's valid (no null fields, for example)
 * and do not have to perform any validation.
 */
public class ProguardConfig {
    private final String name;
    private final File proguardTemplate;
    private final File[] additionalConfigFiles;
    private final File proguardOutputFile;
    private final File[] inputJars;
    private final File outputJar;
    private final String[] mainClasses;
    private final boolean shrink;

    /**
     * @param name
     * @param proguardTemplate      The primary config file, erroneously named.
     * @param additionalConfigFiles List of additional config files, or 0-length list of no additional configuration is needed.
     *                              This is to support projects that specify their own proguard dependencies.
     * @param proguardFile
     * @param inJars
     * @param outJar
     * @param mainClasses
     * @param shrink
     */
    ProguardConfig( String name, File proguardTemplate, File[] additionalConfigFiles, File proguardFile, File[] inJars, File outJar, String[] mainClasses, boolean shrink ) {
        this.name = name;
        this.proguardTemplate = proguardTemplate;
        this.additionalConfigFiles = additionalConfigFiles;
        this.proguardOutputFile = proguardFile;
        this.inputJars = inJars;
        this.outputJar = outJar;
        this.mainClasses = mainClasses;
        this.shrink = shrink;

        verifySettings();
    }

    public File[] getAdditionalConfigFiles() {
        return additionalConfigFiles;
    }

    private void verifyNotNull( Object o, String name ) {
        if ( o == null ) {
            throw new IllegalStateException( "The parameter for the " + name + " must not be null." );
        }
    }

    private void verifySettings() {
        verifyNotNull( name, "name" );
        verifyNotNull( proguardTemplate, "template" );
        verifyNotNull( proguardOutputFile, "ProGuard file" );
        verifyNotNull( inputJars, "input jars" );
        verifyNotNull( outputJar, "output jars" );
        verifyNotNull( mainClasses, "main classes" );
        verifyNotNull( additionalConfigFiles, "additional config files" );
    }

    public String getName() {
        return name;
    }

    public File getProguardTemplate() {
        return proguardTemplate;
    }

    public File getProguardOutputFile() {
        return proguardOutputFile;
    }

    public File[] getInputJars() {
        return inputJars;
    }

    public File getOutputJar() {
        return outputJar;
    }

    public String[] getMainClasses() {
        return mainClasses;
    }

    public boolean getShrink() {
        return shrink;
    }
}