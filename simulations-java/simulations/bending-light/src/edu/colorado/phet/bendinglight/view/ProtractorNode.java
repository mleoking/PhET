// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.modules.intro.ToolNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;

/**
 * @author Sam Reid
 */
public class ProtractorNode extends ToolNode {
    private final ModelViewTransform transform;
    private final ProtractorModel protractorModel;
    private final BufferedImage image;
    private final double scale;

    public ProtractorNode( final ModelViewTransform transform, final Property<Boolean> showProtractor, final ProtractorModel protractorModel,
                           Function2<Shape, Shape, Shape> translateShape, Function2<Shape, Shape, Shape> rotateShape,
                           double scale ) {//Passed in as a separate arg since this node modifies its entire transform
        this.scale = scale;
        this.transform = transform;
        this.protractorModel = protractorModel;
        image = RESOURCES.getImage( "protractor.png" );
        final PImage imageNode = new PImage( image ) {{
            showProtractor.addObserver( new SimpleObserver() {
                public void update() {
                    ProtractorNode.this.setVisible( showProtractor.getValue() );
                }
            } );
        }};
        addChild( imageNode );
        final Ellipse2D.Double outerShape = new Ellipse2D.Double( 0, 0, image.getWidth(), image.getHeight() );
        Area outerRimShape = new Area( outerShape ) {{
            final double rx = image.getWidth() * 0.3;//tuned to the given image
            final double ry = image.getHeight() * 0.3;
            subtract( new Area( new Ellipse2D.Double( outerShape.getCenterX() - rx, outerShape.getCenterY() - ry, rx * 2, ry * 2 ) ) );//cut out the semicircles in the middle
        }};

        Rectangle2D.Double innerBarShape = new Rectangle2D.Double( 20, outerShape.getCenterY(), outerShape.getWidth() - 40, 38 );

//        addChild( new PhetPPath( innerBarShape, new Color( 0, 255, 0, 128 ) ) {{//For debugging the drag hit area
        addChild( new PhetPPath( translateShape.apply( innerBarShape, outerRimShape ), new Color( 0, 0, 0, 0 ) ) {{//For debugging the drag hit area
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    dragAll( event.getDeltaRelativeTo( getParent() ) );
                }
            } );
        }} );

        //        addChild( new PhetPPath( outerRimShape, new Color( 255, 0, 0, 128 ) ) {{//For debugging the drag hit area
        addChild( new PhetPPath( rotateShape.apply( innerBarShape, outerRimShape ), new Color( 0, 0, 0, 0 ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                Point2D start = null;

                public void mousePressed( PInputEvent event ) {
                    start = event.getPositionRelativeTo( getParent() );
                }

                public void mouseDragged( PInputEvent event ) {
                    Point2D end = event.getPositionRelativeTo( getParent() );
                    double startAngle = new ImmutableVector2D( getFullBounds().getCenter2D(), start ).getAngle();
                    double angle = new ImmutableVector2D( getFullBounds().getCenter2D(), end ).getAngle();
                    double deltaAngle = angle - startAngle;
                    protractorModel.angle.setValue( protractorModel.angle.getValue() + deltaAngle );
                }
            } );
        }} );

        final SimpleObserver updateTransform = new SimpleObserver() {
            public void update() {
                doUpdateTransform();
            }
        };
        protractorModel.position.addObserver( updateTransform );
        protractorModel.angle.addObserver( updateTransform );
    }

    protected void doUpdateTransform() {
        setTransform( new AffineTransform() );
        setScale( scale );
        final Point2D point2D = transform.modelToView( protractorModel.position.getValue() ).toPoint2D();
        setOffset( ( point2D.getX() - image.getWidth() / 2 ) * scale, ( point2D.getY() - image.getHeight() / 2 ) * scale );
        rotateAboutPoint( protractorModel.angle.getValue(), image.getWidth() / 2, image.getHeight() / 2 );
    }

    public static BufferedImage newProtractorImage( int height ) {
        return multiScaleToHeight( RESOURCES.getImage( "protractor.png" ), height );
    }

    public void dragAll( PDimension delta ) {
        protractorModel.translate( transform.viewToModelDelta( new ImmutableVector2D( delta.width / getScale(), delta.height / getScale() ) ) );
    }

    @Override
    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( isVisible );
        setChildrenPickable( isVisible );
    }
}
