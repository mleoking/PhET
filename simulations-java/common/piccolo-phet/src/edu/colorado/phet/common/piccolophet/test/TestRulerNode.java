/*
* The Physics Education Technology (PhET) project provides 
* a suite of interactive educational simulations. 
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or 
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
* 
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.piccolophet.test;

import java.awt.Font;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.PCanvas;

public class TestRulerNode {

    public static void main( String[] args ) {

        PCanvas pCanvas = new PCanvas();
        JFrame frame = new JFrame();
        frame.setContentPane( pCanvas );
        frame.setSize( 800, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        int distanceBetweenFirstAndLastTick = 350;
        int rulerInsetWidth = 14;
        int rulerHeight = 40;
        String[] majorTickLabels;
        Font majorTickFont = new PhetFont( Font.PLAIN, 14 );
        String units = "nm";
        Font unitsFont = new PhetFont( Font.PLAIN, 10 );
        int numMinorTicksBetweenMajors = 4;
        int majorTickHeight = 10;
        int minorTickHeight = 6;

        // Ruler that reads 0-10 nm.
        majorTickLabels = new String[11];
        for ( int i = 0; i < majorTickLabels.length; i++ ) {
            majorTickLabels[i] = String.valueOf( i );
        }
        RulerNode ruler1 = new RulerNode( distanceBetweenFirstAndLastTick, rulerInsetWidth, rulerHeight,
                                          majorTickLabels, majorTickFont, units, unitsFont,
                                          numMinorTicksBetweenMajors, majorTickHeight, minorTickHeight );
        pCanvas.getLayer().addChild( ruler1 );

        // Ruler that reads 0-20 nm.
        // Units are placed next to "10" label.
        // This ruler is twice as long, but the tick spacing should be identical to ruler1 above.
        majorTickLabels = new String[21];
        for ( int i = 0; i < majorTickLabels.length; i++ ) {
            majorTickLabels[i] = String.valueOf( i );
        }
        RulerNode ruler2 = new RulerNode( 2 * distanceBetweenFirstAndLastTick, rulerInsetWidth, rulerHeight,
                                          majorTickLabels, majorTickFont, units, unitsFont,
                                          numMinorTicksBetweenMajors, majorTickHeight, minorTickHeight );
        ruler2.setUnitsAssociatedMajorTickLabel( "10" );
        pCanvas.getLayer().addChild( ruler2 );

        // Test the simplified constructor
        units = "ft";
        numMinorTicksBetweenMajors = 2;
        int fontSize = 12;
        RulerNode ruler3 = new RulerNode( 2 * distanceBetweenFirstAndLastTick, rulerHeight, majorTickLabels, units, numMinorTicksBetweenMajors, fontSize );
        pCanvas.getLayer().addChild( ruler3 );

        // Test boundary conditions (null units, no minor ticks)
        units = null;
        numMinorTicksBetweenMajors = 0;
        RulerNode ruler4 = new RulerNode( 2 * distanceBetweenFirstAndLastTick, rulerHeight, majorTickLabels, units, numMinorTicksBetweenMajors, fontSize );
        pCanvas.getLayer().addChild( ruler4 );

        // Vertically align left edges of rulers
        int x = 20;
        int y = 50;
        int yspace = 5;
        ruler1.setOffset( x, y );
        ruler2.setOffset( x, ruler1.getFullBounds().getY() + ruler1.getFullBounds().getHeight() + yspace );
        ruler3.setOffset( x, ruler2.getFullBounds().getY() + ruler2.getFullBounds().getHeight() + yspace );
        ruler4.setOffset( x, ruler3.getFullBounds().getY() + ruler3.getFullBounds().getHeight() + yspace );

        frame.show();
    }

}
