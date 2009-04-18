package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

public class BigVanillaBunny extends PNode {
    public BigVanillaBunny() {
        PImage child = NaturalSelectionResources.getImageNode( "big_bunny.png" );

        addChild( child );
    }

    public Point2D getColorPosition() {
        return new Point2D.Double( 58 + getOffset().getX(), 75 + getOffset().getY() );
    }

    public Point2D getTeethPosition() {
        return new Point2D.Double( 70 + getOffset().getX(), 55 + getOffset().getY() );
    }

    public Point2D getTailPosition() {
        return new Point2D.Double( 3 + getOffset().getX(), 84 + getOffset().getY() );
    }

    public Point2D getEarsPosition() {
        return new Point2D.Double( 39 + getOffset().getX(), 17 + getOffset().getY() );
    }

    public Point2D getEyePosition() {
        return new Point2D.Double( 58 + getOffset().getX(), 41 + getOffset().getY() );
    }
}