package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.CCKLookAndFeel;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Resistor;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:28 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class ToolboxNode extends PhetPNode {
    private PPath toolboxBounds;
    private PhetPCanvas canvas;
    private CCKModel model;
    private ArrayList branchMakers = new ArrayList();
    private static final int TOP_INSET = 50;
    private static final double BETWEEN_INSET = 20;
    private CircuitInteractionModel circuitInteractionModel;

    public ToolboxNode( PhetPCanvas canvas, CCKModel model ) {
        this.canvas = canvas;
        this.model = model;
        this.circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );
        this.toolboxBounds = new PPath( new Rectangle( 100, 600 ) );
        toolboxBounds.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        toolboxBounds.setPaint( CCKLookAndFeel.toolboxColor );
        addChild( toolboxBounds );

        addBranchMaker( new WireMaker() );
        addBranchMaker( new ResistorMaker() );
        addBranchMaker( new BatteryMaker() );
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
        private Branch createdBranch;

        public BranchMaker( String name ) {
            label = new PText( name );
            label.setFont( new Font( "Lucida Sans", Font.PLAIN, 11 ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    // If we haven't created the branch yet
                    if( createdBranch == null ) {
                        createdBranch = createBranch();
                        // Position the branch so it's centered on the mouse event.
                        setBranchLocationFromEvent( event );
                        model.getCircuit().addBranch( createdBranch );
                        model.getCircuit().setSelection( createdBranch );
                    }
                    circuitInteractionModel.translate( createdBranch, getWorldLocation( event ) );
                }

                public void mouseReleased( PInputEvent event ) {
                    circuitInteractionModel.dropBranch( createdBranch );
                    createdBranch = null;
                }
            } );
        }

        public Point2D getWorldLocation( PInputEvent event ) {
            Point2D location = new Point2D.Double( event.getCanvasPosition().getX(),
                                                   event.getCanvasPosition().getY() );
            canvas.getPhetRootNode().globalToWorld( location );
            return location;
        }

        //This assumes the branch is always centered on the mouse.
        private void setBranchLocationFromEvent( PInputEvent event ) {
            Point2D location = getWorldLocation( event );
            double dx = createdBranch.getEndPoint().getX() - createdBranch.getStartPoint().getX();
            double dy = createdBranch.getEndPoint().getY() - createdBranch.getStartPoint().getY();
            createdBranch.getStartJunction().setPosition( location.getX() - dx / 2,
                                                          location.getY() - dy / 2 );
            createdBranch.getEndJunction().setPosition( location.getX() + dx / 2,
                                                        location.getY() + dy / 2 );
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
            WireNode child = new WireNode( model, createWire() );
            child.scale( 40 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        private Wire createWire() {
            return new Wire( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1.5, 0 ) );
        }

        protected Branch createBranch() {
            return createWire();
        }
    }

    class ResistorMaker extends BranchMaker {
        public ResistorMaker() {
            super( "Resistor" );
            ComponentNode child = new ComponentNode( model, createResistor(), CCKImageSuite.getInstance().getLifelikeSuite().getResistorImage() );
            child.scale( 60 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createResistor();
        }

        private Resistor createResistor() {
            return new Resistor( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1 );
        }
    }

    class BatteryMaker extends BranchMaker {
        public BatteryMaker() {
            super( "Battery" );
            ComponentNode child = new ComponentNode( model, createBattery(), CCKImageSuite.getInstance().getLifelikeSuite().getBatteryImage() );
            child.scale( 60 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createBattery();
        }

        private Battery createBattery() {
            return new Battery( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1, 0.01, true );
        }
    }
}
