package edu.colorado.phet.build.util;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;

/**
 * Created by: Sam
 * Feb 9, 2008 at 2:51:15 PM
 */
public class CheckNamingConventions {
    public static final File basedir = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java" );

    public static void main( String[] args ) throws IOException {

        PhetProject[] p = PhetProject.getAllProjects( basedir );
        for ( int i = 0; i < p.length; i++ ) {
            checkNamingConventions( p[i] );
        }
    }

    private static void checkNamingConventions( PhetProject project ) throws IOException {
        //check that properties file exists with correct name
        assertFileExists( project.getBuildPropertiesFile() );
        //check that data dir exists with correct name
        assertFileExists( project.getDataDirectory() );

        PhetProjectFlavor[] f = project.getFlavors();
        for ( int i = 0; i < f.length; i++ ) {
            String className = f[i].getMainclass();
            assertClassNameCorrect( project, f[i], className );
            String path = className.replace( '.', '/' ) + ".java";
            assertFileExists( new File( project.getProjectDir(), "src/" + path ) );
        }

        //optionally make sure each source path appears in IML file (IntelliJ Idea only)
        final File file = new File( basedir, "intellij-idea-all.iml" );
        if ( file.exists() ) {
            String s = FileUtils.loadFileAsString( file );
            int index = s.indexOf( "" + project.getName() + "/src" );
            if ( index < 0 ) {
                System.out.println( "Missing source: " + project.getName() );
            }
        }
    }

    private static void assertClassNameCorrect( PhetProject project, PhetProjectFlavor phetProjectFlavor, String className ) {
        final String s = phetProjectFlavor.getJavaStyleName();

        String correctClassName = "edu.colorado.phet." + project.getPackageName() + "." + s + "Application";
        if ( !className.equals( correctClassName ) ) {
            System.out.println( "Wrong class name. expected: " + correctClassName + ", found: " + className );
        }
    }

    private static void assertFileExists( File propertyFile ) {
        if ( !propertyFile.exists() ) {
            System.out.println( "File doesn't exist: " + propertyFile.getAbsolutePath() );
        }
//        else {
//            System.out.println( "File exists: " + propertyFile.getAbsolutePath() );
//        }
    }
}
