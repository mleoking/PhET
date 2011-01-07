// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:54:25 PM
 */
public class AngMomSimulationPanel extends TorqueSimulationPanel {
    public AngMomSimulationPanel( AngularMomentumModule angularMomentumModule, JFrame parentFrame ) {
        super( angularMomentumModule, parentFrame );
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new AngMomControlPanel( rulerNode, getRotationGraphSet(), getGraphSetModel(), (AbstractTorqueModule) getAbstractRotationModule(), getVectorViewModel(), getAngleUnitModel() );//todo: better typing
    }

    protected GraphSuiteSet createRotationGraphSet() {
        return new AngMomGraphSet( this, (TorqueModel) getRotationModel(), getAngleUnitModel() );
    }
}
