/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.proguard;

import java.io.File;
import java.util.Arrays;

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
    private final File proguardOutputFile;
    private final File[] inputJars;
    private final File outputJar;
    private final String[] mainClasses;
    private final boolean shrink;

    ProguardConfig( String name, File proguardTemplate, File proguardFile, File[] inJars, File outJar, String[] mainClasses, boolean shrink ) {
        this.name = name;
        this.proguardTemplate = proguardTemplate;
        this.proguardOutputFile = proguardFile;
        this.inputJars = inJars;
        this.outputJar = outJar;
        this.mainClasses = mainClasses;
        this.shrink = shrink;

        verifySettings();
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

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ProguardConfig that = (ProguardConfig) o;

        if ( shrink != that.shrink ) {
            return false;
        }
        if ( !Arrays.equals( inputJars, that.inputJars ) ) {
            return false;
        }
        if ( !Arrays.equals( mainClasses, that.mainClasses ) ) {
            return false;
        }
        if ( !name.equals( that.name ) ) {
            return false;
        }
        if ( !outputJar.equals( that.outputJar ) ) {
            return false;
        }
        if ( !proguardOutputFile.equals( that.proguardOutputFile ) ) {
            return false;
        }
        if ( !proguardTemplate.equals( that.proguardTemplate ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 31 * result + proguardTemplate.hashCode();
        result = 31 * result + proguardOutputFile.hashCode();
        result = 31 * result + inputJars.hashCode();
        result = 31 * result + outputJar.hashCode();
        result = 31 * result + mainClasses.hashCode();
        result = 31 * result + ( shrink ? 1 : 0 );
        return result;
    }
}
