/*  */
package edu.colorado.phet.waveinterference.tests;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.DetectorSetControlPanel;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.IntensityReaderSet;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:04:03 AM
 */
public class TestStripChartModule extends BasicWaveTestModule {
    private WaveModelGraphic waveModelGraphic;
    private IntensityReaderSet intensityReaderSet;

    public TestStripChartModule() {
        super( "Strip Chart" );

        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        super.getPhetPCanvas().addScreenChild( waveModelGraphic );

        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveModelGraphic.getCellDimensions().width );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int) cellDim.getValue();
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );
        intensityReaderSet = new IntensityReaderSet();
        getPhetPCanvas().addScreenChild( intensityReaderSet );
        DetectorSetControlPanel detectorSetControlPanel = new DetectorSetControlPanel( "Amplitude", intensityReaderSet, getPhetPCanvas(), getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates(), getClock() );

        getControlPanel().addControl( cellDim );
        getControlPanel().addControl( detectorSetControlPanel );
        detectorSetControlPanel.addIntensityReader( "Amplitude" );
    }


    protected void step() {
        super.step();
        waveModelGraphic.update();
        intensityReaderSet.update();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestStripChartModule() );
    }
}
