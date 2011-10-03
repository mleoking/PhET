package com.pixelzoom.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Shows all available fonts.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShowAvailableFonts {

    public static void main( String[] args ) {

        // get a set of all available fonts
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font fonts[] = ge.getAllFonts();

        // print them
        String[][] rowData = new String[fonts.length][3];
        for ( int i = 0; i < fonts.length; i++ ) {
            rowData[i][0] = fonts[i].getFontName();
            rowData[i][1] = fonts[i].getFamily();
            System.out.println( fonts[i].getFontName() + "," + fonts[i].getFamily() );
        }
    }
}
