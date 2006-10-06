package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.grabbag.GrabBagResistor;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.*;
import edu.colorado.phet.cck.piccolo_cck.lifelike.*;
import edu.colorado.phet.cck.piccolo_cck.schematic.SchematicResistorNode;
import edu.colorado.phet.cck.piccolo_cck.schematic.SchematicWireNode;
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
    private ReadoutSetNode readoutLayer;
    private PNode electronLayer;
    private PNode solderLayer;
    private PNode branchLayer;
    private PNode junctionLayer;
    private ClipFactory clipFactory;
    private boolean lifelike = true;

    public CircuitNode( final CCKModel cckModel, final Circuit circuit, final Component component, ICCKModule module ) {
        this.cckModel = cckModel;
        this.circuit = circuit;
        this.component = component;
        this.module = module;
        solderLayer = new PNode();
        branchLayer = new PNode();
        junctionLayer = new PNode();
        clipFactory = new ClipFactory() {
            public Shape getClip( ElectronNode electronNode ) {
                if( electronNode.getElectron().getBranch() instanceof Bulb ) {
                    TotalBulbComponentNode totalBulbComponentNode = (TotalBulbComponentNode)getNode( electronNode.getElectron().getBranch() );
                    return totalBulbComponentNode.getClipShape( electronLayer.getParent() );
                }
                else
                if( electronNode.getElectron().getBranch() instanceof SeriesAmmeter ) {//todo: could extend this to electrons in neighboring branches
                    SeriesAmmeterNode seriesAmmeterNode = (SeriesAmmeterNode)getNode( electronNode.getElectron().getBranch() );
                    return seriesAmmeterNode.getClipShape( electronLayer.getParent() );
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

    private PNode getNode( Branch branch ) {
        for( int i = 0; i < branchLayer.getChildrenCount(); i++ ) {
            if( ( (BranchNode)branchLayer.getChild( i ) ).getBranch() == branch ) {
                return branchLayer.getChild( i );
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

    protected BranchNode createNode( Branch branch ) {
        if( lifelike ) {
            return createLifelikeNode( branch );
        }
        else {
            return createSchematicNode( branch );
        }
    }

    private BranchNode createSchematicNode( final Branch branch ) {
        if( branch instanceof Wire ) {
            return new SchematicWireNode( cckModel, (Wire)branch, component );
        }
        else if( branch instanceof GrabBagResistor ) {
            return new GrabBagResistorNode( cckModel, (GrabBagResistor)branch, component, module );
        }
        else if( branch instanceof Resistor ) {
            return new SchematicResistorNode( cckModel, (Resistor)branch, component, module );
        }
        else if( branch instanceof ACVoltageSource ) {
            return new ACVoltageSourceNode( cckModel, (ACVoltageSource)branch, component, module );
        }
        else if( branch instanceof Battery ) {
            return new BatteryNode( cckModel, (Battery)branch, component, module );
        }
        else if( branch instanceof Bulb ) {
            return new TotalBulbComponentNode( cckModel, (Bulb)branch, component, module );
        }
        else if( branch instanceof Switch ) {
            return new SwitchNode( cckModel, (Switch)branch, component );
        }
        else if( branch instanceof Capacitor ) {
            return new CapacitorNode( cckModel, (Capacitor)branch, component, module );
        }
        else if( branch instanceof Inductor ) {
            return new InductorNode( cckModel, (Inductor)branch, component, module );
        }
        else if( branch instanceof SeriesAmmeter ) {
            return new SeriesAmmeterNode( component, (SeriesAmmeter)branch, module );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
    }

    private BranchNode createLifelikeNode( Branch branch ) {
        if( branch instanceof Wire ) {
            return new WireNode( cckModel, (Wire)branch, component );
        }
        else if( branch instanceof GrabBagResistor ) {
            return new GrabBagResistorNode( cckModel, (GrabBagResistor)branch, component, module );
        }
        else if( branch instanceof Resistor ) {
            return new ResistorNode( cckModel, (Resistor)branch, component, module );
        }
        else if( branch instanceof ACVoltageSource ) {
            return new ACVoltageSourceNode( cckModel, (ACVoltageSource)branch, component, module );
        }
        else if( branch instanceof Battery ) {
            return new BatteryNode( cckModel, (Battery)branch, component, module );
        }
        else if( branch instanceof Bulb ) {
            return new TotalBulbComponentNode( cckModel, (Bulb)branch, component, module );
        }
        else if( branch instanceof Switch ) {
            return new SwitchNode( cckModel, (Switch)branch, component );
        }
        else if( branch instanceof Capacitor ) {
            return new CapacitorNode( cckModel, (Capacitor)branch, component, module );
        }
        else if( branch instanceof Inductor ) {
            return new InductorNode( cckModel, (Inductor)branch, component, module );
        }
        else if( branch instanceof SeriesAmmeter ) {
            return new SeriesAmmeterNode( component, (SeriesAmmeter)branch, module );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
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
        return lifelike;
    }

    public void setLifelike( boolean lifelike ) {
        this.lifelike = lifelike;
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
    }

    private void removeBranchGraphics() {
        while( getNumBranchNodes() > 0 ) {
            removeBranchNode( 0 );
        }
    }

    private void removeBranchNode( int i ) {
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
}
