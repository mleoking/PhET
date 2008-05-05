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
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
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

    private DefaultTemporalVariable massVar = new DefaultTemporalVariable();
    private DefaultTemporalVariable calIntakeVar = new DefaultTemporalVariable();
    private DefaultTemporalVariable calBurnVar = new DefaultTemporalVariable();
    private FitnessModel model;

    public ChartNode( final FitnessModel model, PhetPCanvas phetPCanvas ) {
        this.model = model;
        GraphSuiteSet graphSuiteSet = new GraphSuiteSet();

        //todo: remove the following bogus line
        TimeSeriesModel tsm = new MotionTimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );

        model.addListener( new FitnessModel.Adapter() {
            public void simulationTimeChanged() {
                updateVars();
            }
        } );
        updateVars();
        model.addListener( new FitnessModel.Adapter() {
            public void unitsChanged() {
                syncMassVar();
            }
        } );

        final ControlGraphSeries weightSeries = new ControlGraphSeries( "Weight", Color.blue, "weight", "lbs", "", massVar );
        weightSeries.setDecimalFormat( new DefaultDecimalFormat( "0" ) );
        model.addListener( new FitnessModel.Adapter() {
            public void unitsChanged() {
                weightSeries.setUnits( model.getUnits().getMassUnit() );
            }
        } );
        final ControlGraph weightGraph = new ControlGraph( phetPCanvas, weightSeries, "Weight", 0, 250, tsm, 7.37E8 );
        double DEFAULT_RANGE_YEARS = 5;
        weightGraph.setHorizontalRange( FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() ),
                                        FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() + FitnessUnits.yearsToSeconds( DEFAULT_RANGE_YEARS ) ) );
        weightGraph.setEditable( false );
        weightChart = new MinimizableControlGraph( "Weight", weightGraph );
        weightChart.setAvailableBounds( 600, 125 );

        ControlGraphSeries intakeSeries = new ControlGraphSeries( "Intake", Color.green, "Intake", FitnessStrings.KCAL_PER_DAY, new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", calIntakeVar );
        intakeSeries.setDecimalFormat( new DefaultDecimalFormat( FitnessStrings.KCAL_PER_DAY_FORMAT ) );
        ControlGraphSeries burnSeries = new ControlGraphSeries( "Burned", Color.red, "Burned", FitnessStrings.KCAL_PER_DAY, new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", calBurnVar );
        burnSeries.setDecimalFormat( new DefaultDecimalFormat( FitnessStrings.KCAL_PER_DAY_FORMAT ) );

        final ControlGraph calorieGraph = new ControlGraph( phetPCanvas, intakeSeries, "Calories", 0, 4000, tsm );
        calorieGraph.addSeries( burnSeries );
        calorieGraph.setHorizontalRange( FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() ),
                                         FitnessUnits.secondsToYears( Human.DEFAULT_VALUE.getAgeSeconds() + FitnessUnits.yearsToSeconds( DEFAULT_RANGE_YEARS ) ) );
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

    private void syncMassVar() {
        massVar.clear();
        ITemporalVariable itv = model.getHuman().getMassVariable();
        for ( int i = 0; i < itv.getSampleCount(); i++ ) {
            massVar.addValue( model.getUnits().modelToViewMass( itv.getData( i ).getValue() ), FitnessUnits.secondsToYears( itv.getData( i ).getTime() ) );
        }
    }

    private void updateVars() {
        updateMassVar();
        updateCalIntakeVar();
        updateCalBurnVar();
    }

    private void updateCalBurnVar() {
        double calBurn = model.getHuman().getCaloricBurnVariable().getValue();
        calBurnVar.setValue( calBurn );
        calBurnVar.addValue( calBurn, getAgeYears() );
    }

    private void updateCalIntakeVar() {
        double calIntake = model.getHuman().getCaloricIntakeVariable().getValue();
        calIntakeVar.setValue( calIntake );
        calIntakeVar.addValue( calIntake, getAgeYears() );
    }

    private void updateMassVar() {
        double newMass = model.getUnits().modelToViewMass( model.getHuman().getMass() );
        massVar.setValue( newMass );
        massVar.addValue( newMass, getAgeYears() );
    }

    private double getAgeYears() {
        return FitnessUnits.secondsToYears( model.getHuman().getAge() );
    }

    public void relayout( int width, int height ) {
        weightChart.setOffset( 0, height - weightChart.getFullBounds().getHeight() - calorieChart.getFullBounds().getHeight() );
        calorieChart.setOffset( 0, weightChart.getFullBounds().getMaxY() );
    }
}
