// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This is the PNode that renders the content of a physical body, such as a planet or space station.  This component is separate from
 * BodyNode since it is used to create icons.  It is also used to be able to switch between rendering types (i.e. image vs cartoon sphere) without
 * changing any other characteristics of the PNode.
 *
 * @author Sam Reid
 */
public abstract class BodyRenderer extends PNode {
    private IBodyColors body;

    public BodyRenderer( IBodyColors body ) {
        this.body = body;
    }

    public IBodyColors getBody() {
        return body;
    }

    public abstract void setDiameter( double viewDiameter );

    /**
     * This SwitchableBodyRenderer displays one representation when the object is at a specific mass, and a different renderer
     * otherwise.  This is so that (e.g.) the planet can be drawn with an earth image when its mass is equal to earth mass
     * or otherwise drawn as a sphere with a gradient paint.
     */
    public static class SwitchableBodyRenderer extends BodyRenderer {

        private final BodyRenderer targetBodyRenderer;
        private final BodyRenderer defaultBodyRenderer;

        public SwitchableBodyRenderer( final Body body, final double targetMass, final BodyRenderer targetBodyRenderer, final BodyRenderer defaultBodyRenderer ) {
            super( body );
            this.targetBodyRenderer = targetBodyRenderer;
            this.defaultBodyRenderer = defaultBodyRenderer;
            body.getMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    addChild( ( body.getMass() == targetMass ? targetBodyRenderer : defaultBodyRenderer ) );
                }
            } );
        }

        @Override
        public void setDiameter( double viewDiameter ) {
            targetBodyRenderer.setDiameter( viewDiameter );
            defaultBodyRenderer.setDiameter( viewDiameter );
        }
    }

    /**
     * Render a SphericalNode for the body.
     */
    public static class SphereRenderer extends BodyRenderer {

        private SphericalNode sphereNode;

        public SphereRenderer( final Color color, final Color highlight, double viewDiameter ) {
            this( new IBodyColors() {
                public Color getHighlight() {
                    return highlight;
                }

                public Color getColor() {
                    return color;
                }
            }, viewDiameter );
        }

        public SphereRenderer( IBodyColors body, double viewDiameter ) {
            super( body );
            //Buffer the sphere node for improved performance (JProfiler reported 80% time spent in rendering before buffering, 15% after)
            sphereNode = new SphericalNode( viewDiameter, createPaint( viewDiameter ), true );
            addChild( sphereNode );
        }

        @Override
        public void setDiameter( double viewDiameter ) {
            sphereNode.setDiameter( viewDiameter );
            sphereNode.setPaint( createPaint( viewDiameter ) );
        }

        private Paint createPaint( double diameter ) {// Create the gradient paint for the sphere in order to give it a 3D look.
            Paint spherePaint = new RoundGradientPaint( diameter / 8, -diameter / 8,
                                                        getBody().getHighlight(),
                                                        new Point2D.Double( diameter / 4, diameter / 4 ),
                                                        getBody().getColor() );
            return spherePaint;
        }
    }

    //Adds triangle edges to the sun to make it look more recognizable
    public static class SunRenderer extends SphereRenderer {

        private final PhetPPath twinkles = new PhetPPath( Color.yellow );
        private final int numSegments;
        private final Function1<Double, Double> twinkleRadius;

        public SunRenderer( IBodyColors body, double viewDiameter, int numSegments, Function1<Double, Double> twinkleRadius ) {
            super( body, viewDiameter );
            this.numSegments = numSegments;
            this.twinkleRadius = twinkleRadius;
            addChild( twinkles );
            twinkles.moveToBack();
            setDiameter( viewDiameter );
        }

        @Override
        public void setDiameter( double viewDiameter ) {
            super.setDiameter( viewDiameter );
            double angle = 0;
            double deltaAngle = Math.PI * 2 / numSegments;
            double radius = viewDiameter / 2;
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( 0, 0 );
            for ( int i = 0; i < numSegments + 1; i++ ) {
                double myRadius = i % 2 == 0 ? twinkleRadius.apply( radius ) : radius;
                ImmutableVector2D target = ImmutableVector2D.parseAngleAndMagnitude( myRadius, angle );
                path.lineTo( target );
                angle += deltaAngle;
            }
            twinkles.setPathTo( path.getGeneralPath() );
        }
    }

    //REVIEW class doc
    public static class ImageRenderer extends BodyRenderer {
        private final PImage imageNode;
        private double viewDiameter;

        public ImageRenderer( IBodyColors body, double viewDiameter, final String imageName ) {
            super( body );

            imageNode = new PImage( GravityAndOrbitsApplication.RESOURCES.getImage( imageName ) );
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