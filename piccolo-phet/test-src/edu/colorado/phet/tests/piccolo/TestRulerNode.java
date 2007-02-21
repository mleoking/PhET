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
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.piccolo.nodes.RulerNode;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;

public class TestRulerNode {
    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        JFrame frame = new JFrame();
        frame.setContentPane( pCanvas );
        frame.setSize( 800, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        String[] readings;
        
        // Ruler that reads 0-10 nm.
        readings = new String[11];
        for ( int i = 0; i < readings.length; i++ ) {
            readings[i] = String.valueOf( i );
        }
        RulerNode ruler1 = new RulerNode( readings, "nm", 300, 40 );
        pCanvas.getLayer().addChild( ruler1 );
        
        // Ruler that reads 0-20 nm.
        // This ruler is twice as long as the above ruler,
        // so I would expect the tick spacing to be the same.
        // But the tick spacing is NOT the same.
        // The problem is caused by RulerNode.horizontalInset.
        readings = new String[21];
        for ( int i = 0; i < readings.length; i++ ) {
            readings[i] = String.valueOf( i );
        }
        RulerNode ruler2 = new RulerNode( readings, "nm", 600, 40 );
        pCanvas.getLayer().addChild( ruler2 );
        
        ruler1.setOffset( 0, 0 );
        ruler2.setOffset( 0, ruler1.getFullBounds().getHeight() );

        frame.show();
    }
    
}
