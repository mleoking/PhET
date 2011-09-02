// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkOptions;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkIO;

/**
 * Module for Energy Skate Park Basics.
 *
 * @author Sam Reid
 */
public class EnergySkateParkBasicsModule extends AbstractEnergySkateParkModule {
    public EnergySkateParkBasicsModule( String name, PhetFrame phetFrame ) {
        super( name, phetFrame, new EnergySkateParkOptions() );
        setControlPanel( null );

        getEnergySkateParkSimulationPanel().getRootNode().removeSplineToolbox();
    }

    //Load the specified track and set its roller coaster mode, used when the user presses different track selection buttons
    public void load( String location, boolean rollerCoasterMode ) {
        EnergySkateParkIO.open( location, this );
        getEnergySkateParkModel().setRollerCoasterMode( rollerCoasterMode );
    }
}