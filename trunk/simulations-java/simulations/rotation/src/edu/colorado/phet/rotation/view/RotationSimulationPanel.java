package edu.colorado.phet.rotation.view;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.controls.RotationControlPanel;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:13:21 AM
 */
public class RotationSimulationPanel extends AbstractRotationSimulationPanel {
    public RotationSimulationPanel( AbstractRotationModule rotationModule, final JFrame parentFrame ) {
        super( rotationModule, parentFrame );
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new RotationControlPanel( rulerNode, getRotationGraphSet(), getGraphSetModel(), getAbstractRotationModule().getVectorViewModel(), parentFrame, getAbstractRotationModule().getRotationModel().getRotationBody( 0 ), getAbstractRotationModule().getRotationModel().getRotationBody( 1 ), getAbstractRotationModule(), super.getAngleUnitModel(), getRotationModel().getRotationPlatform() );
    }

    protected RotationPlayAreaNode createPlayAreaNode() {
        return new RotationSimPlayAreaNode( getAbstractRotationModule().getRotationModel(), getAbstractRotationModule().getVectorViewModel(), getAngleUnitModel() );
    }
}
