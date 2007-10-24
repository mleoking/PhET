package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:54:25 PM
 */
public class AngMomSimulationPanel extends TorqueSimulationPanel {
    public AngMomSimulationPanel( AngularMomentumModule angularMomentumModule, JFrame parentFrame ) {
        super(angularMomentumModule, parentFrame );
    }
    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new AngMomControlPanel( rulerNode, getRotationGraphSet(), getGraphSetModel(), (AbstractTorqueModule) getAbstractRotationModule(), getVectorViewModel() );//todo: better typing
    }
    protected GraphSuiteSet createRotationGraphSet() {
        return new AngMomGraphSet( this, (TorqueModel) getRotationModel(), getAngleUnitModel() );
    }
}
