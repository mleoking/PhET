package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.motion.model.DefaultTimeSeries;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.MyRepaintManager;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 */

public abstract class AbstractRotationModule extends PiccoloModule {
    private AbstractRotationSimulationPanel rotationSimulationPanel;
    private RotationModel rotationModel;
    private VectorViewModel vectorViewModel;

//    public static AbstractRotationModule INSTANCE;

    public AbstractRotationModule( String name, JFrame parentFrame ) {//30millis = 0.03 sec
        super( name, new RotationClock() );
//        INSTANCE = this;
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
        addRepaintOnActivateBehavior();
    }

    protected void updateRepaintManager() {
//        if( SynchronizedPSwingRepaintManager.getInstance() != null ) {
//            SynchronizedPSwingRepaintManager.getInstance().setSynchronousPaint( getClock().isRunning() );
//        }

        //todo: this is disabled until the concurrentmodification exception is resolved when using multiple tabs
        if ( MyRepaintManager.getInstance() != null ) {
//            MyRepaintManager.getInstance().setCoalesceRectangles( getClock().isRunning() );
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

    public void activate() {
        super.activate();
        if ( clockPaused ) {
            getClock().pause();
        }
        else {
            getClock().start();
        }
    }

    private boolean clockPaused = true;

    public void deactivate() {
        this.clockPaused = getClock().isPaused();
        super.deactivate();
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

    public void reset() {
        if ( confirmReset() ) {
            super.reset();
            rotationModel.resetAll();
            rotationSimulationPanel.resetAll();
            vectorViewModel.resetAll();
            if ( getClock() instanceof ConstantDtClock ) {
                ConstantDtClock constantDtClock = (ConstantDtClock) getClock();
                constantDtClock.setDt( RotationClock.DEFAULT_CLOCK_DT );
            }
            DefaultTimeSeries.verifySeriesCleared();
            rotationModel.getTimeSeriesModel().setPaused( true );
        }
    }

    private boolean confirmReset() {
        int val=JOptionPane.showConfirmDialog( rotationSimulationPanel, PhetCommonResources.getString( "ControlPanel.message.confirmResetAll" ));
        return val==JOptionPane.OK_OPTION;
    }

    public VectorViewModel getVectorViewModel() {
        return vectorViewModel;
    }

    public void initFinished() {
//        RepaintManager.setCurrentManager( new SynchronizedPSwingRepaintManager() );
        updateRepaintManager();
    }
}
