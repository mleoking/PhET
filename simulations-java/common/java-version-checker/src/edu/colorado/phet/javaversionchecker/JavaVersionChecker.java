package edu.colorado.phet.javaversionchecker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;

public class JavaVersionChecker {
    public static void main( String[] args ) throws IOException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        String javaVersion = System.getProperty( "java.version" );
        System.out.println( "javaVersion = " + javaVersion );
        if ( javaVersion.startsWith( "1.4" ) || javaVersion.startsWith( "1.3" ) || true ) {
            showJavaVersionErrorDialog();
        }
        else {
            launchApplication( args );
        }
    }

    private static void launchApplication( String[] args ) throws ClassNotFoundException, InvocationTargetException, IOException, NoSuchMethodException, IllegalAccessException {
        JARLauncher.main( args );
    }

    private static void showJavaVersionErrorDialog() {
        JOptionPane.showMessageDialog( null, "PhET requires Java 1.5+.\nDetected version: " + System.getProperty( "java.version" ) + "\n\nPlease visit java.com to get the latest version of Java" );
    }
}
