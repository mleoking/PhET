// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.colorado.phet.fluidpressureandflow.modules.fluidflow.model.Pipe;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Node that represents the thing with which the user interacts in order to
 * inject particles (generally ions) into the chamber.
 * <p/>
 * Copied from ParticleInjectorNode in membrane-channels on 12-9-2010
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class DropperNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Height of the injector prior to rotation.  This is in world size, which
    // is close to pixels (but not quite exactly due to all that transform
    // craziness).
    private static final double INJECTOR_HEIGHT = 130;

    // Offset of button within this node.  This was determined by trial and
    // error and will need to be tweaked if the images change.
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( -100, -65 );

    private final PImage buttonImageNode;
    private final BufferedImage unpressedButtonImage;
    private final BufferedImage pressedButtonImage;

    /*
     * Constructs a particle injection node.
     *
     * @param mvt           - Model-view transform for relating view space to model space.
     * @param rotationAngle - Angle of rotation for the injection bulb.
     */
    public DropperNode( final ModelViewTransform mvt, double rotationAngle, final Pipe pipe, final SimpleObserver squirt ) {
        double NOMINAL_ION_INJECTION_VELOCITY = 30;
        Vector2D nominalInjectionVelocityVector = new Vector2D( NOMINAL_ION_INJECTION_VELOCITY, 0 );
        nominalInjectionVelocityVector.rotate( rotationAngle );

        // Create the base node to which the various constituent parts can be
        // added.
        PNode injectorNode = new PNode();

        // Load the graphic images for this device.  These are offset in order
        // to make the center of rotation be the center of the bulb.
        BufferedImage injectorBodyImage = FluidPressureAndFlowApplication.RESOURCES.getImage( "squeezer_background.png" );
        PNode injectorBodyImageNode = new PImage( injectorBodyImage );
        Rectangle2D originalBodyBounds = injectorBodyImageNode.getFullBounds();
        injectorBodyImageNode.setOffset( -originalBodyBounds.getWidth() / 2, -originalBodyBounds.getHeight() / 2 );
        injectorNode.addChild( injectorBodyImageNode );
        pressedButtonImage = FluidPressureAndFlowApplication.RESOURCES.getImage( "button_pressed.png" );
        unpressedButtonImage = FluidPressureAndFlowApplication.RESOURCES.getImage( "button_unpressed.png" );
        buttonImageNode = new PImage( unpressedButtonImage );
        buttonImageNode.setOffset( BUTTON_OFFSET );
        injectorNode.addChild( buttonImageNode );

        // Rotate and scale the image node as a whole.
        double scale = INJECTOR_HEIGHT / injectorBodyImageNode.getFullBoundsReference().height;
        injectorNode.rotate( -rotationAngle );
        injectorNode.scale( scale );

        // Add the injector image node.  Note that the position has to be
        // tweaked in order to account for the rotation of the node image,
        // since the rotation of the square image enlarges the bounds.
        injectorNode.setOffset( -Math.abs( Math.sin( rotationAngle * 2 ) ) * 30, 0 );
        addChild( injectorNode );

        // Set up the injection point offset. This makes some assumptions
        // about the nature of the image, and will need to be updated if the
        // image is changed.
        final double distanceCenterToTip = 0.7 * INJECTOR_HEIGHT;
        final double centerOffsetX = 0.4 * INJECTOR_HEIGHT;
        Dimension2D injectionPointOffset = new PDimension();
        injectionPointOffset.setSize( distanceCenterToTip * Math.cos( rotationAngle ) + centerOffsetX,
                                      distanceCenterToTip * Math.sin( -rotationAngle ) );

        // Set up the button handling.
        injectorBodyImageNode.setPickable( false );
        buttonImageNode.setPickable( true );
        buttonImageNode.addInputEventListener( new CursorHandler() );
        buttonImageNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
                buttonImageNode.setImage( pressedButtonImage );
                squirt.update();
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                buttonImageNode.setImage( unpressedButtonImage );
            }
        } );

        final SimpleObserver updateLocation = new SimpleObserver() {
            public void update() {
                final Point2D site = mvt.modelToView( pipe.getSplineCrossSections().get( 11 ).getTop() );//TODO: make this a function of x instead of array index
                setOffset( site.getX(), site.getY() - distanceCenterToTip + 5 );
            }
        };
        pipe.addShapeChangeListener( updateLocation );
        updateLocation.update();
    }
}