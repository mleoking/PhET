/*  */
package edu.colorado.phet.waveinterference.tests;

import java.awt.*;

import edu.colorado.phet.waveinterference.view.*;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 8:00:15 PM
 */

public class TestExpandableChart extends TestTopView {
    private ExpandableWaveChart expandableWaveChart;
//    private WaveChartGraphic waveChartGraphic;

    public TestExpandableChart() {
        super( "Test Expandable Chart" );
        getWaveModelGraphic().setCellDimensions( 4, 4 );
        getWaveModelGraphic().setOffset( 100, 10 );

        final MutableColor waterColor = new MutableColor( new Color( 130, 185, 255 ) );
        getWaveModelGraphic().setColorMap( new IndexColorMap( getLattice(), waterColor ) );
        WaveChartGraphic waveChartGraphic = new WaveChartGraphic( "Displacement", getWaveModelGraphic().getLatticeScreenCoordinates(), getWaveModel(), waterColor, "units.meters", 0, 125 );
//        getPhetPCanvas().getLayer().addChild( waveChartGraphic );

        final CrossSectionGraphic crossSectionGraphic = new CrossSectionGraphic( getWaveModel(), getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( crossSectionGraphic );

        MutableColorChooser mutableColorChooser = new MutableColorChooser( waterColor );
        getControlPanel().addControlFullWidth( mutableColorChooser );

        expandableWaveChart = new ExpandableWaveChart( waveChartGraphic, getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( expandableWaveChart );
    }

    protected void step() {
        super.step();
        expandableWaveChart.updateChart();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestExpandableChart() );
    }
}
