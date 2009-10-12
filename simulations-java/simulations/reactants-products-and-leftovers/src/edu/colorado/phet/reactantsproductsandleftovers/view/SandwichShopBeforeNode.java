package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.BreadQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.CheeseQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.MeatQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


public class SandwichShopBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = new PDimension( 400, 300 );
    private static final double X_MARGIN = 10;
    private static final double Y_MARGIN = 10;
    private static final double REACTANTS_SCALE = 0.5; //XXX
    
    private final SandwichShop model;

    private final ArrayList<BreadNode> bread;
    private final ArrayList<MeatNode> meat;
    private final ArrayList<CheeseNode> cheese;
    
    public SandwichShopBeforeNode( final SandwichShop model ) {
        super();
        
        this.model = model;
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        bread = new ArrayList<BreadNode>();
        meat = new ArrayList<MeatNode>();
        cheese = new ArrayList<CheeseNode>();
        
        BoxNode boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        PText titleNode = new PText( RPALStrings.LABEL_BEFORE_SANDWICH );
        titleNode.setFont( new PhetFont( 36 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        PNode controlsNode = new PNode();
        controlsNode.scale(  2  ); //XXX
        addChild( controlsNode );
        BreadQuantityControlNode breadControlNode = new BreadQuantityControlNode( model );
        MeatQuantityControlNode meatControlNode = new MeatQuantityControlNode( model );
        CheeseQuantityControlNode cheeseControlNode = new CheeseQuantityControlNode( model );
        controlsNode.addChild( breadControlNode );
        controlsNode.addChild( meatControlNode );
        controlsNode.addChild( cheeseControlNode );
        double breadWidth = breadControlNode.getFullBoundsReference().getWidth();
        double meatWidth = meatControlNode.getFullBoundsReference().getWidth();
        double cheeseWidth = cheeseControlNode.getFullBoundsReference().getWidth();
        double maxWidth = Math.max( breadWidth, Math.max( meatWidth, cheeseWidth ) );
        double deltaX = maxWidth + 10;
        breadControlNode.setOffset( 0, 0 );
        meatControlNode.setOffset( deltaX, 0 );
        cheeseControlNode.setOffset( 2 * deltaX, 0 );
        
        // layout, origin at upper-left corner of box
        boxNode.setOffset( 0, 0 );
        double x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        double y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 10;
        titleNode.setOffset( x, y );
        x = boxNode.getFullBoundsReference().getCenterX() - ( controlsNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMaxY() + 10;
        controlsNode.setOffset( x, y );
        
        update();
    }
    
    private void update() {
        
        PDimension boxSize = BOX_SIZE;
        
        // bread
        if ( model.getBread() < bread.size() ) {
            while ( model.getBread() < bread.size() ) {
                BreadNode node = bread.get( bread.size() - 1 );
                removeChild( node );
                bread.remove( node );
            }
        }
        else {
            while ( model.getBread() > bread.size() ) {
                BreadNode node = new BreadNode();
                addChild( node );
                node.scale( REACTANTS_SCALE );
                Point2D p = getRandomLocation( boxSize, node );
                node.setOffset( p );
                bread.add( node );
            }
        }
        
        // meat
        if ( model.getMeat() < meat.size() ) {
            while ( model.getMeat() < meat.size() ) {
                MeatNode node = meat.get( meat.size() - 1 );
                removeChild( node );
                meat.remove( node );
            }
        }
        else {
            while ( model.getMeat() > meat.size() ) {
                MeatNode node = new MeatNode();
                addChild( node );
                node.scale( REACTANTS_SCALE );
                Point2D p = getRandomLocation( boxSize, node );
                node.setOffset( p );
                meat.add( node );
            }
        }
        
        // cheese
        if ( model.getCheese() < cheese.size() ) {
            while ( model.getCheese() < cheese.size() ) {
                CheeseNode node = cheese.get( cheese.size() - 1 );
                removeChild( node );
                cheese.remove( node );
            }
        }
        else {
            while ( model.getCheese() > cheese.size() ) {
                CheeseNode node = new CheeseNode();
                addChild( node );
                node.scale( REACTANTS_SCALE );
                Point2D p = getRandomLocation( boxSize, node );
                node.setOffset( p );
                cheese.add( node );
            }
        }
        
    }
    
    /*
     * Gets a point to be used for the upper-left corner of an ingredient image.
     */
    private static Point2D getRandomLocation( PDimension boxSize, PNode node ) {
        double xRange = boxSize.getWidth() - node.getFullBoundsReference().getWidth() - 2 * X_MARGIN;
        double yRange = boxSize.getHeight() - node.getFullBoundsReference().getHeight() - 2 * Y_MARGIN;
        double xMin = X_MARGIN;
        double yMin = Y_MARGIN;
        double xPercent = Math.random();
        double yPercent = Math.random();
        double x = xMin + ( xPercent * xRange );
        double y = yMin + ( yPercent * yRange );
        return new Point2D.Double( x, y );
    }

}
