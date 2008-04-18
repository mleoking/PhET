package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.MotionTimeSeriesModel;
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
public class NewCaloriePanel extends PNode {
    private MinimizableControlGraph weightChart;
    private MinimizableControlGraph calorieChart;
    private PhetPCanvas phetPCanvas;
    private StackedBarChartNode stackedBarChart;
    private DietNode dietNode;
    private ExerciseNode exerciseNode;

    public NewCaloriePanel( final FitnessModel model, PhetPCanvas phetPCanvas ) {
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

        stackedBarChart = new StackedBarChartNode( "Calories/Day", 10 );

        StackedBarNode intakeBars = new StackedBarNode( 100 );
        intakeBars.addElement( new StackedBarNode.BarChartElement( "Lipids", FitnessColorScheme.FATS, 150 ), StackedBarNode.LEFT );
        intakeBars.addElement( new StackedBarNode.BarChartElement( "Carbs", FitnessColorScheme.CARBS, 75 ), StackedBarNode.LEFT );
        intakeBars.addElement( new StackedBarNode.BarChartElement( "Proteins", FitnessColorScheme.PROTEIN, 150 ), StackedBarNode.LEFT );

        StackedBarNode exerciseBars = new StackedBarNode( 100 );
        exerciseBars.addElement( new StackedBarNode.BarChartElement( "BMR", FitnessColorScheme.BMR, 100 ), StackedBarNode.RIGHT );
        exerciseBars.addElement( new StackedBarNode.BarChartElement( "Activity", FitnessColorScheme.ACTIVITY, 200 ), StackedBarNode.RIGHT );
        exerciseBars.addElement( new StackedBarNode.BarChartElement( "Exercise", FitnessColorScheme.EXERCISE, 50 ), StackedBarNode.RIGHT );


        stackedBarChart.addStackedBarNode( intakeBars );
        stackedBarChart.addStackedBarNode( exerciseBars );
        addChild( stackedBarChart );

        dietNode = new DietNode();
        addChild( dietNode );

        exerciseNode = new ExerciseNode();
        addChild( exerciseNode );

        relayout();
    }

    private void relayout() {
        weightChart.setOffset( 0, phetPCanvas.getHeight() - weightChart.getFullBounds().getHeight() - calorieChart.getFullBounds().getHeight() );
        calorieChart.setOffset( 0, weightChart.getFullBounds().getMaxY() );
        double width = phetPCanvas.getWidth() - getOffset().getX();
        stackedBarChart.setOffset( width / 2 - stackedBarChart.getFullBounds().getWidth() / 2, weightChart.getFullBounds().getY() );
        dietNode.setOffset( stackedBarChart.getFullBounds().getX() - dietNode.getFullBounds().getWidth() - 2, 100 );
        exerciseNode.setOffset( stackedBarChart.getFullBounds().getMaxX() + 2, 100 );
    }

}