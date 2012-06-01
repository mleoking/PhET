// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import lombok.Data;

import java.awt.image.BufferedImage;

import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScale;
import static edu.colorado.phet.fractions.FractionsResources.Images.SCALE;

/**
 * Immutable model object for a scale upon which fractions can be placed.
 *
 * @author Sam Reid
 */
@Data public class Scale {
    private static final BufferedImage scale = multiScale( SCALE, 0.5 );
    public final Vector2D position;

    public PNode toNode() {
        return new PImage( scale ) {{
            setOffset( position.getX(), position.getY() );
        }};
    }

    public Vector2D getCenter() { return position.plus( scale.getWidth() / 2, scale.getHeight() / 2 ); }

    private Vector2D getAttachmentPoint() { return getCenter().plus( 0, -25 ); }

    public Vector2D getAttachmentPoint( MovableFraction fraction ) {
        return getAttachmentPoint( this, fraction );
    }

    private static final Cache<MovableFraction, Double> getHeight = new Cache<MovableFraction, Double>( new F<MovableFraction, Double>() {
        @Override public Double f( final MovableFraction movableFraction ) {
            return movableFraction.toNode().getFullBounds().getHeight();
        }
    } );

    public static Vector2D getAttachmentPoint( Scale scale, MovableFraction fraction ) {
        getHeight.checkAndClearCache();
        return scale.getAttachmentPoint().plus( 0, -getHeight.f( fraction ) / 2 );
    }
}