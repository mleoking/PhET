package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.data.Range;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.MotionTimeSeriesModel;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.colorado.phet.eatingandexercise.util.YearMonthFormat;
import edu.colorado.phet.eatingandexercise.view.EatingAndExercisePText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

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
    private EatingAndExerciseModel model;
    private ControlGraph weightGraph;
    private ControlGraph calorieGraph;
    private static final double DEFAULT_RANGE_YEARS = 2;
    private EatingAndExerciseModel.Units previousUnits;

    public ChartNode( final EatingAndExerciseModel model, PhetPCanvas phetPCanvas ) {
        this.model = model;
        GraphSuiteSet graphSuiteSet = new GraphSuiteSet();

        //todo: remove the following bogus line
        TimeSeriesModel tsm = new MotionTimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );

        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void simulationTimeChanged() {
                updateVars();
            }
        } );
        updateVars();
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                updateWeightMassLabel();
                syncVerticalRanges();
            }
        } );
        model.getHuman().addListener( new Human.Adapter() {
            public void weightChanged() {
                massVar.setValue( getMassDisplayValue() );
            }
        } );
        model.getHuman().addListener( new Human.Adapter() {
            public void caloricIntakeChanged() {
                calIntakeVar.setValue( model.getHuman().getDailyCaloricIntake() );
            }

            public void caloricBurnChanged() {
                calBurnVar.setValue( model.getHuman().getDailyCaloricBurn() );
            }
        } );

        final ControlGraphSeries weightSeries = new ControlGraphSeries( EatingAndExerciseResources.getString( "weight" ), Color.blue, EatingAndExerciseResources.getString( "weight" ), EatingAndExerciseResources.getString( "units.lbs" ), "", massVar );
        weightSeries.setDecimalFormat( new DefaultDecimalFormat( "0" ) );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void unitsChanged() {
                weightSeries.setUnits( model.getUnits().getMassUnit() );
            }
        } );
        weightGraph = new EatingAndExerciseControlGraph( phetPCanvas, weightSeries, EatingAndExerciseResources.getString( "weight" ), 0, 250, tsm );
        weightGraph.setEditable( false );
//        weightGraph.getJFreeChartNode().getChart().getXYPlot().getDomainAxis().setLabel( "Label" );//takes up too much vertical space
        updateWeightMassLabel();
        weightChart = new MinimizableControlGraph( EatingAndExerciseResources.getString( "weight" ), weightGraph );
        weightChart.setAvailableBounds( 600, 125 );

        ControlGraphSeries intakeSeries = new ControlGraphSeries( EatingAndExerciseResources.getString( "calories.intake" ), Color.green, EatingAndExerciseResources.getString( "calories.intake" ), EatingAndExerciseStrings.KCAL_PER_DAY, new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", calIntakeVar );
        intakeSeries.setDecimalFormat( new DefaultDecimalFormat( EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT ) );
        ControlGraphSeries burnSeries = new ControlGraphSeries( EatingAndExerciseResources.getString( "calories.burned" ), Color.red, EatingAndExerciseResources.getString( "calories.burned" ), EatingAndExerciseStrings.KCAL_PER_DAY, new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", calBurnVar );
        burnSeries.setDecimalFormat( new DefaultDecimalFormat( EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT ) );

        calorieGraph = new EatingAndExerciseControlGraph( phetPCanvas, intakeSeries, EatingAndExerciseResources.getString( "units.calories" ), 0, 6000, tsm );
        calorieGraph.getJFreeChartNode().getChart().getXYPlot().getRangeAxis().setLabel( EatingAndExerciseResources.getString( "units.cal-day" ) );
        calorieGraph.addSeries( burnSeries );
        updateGraphDomains( DEFAULT_RANGE_YEARS );
        calorieGraph.setEditable( false );
        model.addListener( new EatingAndExerciseModel.Adapter() {
            public void simulationTimeChanged() {
                calorieGraph.forceUpdateAll();
            }
        } );
        calorieChart = new MinimizableControlGraph( EatingAndExerciseResources.getString( "units.calories" ), calorieGraph );
        calorieChart.setAvailableBounds( 600, 125 );

        calorieGraph.addListener( new ControlGraph.Adapter() {
            public void zoomChanged() {
                weightGraph.setDomain( calorieGraph.getMinDataX(), calorieGraph.getMaxDataX() );
            }
        } );
        weightGraph.addListener( new ControlGraph.Adapter() {
            public void zoomChanged() {
                calorieGraph.setDomain( weightGraph.getMinDataX(), weightGraph.getMaxDataX() );
            }
        } );

        MinimizableControlGraph[] graphs = {weightChart, calorieChart};
        weightChart.setAlignedLayout( graphs );
        calorieChart.setAlignedLayout( graphs );
        graphSuiteSet.addGraphSuite( graphs );

        addChild( weightChart );
        addChild( calorieChart );

        resetChartVerticalRanges();
        syncVerticalRanges();
    }

    public void clearAndResetDomains() {
        clearData();
        updateGraphDomains();
    }

    private void updateWeightMassLabel() {
        String weight = EatingAndExerciseResources.getString( "weight" );
        weightGraph.getJFreeChartNode().getChart().getXYPlot().getRangeAxis().setLabel( weight + " (" + model.getUnits().getMassUnit() + ")" );
    }

    private void syncVerticalRanges() {
        if ( previousUnits == null ) {
            previousUnits = model.getUnits();
        }
        if ( previousUnits != model.getUnits() ) {
            double max = weightGraph.getJFreeChartNode().getChart().getXYPlot().getRangeAxis().getUpperBound();
            double modelMax = previousUnits.viewToModelMass( max );
            double viewMax = model.getUnits().modelToViewMass( modelMax );
            weightGraph.setVerticalRange( 0, viewMax );
            syncMassVar();
        }
        previousUnits = model.getUnits();
    }

    private void resetChartVerticalRanges() {
        weightGraph.setVerticalRange( 0, 250 );
        calorieGraph.setVerticalRange( 0, 6000 );
    }

    private void updateGraphDomains() {
        double min = weightGraph.getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getLowerBound();
        double max = weightGraph.getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getUpperBound();
        double currentRange = max - min;
        updateGraphDomains( currentRange );
    }

    private void updateGraphDomains( double rangeYears ) {
        double startTime = model.getHuman().getAge();
        calorieGraph.setDomain( EatingAndExerciseUnits.secondsToYears( startTime ),
                                EatingAndExerciseUnits.secondsToYears( startTime + EatingAndExerciseUnits.yearsToSeconds( rangeYears ) ) );
        weightGraph.setDomain( EatingAndExerciseUnits.secondsToYears( startTime ),
                               EatingAndExerciseUnits.secondsToYears( startTime + EatingAndExerciseUnits.yearsToSeconds( rangeYears ) ) );
    }

    private void resetChartArea() {
        clearAndResetDomains();
    }

    private void syncMassVar() {
        massVar.clear();
        ITemporalVariable itv = model.getHuman().getMassVariable();
        for ( int i = 0; i < itv.getSampleCount(); i++ ) {
            massVar.addValue( model.getUnits().modelToViewMass( itv.getData( i ).getValue() ), EatingAndExerciseUnits.secondsToYears( itv.getData( i ).getTime() ) );
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
        massVar.setValue( getMassDisplayValue() );
        massVar.addValue( getMassDisplayValue(), getAgeYears() );
    }

    private double getMassDisplayValue() {
        return model.getUnits().modelToViewMass( model.getHuman().getMass() );
    }

    private double getAgeYears() {
        return EatingAndExerciseUnits.secondsToYears( model.getHuman().getAge() );
    }

    public void relayout( double width, double height ) {
        weightChart.setAvailableBounds( width, height / 2 );
        calorieChart.setAvailableBounds( width, height / 2 );
        weightChart.setOffset( 0, height - weightChart.getFullBounds().getHeight() - calorieChart.getFullBounds().getHeight() );
        calorieChart.setOffset( 0, weightChart.getFullBounds().getMaxY() );

        //have to relayout after both are resized
        //todo: internalize this
        weightChart.relayoutControlGraph();
        calorieChart.relayoutControlGraph();
    }

    public void resetAll() {
        clearData();
        updateGraphDomains( DEFAULT_RANGE_YEARS );
        resetChartVerticalRanges();
    }

    private void clearData() {
        massVar.clear();
        //todo: remove the need for this workaround
        model.getHuman().clearMassData();
        calBurnVar.clear();
        calIntakeVar.clear();
    }

    private class EatingAndExerciseControlGraph extends ControlGraph {
        private GradientButtonNode gradientButtonNode;
        private PNode axisLabel;

        public EatingAndExerciseControlGraph( PhetPCanvas canvas, ControlGraphSeries series, String title, int minY, int maxY, TimeSeriesModel timeSeriesModel ) {
            super( canvas, series, title, minY, maxY, timeSeriesModel );
            gradientButtonNode = new GradientButtonNode( EatingAndExerciseResources.getString( "time.reset" ), 12, Color.green );
            gradientButtonNode.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resetChartArea();
                }
            } );
            addChild( gradientButtonNode );

            axisLabel = new PNode();
            PText text = new EatingAndExercisePText( EatingAndExerciseResources.getString( "units.time.yrs" ) );
            axisLabel.addChild( text );
            axisLabel.addChild( new PhetPPath( new Arrow( new Point2D.Double( text.getFullBounds().getMaxX(), text.getFullBounds().getCenterY() ),
                                                          new Vector2D.Double( 20, 0 ), 6, 6, 2, 0.5, true ).getShape(), Color.black ) );
            addChild( axisLabel );

            NumberAxis numberAxis = (NumberAxis) getJFreeChartNode().getChart().getXYPlot().getDomainAxis();

            numberAxis.setTickUnit( new NumberTickUnit( 2.0*DEFAULT_RANGE_YEARS/12.0) );
//            TickUnitSource source = new TickUnitSource() {
//                public TickUnit getLargerTickUnit( TickUnit unit ) {
//                    return new NumberTickUnit( unit.getSize() + 1.0 / 12.0 );
//                }
//
//                public TickUnit getCeilingTickUnit( TickUnit unit ) {
//                    return getLargerTickUnit( unit );
//                }
//
//                public TickUnit getCeilingTickUnit( double size ) {
//                    return new NumberTickUnit( 100 );
//                }
//            };
//            numberAxis.setStandardTickUnits( source );
            numberAxis.setNumberFormatOverride( new YearMonthFormat() );

            relayout();
        }

        public void relayout() {
            super.relayout();
            if ( gradientButtonNode != null ) {
                int buttonInsetX = 2;
                gradientButtonNode.setOffset( getJFreeChartNode().getDataArea().getMaxX() - gradientButtonNode.getFullBounds().getWidth()
                                              - buttonInsetX + getJFreeChartNode().getOffset().getX(),
                                              getJFreeChartNode().getDataArea().getMaxY() - gradientButtonNode.getFullBounds().getHeight() );
                axisLabel.setOffset( getJFreeChartNode().getDataArea().getCenterX() - axisLabel.getFullBounds().getWidth()
                                     - buttonInsetX + getJFreeChartNode().getOffset().getX(),
                                     getJFreeChartNode().getDataArea().getMaxY() - axisLabel.getFullBounds().getHeight() );
            }
        }

        protected void zoomHorizontal( double v ) {
            double min = getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getLowerBound();
            double max = getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getUpperBound();
            double currentRange = max - min;
            double newRange = Math.max( DEFAULT_RANGE_YEARS, currentRange * v );

            setDomain( min, min + newRange );
            forceUpdateAll();
        }

        public void setDomain( double minDomainValue, double maxDomainValue ) {
            super.setDomain( minDomainValue, maxDomainValue );
            if ( getZoomControl() != null ) {
                getZoomControl().setHorizontalZoomInEnabled( maxDomainValue - minDomainValue > DEFAULT_RANGE_YEARS );
            }
            syncMassVar();//todo: remove the need for this workaround
        }

        protected void zoomVertical( double zoomValue ) {
            Range verticalRange = getVerticalRange( zoomValue );
            setVerticalRange( 0, verticalRange.getUpperBound() );
            notifyZoomChanged();
            forceUpdateAll();
        }
    }

}
