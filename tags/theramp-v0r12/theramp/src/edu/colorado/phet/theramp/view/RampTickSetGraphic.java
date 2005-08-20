/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 4:06:59 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */

public class RampTickSetGraphic extends PNode {
    private SurfaceGraphic surfaceGraphic;
    private ArrayList tickGraphics = new ArrayList();

    public RampTickSetGraphic( SurfaceGraphic surfaceGraphic ) {
        super();
        this.surfaceGraphic = surfaceGraphic;
        for( int i = 0; i <= surfaceGraphic.getSurface().getLength(); i++ ) {
            double x = i;
            addTickGraphic( x );
        }
        //setIgnoreMouse( true );
    }

    private void addTickGraphic( double x ) {
        RampTickSetGraphic.TickGraphic tickGraphic = new TickGraphic( x );
        tickGraphics.add( tickGraphic );
        addChild( tickGraphic );
    }

    public void update() {
        for( int i = 0; i < tickGraphics.size(); i++ ) {
            TickGraphic tickGraphic = (TickGraphic)tickGraphics.get( i );
            tickGraphic.update();
        }
    }

    public class TickGraphic extends PNode {
        private double x;
        private PPath phetShapeGraphic;

        public TickGraphic( double x ) {
            super();
            this.x = x;
//            phetShapeGraphic = new PhetShapeGraphic( component, new Line2D.Double( 0, 0, 0, 7 ), new BasicStroke( 2 ), Color.black );
            phetShapeGraphic = new PPath( new Line2D.Double( 0, 0, 0, 7 ) );
            phetShapeGraphic.setStroke( new BasicStroke( 2 ) );
            phetShapeGraphic.setPaint( Color.black );
            addChild( phetShapeGraphic );
            update();
        }

        public void update() {
            Point2D loc = surfaceGraphic.getSurface().getLocation( x );

            setTransform( new AffineTransform() );
            setOffset( surfaceGraphic.getViewLocation( loc ) );
            rotate( surfaceGraphic.getViewAngle() );
        }
    }
}
