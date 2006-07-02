/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import org.cove.jade.primitives.CircleParticle;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 6:03:05 PM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */
class CircleParticleForElectron extends CircleParticle {
    private ElectronNodeJade electronNode;

    /**
     * Instantiates a CircleParticle. (px,py) = center, r = radius in pixels.
     */
    public CircleParticleForElectron( ElectronNodeJade electronNode ) {
        super( electronNode.getOffset().getX(), electronNode.getOffset().getY(), electronNode.getRadius() );
        this.electronNode = electronNode;
    }

    public void update() {
        electronNode.setOffset( curr.x - electronNode.getFullBounds().getWidth() / 2, curr.y - electronNode.getFullBounds().getHeight() / 2 );
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double( curr.x, curr.y );
    }

    public void setLocation( Point2D location ) {
        super.setPos( location.getX(), location.getY() );
    }

    public ElectronNodeJade getElectronNode() {
        return electronNode;
    }
}
