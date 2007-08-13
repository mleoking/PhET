package edu.colorado.phet.rotation;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.model.DefaultTimeSeries;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationModel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Dec 27, 2006
 * Time: 11:32:36 AM
 */

public abstract class AbstractRotationModule extends PiccoloModule {
    private AbstractRotationSimulationPanel rotationSimulationPanel;
    private RotationModel rotationModel;
    private VectorViewModel vectorViewModel;

    public static final int DEFAULT_DELAY = 30;
    public static final double DEFAULT_CLOCK_DT = DEFAULT_DELAY / 1000.0;
    static int count;
    static double sum = 0;

    public AbstractRotationModule( JFrame parentFrame ) {//30millis = 0.03 sec
        super( "Rotation", new ConstantDtClock( DEFAULT_DELAY, DEFAULT_CLOCK_DT ) {

            protected void doTick() {
//                QuickProfiler timer=new QuickProfiler( );
                super.doTick();
//                long time=timer.getTime();
//                sum+=time;
//                count++;
//                System.out.println( "count="+count+", tick time="+sum/count );
            }
        } );
        setModel( new BaseModel() );
        setLogoPanel( null );
        setClockControlPanel( null );
        rotationModel = createModel( (ConstantDtClock)getClock() );
        vectorViewModel = new VectorViewModel();

        rotationSimulationPanel = createSimulationPanel( parentFrame );
        setSimulationPanel( rotationSimulationPanel );
    }

    protected abstract RotationModel createModel( ConstantDtClock clock );

    protected abstract AbstractRotationSimulationPanel createSimulationPanel( JFrame parentFrame );

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

    public void startApplication() {
        rotationSimulationPanel.startApplication();
    }

    public void resetAll() {
        rotationModel.resetAll();
        rotationSimulationPanel.resetAll();
        vectorViewModel.resetAll();
        if( getClock() instanceof ConstantDtClock ) {
            ConstantDtClock constantDtClock = (ConstantDtClock)getClock();
            constantDtClock.setDt( DEFAULT_CLOCK_DT );
        }
        DefaultTimeSeries.verifySeriesCleared();
    }

    public VectorViewModel getVectorViewModel() {
        return vectorViewModel;
    }
}
