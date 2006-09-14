/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicInductorGraphic extends SchematicOscillateGraphic {
    private PhetShapeGraphic leftBranch;
    private PhetShapeGraphic rightBranch;

    public SchematicInductorGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform, double wireThickness ) {
        super( parent, component, transform, wireThickness );
        leftBranch = new PhetShapeGraphic( parent, null, new BasicStroke( 6.0f ), Color.black );
        rightBranch = new PhetShapeGraphic( parent, null, new BasicStroke( 6.0f ), Color.black );
        changed();
        setVisible( true );
    }

    protected double getY( double x, double dist, double fracDistToStartSine, double omega ) {
        return Math.abs( 15 * Math.sin( ( x - dist * fracDistToStartSine ) * omega * 1.5 ) );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if( leftBranch != null ) {
            leftBranch.setVisible( visible );
        }
    }

    protected void changed() {
        super.changed();
        if( leftBranch != null ) {
            //draw a circle around the resistor part.
            Point2D catPoint = super.getCatPoint();
            Point2D anoPoint = getAnoPoint();
            Vector2D.Double v = new Vector2D.Double( catPoint, anoPoint );
            AbstractVector2D s = v.getScaledInstance( super.getFracDistToStartSine() );
            Point2D dst = s.getDestination( catPoint );
            leftBranch.setShape( new Line2D.Double( catPoint, dst ) );

            Point2D d2 = s.getScaledInstance( -1 ).getDestination( anoPoint );
            rightBranch.setShape( new Line2D.Double( anoPoint, d2 ) );
        }
    }

    public void paint( Graphics2D g ) {
        leftBranch.paint( g );
        rightBranch.paint( g );
        super.paint( g );
    }

    public boolean contains( int x, int y ) {
        return super.contains( x, y ) || leftBranch.getShape().contains( x, y ) || rightBranch.getShape().contains( x, y );
    }
}
