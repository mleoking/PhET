/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 2, 2006
 * Time: 12:01:51 AM
 * Copyright (c) Jul 2, 2006 by Sam Reid
 */

public class DoorknobNode extends PNode {
    private PImage image;

    public DoorknobNode() {
        image = PImageFactory.create( "images/knob.jpg" );
        addChild( image );
    }

    public Point2D getGlobalKnobPoint() {
        return new Point2D.Double( getOffset().getX() + image.getFullBounds().getWidth() / 2.0, getOffset().getY() + image.getFullBounds().getHeight() * 0.35 );
    }
}
