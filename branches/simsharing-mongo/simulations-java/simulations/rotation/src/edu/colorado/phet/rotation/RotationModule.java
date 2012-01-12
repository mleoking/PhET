// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.view.RotationSimulationPanel;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:05:16 AM
 */
public class RotationModule extends AbstractRotationModule {

    public RotationModule( JFrame parentFrame ) {
        super( RotationStrings.getString( "module.rotation" ), parentFrame );
    }

    protected RotationModel createModel( ConstantDtClock clock ) {
        return new RotationModel( clock );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame ) {
        return new RotationSimulationPanel( this, parentFrame );
    }

    public RotationClock getRotationClock() {
        return (RotationClock) getConstantDTClock();
    }
}
