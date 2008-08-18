package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseCanvas;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.colorado.phet.eatingandexercise.view.EatingAndExerciseColorScheme;
import edu.colorado.phet.eatingandexercise.view.StackedBarChartNode;
import edu.colorado.phet.eatingandexercise.view.StackedBarNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 9, 2008 at 8:59:34 PM
 */
public class CaloriePanel extends PNode {
    private EatingAndExerciseCanvas phetPCanvas;
    private StackedBarChartNode stackedBarChart;
    private CalorieNode foodNode;
    private CalorieNode exerciseNode;
    private ChartNode chartNode;

    public CaloriePanel( final EatingAndExerciseModel model, final EatingAndExerciseCanvas phetPCanvas, Frame parentFrame ) {
        this.phetPCanvas = phetPCanvas;
        this.chartNode = new ChartNode( model, phetPCanvas );
        addChild( chartNode );

        Function.LinearFunction transform = new Function.LinearFunction( 0, 3000, 0, 250 );
        stackedBarChart = new StackedBarChartNode( transform, EatingAndExerciseResources.getString( "units.cal-per-day" ), 10, 250, 1000, 8000 );

        StackedBarNode intakeBars = new StackedBarNode( transform, 100 );
        Color labelColor = Color.black;
        intakeBars.addElement( new BarChartElementAdapter( EatingAndExerciseStrings.FATS, EatingAndExerciseColorScheme.FATS, model.getHuman().getLipids(), "stick_butter.png", labelColor ), StackedBarNode.NONE );
        intakeBars.addElement( new BarChartElementAdapter( EatingAndExerciseResources.getString( "food.carbs" ), EatingAndExerciseColorScheme.CARBS, model.getHuman().getCarbs(), "carbs.png", labelColor ), StackedBarNode.NONE );
        intakeBars.addElement( new BarChartElementAdapter( EatingAndExerciseResources.getString( "food.protien" ), EatingAndExerciseColorScheme.PROTEIN, model.getHuman().getProteins(), "j0413686.gif", labelColor ), StackedBarNode.NONE );

        StackedBarNode exerciseBars = new StackedBarNode( transform, 100 );
        exerciseBars.addElement( new BarChartElementAdapter( EatingAndExerciseResources.getString( "exercise.resting.bmr" ), EatingAndExerciseColorScheme.BMR, model.getHuman().getBmr(), "heart2.png" ), StackedBarNode.NONE );
        exerciseBars.addElement( new BarChartElementAdapter( EatingAndExerciseResources.getString( "exercise.lifestyle" ), EatingAndExerciseColorScheme.ACTIVITY, model.getHuman().getActivity(), "j0417518.png" ), StackedBarNode.NONE );
        exerciseBars.addElement( new BarChartElementAdapter( EatingAndExerciseResources.getString( "exercise" ), EatingAndExerciseColorScheme.EXERCISE, model.getHuman().getExercise(), "road_biker.png" ), StackedBarNode.NONE );

        stackedBarChart.addStackedBarNode( intakeBars );
        stackedBarChart.addStackedBarNode( exerciseBars );
        addChild( stackedBarChart );

        foodNode = new CalorieNode( parentFrame, EatingAndExerciseResources.getString( "edit.diet" ),
                                    new Color( 100, 100, 255 ), model.getAvailableFoods(),
                                    model.getHuman().getSelectedFoods(), EatingAndExerciseResources.getString( "food.sources" ), EatingAndExerciseResources.getString( "diet" ), "plate-2.png" ) {
            protected ICalorieSelectionPanel createCalorieSelectionPanel() {
                return new FoodSelectionPanel( model.getHuman(), getAvailable(), getCalorieSet(), getAvailableTitle(), getSelectedTitle() );
            }
        };
        addChild( foodNode );

        exerciseNode = new CalorieNode( parentFrame, EatingAndExerciseResources.getString( "exercise.edit" ),
                                        Color.red, model.getAvailableExercise(),
                                        model.getHuman().getSelectedExercise(),
                                        EatingAndExerciseResources.getString( "menu.options" ), EatingAndExerciseResources.getString( "exercise.daily" ), "planner.png" ) {
            protected ICalorieSelectionPanel createCalorieSelectionPanel() {
                return new ExerciseSelectionPanel( model.getHuman(), getAvailable(), getCalorieSet(), getAvailableTitle(), getSelectedTitle() );
            }
        };
        addChild( exerciseNode );

        addChild( foodNode.getTooltipLayer() );
        addChild( exerciseNode.getTooltipLayer() );

        relayout();

        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                relayout();
            }

            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
    }

    public void resetAll() {
        chartNode.resetAll();
        foodNode.resetAll();
        exerciseNode.resetAll();
    }

    public PNode getEditDietButton() {
        return foodNode.getEditButton();
    }

    public void applicationStarted() {
    }

    public void addEditorClosedListener( ActionListener actionListener ) {
        foodNode.addEditorClosedListener( actionListener );
        exerciseNode.addEditorClosedListener( actionListener );
    }

    public void clearAndResetDomains() {
        chartNode.clearAndResetDomains();
    }

    public void addFoodPressedListener( ActionListener actionListener ) {
        foodNode.addItemPressedListener( actionListener );
    }

    public PNode getPlateNode() {
        return foodNode.getDropTarget();
    }

    public void addExerciseDraggedListener( ActionListener actionListener ) {
        exerciseNode.addItemPressedListener( actionListener );
    }

    public PNode getDiaryNode() {
        return exerciseNode.getDropTarget();
    }

    public static class BarChartElementAdapter extends StackedBarNode.BarChartElement {
        public BarChartElementAdapter( String name, Paint paint, final DefaultTemporalVariable variable, String image, Color textColor ) {
            super( name, paint, variable.getValue(), EatingAndExerciseResources.getImage( image ), textColor );
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
        double width = phetPCanvas.getWidth() - getOffset().getX();
        stackedBarChart.setOffset( width / 2 - stackedBarChart.getFullBounds().getWidth() / 2, foodNode.getPlateBottomY() );

        foodNode.setOffset( stackedBarChart.getFullBounds().getX() - foodNode.getDropTarget().getWidth(), 0 );
        exerciseNode.setOffset( stackedBarChart.getFullBounds().getMaxX() + 25, 0 );

        double w = phetPCanvas.getWidth() - phetPCanvas.getControlPanelWidth();
        chartNode.relayout( w, phetPCanvas.getHeight() - foodNode.getPlateBottomY() );
        chartNode.setOffset( 0, foodNode.getPlateBottomY() );
    }
}