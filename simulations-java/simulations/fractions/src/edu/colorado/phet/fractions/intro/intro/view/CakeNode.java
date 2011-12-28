// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.FractionsResources;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;
import edu.colorado.phet.fractions.intro.intro.model.FractionsIntroModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Shows just one cake with the specified pieces.
 *
 * @author Sam Reid
 */
public class CakeNode extends PNode {
    public CakeNode( int denominator, final int[] pieces, final Property<ContainerState> containerStateProperty, final int container ) {
        addChild( new PImage( cropSides( FractionsResources.RESOURCES.getImage( "cake/cake_grid_" + denominator + ".png" ) ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    FractionsIntroModel.setUserToggled( true );
                    containerStateProperty.set( containerStateProperty.get().toggle( new CellPointer( container, 0 ) ) );
                    FractionsIntroModel.setUserToggled( false );
                }
            } );
        }} );

        final BufferedImage[] images = new BufferedImage[pieces.length];
        for ( int i = 0; i < images.length; i++ ) {
            images[i] = cropSides( FractionsResources.RESOURCES.getImage( "cake/cake_" + denominator + "_" + pieces[i] + ".png" ) );
        }


        final PBasicInputEventHandler sharedMouseListener = new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {

                //TODO: trim whitespace on the sides of the image so cakes don't overlap (or else space out the cakes more)
                FractionsIntroModel.setUserToggled( true );

                //Find the color of the pixel hit by the mouse.  If white or transparent in the image, subtract a piece because the user clicked the background
                //Otherwise add a piece
                Point2D position = event.getPositionRelativeTo( CakeNode.this );

                //Chain of responsibility sort of thing to see which cake slice gets to handle it.
                for ( int i = images.length - 1; i >= 0; i-- ) {
                    BufferedImage image = images[i];
                    int pixel = image.getRGB( (int) position.getX(), (int) position.getY() );
                    Color color = new Color( pixel, true );
                    System.out.println( "color = " + color );
                    if ( color.getRed() > 0 || color.getGreen() > 0 || color.getBlue() > 0 ) {
                        //Clicked on a piece of cake, turning it off...
                        System.out.println( "//Clicked on a piece of cake, turning it off..." );
                        int piece = pieces[i];
                        containerStateProperty.set( containerStateProperty.get().toggle( new CellPointer( container, piece - 1 ) ) );
                        return;
                    }
                }

                //Clicked in a blank area, turning on an empty cell.
                System.out.println( "//Clicked in a blank area, turning on an empty cell." );
                containerStateProperty.set( containerStateProperty.get().toggle( new CellPointer( container, containerStateProperty.get().getContainer( container ).getLowestEmptyCell() ) ) );

                FractionsIntroModel.setUserToggled( false );
            }
        };

        for ( int i = 0; i < pieces.length; i++ ) {
            addChild( new PImage( images[i] ) {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( sharedMouseListener );
            }} );
        }
    }

    //Trim the sides since there is too much alpha left over in the images and it causes them to overlap so that mouse presses are caught by adjacent cakes instead of the desired cake
    private BufferedImage cropSides( BufferedImage image ) {
        final int TRIM = 24;
        BufferedImage im = new BufferedImage( image.getWidth() - TRIM * 2, image.getHeight(), image.getType() );
        Graphics2D g2 = im.createGraphics();
        g2.drawRenderedImage( image, AffineTransform.getTranslateInstance( -TRIM, 0 ) );
        g2.dispose();
        return im;
    }
}