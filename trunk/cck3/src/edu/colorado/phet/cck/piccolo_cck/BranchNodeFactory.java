package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.grabbag.GrabBagResistor;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.*;
import edu.colorado.phet.cck.piccolo_cck.lifelike.*;
import edu.colorado.phet.cck.piccolo_cck.schematic.*;

import javax.swing.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 1:12:06 AM
 * Copyright (c) Oct 6, 2006 by Sam Reid
 */

public class BranchNodeFactory {
    private boolean lifelike = true;
    private CCKModel cckModel;
    private JComponent component;
    private ICCKModule module;

    public BranchNodeFactory( CCKModel cckModel, JComponent component, ICCKModule module, boolean lifelike ) {
        this.cckModel = cckModel;
        this.component = component;
        this.module = module;
        setLifelike( lifelike );
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
            return new SchematicResistorNode( cckModel, (GrabBagResistor)branch, component, module );
        }
        else if( branch instanceof Resistor ) {
            return new SchematicResistorNode( cckModel, (Resistor)branch, component, module );
        }
        else if( branch instanceof ACVoltageSource ) {
            return new SchematicACNode( cckModel, (ACVoltageSource)branch, component, module );
        }
        else if( branch instanceof Battery ) {
            return new SchematicBatteryNode( cckModel, (Battery)branch, component, module );
        }
        else if( branch instanceof Bulb ) {
            return new TotalBulbComponentNode( cckModel, (Bulb)branch, component, module );
        }
        else if( branch instanceof Switch ) {
            return new SchematicSwitchNode( cckModel, (Switch)branch, component );
        }
        else if( branch instanceof Capacitor ) {
            return new SchematicCapacitorNode( cckModel, (Capacitor)branch, component, module );
        }
        else if( branch instanceof Inductor ) {
            return new SchematicInductorNode( cckModel, (Inductor)branch, component, module );
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

    public void setLifelike( boolean lifelike ) {
        this.lifelike = lifelike;
        notifyDisplayStyleChanged();
    }

    public boolean isLifelike() {
        return lifelike;
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void displayStyleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDisplayStyleChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.displayStyleChanged();
        }
    }
}
