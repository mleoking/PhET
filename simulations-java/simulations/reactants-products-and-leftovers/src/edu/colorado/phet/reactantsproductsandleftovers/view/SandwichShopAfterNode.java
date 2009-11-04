package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichShopAfterNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = new PDimension( 400, 300 );
    private static final double DISPLAYS_X_SPACING = 40;
    private static final double Y_MARGIN = 25;
    private static final double IMAGE_SCALE = 0.25; //XXX
    
    private final SandwichShopModel model;

    private final BoxNode boxNode;
    private final PComposite sandwichesParent, breadParent, meatParent, cheeseParent;
    private final ArrayList<SubstanceNode> sandwichList;
    private final ArrayList<SubstanceNode> breadList;
    private final ArrayList<SubstanceNode> meatList;
    private final ArrayList<SubstanceNode> cheeseList;
    private final PNode sandwichQuantityDisplayNode;
    
    public SandwichShopAfterNode( final SandwichShopModel model ) {
        super();
        
        this.model = model;
        model.getReaction().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        sandwichList = new ArrayList<SubstanceNode>();
        breadList = new ArrayList<SubstanceNode>();
        meatList = new ArrayList<SubstanceNode>();
        cheeseList = new ArrayList<SubstanceNode>();
        
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        sandwichesParent = new PComposite();
        addChild( sandwichesParent );
        
        breadParent = new PComposite();
        addChild( breadParent );
        
        meatParent = new PComposite();
        addChild( meatParent );
        
        cheeseParent = new PComposite();
        addChild( cheeseParent );
        
        PText titleNode = new PText( RPALStrings.LABEL_AFTER_SANDWICH );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        PNode valuesNode = new PNode();
        addChild( valuesNode );
        sandwichQuantityDisplayNode = new QuantityDisplayNode( model.getSandwich(), SandwichShopDefaults.QUANTITY_RANGE, IMAGE_SCALE );
        PNode breadQuantityDisplayNode = new QuantityDisplayNode( model.getBread(), SandwichShopDefaults.QUANTITY_RANGE, IMAGE_SCALE );
        PNode meatQuantityDisplayNode = new QuantityDisplayNode( model.getMeat(), SandwichShopDefaults.QUANTITY_RANGE, IMAGE_SCALE );
        PNode cheeseQuantityDisplayNode = new QuantityDisplayNode( model.getCheese(), SandwichShopDefaults.QUANTITY_RANGE, IMAGE_SCALE );
        valuesNode.addChild( sandwichQuantityDisplayNode );
        valuesNode.addChild( breadQuantityDisplayNode );
        valuesNode.addChild( meatQuantityDisplayNode );
        valuesNode.addChild( cheeseQuantityDisplayNode );
        double sandwichWidth = sandwichQuantityDisplayNode.getFullBoundsReference().getWidth();
        double breadWidth = breadQuantityDisplayNode.getFullBoundsReference().getWidth();
        double meatWidth = meatQuantityDisplayNode.getFullBoundsReference().getWidth();
        double cheeseWidth = cheeseQuantityDisplayNode.getFullBoundsReference().getWidth();
        double maxWidth = Math.max( sandwichWidth, Math.max( breadWidth, Math.max( meatWidth, cheeseWidth ) ) );
        double deltaX = maxWidth + DISPLAYS_X_SPACING;
        double xOffset = 0;
        sandwichQuantityDisplayNode.setOffset( xOffset, 0 );
        xOffset += deltaX;
        breadQuantityDisplayNode.setOffset( xOffset, 0 );
        xOffset += deltaX;
        meatQuantityDisplayNode.setOffset( xOffset, 0 );
        xOffset += deltaX;
        cheeseQuantityDisplayNode.setOffset( xOffset, 0 );
        
        // leftovers label
        double width = cheeseQuantityDisplayNode.getFullBoundsReference().getMaxX() - breadQuantityDisplayNode.getFullBoundsReference().getMinX();
        PNode leftoversLabelNode = new LeftoversLabelNode( width );
        addChild( leftoversLabelNode ); // add after using valueNodes bounds
        
        // layout, origin at upper-left corner of box
        boxNode.setOffset( 0, 0 );
        // title
        double x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        double y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 10;
        titleNode.setOffset( x, y );
        // value display
        x = boxNode.getFullBoundsReference().getCenterX() - ( valuesNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( valuesNode ) + 15; //XXX
        y = boxNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( valuesNode ) + 15;
        valuesNode.setOffset( x, y );
        // sandwiches
        x = 20; //XXX
        y = boxNode.getFullBoundsReference().getHeight() - 50; //XXX
        sandwichesParent.setOffset( x, y );
        // bread
        x =+ 125; //XXX
        breadParent.setOffset( x, y );
        // meat
        x += 100; //XXX
        meatParent.setOffset( x, y );
        // cheese
        x += 100; //XXX
        cheeseParent.setOffset( x, y );
        // leftovers label
        x = 125; //XXX
        y = valuesNode.getFullBoundsReference().getMaxY() + 2;
        leftoversLabelNode.setOffset( x, y );

        update();
    }
    
    private void update() {
        
        sandwichQuantityDisplayNode.setVisible( model.getReaction().isReaction() );
        
        // sandwiches
        if ( model.getSandwich().getQuantity() < sandwichList.size() ) {
            while ( model.getSandwich().getQuantity() < sandwichList.size() ) {
                SubstanceNode node = sandwichList.get( sandwichList.size() - 1 );
                node.cleanup();
                sandwichesParent.removeChild( node );
                sandwichList.remove( node );
            }
        }
        else {
            while ( model.getSandwich().getQuantity() > sandwichList.size() ) {
                SubstanceNode node = new SubstanceNode( model.getSandwich() );
                sandwichesParent.addChild( node );
                sandwichList.add( node );
                node.scale( IMAGE_SCALE );
                if ( sandwichesParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = sandwichesParent.getChild( sandwichesParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - PNodeLayoutUtils.getOriginYOffset( node )- Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // bread
        if ( model.getBread().getLeftovers() < breadList.size() ) {
            while ( model.getBread().getLeftovers() < breadList.size() ) {
                SubstanceNode node = breadList.get( breadList.size() - 1 );
                node.cleanup();
                breadParent.removeChild( node );
                breadList.remove( node );
            }
        }
        else {
            while ( model.getBread().getLeftovers() > breadList.size() ) {
                SubstanceNode node = new SubstanceNode( model.getBread() );
                breadParent.addChild( node );
                breadList.add( node );
                node.scale( IMAGE_SCALE );
                if ( breadParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = breadParent.getChild( breadParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // meat
        if ( model.getMeat().getLeftovers() < meatList.size() ) {
            while ( model.getMeat().getLeftovers() < meatList.size() ) {
                SubstanceNode node = meatList.get( meatList.size() - 1 );
                node.cleanup();
                meatParent.removeChild( node );
                meatList.remove( node );
            }
        }
        else {
            while ( model.getMeat().getLeftovers() > meatList.size() ) {
                SubstanceNode node = new SubstanceNode( model.getMeat() );
                meatParent.addChild( node );
                meatList.add( node );
                node.scale( IMAGE_SCALE );
                if ( meatParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = meatParent.getChild( meatParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // cheese
        if ( model.getCheese().getLeftovers() < cheeseList.size() ) {
            while ( model.getCheese().getLeftovers() < cheeseList.size() ) {
                SubstanceNode node = cheeseList.get( cheeseList.size() - 1 );
                node.cleanup();
                cheeseParent.removeChild( node );
                cheeseList.remove( node );
            }
        }
        else {
            while ( model.getCheese().getLeftovers() > cheeseList.size() ) {
                SubstanceNode node = new SubstanceNode( model.getCheese() );
                cheeseParent.addChild( node );
                cheeseList.add( node );
                node.scale( IMAGE_SCALE );
                if ( cheeseParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = cheeseParent.getChild( cheeseParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
    }
}
