
package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Image;
import java.util.ArrayList;

import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;


public class SandwichImageFactory {

    private static final int Y_SPACING = 11;

    /* not intended for instantiation */
    private SandwichImageFactory() {}

    //TODO reduce memory by reusing image if model hasn't changed
    public static Image createImage( SandwichShopModel model ) {

        Image image = null;

        if ( !model.getReaction().isReaction() ) {
            image = new PText( "?" ).toImage(); // so we have something to see
        }
        else {
            PNode parent = new PNode();

            // create nodes
            ArrayList<PImage> breadNodes = new ArrayList<PImage>();
            for ( int i = 0; i < model.getBread().getCoefficient(); i++ ) {
                breadNodes.add( new PImage( model.getBread().getImage() ) );
            }
            ArrayList<PImage> meatNodes = new ArrayList<PImage>();
            for ( int i = 0; i < model.getMeat().getCoefficient(); i++ ) {
                meatNodes.add( new PImage( model.getMeat().getImage() ) );
            }
            ArrayList<PImage> cheeseNodes = new ArrayList<PImage>();
            for ( int i = 0; i < model.getCheese().getCoefficient(); i++ ) {
                cheeseNodes.add( new PImage( model.getCheese().getImage() ) );
            }

            // save one piece of bread for the top
            PImage topBreadNode = null;
            if ( breadNodes.size() > 1 ) {
                topBreadNode = breadNodes.remove( 0 );
            }

            // stack ingredients, starting with bread, alternating ingredients
            PNode previousNode = null;
            int max = Math.max( breadNodes.size(), Math.max( meatNodes.size(), cheeseNodes.size() ) );
            for ( int i = 0; i < max; i++ ) {
                // bread
                if ( breadNodes.size() > 0 ) {
                    PNode node = breadNodes.remove( 0 );
                    parent.addChild( node );
                    stackNode( node, previousNode );
                    previousNode = node;
                }
                // meat
                if ( meatNodes.size() > 0 ) {
                    PNode node = meatNodes.remove( 0 );
                    parent.addChild( node );
                    stackNode( node, previousNode );
                    previousNode = node;
                }
                // cheese
                if ( cheeseNodes.size() > 0 ) {
                    PNode node = cheeseNodes.remove( 0 );
                    parent.addChild( node );
                    stackNode( node, previousNode );
                    previousNode = node;
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
