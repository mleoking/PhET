/*  */
package edu.colorado.phet.waveinterference.tests;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.*;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:05:17 AM
 */
public class TestWaveRotateModule extends BasicWaveTestModule {
//    private WaveSideView waveSideView;
    //    private WaveModelGraphic waveModelGraphic;
    //    private RotationGlyph rotationGlyph;
    private RotationWaveGraphic rotationWaveGraphic;

    public TestWaveRotateModule() {
        this( "Wave Rotate" );
    }

    protected TestWaveRotateModule( String name ) {
        super( name );

        final WaveModelGraphic waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        final WaveSideView waveSideView = new WaveSideViewFull( getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates() );
        RotationGlyph rotationGlyph = new RotationGlyph();
        rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
        rotationWaveGraphic.setOffset( 50, 20 );
        getPhetPCanvas().addScreenChild( rotationWaveGraphic );

        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveSideView.getDistBetweenCells() );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int) cellDim.getValue();
                waveSideView.setSpaceBetweenCells( dim );
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );

        WaveRotateControl waveRotateControl = new WaveRotateControl( rotationWaveGraphic );
        getControlPanel().addControl( cellDim );
        getControlPanel().addControl( waveRotateControl );
        rotationWaveGraphic.updateLocations();
        rotationWaveGraphic.setViewAngle( 0 );
    }


    protected void step() {
        super.step();
        rotationWaveGraphic.update();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestWaveRotateModule() );
    }
}
