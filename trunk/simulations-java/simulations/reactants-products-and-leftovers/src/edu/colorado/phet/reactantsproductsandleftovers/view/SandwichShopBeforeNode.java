package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.BreadQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.CheeseQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.MeatQuantityControlNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichShopBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = new PDimension( 400, 300 );
    private static final double X_MARGIN = 10;
    private static final double Y_MARGIN = 10;
    private static final double REACTANTS_SCALE = 0.5; //XXX
    
    private final SandwichShop model;

    private final BoxNode boxNode;
    private final PComposite imagesParent;
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
        
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        imagesParent = new PComposite();
        addChild( imagesParent );
        
        PText titleNode = new PText( RPALStrings.LABEL_BEFORE_SANDWICH );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        PNode controlsNode = new PNode();
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
        double deltaX = maxWidth + 20;
        breadControlNode.setOffset( 0, 0 );
        meatControlNode.setOffset( deltaX, 0 );
        cheeseControlNode.setOffset( 2 * deltaX, 0 );
        
        // layout, origin at upper-left corner of box
        boxNode.setOffset( 0, 0 );
        double x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        double y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 10;
        titleNode.setOffset( x, y );
        x = boxNode.getFullBoundsReference().getCenterX() - ( controlsNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( controlsNode );
        y = boxNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( controlsNode ) + 15;
        controlsNode.setOffset( x, y );
        
        update();
    }
    
    private void update() {
        
        // bread
        if ( model.getBread() < bread.size() ) {
            while ( model.getBread() < bread.size() ) {
                BreadNode node = bread.get( bread.size() - 1 );
                imagesParent.removeChild( node );
                bread.remove( node );
            }
        }
        else {
            while ( model.getBread() > bread.size() ) {
                BreadNode node = new BreadNode();
                imagesParent.addChild( node );
                node.scale( REACTANTS_SCALE );
                setRandomOffset( node );
                bread.add( node );
            }
        }
        
        // meat
        if ( model.getMeat() < meat.size() ) {
            while ( model.getMeat() < meat.size() ) {
                MeatNode node = meat.get( meat.size() - 1 );
                imagesParent.removeChild( node );
                meat.remove( node );
            }
        }
        else {
            while ( model.getMeat() > meat.size() ) {
                MeatNode node = new MeatNode();
                imagesParent.addChild( node );
                node.scale( REACTANTS_SCALE );
                setRandomOffset( node );
                meat.add( node );
            }
        }
        
        // cheese
        if ( model.getCheese() < cheese.size() ) {
            while ( model.getCheese() < cheese.size() ) {
                CheeseNode node = cheese.get( cheese.size() - 1 );
                imagesParent.removeChild( node );
                cheese.remove( node );
            }
        }
        else {
            while ( model.getCheese() > cheese.size() ) {
                CheeseNode node = new CheeseNode();
                imagesParent.addChild( node );
                node.scale( REACTANTS_SCALE );
                setRandomOffset( node );
                cheese.add( node );
            }
        }
    }
    
    private void setRandomOffset( PNode node ) {
        double xMargin = ( 2 * X_MARGIN ) + node.getFullBoundsReference().getWidth();
        double yMargin = ( 2 * Y_MARGIN ) + node.getFullBoundsReference().getHeight();
        Point2D p = boxNode.getRandomPoint( xMargin, yMargin );
        node.setOffset( p );
    }
}
