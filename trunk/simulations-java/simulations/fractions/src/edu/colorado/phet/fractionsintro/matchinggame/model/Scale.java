// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

import java.awt.image.BufferedImage;

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

    public Vector2D center() { return position.plus( scale.getWidth() / 2, scale.getHeight() / 2 ); }
}
