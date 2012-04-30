// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images;

/**
 * Shows the grass above the dirt
 *
 * @author Sam Reid
 */
public class GrassNode {
    public static PhetPPath GrassNode( ModelViewTransform transform, final double x0, final double x1 ) {
        final BufferedImage grassTexture = BufferedImageUtils.multiScaleToHeight( Images.GRASS_TEXTURE, 15 );
        Rectangle2D grassRectangle = transform.modelToView( new Rectangle2D.Double( x0, 0, x1 - x0, 0.12 ) ).getBounds2D();
        ImmutableVector2D origin = transform.modelToView( ImmutableVector2D.ZERO );
        return new PhetPPath( grassRectangle, new TexturePaint( grassTexture, new Rectangle2D.Double( origin.getX(), origin.getY(), grassTexture.getWidth(), grassTexture.getHeight() ) ) );
    }
}