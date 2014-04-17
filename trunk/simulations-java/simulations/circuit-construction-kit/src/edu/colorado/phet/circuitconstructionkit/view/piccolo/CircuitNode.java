// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:37 PM
 */

public class CircuitNode extends PhetPNode {
    private CCKModel cckModel;
    private Circuit circuit;
    private Component component;
    private CCKModule module;
    private BooleanProperty readoutsVisibleProperty;
    private ReadoutSetNode readoutLayer;
    private PNode electronLayer;
    private PNode solderLayer;
    private PNode branchLayer;
    private PNode junctionLayer;
    private ClipFactory clipFactory;
    private BranchNodeFactory branchNodeFactory;
    private boolean changingLifelike;
    private ReadoutSetNode editingReadoutLayer;

    public CircuitNode( final CCKModel cckModel, final Circuit circuit, final JComponent component, CCKModule module, BranchNodeFactory branchNodeFactory, BooleanProperty readoutsVisibleProperty, final BooleanProperty lifelikeProperty ) {
        this.branchNodeFactory = branchNodeFactory;
        this.cckModel = cckModel;
        this.circuit = circuit;
        this.component = component;
        this.module = module;
        this.readoutsVisibleProperty = readoutsVisibleProperty;
        solderLayer = new PNode();
        branchLayer = new PNode();
        junctionLayer = new PNode();
        clipFactory = new ClipFactory() {//clips are used instead of drawing the component on top of the electron because that obscures the junction graphics

            public Shape getClip( ElectronNode electronNode ) {
                if ( !changingLifelike ) {
                    Branch branch = electronNode.getElectron().getBranch();
                    BranchNode node = getNode( branch );
                    if ( node == null ) {
                        new RuntimeException( "Null node for branch: " + branch ).printStackTrace();
                        //during the schematic/lifelike switch, this code is called sometimes
                        return null;
                    }
                    else {
                        return node.getClipShape( electronLayer.getParent() );
                    }
                }
                else {
                    return null;
                }
            }
        };
        electronLayer = new ElectronSetNode( this, cckModel );
        readoutLayer = new ReadoutSetNode( module, circuit );

        editingReadoutLayer = new ReadoutSetNode( module, circuit );

        addChild( solderLayer );
        addChild( branchLayer );
        addChild( junctionLayer );

        addChild( electronLayer );
        addChild( readoutLayer );
        addChild( editingReadoutLayer );

        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                addBranchNode( branch );
            }

            public void junctionAdded( final Junction junction ) {
                final SolderNode solderNode = new SolderNode( circuit, junction, Color.gray );
                solderLayer.addChild( solderNode );

                final JunctionNode junctionNode = new JunctionNode( cckModel, junction, CircuitNode.this, component );
                junctionLayer.addChild( junctionNode );

                final CircuitListenerAdapter removalListener = new CircuitListenerAdapter() {
                    public void junctionRemoved( Junction removedJunction ) {
                        if ( removedJunction == junction ) {
                            junctionLayer.removeChild( junctionNode );
                            solderLayer.removeChild( solderNode );
                        }
                    }
                };
                circuit.addCircuitListener( removalListener );
            }

            public void selectionChanged() {
                updateBranchOrder();
            }

            public void branchRemoved( Branch branch ) {
                removeBranchNode( branch );
            }
        } );
        readoutsVisibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                CircuitNode.this.updateReadoutsVisible();
            }
        } );
        lifelikeProperty.addObserver( new SimpleObserver() {
            public void update() {
                changingLifelike = true;//disable clip computations while some nodes may not have graphics.
                Branch[] orderedList = getBranchOrder();
                removeBranchGraphics();
                for ( int i = 0; i < orderedList.length; i++ ) {
                    Branch branch = orderedList[i];
                    if ( branch instanceof Wire ) {
                        Wire wire = (Wire) branch;
                        wire.setThickness( lifelikeProperty.get() ? Wire.LIFELIKE_THICKNESS : Wire.SCHEMATIC_THICKNESS );
                    }
                    addBranchNode( branch );
                }
                changingLifelike = false;
                for ( int i = 0; i < electronLayer.getChildrenCount(); i++ ) {//notify electrons to recompute their clips
                    ElectronNode electronNode = (ElectronNode) electronLayer.getChild( i );
                    electronNode.update();
                }
            }
        } );
    }

    private void removeBranchNode( Branch branch ) {
        for ( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
            BranchNode branchNode = (BranchNode) branchLayer.getChild( i );
            if ( branchNode.getBranch() == branch ) {
                removeBranchGraphic( branchNode );
                i--;
            }
        }
        updateBranchOrder();
    }

    private void updateBranchOrder() {
        moveSwitchesToFront();
        moveSelectedBranchesToFront();
    }

    private void moveSwitchesToFront() {
        for ( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
            BranchNode pNode = (BranchNode) branchLayer.getChild( i );
            if ( pNode.getBranch() instanceof Switch ) {
                pNode.moveToFront();
            }
        }
    }

    private void moveSelectedBranchesToFront() {
        for ( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
            BranchNode pNode = (BranchNode) branchLayer.getChild( i );
            if ( pNode.getBranch().isSelected() ) {
                pNode.moveToFront();
            }
        }
    }

    private void addBranchNode( Branch branch ) {
        branchLayer.addChild( createNode( branch ) );
        updateBranchOrder();
        updateReadoutsVisible();
    }

    private BranchNode createNode( Branch branch ) {
        return branchNodeFactory.createNode( branch );
    }

    private BranchNode getNode( Branch branch ) {
        for ( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
            if ( ( (BranchNode) branchLayer.getChild( i ) ).getBranch() == branch ) {
                return (BranchNode) branchLayer.getChild( i );
            }
        }
        return null;
    }

    private void removeBranchGraphic( BranchNode branchNode ) {
        branchLayer.removeChild( branchNode );
    }

    public Circuit getCircuit() {
        return cckModel.getCircuit();
    }

    public boolean isElectronsVisible() {
        return electronLayer.getVisible();
    }

    public void setElectronsVisible( boolean b ) {
        electronLayer.setVisible( b );
    }

    private void updateReadoutsVisible() {
        readoutLayer.setVisible( readoutsVisibleProperty.get() );
        readoutLayer.setAllReadoutsVisible( readoutsVisibleProperty.get() );
    }

    public ClipFactory getClipFactory() {
        return clipFactory;
    }

    public int getNumJunctionNodes() {
        return junctionLayer.getChildrenCount();
    }

    public JunctionNode getJunctionNode( int i ) {
        return (JunctionNode) junctionLayer.getChild( i );
    }

    public int getNumBranchNodes() {
        return branchLayer.getChildrenCount();
    }

    public BranchNode getBranchNode( int i ) {
        return (BranchNode) branchLayer.getChild( i );
    }

    private void removeBranchGraphics() {
        while ( getNumBranchNodes() > 0 ) {
            removeBranchNode( 0 );
        }
    }

    private void removeBranchNode( int i ) {
        BranchNode node = (BranchNode) branchLayer.getChild( i );
        node.delete();
        branchLayer.removeChild( i );
        //todo detach listeners.
    }

    private Branch[] getBranchOrder() {//this is a workaround because there is no model-representation for layering.
        ArrayList list = new ArrayList();
        for ( int i = 0; i < getNumBranchNodes(); i++ ) {
            list.add( getBranchNode( i ).getBranch() );
        }
        return (Branch[]) list.toArray( new Branch[0] );
    }

    public java.awt.geom.AffineTransform getTransformForZoom( double zoom, CCKSimulationPanel panel ) {
        double scale = 1.0 / zoom;
        AffineTransform preTx = getTransform();

        //setup the desired final state, after zoom.
        setScale( 1.0 );
        setOffset( 0, 0 );
        scaleAboutPoint( scale, 5, 5 );

        AffineTransform postTx = getTransform();
        //now go from start to finish
        setTransform( preTx );
        return postTx;
    }

    public boolean isReadoutVisible( Branch branch ) {
        return readoutLayer.isReadoutVisible( branch );
    }

    public boolean isReadoutGraphicVisible() {
        return readoutLayer.getVisible();
    }

    public void addBranchNodeFactoryListener( BranchNodeFactory.Listener listener ) {
        branchNodeFactory.addListener( listener );
    }

    //For the black-box feature, make current elements of the circuit unpickable so the user can't drag them
    public void makeChildrenUnpickable( PNode layer ) {
        for ( Object node : layer.getChildrenReference() ) {
            PNode n = (PNode) node;
            n.setPickable( false );
            while ( n.getInputEventListeners().length > 0 ) {
                n.removeInputEventListener( n.getInputEventListeners()[0] );
            }
        }
    }

    //For the black-box feature, make current elements of the circuit unpickable so the user can't drag them
    public void makeElementsUnpickable() {
        makeChildrenUnpickable( solderLayer );
        makeChildrenUnpickable( branchLayer );
        makeChildrenUnpickable( junctionLayer );
    }

    //Hide the interior branches inside the black box
    public void makeBranchesInvisible() {
        for ( int i = 0; i < getNumBranchNodes(); i++ ) {
            BranchNode branchNode = getBranchNode( i );
            Branch branch = branchNode.getBranch();
            Junction start = branch.getStartJunction();
            Junction end = branch.getEndJunction();
            int starts = getCircuit().getAdjacentBranches( start ).length;
            int ends = getCircuit().getAdjacentBranches( end ).length;
            branchNode.setVisible( starts == 1 || ends == 1 );
        }
    }
}
