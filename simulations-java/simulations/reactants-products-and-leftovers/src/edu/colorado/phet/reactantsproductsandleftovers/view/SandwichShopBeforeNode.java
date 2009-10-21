package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
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
    private static final double CONTROLS_X_SPACING = 20;
    private static final double Y_MARGIN = 25;
    private static final double REACTANTS_SCALE = 0.5; //XXX
    
    private final SandwichShop model;

    private final BoxNode boxNode;
    private final PComposite breadParent, meatParent, cheeseParent;
    private final ArrayList<BreadNode> breadList;
    private final ArrayList<MeatNode> meatList;
    private final ArrayList<CheeseNode> cheeseList;
    
    public SandwichShopBeforeNode( final SandwichShop model ) {
        super();
        
        this.model = model;
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        breadList = new ArrayList<BreadNode>();
        meatList = new ArrayList<MeatNode>();
        cheeseList = new ArrayList<CheeseNode>();
        
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        breadParent = new PComposite();
        addChild( breadParent );
        
        meatParent = new PComposite();
        addChild( meatParent );
        
        cheeseParent = new PComposite();
        addChild( cheeseParent );
        
        PText titleNode = new PText( RPALStrings.LABEL_BEFORE_SANDWICH );
        titleNode.setFont( new PhetFont( 30 ) );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        PNode controlsNode = new PNode();
        addChild( controlsNode );
        PNode breadControlNode = new BreadQuantityControlNode( model );
        PNode meatControlNode = new MeatQuantityControlNode( model );
        PNode cheeseControlNode = new CheeseQuantityControlNode( model );
        controlsNode.addChild( breadControlNode );
        controlsNode.addChild( meatControlNode );
        controlsNode.addChild( cheeseControlNode );
        double breadWidth = breadControlNode.getFullBoundsReference().getWidth();
        double meatWidth = meatControlNode.getFullBoundsReference().getWidth();
        double cheeseWidth = cheeseControlNode.getFullBoundsReference().getWidth();
        double maxWidth = Math.max( breadWidth, Math.max( meatWidth, cheeseWidth ) );
        double deltaX = maxWidth + CONTROLS_X_SPACING;
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
        x = 65; //XXX
        y = boxNode.getFullBoundsReference().getHeight() - 50; //XXX
        breadParent.setOffset( x, y );
        x += 130; //XXX
        meatParent.setOffset( x, y );
        x += 120; //XXX
        cheeseParent.setOffset( x, y );
        
        update();
    }
    
    private void update() {
        
        // bread
        if ( model.getBread() < breadList.size() ) {
            while ( model.getBread() < breadList.size() ) {
                BreadNode node = breadList.get( breadList.size() - 1 );
                breadParent.removeChild( node );
                breadList.remove( node );
            }
        }
        else {
            while ( model.getBread() > breadList.size() ) {
                BreadNode node = new BreadNode();
                node.scale( REACTANTS_SCALE );
                breadParent.addChild( node );
                breadList.add( node );
                if ( breadParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = breadParent.getChild( breadParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // meat
        if ( model.getMeat() < meatList.size() ) {
            while ( model.getMeat() < meatList.size() ) {
                MeatNode node = meatList.get( meatList.size() - 1 );
                meatParent.removeChild( node );
                meatList.remove( node );
            }
        }
        else {
            while ( model.getMeat() > meatList.size() ) {
                MeatNode node = new MeatNode();
                node.scale( REACTANTS_SCALE );
                meatParent.addChild( node );
                meatList.add( node );
                if ( meatParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = meatParent.getChild( meatParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
        
        // cheese
        if ( model.getCheese() < cheeseList.size() ) {
            while ( model.getCheese() < cheeseList.size() ) {
                CheeseNode node = cheeseList.get( cheeseList.size() - 1 );
                cheeseParent.removeChild( node );
                cheeseList.remove( node );
            }
        }
        else {
            while ( model.getCheese() > cheeseList.size() ) {
                CheeseNode node = new CheeseNode();
                node.scale( REACTANTS_SCALE );
                cheeseParent.addChild( node );
                cheeseList.add( node );
                if ( cheeseParent.getChildrenCount() > 1 ) {
                    double x = 0;
                    double y = cheeseParent.getChild( cheeseParent.getChildrenCount() - 2 ).getFullBoundsReference().getMinY() - Y_MARGIN;
                    node.setOffset( x, y );
                }
            }
        }
    }
}
