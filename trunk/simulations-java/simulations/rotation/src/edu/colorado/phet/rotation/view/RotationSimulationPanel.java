package edu.colorado.phet.rotation.view;

import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.RotationControlPanel;
import edu.colorado.phet.rotation.RotationModule;

import javax.swing.*;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:13:21 AM
 */
public class RotationSimulationPanel extends AbstractRotationSimulationPanel {
    public RotationSimulationPanel( AbstractRotationModule rotationModule ) {
        super( rotationModule );
    }

    protected JComponent createControlPanel() {
        return new RotationControlPanel( getRotationGraphSet(), getGraphSetModel(), ( (RotationModule)getAbstractRotationModule() ).getVectorViewModel() );
    }

    protected RotationPlayAreaNode createPlayAreaNode() {
        return new RotationPlayAreaNode( getAbstractRotationModule().getRotationModel(), ( (RotationModule)getAbstractRotationModule() ).getVectorViewModel() );
    }
}
