package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.*;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
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
    private ArrayList junctionGraphics = new ArrayList();
    private ArrayList branchGraphics = new ArrayList();
    private ReadoutSetNode readoutNode;
    private PNode electronNode;

    public CircuitNode( CCKModel cckModel, Circuit circuit, Component component, ICCKModule module ) {
        this.cckModel = cckModel;
        this.circuit = circuit;
        this.component = component;
        this.module = module;
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                BranchNode branchNode = createNode( branch );
                branchGraphics.add( branchNode );
                addChild( branchNode );
                electronNode.moveToFront();
            }

            public void junctionAdded( Junction junction ) {
                JunctionNode node = createNode( junction );
                junctionGraphics.add( node );
                addChild( node );
                electronNode.moveToFront();
            }

            public void junctionRemoved( Junction junction ) {
                for( int i = 0; i < junctionGraphics.size(); i++ ) {
                    JunctionNode junctionNode = (JunctionNode)junctionGraphics.get( i );
                    if( junctionNode.getJunction() == junction ) {
                        removeJunctionGraphic( junctionNode );
                    }
                }
                electronNode.moveToFront();
            }

            public void selectionChanged() {
                for( int i = 0; i < branchGraphics.size(); i++ ) {
                    BranchNode pNode = (BranchNode)branchGraphics.get( i );
                    if( pNode.getBranch().isSelected() ) {
                        pNode.moveToFront();
                    }
                }
                for( int i = 0; i < junctionGraphics.size(); i++ ) {
                    ( (JunctionNode)junctionGraphics.get( i ) ).moveToFront();
                }
                electronNode.moveToFront();
            }

            public void branchRemoved( Branch branch ) {
                for( int i = 0; i < branchGraphics.size(); i++ ) {
                    BranchNode branchNode = (BranchNode)branchGraphics.get( i );
                    if( branchNode.getBranch() == branch ) {
                        removeBranchGraphic( branchNode );
                        i--;
                    }
                }
                electronNode.moveToFront();
            }
        } );
        electronNode = new ElectronSetNode( cckModel );
        addChild( electronNode );

        readoutNode = new ReadoutSetNode( module, circuit );
        addChild( readoutNode );
    }

    private void removeBranchGraphic( BranchNode branchNode ) {
        branchGraphics.remove( branchNode );
        removeChild( branchNode );
    }

    private void removeJunctionGraphic( JunctionNode junctionNode ) {
        junctionGraphics.remove( junctionNode );
        removeChild( junctionNode );
    }

    public JunctionNode createNode( Junction junction ) {
        return new JunctionNode( cckModel, junction, this, component );
    }

    public BranchNode createNode( Branch branch ) {
        if( branch instanceof Wire ) {
            return new WireNode( cckModel, (Wire)branch, component );
        }
        else if( branch instanceof Resistor ) {
            return new ResistorNode( cckModel, (Resistor)branch, component, module );
        }
        else if( branch instanceof ACVoltageSource ) {
            return new ACVoltageSourceNode( cckModel, (ACVoltageSource)branch, component );
        }
        else if( branch instanceof Battery ) {
            return new ComponentImageNode.BatteryNode( cckModel, (Battery)branch, component, module );
        }
        else if( branch instanceof Bulb ) {
            return new TotalBulbComponentNode( cckModel, (Bulb)branch, component );
        }
        else if( branch instanceof Switch ) {
            return new SwitchNode( cckModel, (Switch)branch, component );
        }
        else if( branch instanceof Capacitor ) {
            return new CapacitorNode( cckModel, (Capacitor)branch, component );
        }
        else if( branch instanceof Inductor ) {
            return new InductorNode( cckModel, (CircuitComponent)branch, component );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
    }

    public Circuit getCircuit() {
        return cckModel.getCircuit();
    }

    public boolean isElectronsVisible() {
        return electronNode.getVisible();
    }

    public void setElectronsVisible( boolean b ) {
        electronNode.setVisible( b );
    }
}
