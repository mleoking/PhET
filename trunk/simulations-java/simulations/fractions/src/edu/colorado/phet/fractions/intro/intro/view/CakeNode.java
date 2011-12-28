// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
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
    public CakeNode( int denominator, int[] pieces, final Property<ContainerState> containerStateProperty, final int container ) {
        addChild( new PImage( FractionsResources.RESOURCES.getImage( "cake/cake_grid_" + denominator + ".png" ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    FractionsIntroModel.setUserToggled( true );
                    containerStateProperty.set( containerStateProperty.get().toggle( new CellPointer( container, 0 ) ) );
                    FractionsIntroModel.setUserToggled( false );
                }
            } );
        }} );
        for ( final int piece : pieces ) {
            final BufferedImage image = FractionsResources.RESOURCES.getImage( "cake/cake_" + denominator + "_" + piece + ".png" );
            addChild( new PImage( image ) {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {

                        //TODO: trim whitespace on the sides of the image so cakes don't overlap (or else space out the cakes more)
                        FractionsIntroModel.setUserToggled( true );

                        //Find the color of the pixel hit by the mouse.  If white or transparent in the image, subtract a piece because the user clicked the background
                        //Otherwise add a piece
                        Point2D position = event.getPositionRelativeTo( CakeNode.this );
                        int pixel = image.getRGB( (int) position.getX(), (int) position.getY() );
                        Color color = new Color( pixel, true );
                        System.out.println( "color = " + color );
                        if ( color.getRed() > 0 || color.getGreen() > 0 || color.getBlue() > 0 ) {
                            //Clicked on a piece of cake, turning it off...
                            System.out.println( "//Clicked on a piece of cake, turning it off..." );
                            containerStateProperty.set( containerStateProperty.get().toggle( new CellPointer( container, piece - 1 ) ) );
                        }
                        else {
                            //Clicked in a blank area, turning on an empty cell.
                            System.out.println( "//Clicked in a blank area, turning on an empty cell." );
                            containerStateProperty.set( containerStateProperty.get().toggle( new CellPointer( container, containerStateProperty.get().getContainer( container ).getLowestEmptyCell() ) ) );
                        }

                        FractionsIntroModel.setUserToggled( false );
                    }
                } );
            }} );
        }
    }
}