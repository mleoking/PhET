// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScale;
import static edu.colorado.phet.fractions.FractionsResources.Images.SCALE;

/**
 * @author Sam Reid
 */
@Data public class Scale {
    private static final BufferedImage scale = multiScale( SCALE, 0.5 );
    public final ImmutableVector2D position;

    public PNode toNode() {
        return new PImage( scale ) {{
            setOffset( position.getX(), position.getY() );
        }};
    }

    public ImmutableVector2D center() { return position.plus( scale.getWidth() / 2, scale.getHeight() / 2 ); }
}
