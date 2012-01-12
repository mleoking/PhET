// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.model.BaseModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:50:51 AM
 */

public class TravoltageModel extends BaseModel {
    private TravoltageModule travoltageModule;
    private MoveElectronsJade moveElectronsJade;
    private JadeElectronSet jadeElectronSet;
    private MoveToFinger moveToFinger;

    public TravoltageModel( TravoltageModule travoltageModule ) {
        this.travoltageModule = travoltageModule;
        jadeElectronSet = new JadeElectronSet();
        moveElectronsJade = new MoveElectronsJade( jadeElectronSet );
        addModelElement( moveElectronsJade );
        moveToFinger = new MoveToFinger( travoltageModule, jadeElectronSet );
    }

    public void addElectron( JadeElectron jadeElectron ) {
        jadeElectronSet.addElectron( jadeElectron );
    }

    public JadeElectronSet getJadeElectronSet() {
        return jadeElectronSet;
    }

    public void startSpark() {
        if( containsModelElement( moveElectronsJade ) ) {
            removeModelElement( moveElectronsJade );
            addModelElement( moveToFinger );
            notifySparkStarted();
        }
    }

    private ArrayList listeners = new ArrayList();

    public void notifyElectronsExiting() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.electronExitedFinger();
        }
    }

    public static interface Listener {
        void sparkStarted();

        void sparkFinished();

        void electronExitedFinger();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifySparkStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.sparkStarted();
        }
    }

    public void notifySparkFinished() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.sparkFinished();
        }
    }

    public void finishSpark() {
        if( containsModelElement( moveToFinger ) ) {
            removeModelElement( moveToFinger );
            addModelElement( moveElectronsJade );
            notifySparkFinished();
        }
    }
}
