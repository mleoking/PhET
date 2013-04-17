/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

/*

Format of a Mac OS X Application Bundle

[Bundle.app]
    Contents/
        Info.plist          <- Comes from info.plist template
        PkgInfo             <- Text file containing the string 'APPL????'

        MacOS/
        Resources/
            [Icon.icns]     <- Icon file referenced in Info.plist

            Java/
                [Jar]       <- The application JAR


 */
public class OSXBundleCommand {
    private static final String PATH_TO_INFO_PLIST_TEMPLATE = "/build-tools/templates/mac-java-app-template.plist";
    private static final String APPLE_CREATOR_CODE = "APPL????";

    private final File baseDir;
    private final File destDir;
    private final File jarFile;
    private final File iconFile;

    private File contentsDir;
    private File resourcesDir;
    private File javaDir;

    public OSXBundleCommand( File baseDir, File destDir, File jarFile, File iconFile ) {
        this.baseDir = baseDir;
        this.destDir = destDir;
        this.jarFile = jarFile;
        this.iconFile = iconFile;
    }

    private void createBundleStructure() {
        destDir.mkdir();

        contentsDir = new File( destDir, "Contents" );

        contentsDir.mkdir();

        new File( contentsDir, "MacOS" ).mkdir();

        resourcesDir = new File( contentsDir, "Resources" );

        resourcesDir.mkdir();

        javaDir = new File( resourcesDir, "Java" );

        javaDir.mkdir();
    }

    private void createPkgInfo() throws IOException {
        File pkgInfo = new File( contentsDir, "PkgInfo" );

        BufferedWriter writer = new BufferedWriter( new FileWriter( pkgInfo ) );

        try {
            writer.write( APPLE_CREATOR_CODE );
        }
        finally {
            writer.close();
        }
    }

    private void copyJar() throws IOException {
        FileUtils.copyTo( jarFile, new File( javaDir, jarFile.getName() ) );
    }

    private void copyIcon() throws IOException {
        FileUtils.copyTo( iconFile, new File( resourcesDir, iconFile.getName() ) );
    }

    public void execute() throws Exception {
        if ( !destDir.getName().toLowerCase().endsWith( ".app" ) ) {
            throw new IllegalArgumentException( "The destination directory must end in .app" );
        }

        createBundleStructure();

        createPkgInfo();

        copyJar();

        copyIcon();

        createInfoPlist();
    }

    private void createInfoPlist() throws IOException {
        File template = new File( baseDir.getAbsolutePath() + PATH_TO_INFO_PLIST_TEMPLATE );

        File infoPlist = new File( contentsDir, "Info.plist" );

        FileUtils.filter( template, infoPlist, new HashMap() );
    }
}
