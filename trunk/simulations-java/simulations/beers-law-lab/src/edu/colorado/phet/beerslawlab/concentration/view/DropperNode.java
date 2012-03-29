// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.common.view.DebugOriginNode;
import edu.colorado.phet.beerslawlab.common.view.MovableDragHandler;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationSolution;
import edu.colorado.phet.beerslawlab.concentration.model.Dropper;
import edu.colorado.phet.beerslawlab.concentration.model.Solute;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.PiccoloPhetResources;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.MomentaryButtonNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Dropper that contains a solute in solution form.
 * Origin is at the center of the hole where solution comes out of the dropper (bottom center).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class DropperNode extends PhetPNode {

    private static final boolean SHOW_ORIGIN = false;
    private static final double BUTTON_Y_OFFSET = 13; // y offset of button location in dropper image file
    private static final double LABEL_Y_OFFSET = 130; // y offset of the label's center in dropper image file

    public static final double TIP_WIDTH = 15; // specific to image file

    // glass portion of the dropper, used to fill dropper with stock solution, specific to the dropper image file
    private static final GeneralPath GLASS_PATH = new DoubleGeneralPath() {{
        final double tipWidth = TIP_WIDTH;
        final double tipHeight = 5;
        final double glassWidth = 46;
        final double glassHeight = 150;
        final double glassYOffset = tipHeight + 14;
        moveTo( -tipWidth / 2, 0 );
        lineTo( -tipWidth / 2, -tipHeight );
        lineTo( -glassWidth / 2, -glassYOffset );
        lineTo( -glassWidth / 2, -glassHeight );
        lineTo( glassWidth / 2, -glassHeight );
        lineTo( glassWidth / 2, -glassYOffset );
        lineTo( tipWidth / 2, -tipHeight );
        lineTo( tipWidth / 2, 0 );
        closePath();
    }}.getGeneralPath();

    public DropperNode( final Dropper dropper, final Solvent solvent, final Property<Solute> solute ) {

        // nodes
        final PPath fluidNode = new PPath( GLASS_PATH ); // fluid inside the dropper
        final PImage foregroundImageNode = new PImage( Images.DROPPER_FOREGROUND );
        final PImage backgroundImageNode = new PImage( Images.DROPPER_BACKGROUND );
        final HTMLNode labelNode = new HTMLNode( "", Color.BLACK, new PhetFont( Font.BOLD, 15 ) );
        final PPath labelBackgroundNode = new PPath() {{
            setPaint( ColorUtils.createColor( new Color( 240, 240, 240 ), 150 ) );
            setStroke( null );
        }};
        MomentaryButtonNode buttonNode = new MomentaryButtonNode( UserComponents.dropperButton,
                                                                  dropper.on, PiccoloPhetResources.getImage( "button_pressed.png" ), PiccoloPhetResources.getImage( "button_unpressed.png" ),
                                                                  dropper.enabled, PiccoloPhetResources.getImage( "button_pressed_disabled.png" ), PiccoloPhetResources.getImage( "button_unpressed_disabled.png" ) ) {{
            scale( 0.3 );
        }};

        // rendering order
        addChild( fluidNode );
        addChild( backgroundImageNode );
        addChild( foregroundImageNode );
        addChild( labelBackgroundNode );
        addChild( labelNode );
        addChild( buttonNode );
        if ( SHOW_ORIGIN ) {
            addChild( new DebugOriginNode() );
        }

        // layout
        {
            // move origin to bottom center (tip) of images
            foregroundImageNode.setOffset( -foregroundImageNode.getFullBoundsReference().getWidth() / 2, -foregroundImageNode.getFullBoundsReference().getHeight() );
            backgroundImageNode.setOffset( -backgroundImageNode.getFullBoundsReference().getWidth() / 2, -backgroundImageNode.getFullBoundsReference().getHeight() );
            // center the button in the dropper's bulb
            buttonNode.setOffset( foregroundImageNode.getFullBoundsReference().getCenterX() - ( buttonNode.getFullBoundsReference().getWidth() / 2 ),
                                  foregroundImageNode.getFullBoundsReference().getMinY() + BUTTON_Y_OFFSET );
            //NOTE: label will be positioned whenever its text is set, to keep it centered in the dropper's glass
        }

        // Change the label and fluid color when the solute changes.
        final RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {

                // label, centered in the dropper's glass
                labelNode.setHTML( dropper.solute.get().formula );
                labelNode.setRotation( -Math.PI / 2 );
                labelNode.setOffset( -( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                     foregroundImageNode.getFullBoundsReference().getMaxY() - ( foregroundImageNode.getFullBoundsReference().getHeight() - LABEL_Y_OFFSET ) + ( labelNode.getFullBoundsReference().getHeight() / 2 ) );

                // translucent background for the label, so that it's visible on all solution colors
                final double width = 1.2 * labelNode.getFullBoundsReference().getWidth();
                final double height = 1.2 * labelNode.getFullBoundsReference().getHeight();
                final double x = labelNode.getFullBoundsReference().getCenterX() - ( width / 2 );
                final double y = labelNode.getFullBoundsReference().getCenterY() - ( height / 2 );
                labelBackgroundNode.setPathTo( new RoundRectangle2D.Double( x, y, width, height, 8, 8 ) );

                // fluid color
                Color color = ConcentrationSolution.createColor( solvent, solute.get(), solute.get().stockSolutionConcentration );
                fluidNode.setPaint( color );
                fluidNode.setStrokePaint( BLLConstants.createFluidStrokeColor( color ) );
            }
        };
        observer.observe( dropper.solute, dropper.solute.get().colorScheme );

        // rewire to a different color scheme when the solute changes
        solute.addObserver( new ChangeObserver<Solute>() {
            public void update( Solute newSolute, Solute oldSolute ) {
                oldSolute.colorScheme.removeObserver( observer );
                newSolute.colorScheme.addObserver( observer );
            }
        } );

        // Visibility
        dropper.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        // Update location
        dropper.location.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( dropper.location.get().toPoint2D() );
            }
        } );

        // Make the background visible only when the dropper is empty
        dropper.empty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean empty ) {
                fluidNode.setVisible( !empty );
                backgroundImageNode.setVisible( empty );
            }
        } );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MovableDragHandler( UserComponents.dropper, dropper, this ) );
    }
}
