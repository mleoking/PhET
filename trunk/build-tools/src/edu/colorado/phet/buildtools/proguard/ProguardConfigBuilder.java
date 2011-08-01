/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.proguard;

import java.io.File;

/**
 * A simple builder object for a ProGuard configuration.
 */
public class ProguardConfigBuilder {
    private volatile String name;
    private volatile File proguardTemplate;
    private volatile File proguardOutputFile;
    private volatile File[] inputJars;
    private volatile File outputJar;
    private volatile String[] mainClasses;
    private volatile boolean shrink;
    private volatile File[] additionalConfigFiles;

    public ProguardConfigBuilder() {
        reset();
    }

    public void reset() {
        this.name = null;
        this.proguardTemplate = null;
        this.proguardOutputFile = null;
        this.inputJars = null;
        this.outputJar = null;
        this.mainClasses = null;
        this.shrink = true;
        this.additionalConfigFiles = null;
    }

    public void setAdditionalConfigFiles( File[] additionalConfigFiles ) {
        this.additionalConfigFiles = additionalConfigFiles;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setProguardTemplate( File proguardTemplate ) {
        this.proguardTemplate = proguardTemplate;
    }

    public void setProguardOutputFile( File proguardOutputFile ) {
        this.proguardOutputFile = proguardOutputFile;
    }

    public void setInputJars( File[] inputJars ) {
        this.inputJars = inputJars;
    }

    public void setOutputJar( File outputJar ) {
        this.outputJar = outputJar;
    }

    public void setMainClasses( String[] mainClasses ) {
        this.mainClasses = mainClasses;
    }

    public void setShrink( boolean shrink ) {
        this.shrink = shrink;
    }

    public ProguardConfig build() {
        try {
            return new ProguardConfig( name, proguardTemplate, additionalConfigFiles,proguardOutputFile, inputJars, outputJar, mainClasses, shrink );
        }
        finally {
            reset();
        }
    }
}


