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
    private final File[] additionalConfigFiles;
    private final File proguardOutputFile;
    private final File[] inputJars;
    private final File outputJar;
    private final String[] mainClasses;
    private final boolean shrink;

    /**
     * @param name
     * @param proguardTemplate
     * @param additionalConfigFiles List of additional config files, or 0-length list of no additional configuration is needed.
     *                              This is to support sims that specify their own proguard dependencies
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        ProguardConfig that = (ProguardConfig) o;

        if ( shrink != that.shrink ) { return false; }
        if ( !Arrays.equals( additionalConfigFiles, that.additionalConfigFiles ) ) { return false; }
        if ( !Arrays.equals( inputJars, that.inputJars ) ) { return false; }
        if ( !Arrays.equals( mainClasses, that.mainClasses ) ) { return false; }
        if ( name != null ? !name.equals( that.name ) : that.name != null ) { return false; }
        if ( outputJar != null ? !outputJar.equals( that.outputJar ) : that.outputJar != null ) { return false; }
        if ( proguardOutputFile != null ? !proguardOutputFile.equals( that.proguardOutputFile ) : that.proguardOutputFile != null ) { return false; }
        if ( proguardTemplate != null ? !proguardTemplate.equals( that.proguardTemplate ) : that.proguardTemplate != null ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( proguardTemplate != null ? proguardTemplate.hashCode() : 0 );
        result = 31 * result + ( additionalConfigFiles != null ? Arrays.hashCode( additionalConfigFiles ) : 0 );
        result = 31 * result + ( proguardOutputFile != null ? proguardOutputFile.hashCode() : 0 );
        result = 31 * result + ( inputJars != null ? Arrays.hashCode( inputJars ) : 0 );
        result = 31 * result + ( outputJar != null ? outputJar.hashCode() : 0 );
        result = 31 * result + ( mainClasses != null ? Arrays.hashCode( mainClasses ) : 0 );
        result = 31 * result + ( shrink ? 1 : 0 );
        return result;
    }
}
