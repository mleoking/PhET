package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.model.DefaultTimeSeries;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolox.pswing.MyRepaintManager;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 */

public abstract class AbstractRotationModule extends PiccoloModule {
    private AbstractRotationSimulationPanel rotationSimulationPanel;
    private RotationModel rotationModel;
    private VectorViewModel vectorViewModel;

    

    public static AbstractRotationModule INSTANCE;

    public AbstractRotationModule( String name,JFrame parentFrame ) {//30millis = 0.03 sec
        super( name, new RotationClock( ) );
        INSTANCE = this;
        setModel( new BaseModel() );
        setLogoPanel( null );
        setClockControlPanel( null );
        rotationModel = createModel( (ConstantDtClock) getClock() );
        vectorViewModel = new VectorViewModel();

        rotationSimulationPanel = createSimulationPanel( parentFrame );
        setSimulationPanel( rotationSimulationPanel );

        getClock().addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updateRepaintManager();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                updateRepaintManager();
            }
        } );
        updateRepaintManager();
    }

    protected void updateRepaintManager() {
//        if( SynchronizedPSwingRepaintManager.getInstance() != null ) {
//            SynchronizedPSwingRepaintManager.getInstance().setSynchronousPaint( getClock().isRunning() );
//        }
        if ( MyRepaintManager.getInstance() != null ) {
            MyRepaintManager.getInstance().setCoalesceRectangles( getClock().isRunning() );
        }
    }

    public ConstantDtClock getConstantDTClock() {
        return (ConstantDtClock) getClock();
    }

    protected abstract RotationModel createModel( ConstantDtClock clock );

    protected abstract AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame );

    public RotationModel getRotationModel() {
        return rotationModel;
    }

    public AbstractRotationSimulationPanel getRotationSimulationPanel() {
        return rotationSimulationPanel;
    }

    public RulerNode getRulerNode() {
        return rotationSimulationPanel.getRulerNode();
    }

    public void startApplication() {
        rotationSimulationPanel.startApplication();
    }

    public void resetAll() {
        rotationModel.resetAll();
        rotationSimulationPanel.resetAll();
        vectorViewModel.resetAll();
        if ( getClock() instanceof ConstantDtClock ) {
            ConstantDtClock constantDtClock = (ConstantDtClock) getClock();
            constantDtClock.setDt( RotationClock.DEFAULT_CLOCK_DT );
        }
        DefaultTimeSeries.verifySeriesCleared();
        rotationModel.getTimeSeriesModel().setPaused( false );
    }

    public VectorViewModel getVectorViewModel() {
        return vectorViewModel;
    }

    public void initFinished() {
//        RepaintManager.setCurrentManager( new SynchronizedPSwingRepaintManager() );
        updateRepaintManager();
    }
}
