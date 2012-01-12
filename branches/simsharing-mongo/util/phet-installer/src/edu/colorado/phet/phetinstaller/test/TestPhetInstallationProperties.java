/* Copyright 2009, University of Colorado */

package edu.colorado.phet.phetinstaller.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Tests the phet-installation.properties file that is written by the PhET installer.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPhetInstallationProperties {
    
    public static void main( String[] args ) {
        
        // command line args
        File file = null;
        if ( args.length > 0 ) {
            file = new File( args[0] );
        }
        else {
            file = promptForFile();
            if ( file == null ) {
                System.exit( 1 );
            }
        }
        
        // read the properties file
        Properties properties = new Properties();
        try {
            properties.load( new FileInputStream( file ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        
        // timestamp right now
        Date now = new Date();
        int nowSeconds = (int)( now.getTime() / 1000 ); // convert from ms to sec
        System.out.println( "now=" + nowSeconds + " -> " + now.toString() );
        
        // print properties, do some conversion tests
        Set keys = properties.keySet();
        Iterator i = keys.iterator();
        while ( i.hasNext() ) {

            Object key = (Object) i.next();
            Object value = properties.get( key );
            System.out.print( "property: " + key + "=" + value );

            // conversion test
            if ( ( (String) key ).equals( "install.date.epoch.seconds" ) ) {
                int seconds = Integer.valueOf( (String) value ).intValue();
                Date date = new Date( seconds * 1000L ); // convert from sec to ms
                System.out.print( " -> " + date.toString() );
            }
            
            // conversion test
            if ( ( (String) key ).equals( "installer.creation.date.epoch.seconds" ) ) {
                int seconds = Integer.valueOf( (String) value ).intValue();
                Date date = new Date( seconds * 1000L ); // convert from sec to ms
                System.out.print( " -> " + date.toString() );
            }
            
            System.out.println();
        }
    }
    
    private static File promptForFile() {
        
        // properties file filter
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith( ".properties" );
            }
            public String getDescription() {
                return null;
            }
        };
        
        // prompt using file chooser
        JFileChooser fc = new JFileChooser( System.getProperty( "user.home" ) );
        fc.setFileFilter( filter );
        fc.showOpenDialog(null);
        return fc.getSelectedFile();
    }
}
