// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 18, 2007
 * Time: 12:49:04 PM
 */
public class MomentOfInertiaModule extends AbstractTorqueModule {
    public MomentOfInertiaModule( JFrame parentFrame ) {
        super( RotationStrings.getString( "variable.moment.of.inertia" ), parentFrame );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame ) {
        return new MomentOfInertiaSimulationPanel( this, parentFrame );
    }
}
