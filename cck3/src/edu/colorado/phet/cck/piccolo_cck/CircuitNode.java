package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:37 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CircuitNode extends PhetPNode {
    private CCKModel cckModel;
    private Circuit circuit;
    private Component component;
    private ICCKModule module;
    private ReadoutSetNode readoutLayer;
    private PNode electronLayer;
    private PNode solderLayer;
    private PNode branchLayer;
    private PNode junctionLayer;
    private ClipFactory clipFactory;
    private BranchNodeFactory branchNodeFactory;
    private boolean changingLifelike;

    public CircuitNode( final CCKModel cckModel, final Circuit circuit, final JComponent component, ICCKModule module, BranchNodeFactory branchNodeFactory ) {
        this.branchNodeFactory = branchNodeFactory;
        this.cckModel = cckModel;
        this.circuit = circuit;
        this.component = component;
        this.module = module;
        solderLayer = new PNode();
        branchLayer = new PNode();
        junctionLayer = new PNode();
        clipFactory = new ClipFactory() {//clips are used instead of drawing the component on top of the electron because that obscures the junction graphics
            public Shape getClip( ElectronNode electronNode ) {
                if( !changingLifelike ) {
                    Branch branch = electronNode.getElectron().getBranch();
                    BranchNode node = getNode( branch );
                    if( node == null ) {
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
        readoutLayer.setVisible( false );
        addChild( solderLayer );
        addChild( branchLayer );
        addChild( junctionLayer );

        addChild( electronLayer );
        addChild( readoutLayer );

        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                addBranchNode( branch );
            }

            public void junctionAdded( Junction junction ) {
                SolderNode solderNode = new SolderNode( circuit, junction, Color.gray );
                solderLayer.addChild( solderNode );

                JunctionNode node = createNode( junction );
                junctionLayer.addChild( node );
            }

            public void junctionRemoved( Junction junction ) {
                for( int i = 0; i < solderLayer.getChildrenCount(); i++ ) {
                    SolderNode solderNode = (SolderNode)solderLayer.getChild( i );
                    if( solderNode.getJunction() == junction ) {
                        solderLayer.removeChild( solderNode );
                        i = -1;
                    }
                }
                for( int i = 0; i < junctionLayer.getChildrenCount(); i++ ) {
                    JunctionNode junctionNode = (JunctionNode)junctionLayer.getChild( i );
                    if( junctionNode.getJunction() == junction ) {
                        removeJunctionGraphic( junctionNode );
                        i = -1;
                    }
                }
            }

            public void selectionChanged() {
                for( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
                    BranchNode pNode = (BranchNode)branchLayer.getChild( i );
                    if( pNode.getBranch().isSelected() ) {
                        pNode.moveToFront();
                    }
                }
            }

            public void branchRemoved( Branch branch ) {
                for( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
                    BranchNode branchNode = (BranchNode)branchLayer.getChild( i );
                    if( branchNode.getBranch() == branch ) {
                        removeBranchGraphic( branchNode );
                        i--;
                    }
                }
            }
        } );
    }

    private void addBranchNode( Branch branch ) {
        branchLayer.addChild( createNode( branch ) );
    }

    private BranchNode createNode( Branch branch ) {
        return branchNodeFactory.createNode( branch );
    }

    private BranchNode getNode( Branch branch ) {
        for( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
            if( ( (BranchNode)branchLayer.getChild( i ) ).getBranch() == branch ) {
                return (BranchNode)branchLayer.getChild( i );
            }
        }
        return null;
    }

    private void removeBranchGraphic( BranchNode branchNode ) {
        branchLayer.removeChild( branchNode );
    }

    private void removeJunctionGraphic( JunctionNode junctionNode ) {
        junctionLayer.removeChild( junctionNode );
    }

    public JunctionNode createNode( Junction junction ) {
        return new JunctionNode( cckModel, junction, this, component );
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

    public void setAllReadoutsVisible( boolean visible ) {
        readoutLayer.setVisible( visible );
    }

    public ClipFactory getClipFactory() {
        return clipFactory;
    }

    public int getNumJunctionNodes() {
        return junctionLayer.getChildrenCount();
    }

    public JunctionNode getJunctionNode( int i ) {
        return (JunctionNode)junctionLayer.getChild( i );
    }

    public int getNumBranchNodes() {
        return branchLayer.getChildrenCount();
    }

    public BranchNode getBranchNode( int i ) {
        return (BranchNode)branchLayer.getChild( i );
    }

    public boolean isLifelike() {
        return branchNodeFactory.isLifelike();
    }

    public void setLifelike( boolean lifelike ) {
        changingLifelike = true;//disable clip computations while some nodes may not have graphics.
        this.branchNodeFactory.setLifelike( lifelike );
        Branch[] orderedList = getBranchOrder();
        removeBranchGraphics();
        for( int i = 0; i < orderedList.length; i++ ) {
            Branch branch = orderedList[i];
            if( branch instanceof Wire ) {
                Wire wire = (Wire)branch;
                wire.setThickness( lifelike ? Wire.LIFELIKE_THICKNESS : Wire.SCHEMATIC_THICKNESS );
            }
            addBranchNode( branch );
        }
        changingLifelike = false;
        for( int i = 0; i < electronLayer.getChildrenCount(); i++ ) {//notify electrons to recompute their clips
            ElectronNode electronNode = (ElectronNode)electronLayer.getChild( i );
            electronNode.update();
        }
    }

    private void removeBranchGraphics() {
        while( getNumBranchNodes() > 0 ) {
            removeBranchNode( 0 );
        }
    }

    private void removeBranchNode( int i ) {
        BranchNode node = (BranchNode)branchLayer.getChild( i );
        node.delete();
        branchLayer.removeChild( i );
        //todo detach listeners.
    }

    private Branch[] getBranchOrder() {//this is a workaround because there is no model-representation for layering.
        ArrayList list = new ArrayList();
        for( int i = 0; i < getNumBranchNodes(); i++ ) {
            list.add( getBranchNode( i ).getBranch() );
        }
        return (Branch[])list.toArray( new Branch[0] );
    }

    public void setZoom( double zoom ) {
        double scale = 1.0 / zoom;
        if( scale != this.getScale() ) {
            AffineTransform preTx = getTransform();

            //setup the desired final state, after zoom. 
            setScale( scale );
            translate( 5 / scale - getFullBounds().getX() / scale - getFullBounds().getWidth() / 2 / scale, 5 / scale - getFullBounds().getY() / scale - getFullBounds().getHeight() / 2 / scale );
            AffineTransform postTx = getTransform();

            //now go from start to finish
            setTransform( preTx );
            animateToTransform( postTx, 2000 );
        }
    }
}
