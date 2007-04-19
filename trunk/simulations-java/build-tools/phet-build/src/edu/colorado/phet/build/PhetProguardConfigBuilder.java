/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import edu.colorado.phet.build.proguard.ProguardConfigBuilder;
import edu.colorado.phet.build.proguard.ProguardConfig;

import java.io.File;

/**
 * Builds a Proguard config for a Phet project.
 */
public class PhetProguardConfigBuilder {
    private final ProguardConfigBuilder builder = new ProguardConfigBuilder();

    public void reset() {
        builder.reset();
    }

    public void setPhetProject( PhetProject project ) {
        builder.setName( project.getName() );
        builder.setProguardTemplate( new File( project.getAntBaseDir(), "templates/proguard2.pro" ) );
        builder.setOutputJar( project.getDestJar() );
        builder.setInputJars( prepend( project.getAllJarFiles(), project.getJarFile() ) );
        builder.setProguardOutputFile( new File( project.getAntOutputDir(), project.getName() + ".pro" ) );
        builder.setMainClasses( project.getMainClasses() );
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
