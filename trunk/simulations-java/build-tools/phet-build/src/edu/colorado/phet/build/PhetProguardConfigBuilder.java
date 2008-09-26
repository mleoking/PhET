/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.build.proguard.ProguardConfig;
import edu.colorado.phet.build.proguard.ProguardConfigBuilder;

/**
 * Builds a Proguard config for a Phet project.
 */
public class PhetProguardConfigBuilder {
    private final ProguardConfigBuilder builder = new ProguardConfigBuilder();
    private String PROGUARD_TEMPLATE = "build-tools/phet-build/templates/proguard-template.pro";

    public void reset() {
        builder.reset();
    }

    public void setDestJar( File outputJar ) {
        builder.setOutputJar( outputJar );
    }

    public void setPhetProject( PhetProject project ) {
        builder.setName( project.getName() );
        builder.setProguardTemplate( new File( project.getAntBaseDir(), PROGUARD_TEMPLATE ) );
        builder.setInputJars( prepend( project.getAllJarFiles(), project.getJarFile() ) );
        builder.setProguardOutputFile( new File( project.getAntOutputDir(), project.getName() + ".pro" ) );
//        builder.setMainClasses( project.getAllMainClasses() );
        builder.setMainClasses( getAllMainClasses( project ) );
    }

    private String[] getAllMainClasses( PhetProject project ) {
        ArrayList list = new ArrayList( Arrays.asList( project.getAllMainClasses() ) );
        list.add( PhetBuildCommand.getMainLauncherClassName() );
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
