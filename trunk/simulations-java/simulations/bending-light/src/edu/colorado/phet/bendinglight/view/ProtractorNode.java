// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.model.ProtractorModel;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScale;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;

/**
 * The protractor node is a circular device for measuring angles.  In this sim it is used for measuring the angle of the incident, reflected and refracted light.
 *
 * @author Sam Reid
 */
public class ProtractorNode extends ToolNode {
    private final ModelViewTransform transform;
    private final ProtractorModel protractorModel;
    private final BufferedImage image;
    private double scale;//The current scale (how much to increase the size of the graphic)
    protected final Rectangle2D.Double innerBarShape;//shape of the inner bar in view coordinates.  Necessary since dragging the bar sometimes has a different behavior (translating) then dragging the outside of the protractor (rotating) (in the prism tab)
    public static final double DEFAULT_SCALE = 0.5;
    private boolean debug = false;//Enable this to show debugging information.

    public ProtractorNode( final ModelViewTransform transform,
                           final Property<Boolean> showProtractor,
                           final ProtractorModel protractorModel,
                           Function2<Shape, Shape, Shape> translateShape,//Function that returns the part of the protractor that can be used for translating it
                           Function2<Shape, Shape, Shape> rotateShape,//Function that returns the part of the protractor that can be used for rotating it
                           double scale,//Passed in as a separate arg since this node modifies its entire transform
                           double multiscale ) {//Just using a global piccolo scale in the "prism break" tab leads to jagged and aliased graphics--in that case it is important to use the multiscaling algorithm
        this.scale = scale;
        this.transform = transform;
        this.protractorModel = protractorModel;

        //Load and add the image
        image = multiScale( RESOURCES.getImage( "protractor.png" ), multiscale );
        final PImage imageNode = new PImage( image ) {{
            showProtractor.addObserver( new SimpleObserver() {
                public void update() {
                    ProtractorNode.this.setVisible( showProtractor.getValue() );
                }
            } );
            setPickable( false );//We handle the drag shapes separately for fine-grained control, since different drag regions have different behavior.
        }};
        addChild( imageNode );

        //Shape for the outer ring of the protractor
        final Ellipse2D.Double outerShape = new Ellipse2D.Double( 0, 0, image.getWidth(), image.getHeight() );
        Area outerRimShape = new Area( outerShape ) {{
            final double rx = image.getWidth() * 0.3;//tuned to the given image
            final double ry = image.getHeight() * 0.3;
            subtract( new Area( new Ellipse2D.Double( outerShape.getCenterX() - rx, outerShape.getCenterY() - ry, rx * 2, ry * 2 ) ) );//cut out the semicircles in the middle
        }};

        //Okay if it overlaps the rotation region since rotation region is in higher z layer
        innerBarShape = new Rectangle2D.Double( 20, outerShape.getCenterY(), outerShape.getWidth() - 40, 90 );

        if ( debug ) {
            addChild( new PhetPPath( innerBarShape, new Color( 0, 255, 0, 128 ) ) );//For debugging the drag hit area
        }

        //Add a mouse listener for dragging when the drag region (entire body in all tabs, just the inner bar on prism break tab) is dragged
        addChild( new PhetPPath( translateShape.apply( innerBarShape, outerRimShape ), debug ? Color.blue : new Color( 0, 0, 0, 0 ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new BoundedDragHandler( ProtractorNode.this ) {
                //Drag by changing the associated model element instead of just moving the node around
                @Override public void dragNode( DragEvent event ) {
                    dragAll( event.delta );
                }
            } );
        }} );

        //Add a mouse listener for rotating when the rotate shape (the outer ring in the 'prism break' tab is dragged)
        addChild( new PhetPPath( rotateShape.apply( innerBarShape, outerRimShape ), debug ? Color.red : new Color( 0, 0, 0, 0 ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                Point2D start = null;

                public void mousePressed( PInputEvent event ) {
                    start = event.getPositionRelativeTo( getParent() );
                }

                public void mouseDragged( PInputEvent event ) {
                    //Compute the change in angle based on the new drag event
                    Point2D end = event.getPositionRelativeTo( getParent() );
                    double startAngle = new ImmutableVector2D( getFullBounds().getCenter2D(), start ).getAngle();
                    double angle = new ImmutableVector2D( getFullBounds().getCenter2D(), end ).getAngle();
                    double deltaAngle = angle - startAngle;

                    //Rotate the protractor model
                    protractorModel.angle.setValue( protractorModel.angle.getValue() + deltaAngle );
                }
            } );
        }} );

        //Update the protractor transform when the model position or angle changes.
        new RichSimpleObserver() {
            public void update() {
                updateTransform();
            }
        }.observe( protractorModel.position, protractorModel.angle );
    }

    //Resize the protractor
    protected void setProtractorScale( double scale ) {
        this.scale = scale;
        updateTransform();
    }

    //Update the transform (scale, offset, rotation) of this protractor to reflect the model values and the specified scale
    protected void updateTransform() {
        setTransform( new AffineTransform() );
        setScale( scale );
        final Point2D point2D = transform.modelToView( protractorModel.position.getValue() ).toPoint2D();
        setOffset( point2D.getX() - image.getWidth() / 2 * scale, point2D.getY() - image.getHeight() / 2 * scale );
        rotateAboutPoint( protractorModel.angle.getValue(), image.getWidth() / 2, image.getHeight() / 2 );
    }

    //Create a protractor image given at the specified height
    public static BufferedImage newProtractorImage( int height ) {
        return multiScaleToHeight( RESOURCES.getImage( "protractor.png" ), height );
    }

    //Translate the protractor, this method is called when dragging out of the toolbox
    public void dragAll( PDimension delta ) {
        protractorModel.translate( transform.viewToModelDelta( new ImmutableVector2D( delta.width, delta.height ) ) );
    }

    //Change the visibility and pickability of this ProtractorNode
    @Override public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( isVisible );
        setChildrenPickable( isVisible );
    }
}
