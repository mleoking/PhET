package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.FitnessStrings;
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

    private PhetPCanvas phetPCanvas;
    private StackedBarChartNode stackedBarChart;
    private CalorieNode foodNode;
    private CalorieNode exerciseNode;
    private ChartNode chartNode;

    public CaloriePanel( final FitnessModel model, PhetPCanvas phetPCanvas, Frame parentFrame ) {
        this.phetPCanvas = phetPCanvas;
        this.chartNode=new ChartNode(model, phetPCanvas);
        addChild( chartNode );

        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );

        Function.LinearFunction transform = new Function.LinearFunction( 0, 3000, 0, 250 );
        stackedBarChart = new StackedBarChartNode( transform, "Calories/Day", 10, 250, 1000, 8000 );

        StackedBarNode intakeBars = new StackedBarNode( transform, 100 );
//        Color labelColor=Color.white;
        Color labelColor = Color.black;
        intakeBars.addElement( new BarChartElementAdapter( FitnessStrings.FATS, FitnessColorScheme.FATS, model.getHuman().getLipids(), "j0232547.gif", labelColor ), StackedBarNode.NONE );
//        intakeBars.addElement( new BarChartElementAdapter( "Carbs", FitnessColorScheme.CARBS, model.getHuman().getCarbs(), "j0410455.gif" ), StackedBarNode.NONE );
        intakeBars.addElement( new BarChartElementAdapter( "Carbs", FitnessColorScheme.CARBS, model.getHuman().getCarbs(), "carbs.png", labelColor ), StackedBarNode.NONE );
        intakeBars.addElement( new BarChartElementAdapter( "Proteins", FitnessColorScheme.PROTEIN, model.getHuman().getProteins(), "j0413686.gif", labelColor ), StackedBarNode.NONE );

        StackedBarNode exerciseBars = new StackedBarNode( transform, 100 );
//        exerciseBars.addElement( new BarChartElementAdapter( "<html><center>Basal<br>Metabolic<br>Rate<br>(BMR)</center></html>", FitnessColorScheme.BMR, model.getHuman().getBmr() ,"heart2.png"), StackedBarNode.RIGHT );
        exerciseBars.addElement( new BarChartElementAdapter( "<html>Resting<br>(BMR)</html>", FitnessColorScheme.BMR, model.getHuman().getBmr(), "heart2.png" ), StackedBarNode.NONE );
        exerciseBars.addElement( new BarChartElementAdapter( "Lifestyle", FitnessColorScheme.ACTIVITY, model.getHuman().getActivity(), "j0417518.png" ), StackedBarNode.NONE );
        exerciseBars.addElement( new BarChartElementAdapter( "Exercise", FitnessColorScheme.EXERCISE, model.getHuman().getExercise(), "road_biker.png" ), StackedBarNode.NONE );

        stackedBarChart.addStackedBarNode( intakeBars );
        stackedBarChart.addStackedBarNode( exerciseBars );
        addChild( stackedBarChart );

        foodNode = new CalorieNode( parentFrame, "Edit Diet", new Color( 100, 100, 255 ), FitnessModel.availableFoods, model.getHuman().getSelectedFoods(), "Grocery Store & Restaurants", "Diet" ) {
            protected ICalorieSelectionPanel createCalorieSelectionPanel() {
                return new FoodSelectionPanel( model.getHuman(), getAvailable(), getCalorieSet(), getAvailableTitle(), getSelectedTitle() );
            }
        };
        addChild( foodNode );

        exerciseNode = new CalorieNode( parentFrame, "Edit Exercise", Color.red, FitnessModel.availableExercise, model.getHuman().getSelectedExercise(), "Options", "Daily Exercise" ) {
            protected ICalorieSelectionPanel createCalorieSelectionPanel() {
                return new ExerciseSelectionPanel( model.getHuman(), getAvailable(), getCalorieSet(), getAvailableTitle(), getSelectedTitle() );
            }
        };
        addChild( exerciseNode );

        relayout();
    }

    public static class BarChartElementAdapter extends StackedBarNode.BarChartElement {
        public BarChartElementAdapter( String name, Paint paint, final DefaultTemporalVariable variable, String image, Color textColor ) {
            super( name, paint, variable.getValue(), FitnessResources.getImage( image ), textColor );
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

        public BarChartElementAdapter( String name, Paint paint, final DefaultTemporalVariable variable, String image ) {
            this( name, paint, variable, image, Color.black );
        }
    }

    private void relayout() {
        chartNode.relayout(phetPCanvas.getWidth(), phetPCanvas.getHeight());
        double width = phetPCanvas.getWidth() - getOffset().getX();
        stackedBarChart.setOffset( width / 2 - stackedBarChart.getFullBounds().getWidth() / 2, chartNode.getFullBounds().getY() );
        foodNode.setOffset( stackedBarChart.getFullBounds().getX() - foodNode.getFullBounds().getWidth() - 5, 10 );
        exerciseNode.setOffset( stackedBarChart.getFullBounds().getMaxX() + 20, 10 );
    }

}