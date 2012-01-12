// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;

/**
 * Created by: Sam
 * Oct 24, 2007 at 10:45:40 AM
 */
public class MomentOfInertiaSimulationPanel extends TorqueSimulationPanel {
    public MomentOfInertiaSimulationPanel( MomentOfInertiaModule momentOfInertiaModule, JFrame parentFrame ) {
        super( momentOfInertiaModule, parentFrame );
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new MomentControlPanel( rulerNode, getRotationGraphSet(), getGraphSetModel(), (AbstractTorqueModule) getAbstractRotationModule(), getVectorViewModel(), getAngleUnitModel() );//todo: better typing
    }

    protected GraphSuiteSet createRotationGraphSet() {
        return new MomentGraphSet( this, (TorqueModel) getRotationModel(), getAngleUnitModel() );
    }
}
