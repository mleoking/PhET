/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.proguard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.java.JavaBuildCommand;
import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * Builds a Proguard config for a Phet project.
 */
public class PhetProguardConfigBuilder {
    private final ProguardConfigBuilder builder = new ProguardConfigBuilder();

    public void reset() {
        builder.reset();
    }

    public void setDestJar( File outputJar ) {
        builder.setOutputJar( outputJar );
    }

    public void setJavaProject( JavaProject project ) {
        builder.setName( project.getName() );
        builder.setProguardTemplate( new File( project.getTrunk(), BuildToolsPaths.PROGUARD_TEMPLATE ) );
        builder.setAdditionalConfigFiles( project.getAdditionalProguardConfigFiles() );
        builder.setInputJars( prepend( project.getAllJarFiles(), project.getJarFile() ) );
        builder.setProguardOutputFile( new File( project.getAntOutputDir(), project.getName() + ".pro" ) );
        builder.setMainClasses( getAllMainClasses( project ) );
    }

    private String[] getAllMainClasses( JavaProject project ) {
        ArrayList list = new ArrayList( Arrays.asList( project.getAllMainClasses() ) );
        list.add( JavaBuildCommand.getMainLauncherClassName( project ) );
        return (String[]) list.toArray( new String[0] );
    }

    public void setShrink( boolean shrink ) {
        builder.setShrink( shrink );
    }

    public ProguardConfig build() {
        return builder.build();
    }

    private static File[] prepend( File[] list, File file ) {
        File[] f = new File[list.length + 1];
        System.arraycopy( list, 0, f, 1, list.length );
        f[0] = file;
        return f;
    }
}
