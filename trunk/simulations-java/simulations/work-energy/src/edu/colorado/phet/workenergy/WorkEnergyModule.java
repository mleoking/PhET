package edu.colorado.phet.workenergy;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;
import edu.colorado.phet.workenergy.view.WorkEnergyObjectNode;

/**
 * @author Sam Reid
 */
public class WorkEnergyModule<ModelType extends WorkEnergyModel> extends Module {
    private ModelType model;

    public WorkEnergyModule( PhetFrame phetFrame, String title, final ModelType model ) {
        super( title, new ConstantDtClock( 30, 1.0 ) );
        this.model = model;
        WorkEnergyCanvas energyCanvas = new WorkEnergyCanvas(model);

        setSimulationPanel( energyCanvas );
        setControlPanel( new WorkEnergyControlPanel() );

        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                super.simulationTimeChanged( clockEvent );
                model.stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    public ModelType getWorkEnergyModel() {
        return model;
    }
}
