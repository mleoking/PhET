/**
 * Class: EmfModel Package: edu.colorado.phet.emf.model Author: Another Guy
 * Date: May 27, 2003
 */

package edu.colorado.phet.radiowaves.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common_1200.model.BaseModel;
import edu.colorado.phet.common_1200.model.clock.AbstractClock;
import edu.colorado.phet.radiowaves.model.movement.MovementType;

public class EmfModel extends BaseModel {

    private ArrayList transmittingElectrons = new ArrayList();
    private boolean staticFieldEnabled;
    private boolean dynamicFieldEnabled;

    public EmfModel( IClock clock ) {
    //        super( clock );
    }


    public void clockTicked( AbstractClock iClock, double v ) {
        // This makes the clock static
        super.clockTicked( iClock, v );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
    }

    public void addTransmittingElectrons( Electron e ) {
        transmittingElectrons.add( e );
    }

    public void setTransmittingElectronMovementStrategy( MovementType movementStrategy ) {
        for ( int i = 0; i < transmittingElectrons.size(); i++ ) {
            Electron electron = (Electron) transmittingElectrons.get( i );
            electron.setMovementStrategy( movementStrategy );
        }
    }

    public void setStaticFieldEnabled( boolean enabled ) {
        staticFieldEnabled = enabled;
    }

    public boolean isStaticFieldEnabled() {
        return staticFieldEnabled;
    }

    public void setDynamicFieldEnabled( boolean enabled ) {
        dynamicFieldEnabled = enabled;
    }

    public boolean isDynamicFieldEnabled() {
        return dynamicFieldEnabled;
    }

    //
    // Static fields and methods
    //
    public static double s_fieldWidth = 800;

    public void setTransmittingFrequency( float freq ) {
        for ( int i = 0; i < transmittingElectrons.size(); i++ ) {
            Electron electron = (Electron) transmittingElectrons.get( i );
            electron.setFrequency( freq );
        }
    }

    public void setTransmittingAmplitude( float amplitude ) {
        for ( int i = 0; i < transmittingElectrons.size(); i++ ) {
            Electron electron = (Electron) transmittingElectrons.get( i );
            electron.setAmplitude( amplitude );
        }
    }

}
