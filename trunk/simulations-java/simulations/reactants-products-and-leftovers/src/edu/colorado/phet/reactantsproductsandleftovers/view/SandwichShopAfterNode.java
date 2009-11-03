package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.BreadLeftoverDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.CheeseLeftoverDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.MeatLeftoverDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.SandwichQuantityDisplayNode;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichShopAfterNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = new PDimension( 400, 300 );
    private static final double DISPLAYS_X_SPACING = 40;
    private static final double Y_MARGIN = 25;
    private static final double REACTANTS_SCALE = 0.5; //XXX
    
    private final SandwichShopModel model;

    private final BoxNode boxNode;
    private final PComposite sandwichesParent, breadParent, meatParent, cheeseParent;
    private final ArrayList<SandwichNode> sandwichList;
    private final ArrayList<BreadNode> breadList;
    private final ArrayList<MeatNode> meatList;
    private final ArrayList<CheeseNode> cheeseList;
    private final PNode sandwichQuantityDisplayNode;
    
    public SandwichShopAfterNode( final SandwichShopModel model ) {
        super();
        
        this.model = model;
        model.getReaction().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        sandwichList = new ArrayList<SandwichNode>();
        breadList = new ArrayList<BreadNode>();
        meatList = new ArrayList<MeatNode>();
        cheeseList = new ArrayList<CheeseNode>();
        
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
        sandwichQuantityDisplayNode = new SandwichQuantityDisplayNode( model );
        PNode breadQuantityDisplayNode = new BreadLeftoverDisplayNode( model );
        PNode meatQuantityDisplayNode = new MeatLeftoverDisplayNode( model );
        PNode cheeseQuantityDisplayNode = new CheeseLeftoverDisplayNode( model );
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
                SandwichNode node = sandwichList.get( sandwichList.size() - 1 );
                sandwichesParent.removeChild( node );
                sandwichList.remove( node );
            }
        }
        else {
            while ( model.getSandwich().getQuantity() > sandwichList.size() ) {
                SandwichNode node = new SandwichNode( model );
                sandwichesParent.addChild( node );
                sandwichList.add( node );
                node.scale( REACTANTS_SCALE );
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
                BreadNode node = breadList.get( breadList.size() - 1 );
                breadParent.removeChild( node );
                breadList.remove( node );
            }
        }
        else {
            while ( model.getBread().getLeftovers() > breadList.size() ) {
                BreadNode node = new BreadNode();
                breadParent.addChild( node );
                breadList.add( node );
                node.scale( REACTANTS_SCALE );
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
                MeatNode node = meatList.get( meatList.size() - 1 );
                meatParent.removeChild( node );
                meatList.remove( node );
            }
        }
        else {
            while ( model.getMeat().getLeftovers() > meatList.size() ) {
                MeatNode node = new MeatNode();
                meatParent.addChild( node );
                meatList.add( node );
                node.scale( REACTANTS_SCALE );
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
                CheeseNode node = cheeseList.get( cheeseList.size() - 1 );
                cheeseParent.removeChild( node );
                cheeseList.remove( node );
            }
        }
        else {
            while ( model.getCheese().getLeftovers() > cheeseList.size() ) {
                CheeseNode node = new CheeseNode();
                cheeseParent.addChild( node );
                cheeseList.add( node );
                node.scale( REACTANTS_SCALE );
                if ( cheeseParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = cheeseParent.getChild( cheeseParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
    }
}
