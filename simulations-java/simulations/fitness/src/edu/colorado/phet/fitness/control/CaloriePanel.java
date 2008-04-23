package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.colorado.phet.fitness.view.FitnessColorScheme;
import edu.colorado.phet.fitness.view.StackedBarChartNode;
import edu.colorado.phet.fitness.view.StackedBarNode;
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
    private DietNode dietNode;
    //    private ExerciseNode exerciseNode;
    //    private ActivityNode activityNode;
    //    private PSwing newDietNode;
    private CalorieNode foodNode;
    private CalorieNode exerciseNode;

    public CaloriePanel( final FitnessModel model, PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        GraphSuiteSet graphSuiteSet = new GraphSuiteSet();
        TimeSeriesModel tsm = new MotionTimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );
        ControlGraph controlGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Weight", Color.blue, "weight", "lbs", "human", new DefaultTemporalVariable() ), "Weight", 0, 100, tsm );
        controlGraph.setEditable( false );
        weightChart = new MinimizableControlGraph( "Weight", controlGraph );
        ControlGraph controlGraph1 = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Calories", Color.blue, "Cal", "Cal", "human", new DefaultTemporalVariable() ), "Calories", 0, 100, tsm );
        controlGraph1.setEditable( false );
        calorieChart = new MinimizableControlGraph( "Calories", controlGraph1 );
        weightChart.setAvailableBounds( 600, 125 );
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

        Function.LinearFunction transform = new Function.LinearFunction( 0, 2500, 0, 250 );
        stackedBarChart = new StackedBarChartNode( transform, "Calories/Day", 10, 250, 1000, 4000 );

        StackedBarNode intakeBars = new StackedBarNode( transform, 100 );
        intakeBars.addElement( new BarChartElementAdapter( "Lipids", FitnessColorScheme.FATS, model.getHuman().getLipids() ), StackedBarNode.LEFT );
        intakeBars.addElement( new BarChartElementAdapter( "Carbs", FitnessColorScheme.CARBS, model.getHuman().getCarbs() ), StackedBarNode.LEFT );
        intakeBars.addElement( new BarChartElementAdapter( "Proteins", FitnessColorScheme.PROTEIN, model.getHuman().getProteins() ), StackedBarNode.LEFT );

        StackedBarNode exerciseBars = new StackedBarNode( transform, 100 );
        exerciseBars.addElement( new BarChartElementAdapter( "<html><center>Basal<br>Metabolic<br>Rate<br>(BMR)</center></html>", FitnessColorScheme.BMR, model.getHuman().getBmr() ), StackedBarNode.RIGHT );
        exerciseBars.addElement( new BarChartElementAdapter( "Activity", FitnessColorScheme.ACTIVITY, model.getHuman().getActivity() ), StackedBarNode.RIGHT );
        exerciseBars.addElement( new BarChartElementAdapter( "Exercise", FitnessColorScheme.EXERCISE, model.getHuman().getExercise() ), StackedBarNode.RIGHT );

        stackedBarChart.addStackedBarNode( intakeBars );
        stackedBarChart.addStackedBarNode( exerciseBars );
        addChild( stackedBarChart );

        dietNode = new DietNode( model );
//        addChild( dietNode );

//        newDietNode = new PSwing( new DietControlPanel() );
//        addChild( newDietNode );

        foodNode = new CalorieNode( "Edit Diet", Color.blue, FitnessModel.availableFoods, model.getHuman().getSelectedFoods() );
        addChild( foodNode );

//        exerciseNode = new ExerciseNode( model );
//        addChild( exerciseNode );

        exerciseNode = new CalorieNode( "Edit Exercise", Color.red, FitnessModel.availableExercise, model.getHuman().getSelectedExercise() );
        addChild( exerciseNode );

//        activityNode = new ActivityNode( model, phetPCanvas );
//        addChild( activityNode );

        relayout();
    }

    public static class BarChartElementAdapter extends StackedBarNode.BarChartElement {
        public BarChartElementAdapter( String name, Paint paint, final DefaultTemporalVariable variable ) {
            super( name, paint, variable.getValue() );
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
        dietNode.setOffset( stackedBarChart.getFullBounds().getX() - dietNode.getFullBounds().getWidth() - 2, 100 );
//        exerciseNode.setOffset( stackedBarChart.getFullBounds().getMaxX() + 2, 100 );
//        activityNode.setOffset( exerciseNode.getOffset().getX(), exerciseNode.getOffset().getY() + 100 );

//        newDietNode.setOffset( stackedBarChart.getFullBounds().getX() - newDietNode.getFullBounds().getWidth() - 5, 0 );
        foodNode.setOffset( stackedBarChart.getFullBounds().getX() - foodNode.getFullBounds().getWidth() - 5, 10 );
        exerciseNode.setOffset( stackedBarChart.getFullBounds().getMaxX() + 20, 10 );
    }

}