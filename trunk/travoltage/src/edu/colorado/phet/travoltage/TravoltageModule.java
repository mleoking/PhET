/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PiccoloModule;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:22:45 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageModule extends PiccoloModule {
    private TravoltagePanel travoltagePanel;
    private TravoltageModel travoltageModel;

    public TravoltageModule() {
        super( "Travoltage", createClock() );
        travoltagePanel = new TravoltagePanel( this );
        setSimulationPanel( travoltagePanel );
        getLegNode().addListener( new PickUpElectrons( this, getLegNode() ) );
//        getLegNode().addListener( new LegNode.Listener() {
//            public void legRotated() {
//                travoltagePanel.remapLocations();
//            }
//        } );
        travoltageModel = new TravoltageModel( this );
        setModel( travoltageModel );
    }

    public LegNode getLegNode() {
        return travoltagePanel.getTravoltageRootNode().getTravoltageBodyNode().getLegNode();
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1.0 );
    }

    public void pickUpElectron() {
        travoltagePanel.getTravoltageRootNode().pickUpElectron();
    }

    public ElectronSetNode getElectronSetNode() {
        return travoltagePanel.getElectronSetNode();
    }

    public ArmNode getArmNode() {
        return travoltagePanel.getTravoltageRootNode().getTravoltageBodyNode().getArmNode();
    }

    public void addElectron( JadeElectron jadeElectron ) {
        travoltageModel.addElectron( jadeElectron );
    }
}
