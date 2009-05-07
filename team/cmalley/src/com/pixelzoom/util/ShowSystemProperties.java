
package com.pixelzoom.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;


/**
 * Shows all System properties and values, sorted by property name.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShowSystemProperties {

    private static final boolean PRINT_TO_STDOUT = true;
    
    public static void main( String[] args ) {
        
        // Get the system properties
        Properties properties = System.getProperties();
        
        // Sort 'em
        Object[] keySet = properties.keySet().toArray();
        List keys = Arrays.asList( keySet );
        Collections.sort( keys );

        // table data
        if ( PRINT_TO_STDOUT ) {
            System.out.println( "property,value" );
        }
        String[][] rowData = new String[keys.size()][2];
        for( int i = 0; i < keys.size(); i++ ) {
            Object key = keys.get( i );
            Object value = properties.get( key );
            if ( PRINT_TO_STDOUT ) {
                System.out.println( key + "," + value );
            }
            rowData[i][0] = key.toString();
            rowData[i][1] = "" + value.toString();
        }
        
        // table in a scroll pane
        String[] colName = { "Property", "Value" };
        JTable table = new JTable( rowData, colName );
        JScrollPane pane = new JScrollPane( table );

        // display in a frame
        JFrame frame = new JFrame( "System properties & default values" );
        frame.setContentPane( pane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        centerOnScreen( frame );
        frame.setVisible( true );
    }

    private static void centerOnScreen( Window window ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int x = (int) ( screenSize.getWidth() / 2 - window.getWidth() / 2 );
        int y = (int) ( screenSize.getHeight() / 2 - window.getHeight() / 2 );
        window.setLocation( x, y );
    }
}
