/*  */
package edu.colorado.phet.waveinterference.tests;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.*;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 12:12:49 AM
 */

public class TestFaucetModule extends BasicWaveTestModule {
    private FaucetGraphic faucetGraphic;
    private WaveModelGraphic waveModelGraphic;
    private WaveSideView waveSideView;

    public TestFaucetModule() {
        super( "Faucet" );
        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        super.getPhetPCanvas().addScreenChild( waveModelGraphic );

//        BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveModelGraphic.getCellDimensions().width );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int) cellDim.getValue();
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );
        getControlPanel().addControl( cellDim );
//        setControlPanel( controlPanel );
        waveSideView = new WaveSideView( getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( waveSideView );
        waveModelGraphic.setOffset( 300, 100 );
        faucetGraphic = new FaucetGraphic( getWaveModel(), getOscillator(), waveModelGraphic.getLatticeScreenCoordinates() );

        getPhetPCanvas().addScreenChild( faucetGraphic );
        setOscillatorRadius( 2 );

        final ModelSlider dropHeight = new ModelSlider( "Drop Height", "pixels", 0, 500, faucetGraphic.getDropHeight() );
        dropHeight.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                faucetGraphic.setDropHeight( dropHeight.getValue() );
            }
        } );

        final ModelSlider dropSpeed = new ModelSlider( "Drop Speed", "pixels/sec", 0, 500, faucetGraphic.getDropSpeed() );
        dropSpeed.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                faucetGraphic.setDropSpeed( dropSpeed.getValue() );
            }
        } );
        getControlPanel().addControl( dropHeight );
        getControlPanel().addControl( dropSpeed );

        final JCheckBox showSideView = new JCheckBox( "Side view", waveSideView.getVisible() );
        showSideView.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                waveSideView.setVisible( showSideView.isSelected() );
            }
        } );
        getControlPanel().addControl( showSideView );

        final JCheckBox showTopView = new JCheckBox( "Top View", waveModelGraphic.getVisible() );
        showTopView.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                waveModelGraphic.setVisible( showTopView.isSelected() );
            }
        } );
        getControlPanel().addControl( showTopView );

        FaucetOnOffControlPanel faucetOnOffControlPanel = new FaucetOnOffControlPanel( faucetGraphic );
        getControlPanel().addControl( faucetOnOffControlPanel );
    }

    protected void step() {
        super.step();
        faucetGraphic.step();
        waveModelGraphic.update();
        waveSideView.update();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestFaucetModule() );
    }
}
