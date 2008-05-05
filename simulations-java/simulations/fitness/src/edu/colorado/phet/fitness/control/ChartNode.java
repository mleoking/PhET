package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.data.Range;

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
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.colorado.phet.fitness.model.FitnessUnits;
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
    private ControlGraph weightGraph;
    private ControlGraph calorieGraph;
    private static final double DEFAULT_RANGE_YEARS = 5;

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
        weightGraph = new FitnessControlGraph( phetPCanvas, weightSeries, "Weight", 0, 250, tsm );


        weightGraph.setEditable( false );
        weightChart = new MinimizableControlGraph( "Weight", weightGraph );
        weightChart.setAvailableBounds( 600, 125 );

        ControlGraphSeries intakeSeries = new ControlGraphSeries( "Intake", Color.green, "Intake", FitnessStrings.KCAL_PER_DAY, new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", calIntakeVar );
        intakeSeries.setDecimalFormat( new DefaultDecimalFormat( FitnessStrings.KCAL_PER_DAY_FORMAT ) );
        ControlGraphSeries burnSeries = new ControlGraphSeries( "Burned", Color.red, "Burned", FitnessStrings.KCAL_PER_DAY, new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ), "", calBurnVar );
        burnSeries.setDecimalFormat( new DefaultDecimalFormat( FitnessStrings.KCAL_PER_DAY_FORMAT ) );

        calorieGraph = new FitnessControlGraph( phetPCanvas, intakeSeries, "Calories", 0, 6000, tsm );

        calorieGraph.addSeries( burnSeries );
        updateGraphDomains( DEFAULT_RANGE_YEARS );
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
        resetChartVerticalRanges();
    }

    private void resetChartVerticalRanges() {
        weightGraph.setVerticalRange( 0, 250 );
        calorieGraph.setVerticalRange( 0, 6000 );
    }

    private void updateGraphRanges() {
        double min = weightGraph.getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getLowerBound();
        double max = weightGraph.getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getUpperBound();
        double currentRange = max - min;
        updateGraphDomains( currentRange );
    }

    private void updateGraphDomains( double rangeYears ) {
        double startTime = model.getHuman().getAge();
        calorieGraph.setHorizontalRange( FitnessUnits.secondsToYears( startTime ),
                                         FitnessUnits.secondsToYears( startTime + FitnessUnits.yearsToSeconds( rangeYears ) ) );
        weightGraph.setHorizontalRange( FitnessUnits.secondsToYears( startTime ),
                                        FitnessUnits.secondsToYears( startTime + FitnessUnits.yearsToSeconds( rangeYears ) ) );
    }

    private void resetChartArea() {
        massVar.clear();
        calIntakeVar.clear();
        calBurnVar.clear();
        updateGraphRanges();
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

    public void resetAll() {
        massVar.clear();
        calBurnVar.clear();
        calIntakeVar.clear();
        updateGraphDomains( DEFAULT_RANGE_YEARS );
        resetChartVerticalRanges();
    }

    private class FitnessControlGraph extends ControlGraph {
        private GradientButtonNode gradientButtonNode;

        public FitnessControlGraph( PhetPCanvas canvas, ControlGraphSeries series, String title, int minY, int maxY, TimeSeriesModel timeSeriesModel ) {
            super( canvas, series, title, minY, maxY, timeSeriesModel );
            gradientButtonNode = new GradientButtonNode( "Reset", 12, Color.green );
            gradientButtonNode.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resetChartArea();
                }
            } );
            addChild( gradientButtonNode );
            relayout();
        }

        public void relayout() {
            super.relayout();
            if ( gradientButtonNode != null ) {
                int buttonInsetX = 2;
                gradientButtonNode.setOffset( getJFreeChartNode().getDataArea().getMaxX() - gradientButtonNode.getFullBounds().getWidth()
                                              - buttonInsetX + getJFreeChartNode().getOffset().getX(),
                                              getJFreeChartNode().getDataArea().getMaxY() - gradientButtonNode.getFullBounds().getHeight() );
            }
        }

        protected void zoomHorizontal( double v ) {
            double min = getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getLowerBound();
            double max = getJFreeChartNode().getChart().getXYPlot().getDomainAxis().getUpperBound();
            double currentRange = max - min;
            double newRange = Math.max( 1, currentRange * v );

            setHorizontalRange( min, min + newRange );
            forceUpdateAll();
        }

        public void setHorizontalRange( double minDomainValue, double maxDomainValue ) {
            super.setHorizontalRange( minDomainValue, maxDomainValue );
            if ( getZoomControl() != null ) {
                getZoomControl().setHorizontalZoomInEnabled( maxDomainValue - minDomainValue > 1 );
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
