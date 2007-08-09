package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.TorqueGraphSet;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;

import javax.swing.*;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:02:17 AM
 */
public class TorqueSimulationPanel extends AbstractRotationSimulationPanel {

    public TorqueSimulationPanel( TorqueModule torqueModule, JFrame parentFrame ) {
        super( torqueModule, parentFrame );
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new TorqueControlPanel( getRotationGraphSet(), getGraphSetModel(), (TorqueModule)getAbstractRotationModule() );//todo: better typing
    }

    protected GraphSuiteSet createRotationGraphSet() {
        return new TorqueGraphSet( this, (TorqueModel)getRotationModel(), getAngleUnitModel() );
    }

    protected RotationPlayAreaNode createPlayAreaNode() {
        return new TorqueSimPlayAreaNode( getRotationModel(), new VectorViewModel(), getAngleUnitModel() );
    }
}
