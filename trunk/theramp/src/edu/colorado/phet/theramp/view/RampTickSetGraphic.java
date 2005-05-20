/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

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

public class RampTickSetGraphic extends CompositePhetGraphic {
    private RampGraphic rampGraphic;
    private ArrayList tickGraphics = new ArrayList();

    public RampTickSetGraphic( RampGraphic rampGraphic ) {
        super( rampGraphic.getComponent() );
        this.rampGraphic = rampGraphic;
        for( int i = 0; i <= rampGraphic.getSurface().getLength(); i++ ) {
            double x = i;
            addTickGraphic( x );
        }
        setIgnoreMouse( true );
    }

    private void addTickGraphic( double x ) {
        RampTickSetGraphic.TickGraphic tickGraphic = new TickGraphic( getComponent(), x );
        tickGraphics.add( tickGraphic );
        addGraphic( tickGraphic );
    }

    public void update() {
        for( int i = 0; i < tickGraphics.size(); i++ ) {
            TickGraphic tickGraphic = (TickGraphic)tickGraphics.get( i );
            tickGraphic.update();
        }
    }

    public class TickGraphic extends CompositePhetGraphic {
        private double x;
        private PhetShapeGraphic phetShapeGraphic;

        public TickGraphic( Component component, double x ) {
            super( component );
            this.x = x;
            phetShapeGraphic = new PhetShapeGraphic( component, new Line2D.Double( 0, 0, 0, 7 ), new BasicStroke( 2 ), Color.black );
            addGraphic( phetShapeGraphic );
            update();
        }

        public void update() {
            Point2D loc = rampGraphic.getSurface().getLocation( x );
            setLocation( rampGraphic.getViewLocation( loc ) );
            setTransform( new AffineTransform() );
            rotate( rampGraphic.getViewAngle() );
        }
    }
}
