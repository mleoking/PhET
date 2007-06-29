package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 */

public abstract class AbstractRotationModule extends PiccoloModule {
    private AbstractRotationSimulationPanel rotationSimulationPanel;
    private RotationModel rotationModel;//The Physical Model

    public AbstractRotationModule() {
        super( "Rotation", new ConstantDtClock( 30, 1.0 ) );
        setModel( new BaseModel() );
        setLogoPanel( null );
        setClockControlPanel( null );
        rotationModel = createModel( (ConstantDtClock)getClock() );

        rotationSimulationPanel = createSimulationPanel();
        setSimulationPanel( rotationSimulationPanel );
    }

    protected abstract RotationModel createModel( ConstantDtClock clock );

    protected abstract AbstractRotationSimulationPanel createSimulationPanel();

    public GraphSetModel getGraphSetModel() {
        return rotationSimulationPanel.getGraphSetModel();
    }

    public RotationModel getRotationModel() {
        return rotationModel;
    }

    public AbstractRotationSimulationPanel getRotationSimulationPanel() {
        return rotationSimulationPanel;
    }

}
