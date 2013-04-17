// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 18, 2007
 * Time: 12:49:08 PM
 */
public class AngularMomentumModule extends AbstractTorqueModule {
    public AngularMomentumModule( JFrame parentFrame ) {
        super( RotationStrings.getString( "variable.angular.momentum" ), parentFrame );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame ) {
        return new AngMomSimulationPanel( this, parentFrame );
    }

}
