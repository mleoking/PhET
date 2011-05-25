// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.game;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.torque.teetertotter.view.TeeterTotterTorqueCanvas;

/**
 * @author John Blanco
 */
public class TorqueGameModule extends Module {

    public TorqueGameModule() {
        this( new TeeterTotterTorqueModel() );

        setClockControlPanel( null );
    }

    private TorqueGameModule( TeeterTotterTorqueModel model ) {
        // TODO: i18n
        super( "Game", model.getClock() );
        setSimulationPanel( new TeeterTotterTorqueCanvas( model ) );
    }}
