package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.motion.model.MotionTimeSeriesModel;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.fitness.model.FitnessUnits;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.colorado.phet.fitness.view.FitnessColorScheme;
import edu.colorado.phet.fitness.view.StackedBarChartNode;
import edu.colorado.phet.fitness.view.StackedBarNode;
import edu.colorado.phet.fitness.FitnessResources;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 9, 2008 at 8:59:34 PM
 */
public class CaloriePanel extends PNode {
    private MinimizableControlGraph weightChart;
    private MinimizableControlGraph calorieChart;
    private PhetPCanvas phetPCanvas;
    private StackedBarChartNode stackedBarChart;
    private CalorieNode foodNode;
    private CalorieNode exerciseNode;

    public CaloriePanel( final FitnessModel model, PhetPCanvas phetPCanvas, Frame parentFrame ) {
        this.phetPCanvas = phetPCanvas;
        GraphSuiteSet graphSuiteSet = new GraphSuiteSet();
        TimeSeriesModel tsm = new MotionTimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );

        ControlGraph weightGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Weight", Color.blue, "weight", "lbs", "", model.getHuman().getMassVariable() ), "Weight", 0, 100, tsm, 7.37E8 );
        weightGraph.setHorizontalRange( Human.DEFAULT_VALUE.getAgeSeconds(), Human.DEFAULT_VALUE.getAgeSeconds() + FitnessUnits.yearsToSeconds( 20 ) );
        weightGraph.setEditable( false );
        weightChart = new MinimizableControlGraph( "Weight", weightGraph );
        weightChart.setAvailableBounds( 600, 125 );

        final ControlGraph calorieGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Calories", Color.green, "Cal", "Cal", new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", model.getHuman().getCaloricIntakeVariable() ), "Calories", 0, 4000, tsm );
        calorieGraph.addSeries( new ControlGraphSeries( "Exercise Calories", Color.red, "cal", "kcal", new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", model.getHuman().getCaloricBurnVariable() ) );
        calorieGraph.setHorizontalRange( Human.DEFAULT_VALUE.getAgeSeconds(), Human.DEFAULT_VALUE.getAgeSeconds() + FitnessUnits.yearsToSeconds( 20 ) );
        calorieGraph.setEditable( false );
        model.addListener( new FitnessModel.Adapter() {
            public void simulationTimeChanged() {
                calorieGraph.forceUpdateAll();
            }
        } );

        calorieChart = new MinimizableControlGraph( "Calories", calorieGraph );
        calorieChart.setAvailableBounds( 600, 125 );

        MinimizableControlGraph[] graphs = {weightChart, calorieChart};
        weightChart.setAlignedLayout( graphs );
        calorieChart.setAlignedLayout( graphs );
        graphSuiteSet.addGraphSuite( graphs );

        addChild( weightChart );
        addChild( calorieChart );
        System.out.println( "a.getFullBounds() = " + weightChart.getFullBounds() );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );

        Function.LinearFunction transform = new Function.LinearFunction( 0, 3500, 0, 250 );
        stackedBarChart = new StackedBarChartNode( transform, "Calories/Day", 10, 250, 1000, 8000 );
                                                                                                                       
        StackedBarNode intakeBars = new StackedBarNode( transform, 100 );
        intakeBars.addElement( new BarChartElementAdapter( "Lipids", FitnessColorScheme.FATS, model.getHuman().getLipids() , "j0232547.gif" ), StackedBarNode.LEFT );
        intakeBars.addElement( new BarChartElementAdapter( "Carbs", FitnessColorScheme.CARBS, model.getHuman().getCarbs() ,"j0410455.gif"), StackedBarNode.LEFT );
        intakeBars.addElement( new BarChartElementAdapter( "Proteins", FitnessColorScheme.PROTEIN, model.getHuman().getProteins(),"j0413686.gif" ), StackedBarNode.LEFT );

        StackedBarNode exerciseBars = new StackedBarNode( transform, 100 );
//        exerciseBars.addElement( new BarChartElementAdapter( "<html><center>Basal<br>Metabolic<br>Rate<br>(BMR)</center></html>", FitnessColorScheme.BMR, model.getHuman().getBmr() ,"heart2.png"), StackedBarNode.RIGHT );
        exerciseBars.addElement( new BarChartElementAdapter( "<html>Resting<br>(BMR)</html>", FitnessColorScheme.BMR, model.getHuman().getBmr() ,"heart2.png"), StackedBarNode.RIGHT );
        exerciseBars.addElement( new BarChartElementAdapter( "Activity", FitnessColorScheme.ACTIVITY, model.getHuman().getActivity(),"j0417518.png" ), StackedBarNode.RIGHT );
        exerciseBars.addElement( new BarChartElementAdapter( "Exercise", FitnessColorScheme.EXERCISE, model.getHuman().getExercise(),"road_biker.png" ), StackedBarNode.RIGHT );

        stackedBarChart.addStackedBarNode( intakeBars );
        stackedBarChart.addStackedBarNode( exerciseBars );
        addChild( stackedBarChart );

        foodNode = new CalorieNode( parentFrame, "Edit Diet", new Color( 100, 100, 255 ), FitnessModel.availableFoods, model.getHuman().getSelectedFoods(), "Grocery Store & Restaurants", "Diet" );
        addChild( foodNode );

        exerciseNode = new CalorieNode( parentFrame, "Edit Exercise", Color.red, FitnessModel.availableExercise, model.getHuman().getSelectedExercise(), "Options", "Daily Exercise" ) {
            protected CalorieSelectionPanel createCalorieSelectionPanel() {
                return new ExerciseSelectionPanel( model.getHuman(), getAvailable(), getCalorieSet(), getAvailableTitle(), getSelectedTitle() );
            }
        };
        addChild( exerciseNode );

        relayout();
    }

    public static class BarChartElementAdapter extends StackedBarNode.BarChartElement {
        public BarChartElementAdapter( String name, Paint paint, final DefaultTemporalVariable variable, String image) {
            super( name, paint, variable.getValue(), FitnessResources.getImage( image ) );
            variable.addListener( new IVariable.Listener() {
                public void valueChanged() {
                    BarChartElementAdapter.this.setValue( variable.getValue() );
                }
            } );
            addListener( new Listener() {
                public void valueChanged() {
                    variable.setValue( BarChartElementAdapter.this.getValue() );
                }

                public void paintChanged() {
                }
            } );
        }
    }

    private void relayout() {
        weightChart.setOffset( 0, phetPCanvas.getHeight() - weightChart.getFullBounds().getHeight() - calorieChart.getFullBounds().getHeight() );
        calorieChart.setOffset( 0, weightChart.getFullBounds().getMaxY() );
        double width = phetPCanvas.getWidth() - getOffset().getX();
        stackedBarChart.setOffset( width / 2 - stackedBarChart.getFullBounds().getWidth() / 2, weightChart.getFullBounds().getY() );
        foodNode.setOffset( stackedBarChart.getFullBounds().getX() - foodNode.getFullBounds().getWidth() - 5, 10 );
        exerciseNode.setOffset( stackedBarChart.getFullBounds().getMaxX() + 20, 10 );
    }

}