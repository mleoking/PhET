// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.util.ArrayList;

import javax.swing.JComponent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.ACVoltageSource;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Bulb;
import edu.colorado.phet.circuitconstructionkit.model.components.Capacitor;
import edu.colorado.phet.circuitconstructionkit.model.components.Inductor;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;
import edu.colorado.phet.circuitconstructionkit.model.components.SeriesAmmeter;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.circuitconstructionkit.model.grabbag.GrabBagResistor;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.ACVoltageSourceNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.BatteryNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.CapacitorNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.GrabBagResistorNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.InductorNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.ResistorNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.SeriesAmmeterNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.SwitchNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.WireNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicACNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicBatteryNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicInductorNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicResistorNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicSwitchNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicWireNode;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 1:12:06 AM
 */

public class BranchNodeFactory {
    private CCKModel cckModel;
    private JComponent component;
    private CCKModule module;
    private BooleanProperty lifelikeProperty;
    private ArrayList listeners = new ArrayList();

    public BranchNodeFactory( CCKModel cckModel, JComponent component, CCKModule module, BooleanProperty lifelikeProperty ) {
        this.cckModel = cckModel;
        this.component = component;
        this.module = module;
        this.lifelikeProperty = lifelikeProperty;
        lifelikeProperty.addObserver( new SimpleObserver() {
            public void update() {
                notifyDisplayStyleChanged();
            }
        } );
    }

    protected BranchNode createNode( Branch branch ) {
        if ( lifelikeProperty.get() ) {
            return createLifelikeNode( branch );
        }
        else {
            return createSchematicNode( branch );
        }
    }

    private BranchNode createSchematicNode( final Branch branch ) {
        if ( branch instanceof Wire ) {
            return new SchematicWireNode( cckModel, (Wire) branch, component );
        }
        else if ( branch instanceof GrabBagResistor ) {
            return new SchematicResistorNode( cckModel, (GrabBagResistor) branch, component, module );
        }
        else if ( branch instanceof Resistor ) {
            return new SchematicResistorNode( cckModel, (Resistor) branch, component, module );
        }
        else if ( branch instanceof ACVoltageSource ) {
            return new SchematicACNode( cckModel, (ACVoltageSource) branch, component, module );
        }
        else if ( branch instanceof Battery ) {
            return new SchematicBatteryNode( cckModel, (Battery) branch, component, module );
        }
        else if ( branch instanceof Bulb ) {
            return new TotalBulbComponentNode( cckModel, (Bulb) branch, component, module );
        }
        else if ( branch instanceof Switch ) {
            return new SchematicSwitchNode( cckModel, (Switch) branch, component, module );
        }
        else if ( branch instanceof Capacitor ) {
            return new SchematicCapacitorNode( cckModel, (Capacitor) branch, component, module );
        }
        else if ( branch instanceof Inductor ) {
            return new SchematicInductorNode( cckModel, (Inductor) branch, component, module );
        }
        else if ( branch instanceof SeriesAmmeter ) {
            return new SeriesAmmeterNode( component, (SeriesAmmeter) branch, module );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
    }

    private BranchNode createLifelikeNode( Branch branch ) {
        if ( branch instanceof Wire ) {
            return new WireNode( cckModel, (Wire) branch, component );
        }
        else if ( branch instanceof GrabBagResistor ) {
            return new GrabBagResistorNode( cckModel, (GrabBagResistor) branch, component, module );
        }
        else if ( branch instanceof Resistor ) {
            return new ResistorNode( cckModel, (Resistor) branch, component, module );
        }
        else if ( branch instanceof ACVoltageSource ) {
            return new ACVoltageSourceNode( cckModel, (ACVoltageSource) branch, component, module );
        }
        else if ( branch instanceof Battery ) {
            return new BatteryNode( cckModel, (Battery) branch, component, module );
        }
        else if ( branch instanceof Bulb ) {
            return new TotalBulbComponentNode( cckModel, (Bulb) branch, component, module );
        }
        else if ( branch instanceof Switch ) {
            return new SwitchNode( cckModel, (Switch) branch, component, module );
        }
        else if ( branch instanceof Capacitor ) {
            return new CapacitorNode( cckModel, (Capacitor) branch, component, module );
        }
        else if ( branch instanceof Inductor ) {
            return new InductorNode( cckModel, (Inductor) branch, component, module );
        }
        else if ( branch instanceof SeriesAmmeter ) {
            return new SeriesAmmeterNode( component, (SeriesAmmeter) branch, module );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
    }

    public static interface Listener {
        void displayStyleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDisplayStyleChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.displayStyleChanged();
        }
    }
}
