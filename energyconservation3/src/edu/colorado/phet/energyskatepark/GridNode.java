/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Todo: see EnergyPositionPlotCanvas for an error in the offset.
 */

public class GridNode extends PhetPNode {
    private static final String METERS = "0 meters";

    public GridNode() {
        double minX = 0;
        double minY = 0;
        double maxX = 30;
        double maxY = 30;
        double dx = 1;
        double dy = 1;
        for( double x = minX; x <= maxX; x += dx ) {
            addChild( createXNode( minY, maxY, x ) );
            if( x % 2 == 0 && x <= 10 ) {
                String aText = "" + (int)x;
                if( aText.equals( "0" ) ) {
                    aText = METERS;
                }
                PText text = new PText( aText );
                text.setOffset( x + dx, minY + dy );

                text.setScale( 0.03f );
                text.getTransformReference( true ).scale( 1, -1 );

                addChild( text );
            }
        }
        for( double y = minY; y <= maxY; y += dy ) {
            addChild( createYNode( minX, maxX, y ) );
            if( y % 2 == 0 && y <= 6 && y >= 2 ) {
                String aText = "" + (int)y;
                if( aText.equals( "0" ) ) {
                    aText = METERS;
                }
                PText text = new PText( aText );
                text.setOffset( minX + dx, y + dy );
                text.setScale( 0.03f );
                text.getTransformReference( true ).scale( 1, -1 );
                addChild( text );
            }
        }
        setPickable( false );
        setChildrenPickable( false );
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( false );
        setChildrenPickable( false );
    }

    private PNode createYNode( double minX, double maxX, double y ) {
        PPath child = new PPath( new Line2D.Double( minX, y, maxX, y ) );
        child.setStroke( new BasicStroke( 0.01f ) );
        return child;
    }

    private PNode createXNode( double minY, double maxY, double x ) {
        PPath child = new PPath( new Line2D.Double( x, minY, x, maxY ) );
        child.setStroke( new BasicStroke( 0.01f ) );
        return child;
    }
}
