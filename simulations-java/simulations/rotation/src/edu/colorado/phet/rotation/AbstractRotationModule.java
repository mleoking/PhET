package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.model.RotationModel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 */

public abstract class AbstractRotationModule extends PiccoloModule {
    private AbstractRotationSimulationPanel rotationSimulationPanel;
    private RotationModel rotationModel;//The Physical Model
    private static final double SPEED_SCALE=30;
    public AbstractRotationModule(JFrame parentFrame) {
        super( "Rotation", new ConstantDtClock( 30, 1.0 ) );
//        super( "Rotation", new ConstantDtClock( (int)( 30/SPEED_SCALE ), 1.0/SPEED_SCALE ) );
//        super( "Rotation", new ConstantDtClock( 0, 1.0/SPEED_SCALE ) );
        setModel( new BaseModel() );
        setLogoPanel( null );
        setClockControlPanel( null );
        rotationModel = createModel( (ConstantDtClock)getClock() );

        rotationSimulationPanel = createSimulationPanel(parentFrame );
        setSimulationPanel( rotationSimulationPanel );
    }

    protected abstract RotationModel createModel( ConstantDtClock clock );

    protected abstract AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame);

    public GraphSetModel getGraphSetModel() {
        return rotationSimulationPanel.getGraphSetModel();
    }

    public RotationModel getRotationModel() {
        return rotationModel;
    }

    public AbstractRotationSimulationPanel getRotationSimulationPanel() {
        return rotationSimulationPanel;
    }

    public RulerNode getRulerNode() {
        return rotationSimulationPanel.getRulerNode();
    }
}
