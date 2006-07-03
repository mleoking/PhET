/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.model.BaseModel;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:50:51 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
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
        }
    }

    public void finishSpark() {
        System.out.println( "TravoltageModel.finishSpark" );
        if( containsModelElement( moveToFinger ) ) {
            removeModelElement( moveToFinger );
            addModelElement( moveElectronsJade );
        }
    }
}
