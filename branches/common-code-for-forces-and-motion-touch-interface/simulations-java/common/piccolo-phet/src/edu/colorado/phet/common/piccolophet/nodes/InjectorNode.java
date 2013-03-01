// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.piccolophet.PiccoloPhetApplication.RESOURCES;

/**
 * Node that represents an injector or "squirter" with which the user can
 * interact in order to inject particles, fluid, or whatever into the model.
 * This superclass doesn't actually have interactivity so that it is easy to
 * add different interaction models in subclasses.  Please check out the
 * various subclasses for examples of how this is done.
 * <p/>
 * Copied from ParticleInjectorNode in membrane-channels on 12-9-2010
 *
 * @author John Blanco
 * @author Sam Reid
 */
public abstract class InjectorNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Height of the injector prior to rotation.  This is in world size, which
    // is close to pixels (but not quite exactly due to all that transform
    // craziness).
    protected static final double INJECTOR_HEIGHT = 130;

    // Offset of button within this node.  This was determined by trial and
    // error and will need to be tweaked if the images change.
    private static final Point2D BUTTON_OFFSET = new Point2D.Double( -100, -65 );

    protected final PImage buttonImageNode;
    protected final BufferedImage unpressedButtonImage;
    protected final BufferedImage pressedButtonImage;
    protected final double distanceCenterToTip;
    protected final PNode injectorNode;
    protected final SimpleObserver inject;

    /*
     * Constructs a particle injection node.
     *
     * @param mvt           - Model-view transform for relating view space to model space.
     * @param rotationAngle - Angle of rotation for the injection bulb.
     */
    public InjectorNode( double rotationAngle, final SimpleObserver inject ) {
        this.inject = inject;

        // Create the root node to which the various constituent parts can be
        // added.
        injectorNode = new PNode();

        // Load the graphic images for this device.  These are offset in order
        // to make the center of rotation be the center of the bulb.
        BufferedImage injectorBodyImage = RESOURCES.getImage( "squeezer_background.png" );
        PNode injectorBodyImageNode = new PImage( injectorBodyImage );
        Rectangle2D originalBodyBounds = injectorBodyImageNode.getFullBounds();
        injectorBodyImageNode.setOffset( -originalBodyBounds.getWidth() / 2, -originalBodyBounds.getHeight() / 2 );
        injectorNode.addChild( injectorBodyImageNode );

        pressedButtonImage = RESOURCES.getImage( "button_pressed.png" );
        unpressedButtonImage = RESOURCES.getImage( "button_unpressed.png" );
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
        distanceCenterToTip = 0.7 * INJECTOR_HEIGHT;
        final double centerOffsetX = 0.4 * INJECTOR_HEIGHT;
        Dimension2D injectionPointOffset = new PDimension();
        injectionPointOffset.setSize( distanceCenterToTip * Math.cos( rotationAngle ) + centerOffsetX,
                                      distanceCenterToTip * Math.sin( -rotationAngle ) );

        // Set up the button handling.
        injectorBodyImageNode.setPickable( false );
        buttonImageNode.setPickable( true );
    }
}