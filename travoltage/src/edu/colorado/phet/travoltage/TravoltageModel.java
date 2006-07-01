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

    public TravoltageModel( TravoltageModule travoltageModule ) {
        this.travoltageModule = travoltageModule;
//        addModelElement( new MoveElectrons( travoltageModule.getElectronSetNode() ) );
        addModelElement( new MoveElectronsJade( travoltageModule, travoltageModule.getElectronSetNode() ) );
    }

}
