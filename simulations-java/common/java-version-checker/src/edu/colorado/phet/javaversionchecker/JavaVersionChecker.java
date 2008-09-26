package edu.colorado.phet.javaversionchecker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.*;

public class JavaVersionChecker {
    public static void main( String[] args ) throws IOException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        String javaVersion = System.getProperty( "java.version" );
        if ( javaVersion.startsWith( "1.4" ) || javaVersion.startsWith( "1.3" ) || javaVersion.startsWith( "1.2" ) || javaVersion.startsWith( "1.1" ) ) {
            showJavaVersionErrorDialog();
            System.exit( 0 );//daemon threads leftover
        }
        else {
            launchApplication( args );
        }
    }

    private static void launchApplication( String[] args ) throws ClassNotFoundException, InvocationTargetException, IOException, NoSuchMethodException, IllegalAccessException {

        //access through reflection so this class can be compiled independently of anything
        Class c = Class.forName( "edu.colorado.phet.common.phetcommon.application.JARLauncher" );

        Method method = c.getMethod( "main", new Class[]{args.getClass()} );
        method.invoke( null, new Object[]{args} );
    }

    private static void showJavaVersionErrorDialog() {
        JOptionPane.showMessageDialog( null, "PhET requires Java 1.5+.\nDetected version: " + System.getProperty( "java.version" ) + "\n\nPlease visit java.com to get the latest version of Java." );
    }
}
