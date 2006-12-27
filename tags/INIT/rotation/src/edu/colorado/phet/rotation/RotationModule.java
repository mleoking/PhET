package edu.colorado.phet.rotation;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 * Copyright (c) Dec 27, 2006 by Sam Reid
 */

public class RotationModule extends Module {

    public RotationModule() {
        super( "Rotation", createClock() );
        setModel( new BaseModel() );
        setSimulationPanel( new RotationSimulationPanel() );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1.0 );
    }
}
