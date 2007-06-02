package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 */

public abstract class AbstractRotationModule extends PiccoloModule {
    private AbstractRotationSimulationPanel rotationSimulationPanel;

    /*The Physical Model*/
    private RotationModel rotationModel;

    public AbstractRotationModule() {
        super( "Rotation", new SwingClock( 30, 1.0 ) );
        setModel( new BaseModel() );
        setLogoPanel( null );
        setClockControlPanel( null );
        getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                rotationModel.clockTicked( clockEvent );
            }
        } );

        rotationModel = createModel(getClock());

        rotationSimulationPanel = createSimulationPanel();
        setSimulationPanel( rotationSimulationPanel );
    }

    protected abstract RotationModel createModel( IClock clock );

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
