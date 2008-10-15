/*  */
package edu.colorado.phet.waveinterference.tests;

import java.awt.*;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.view.MutableColor;
import edu.colorado.phet.waveinterference.view.ScreenChartGraphic;
import edu.colorado.phet.waveinterference.view.ScreenControlPanel;
import edu.colorado.phet.waveinterference.view.ScreenNode;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:17:23 AM
 */

public class TestExpandableScreenAndChart extends TestWaveColor {
    private ScreenNode screenGraphic;
    private ScreenChartGraphic screenChart;
    private Oscillator oscillator2;

    public TestExpandableScreenAndChart() {
        super( "Screen Graphic" );
        getWaveModel().setSize( 50, 50 );
        screenGraphic = new ScreenNode( getWaveModel(), getLatticeScreenCoordinates(), getWaveModelGraphic() );
        screenGraphic.setIntensityScale( 2.5 );
        getPhetPCanvas().addScreenChild( screenGraphic );
        getPhetPCanvas().removeScreenChild( getWaveModelGraphic() );
        getPhetPCanvas().addScreenChild( getWaveModelGraphic() );

        screenChart = new ScreenChartGraphic( "Screen Chart", getLatticeScreenCoordinates(), getWaveModel(), new MutableColor( Color.black ), screenGraphic.getBrightnessScreenGraphic() );
        ExpandableScreenChartGraphic expandableScreenChartGraphic = new ExpandableScreenChartGraphic( getPhetPCanvas(), screenChart );
        getPhetPCanvas().addScreenChild( expandableScreenChartGraphic );
        getWaveModelGraphic().setOffset( 100, 100 );
        oscillator2 = new Oscillator( getWaveModel(), 10, 10 );
        oscillator2.setAmplitude( 2 );
        getControlPanel().addControl( new ScreenControlPanel( screenGraphic ) );
    }

    protected void step() {
        super.step();
        screenGraphic.updateScreen();
        screenChart.updateChart();
        oscillator2.setTime( getOscillator().getTime() );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestExpandableScreenAndChart() );
    }
}
