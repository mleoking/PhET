// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.sandwich;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.Bread;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel.SandwichReaction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Factory that creates sandwich images, uses knowledge about someone might make a sandwich.
 * No image caching is implemented, since it would complicate the code and result in
 * very little memory savings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichImageFactory {

    private static final int Y_SPACING = 4;

    /* not intended for instantiation */
    private SandwichImageFactory() {}

    public static Image createImage( SandwichReaction sandwichReaction ) {

        Image image = null;

        if ( !sandwichReaction.isReaction() ) {
            image = new PText( "?" ).toImage(); // so we have something to see
        }
        else {
            PNode parent = new PNode();

            // create a list of images for each reactant
            HashMap<Class<?>,ArrayList<PImage>> map = new HashMap<Class<?>,ArrayList<PImage>>();
            for ( Reactant reactant : sandwichReaction.getReactants() ) {
                Class<?> moleculeClass = reactant.getMolecule().getClass();
                assert( map.get( moleculeClass ) == null );
                ArrayList<PImage> list = new ArrayList<PImage>();
                map.put( moleculeClass, list );
                for ( int i = 0; i < reactant.getCoefficient(); i++ ) {
                    list.add( new PImage( reactant.getImage() ) );
                }
            }
            
            // save one piece of bread for the top
            ArrayList<PImage> breadList = map.get( Bread.class );
            PImage topBreadNode = null;
            if ( breadList.size() > 1 ) {
                topBreadNode = breadList.remove( 0 );
            }

            // stack ingredients, starting with bread, alternating other ingredients
            boolean imageAdded = true;
            PNode previousNode = null;
            while ( imageAdded ) { 
                
                imageAdded = false;
                
                // one slide of bread first...
                if ( breadList.size() > 0 ) {
                    PNode node = breadList.remove( 0 );
                    parent.addChild( node );
                    stackNode( node, previousNode );
                    previousNode = node;
                    imageAdded = true;
                }
                
                // ...then one of each other ingredient
                for ( Class<?> moleculeClass : map.keySet() ) {
                    ArrayList<PImage> list = map.get( moleculeClass );
                    if ( list != breadList && list.size() > 0 ) {
                        PNode node = list.remove( 0 );
                        parent.addChild( node );
                        stackNode( node, previousNode );
                        previousNode = node;
                        imageAdded = true;
                    }
                }
            }
            
            // top bread
            if ( topBreadNode != null ) {
                parent.addChild( topBreadNode );
                stackNode( topBreadNode, previousNode );
            }

            assert ( parent.getChildrenCount() > 0 );
            image = parent.toImage();
        }

        return image;
    }

    /*
     * Adjusts the offset of node so that it's slightly above previousNode.
     */
    private static void stackNode( PNode node, PNode previousNode ) {
        double x = 0;
        double y = 0;
        if ( previousNode != null ) {
            x = previousNode.getFullBoundsReference().getCenterX() - ( node.getFullBoundsReference().getWidth() / 2 );
            y = previousNode.getYOffset() - Y_SPACING;
        }
        node.setOffset( x, y );
    }
}
