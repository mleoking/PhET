/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 18, 2005
 * Time: 11:28:29 PM
 * Copyright (c) Dec 18, 2005 by Sam Reid
 */

public class IntensityReader extends PNode {
    private SimpleWavefunctionGraphic simpleWavefunctionGraphic;
    private PText crosshairs;
    private PText readout;

    public IntensityReader( SimpleWavefunctionGraphic simpleWavefunctionGraphic ) {
        this.simpleWavefunctionGraphic = simpleWavefunctionGraphic;
        crosshairs = new PText( "x" );
        crosshairs.setFont( new Font( "Lucida Sans", Font.BOLD, 24 ) );
        crosshairs.setTextPaint( new GradientPaint( new Point2D.Double(), Color.blue, new Point2D.Double( 0, crosshairs.getHeight() ), Color.red ) );
        addInputEventListener( new PDragEventHandler() );
        addChild( crosshairs );

        readout = new PText( "Value=???" );
        readout.setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );
        readout.setTextPaint( Color.blue );
        addChild( readout );
        readout.setOffset( 0, crosshairs.getHeight() + 5 );

        update();
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } ).start();
    }

    private void update() {
        //get the coordinate in the wavefunctiongraphic.
        Point2D location = crosshairs.getGlobalTranslation();
//        crosshairs.localToGlobal( location );
        simpleWavefunctionGraphic.globalToLocal( location );
//        simpleWavefunctionGraphic.localToParent( location );
        Point gc = simpleWavefunctionGraphic.getGridCoordinates( location );
        Dimension d = simpleWavefunctionGraphic.getCellDimensions();
        readout.setText( "Location=" + gc.x / d.width + ", " + gc.y / d.height );
//        double valueAtCursor = simpleWavefunctionGraphic.getWavefunction().
    }
}
