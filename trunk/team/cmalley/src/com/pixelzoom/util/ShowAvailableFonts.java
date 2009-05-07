package com.pixelzoom.util;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Shows all available fonts.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShowAvailableFonts {

    private static final boolean PRINT_TO_STDOUT = true;
    
    public static void main( String[] args ) {

        // get a set of all available fonts
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font fonts[] = ge.getAllFonts();
        
        // table data
        String[][] rowData = new String[ fonts.length ][3];
        if ( PRINT_TO_STDOUT ) {
            System.out.println("font name,family");
        }
        for ( int i = 0; i < fonts.length; i++ ) {
            rowData[i][0] = fonts[i].getFontName();
            rowData[i][1] = fonts[i].getFamily();
            if ( PRINT_TO_STDOUT ) {
                System.out.println( fonts[i].getFontName() + "," + fonts[i].getFamily() );
            }
        }
        Arrays.sort( rowData, new MutidimensionalArrayComparator() );

        // table in a scroll pane
        String[] colName = { "Face name", "Family name" };
        JTable table = new JTable( rowData, colName );
        JScrollPane pane = new JScrollPane( table );

        // display in a frame
        JFrame frame = new JFrame( "Fonts available" );
        frame.setContentPane( pane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        centerOnScreen( frame );
        frame.setVisible( true );
    }
    
    private static class MutidimensionalArrayComparator implements Comparator {

        public int compare( Object obj1, Object obj2 ) {
            int result = 0;
            String[] array1 = (String[]) obj1;
            String[] array2 = (String[]) obj2;
            // sort until we find fields that are not equal
            for ( int i = 0; i < array1.length; i++ ) {
                result = array1[i].compareTo( array2[i] );
                if ( result != 0 ) {
                    break;
                }
            }
            return result;
        }
    }

    private static void centerOnScreen( Window window ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int x = (int) ( screenSize.getWidth() / 2 - window.getWidth() / 2 );
        int y = (int) ( screenSize.getHeight() / 2 - window.getHeight() / 2 );
        window.setLocation( x, y );
    }
}
