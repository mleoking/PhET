package edu.colorado.phet.fitness.control;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.MotionTimeSeriesModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.colorado.phet.fitness.model.FitnessUnits;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * May 5, 2008 at 7:40:13 AM
 */
public class ChartNode extends PNode {
    private MinimizableControlGraph weightChart;
    private MinimizableControlGraph calorieChart;

    private DefaultTemporalVariable massVar;
    private DefaultTemporalVariable calIntakeVar = new DefaultTemporalVariable();
    private DefaultTemporalVariable calBurnVar = new DefaultTemporalVariable();

    public ChartNode( final FitnessModel model, PhetPCanvas phetPCanvas ) {

        GraphSuiteSet graphSuiteSet = new GraphSuiteSet();
        //todo: remove the following bogus line
        TimeSeriesModel tsm = new MotionTimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );

        massVar = new DefaultTemporalVariable();
        model.addListener( new FitnessModel.Adapter() {
            public void simulationTimeChanged() {
                super.simulationTimeChanged();
                massVar.addValue( model.getUnits().modelToViewMass( model.getHuman().getMass() ), FitnessUnits.secondsToYears( model.getHuman().getAge() ) );
            }
        } );
        massVar.setValue( model.getUnits().modelToViewMass( model.getHuman().getMass() ) );
        model.addListener( new FitnessModel.Adapter() {
            public void unitsChanged() {
                massVar.clear();
                ITemporalVariable itv = model.getHuman().getMassVariable();
                for ( int i = 0; i < itv.getSampleCount(); i++ ) {
                    massVar.addValue( model.getUnits().modelToViewMass( itv.getData( i ).getValue() ), itv.getData( i ).getTime() );
                }
            }
        } );

        final ControlGraph weightGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Weight", Color.blue, "weight", "lbs", "", massVar ), "Weight", 0, 250, tsm, 7.37E8 );
        weightGraph.setHorizontalRange( FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() ),
                                        FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() + FitnessUnits.yearsToSeconds( 10 ) ) );
        weightGraph.setEditable( false );
        weightChart = new MinimizableControlGraph( "Weight", weightGraph );
        weightChart.setAvailableBounds( 600, 125 );

        final ControlGraph calorieGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Calories", Color.green, "Cal", "Cal", new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", model.getHuman().getCaloricIntakeVariable() ), "Calories", 0, 4000, tsm );
        calorieGraph.addSeries( new ControlGraphSeries( "Exercise Calories", Color.red, "cal", FitnessStrings.KCAL, new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", model.getHuman().getCaloricBurnVariable() ) );
        calorieGraph.setHorizontalRange( FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() ),
                                         FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() + FitnessUnits.yearsToSeconds( 10 ) ) );
        calorieGraph.setEditable( false );
        model.addListener( new FitnessModel.Adapter() {
            public void simulationTimeChanged() {
                calorieGraph.forceUpdateAll();
            }
        } );
        calorieChart = new MinimizableControlGraph( "Calories", calorieGraph );
        calorieChart.setAvailableBounds( 600, 125 );

        calorieGraph.addListener( new ControlGraph.Adapter() {
            public void zoomChanged() {
                weightGraph.setHorizontalRange( calorieGraph.getMinDataX(), calorieGraph.getMaxDataX() );
            }
        } );
        weightGraph.addListener( new ControlGraph.Adapter() {
            public void zoomChanged() {
                calorieGraph.setHorizontalRange( weightGraph.getMinDataX(), weightGraph.getMaxDataX() );
            }
        } );

        MinimizableControlGraph[] graphs = {weightChart, calorieChart};
        weightChart.setAlignedLayout( graphs );
        calorieChart.setAlignedLayout( graphs );
        graphSuiteSet.addGraphSuite( graphs );

        addChild( weightChart );
        addChild( calorieChart );
        System.out.println( "a.getFullBounds() = " + weightChart.getFullBounds() );

    }

    public void relayout( int width, int height ) {
        weightChart.setOffset( 0, height - weightChart.getFullBounds().getHeight() - calorieChart.getFullBounds().getHeight() );
        calorieChart.setOffset( 0, weightChart.getFullBounds().getMaxY() );
    }
}
