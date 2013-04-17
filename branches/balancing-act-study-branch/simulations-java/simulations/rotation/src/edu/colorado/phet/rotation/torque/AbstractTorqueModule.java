// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:57:07 AM
 */
public abstract class AbstractTorqueModule extends AbstractRotationModule {
    public AbstractTorqueModule( String name, JFrame parentFrame ) {
        super( name, parentFrame );
    }

    protected RotationModel createModel( ConstantDtClock clock ) {
        return new TorqueModel( clock );
    }

    public TorqueModel getTorqueModel() {
        return (TorqueModel) getRotationModel();
    }
}
