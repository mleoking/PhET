package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKLookAndFeel;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:28 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class ToolboxNode extends PhetPNode {
    private PPath toolboxBounds;
    private CCKModel model;
    private ArrayList branchMakers = new ArrayList();
    private static final int TOP_INSET = 50;
    private static final double BETWEEN_INSET = 20;

    public ToolboxNode( CCKModel model ) {
        this.model = model;
        this.toolboxBounds = new PPath( new Rectangle( 100, 600 ) );
        toolboxBounds.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        toolboxBounds.setPaint( CCKLookAndFeel.toolboxColor );
        addChild( toolboxBounds );

        addBranchMaker( new WireMaker() );
        addBranchMaker( new WireMaker() );
        addBranchMaker( new WireMaker() );
    }

    private void addBranchMaker( BranchMaker branchMaker ) {
        double y = getYForNextItem();
        branchMaker.setOffset( toolboxBounds.getFullBounds().getWidth() / 2 - branchMaker.getFullBounds().getWidth() / 2, y );
        addChild( branchMaker );
        branchMakers.add( branchMaker );
    }

    private double getYForNextItem() {
        if( branchMakers.size() == 0 ) {
            return TOP_INSET;
        }
        else {
            BranchMaker maker = (BranchMaker)branchMakers.get( branchMakers.size() - 1 );
            return maker.getFullBounds().getMaxY() + BETWEEN_INSET;
        }
    }

    abstract class BranchMaker extends PComposite {//tricky way of circumventing children's behaviors
        private PText label;

        public BranchMaker( String name ) {
            label = new PText( name );
            label.setFont( new Font( "Lucida Sans", Font.PLAIN, 11 ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    super.mouseDragged( event );
                    Branch branch = createBranch();
                    model.getCircuit().addBranch( branch );
                }

                public void mouseReleased( PInputEvent event ) {
                    super.mouseReleased( event );
                }
            } );
        }

        protected abstract Branch createBranch();

        public void setDisplayGraphic( PNode child ) {
            addChild( child );
            addChild( label );
            double labelInsetDY = 4;
            label.setOffset( child.getFullBounds().getWidth() / 2 - label.getFullBounds().getWidth() / 2, child.getFullBounds().getMaxY() + labelInsetDY );
        }
    }

    class WireMaker extends BranchMaker {
        public WireMaker() {
            super( "Wire" );
            WireNode child = new WireNode( new Wire( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1.5, 0 ) ) );
            child.scale( 40 );//todo choose scale based on insets?
            //child.setNonInteractive();//todo how to make this node use graphic code, but different interaction model?  Could use PComposite, see above.
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return new Wire( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1.5, 0 ) );
        }
    }
}
