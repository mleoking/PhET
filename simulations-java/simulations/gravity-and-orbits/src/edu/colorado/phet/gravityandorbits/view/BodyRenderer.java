package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsResources;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This is the PNode that renders the content of a physical body, such as a planet or space station
 *
 * @author Sam Reid
 */
public abstract class BodyRenderer extends PNode {
    protected Body body;

    public BodyRenderer( Body body ) {
        this.body = body;
    }

    public abstract void setDiameter( double viewDiameter );

    public static class SphereRenderer extends BodyRenderer {

        private SphericalNode sphereNode;

        public SphereRenderer( Body body, double viewDiameter ) {
            super( body );
            sphereNode = new SphericalNode( viewDiameter, createPaint( viewDiameter ), false );
            addChild( sphereNode );
        }

        @Override
        public void setDiameter( double viewDiameter ) {
            sphereNode.setDiameter( viewDiameter );
            sphereNode.setPaint( createPaint( viewDiameter ) );
        }

        private Paint createPaint( double diameter ) {// Create the gradient paint for the sphere in order to give it a 3D look.
            Paint spherePaint = new RoundGradientPaint( diameter / 8, -diameter / 8,
                                                        body.getHighlight(),
                                                        new Point2D.Double( diameter / 4, diameter / 4 ),
                                                        body.getColor() );
            return spherePaint;
        }
    }

    public static class ImageRenderer extends BodyRenderer {
        private final PImage imageNode;
        private double viewDiameter;

        public ImageRenderer( Body body, double viewDiameter, final String imageName ) {
            super( body );

            imageNode = new PImage( BufferedImageUtils.multiScaleToWidth( GravityAndOrbitsResources.getImage( imageName ), 50 ) );
            addChild( imageNode );
            this.viewDiameter = viewDiameter;
            updateViewDiameter();
        }

        @Override
        public void setDiameter( double viewDiameter ) {
            this.viewDiameter = viewDiameter;
            updateViewDiameter();
        }

        private void updateViewDiameter() {
            imageNode.setTransform( new AffineTransform() );
            final double scale = viewDiameter / imageNode.getFullBounds().getWidth();
            imageNode.setScale( scale );
            //Make sure the image is centered on the body's center
            imageNode.translate( -imageNode.getFullBounds().getWidth() / 2 / scale, -imageNode.getFullBounds().getHeight() / 2 / scale );
        }
    }
}
