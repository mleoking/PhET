/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EC3Canvas;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.LucidaSansFont;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 27, 2005
 * Time: 9:29:24 PM
 * Copyright (c) Dec 27, 2005 by Sam Reid
 */

public class ZeroPointPotentialGraphic extends PhetPNode {
    private EC3Canvas canvas;

    public ZeroPointPotentialGraphic( final EC3Canvas canvas ) {
        this.canvas = canvas;
        PPath path = new PPath( new Line2D.Double( 0, 0, 5000, 0 ) );
        path.setStroke( new BasicStroke( 3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3, new float[]{16, 8}, 0 ) );
        path.setStrokePaint( Color.red );
        addChild( path );

        PText text = new PText( EnergySkateParkStrings.getString( "pe.0.at.this.dotted.line" ) );
        text.setFont( new LucidaSansFont( 16, true ) );
        text.setTextPaint( new Color( 255, 60, 80 ) );
        addChild( text );
        text.setOffset( 2, 2 );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                double dy = event.getCanvasDelta().getHeight();
                PDimension dim = new PDimension( 0, dy );
                canvas.getPhetRootNode().screenToWorld( dim );
                canvas.getEnergyConservationModel().translateZeroPointPotentialY( dim.getHeight() );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    public void setZeroPointPotential( double y ) {
        Point2D.Double pt = new Point2D.Double( 0, y );
        canvas.getPhetRootNode().worldToScreen( pt );
        double viewY = pt.getY();
        setOffset( 0, viewY );
    }
}
