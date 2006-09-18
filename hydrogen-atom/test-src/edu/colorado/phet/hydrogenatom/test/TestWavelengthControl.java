/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.test;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.control.WavelengthControl;
import edu.colorado.phet.piccolo.PhetPCanvas;


public class TestWavelengthControl extends JFrame {

    public static void main( String[] args ) {
        JFrame frame = new TestWavelengthControl( "WavelengthControl test harness");
        frame.show();
    }
    
    public TestWavelengthControl( String title ) {
        super( title );
        
        Color uvTrackColor = Color.DARK_GRAY;
        Color uvLabelColor = Color.WHITE;
        Color irTrackColor = Color.BLACK;
        Color irLabelColor = Color.WHITE;
        
        PhetPCanvas canvas = new PhetPCanvas();

        final double xOffset = 100;
        double yOffset = 50;
        final double ySpacing = 100;
        
        // Visible range (default)
        WavelengthControl c1 = new WavelengthControl( canvas );
        canvas.getLayer().addChild( c1 );
        c1.setOffset( xOffset, yOffset );
        yOffset += ySpacing;
        
        // Visible range (specified)
        WavelengthControl c2 = new WavelengthControl( canvas, VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH  );
        canvas.getLayer().addChild( c2 );
        c2.setOffset( xOffset, yOffset );
        yOffset += ySpacing;
        
        // UV & IR with default colors
        WavelengthControl c3 = new WavelengthControl( canvas, 90, 900 );
        canvas.getLayer().addChild( c3 );
        c3.setOffset( xOffset, yOffset );
        yOffset += ySpacing;
        
        // UV & IR with custom colors, fonts, etc
        WavelengthControl c4 = new WavelengthControl( canvas, 90, 900, uvTrackColor, uvLabelColor, irTrackColor, irLabelColor );
        c4.setWavelength( 300 );
        c4.setTextFieldColors( Color.RED, Color.BLACK );
        c4.setUnitsForeground( Color.BLUE );
        c4.setTextFieldFont( new Font( "Lucida Sans", Font.PLAIN, 20 ) );
        c4.setUnitsFont( new Font( "Lucida Sans", Font.ITALIC, 14 ) );
        c4.setTextFieldColumns( 5 );
        canvas.getLayer().addChild( c4 );
        c4.setOffset( xOffset, yOffset );
        yOffset += ySpacing;
        
        // UV & IR with no room for labels
        WavelengthControl c5 = new WavelengthControl( canvas, VisibleColor.MIN_WAVELENGTH - 10, VisibleColor.MAX_WAVELENGTH + 10 );
        canvas.getLayer().addChild( c5 );
        c5.setOffset( xOffset, yOffset );
        yOffset += ySpacing;
        
        // UV only
        WavelengthControl c6 = new WavelengthControl( canvas, 90, VisibleColor.MAX_WAVELENGTH );
        canvas.getLayer().addChild( c6 );
        c6.setOffset( xOffset, yOffset );
        yOffset += ySpacing;
        
        // IR only
        WavelengthControl c7 = new WavelengthControl( canvas, VisibleColor.MIN_WAVELENGTH, 900 );
        canvas.getLayer().addChild( c7 );
        c7.setOffset( xOffset, yOffset );
        yOffset += ySpacing;
        
        getContentPane().add( canvas );
        setSize( 600, (int)yOffset );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }

}
