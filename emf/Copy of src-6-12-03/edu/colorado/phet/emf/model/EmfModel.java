/**
 * Class: EmfModel
 * Package: edu.colorado.phet.emf.model
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.emf.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.emf.model.movement.MovementType;

import java.util.ArrayList;

public class EmfModel extends BaseModel {

    private ArrayList transmittingElectrons = new ArrayList();
    private boolean staticFieldEnabled;
    private boolean dynamicFieldEnabled;

    private EmfModel() {
        this.getClock().setDt( 2 );
    }

    public void addTransmittingElectrons( Electron e ) {
        transmittingElectrons.add( e );
    }

    public void setTransmittingElectronMovementStrategy( MovementType movementStrategy ) {
        for( int i = 0; i < transmittingElectrons.size(); i++ ) {
            Electron electron = (Electron)transmittingElectrons.get( i );
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
    public static double s_fieldWidth = 600;

    private static EmfModel s_instance = new EmfModel();
    public static EmfModel instance() {
        return s_instance;
    }

    public void setTransmittingFrequency( float freq ) {
        for( int i = 0; i < transmittingElectrons.size(); i++ ) {
            Electron electron = (Electron)transmittingElectrons.get( i );
            electron.setFrequency( freq );
        }
    }

}
