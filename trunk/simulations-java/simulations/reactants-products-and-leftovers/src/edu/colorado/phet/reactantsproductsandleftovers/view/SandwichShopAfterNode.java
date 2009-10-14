package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.*;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichShopAfterNode extends PhetPNode {
    
    private static final boolean DEBUG_LEFTOVERS = false;
    
    private static final PDimension BOX_SIZE = new PDimension( 400, 300 );
    private static final double DISPLAYS_X_SPACING = 30;
    private static final double X_MARGIN = 10;
    private static final double Y_MARGIN = 10;
    private static final double REACTANTS_SCALE = 0.5; //XXX
    
    private final SandwichShop model;

    private final BoxNode boxNode;
    private final PComposite imagesParent;
    private final ArrayList<SandwichNode> sandwiches;
    private final ArrayList<BreadNode> bread;
    private final ArrayList<MeatNode> meat;
    private final ArrayList<CheeseNode> cheese;
    
    public SandwichShopAfterNode( final SandwichShop model ) {
        super();
        
        this.model = model;
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        sandwiches = new ArrayList<SandwichNode>();
        bread = new ArrayList<BreadNode>();
        meat = new ArrayList<MeatNode>();
        cheese = new ArrayList<CheeseNode>();
        
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        imagesParent = new PComposite();
        addChild( imagesParent );
        
        PText titleNode = new PText( RPALStrings.LABEL_AFTER_SANDWICH );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        PNode valuesNode = new PNode();
        addChild( valuesNode );
        PNode breadNode = new BreadLeftoverDisplayNode( model );
        PNode meatNode = new MeatLeftoverDisplayNode( model );
        PNode cheeseNode = new CheeseLeftoverDisplayNode( model );
        PNode sandwichesNode = new SandwichesQuantityDisplayNode( model );
        valuesNode.addChild( breadNode );
        valuesNode.addChild( meatNode );
        valuesNode.addChild( cheeseNode );
        valuesNode.addChild( sandwichesNode );
        double breadWidth = breadNode.getFullBoundsReference().getWidth();
        double meatWidth = meatNode.getFullBoundsReference().getWidth();
        double cheeseWidth = cheeseNode.getFullBoundsReference().getWidth();
        double sandwichWidth = sandwichesNode.getFullBoundsReference().getWidth();
        double maxWidth = Math.max( Math.max( breadWidth, Math.max( meatWidth, cheeseWidth ) ), sandwichWidth );
        double deltaX = maxWidth + DISPLAYS_X_SPACING;
        breadNode.setOffset( 0, 0 );
        meatNode.setOffset( deltaX, 0 );
        cheeseNode.setOffset( 2 * deltaX, 0 );
        sandwichesNode.setOffset( 3 * deltaX, 0 );
        
        PNode leftoversNode = new LeftoversDisplayNode( model );
        if ( DEBUG_LEFTOVERS && PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( leftoversNode );
        }
        
        // layout, origin at upper-left corner of box
        boxNode.setOffset( 0, 0 );
        imagesParent.setOffset( 0, 0 );
        // title
        double x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        double y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 10;
        titleNode.setOffset( x, y );
        // value display
        x = boxNode.getFullBoundsReference().getCenterX() - ( valuesNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( valuesNode );
        y = boxNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( valuesNode ) + 15;
        valuesNode.setOffset( x, y );
        // dev leftovers
        x = boxNode.getFullBoundsReference().getMaxX() - leftoversNode.getFullBoundsReference().getWidth() - 20;
        y = boxNode.getFullBoundsReference().getMaxY() - leftoversNode.getFullBoundsReference().getHeight() - 20;
        leftoversNode.setOffset( x, y );
        
        update();
    }
    
    private void update() {
        
        // sandwiches
        if ( model.getSandwiches() < sandwiches.size() ) {
            while ( model.getSandwiches() < sandwiches.size() ) {
                SandwichNode node = sandwiches.get( sandwiches.size() - 1 );
                imagesParent.removeChild( node );
                sandwiches.remove( node );
            }
        }
        else {
            while ( model.getSandwiches() > sandwiches.size() ) {
                SandwichNode node = new SandwichNode( model.getSandwichFormula() );
                imagesParent.addChild( node );
                node.scale( REACTANTS_SCALE );
                setRandomOffset( node );
                sandwiches.add( node );
            }
        }
        
        // bread
        if ( model.getBreadLeftover() < bread.size() ) {
            while ( model.getBreadLeftover() < bread.size() ) {
                BreadNode node = bread.get( bread.size() - 1 );
                imagesParent.removeChild( node );
                bread.remove( node );
            }
        }
        else {
            while ( model.getBreadLeftover() > bread.size() ) {
                BreadNode node = new BreadNode();
                imagesParent.addChild( node );
                node.scale( REACTANTS_SCALE );
                setRandomOffset( node );
                bread.add( node );
            }
        }
        
        // meat
        if ( model.getMeatLeftover() < meat.size() ) {
            while ( model.getMeatLeftover() < meat.size() ) {
                MeatNode node = meat.get( meat.size() - 1 );
                imagesParent.removeChild( node );
                meat.remove( node );
            }
        }
        else {
            while ( model.getMeatLeftover() > meat.size() ) {
                MeatNode node = new MeatNode();
                imagesParent.addChild( node );
                node.scale( REACTANTS_SCALE );
                setRandomOffset( node );
                meat.add( node );
            }
        }
        
        // cheese
        if ( model.getCheeseLeftover() < cheese.size() ) {
            while ( model.getCheeseLeftover() < cheese.size() ) {
                CheeseNode node = cheese.get( cheese.size() - 1 );
                imagesParent.removeChild( node );
                cheese.remove( node );
            }
        }
        else {
            while ( model.getCheeseLeftover() > cheese.size() ) {
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
