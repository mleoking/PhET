
package com.pixelzoom.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.*;

/**
 * Shows all UIManager properties and default values, sorted by property name.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShowUIManagerProperties {

    private static final boolean PRINT_TO_STDOUT = true;

    public static void main( String[] args ) {

        UIDefaults defaults = UIManager.getDefaults();

        // sort by property name
        ArrayList sortedKeyList = new ArrayList();
        int i = 0;
        Enumeration e = defaults.keys();
        while ( e.hasMoreElements() ) {
            sortedKeyList.add( e.nextElement() );
        }
        Collections.sort( sortedKeyList );

        // create table data
        if ( PRINT_TO_STDOUT ) {
            System.out.println( "property,value" );
        }
        String[][] rowData = new String[defaults.size()][2];
        Iterator it = sortedKeyList.iterator();
        int row = 0;
        while ( it.hasNext() ) {
            Object key = it.next();
            rowData[row][0] = key.toString();
            rowData[row][1] = "" + defaults.get( key );
            if ( PRINT_TO_STDOUT ) {
                System.out.println( rowData[row][0] + "," + rowData[row][1] );
            }
            row++;
        }

        // table in a scroll pane
        String[] colName = { "Property", "Value" };
        JTable table = new JTable( rowData, colName );
        JScrollPane pane = new JScrollPane( table );

        // display in a frame
        JFrame frame = new JFrame( "UIManager properties & default values" );
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
