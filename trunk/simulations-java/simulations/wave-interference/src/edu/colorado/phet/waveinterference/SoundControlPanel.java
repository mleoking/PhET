// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.colorado.phet.waveinterference.view.*;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:15:03 PM
 */

public class SoundControlPanel extends WaveInterferenceControlPanel {
    private SoundModule soundModule;
    private MultiOscillatorControlPanel multiOscillatorControlPanel;
    private SlitControlPanel slitControlPanel;

    public SoundControlPanel(SoundModule soundModule, boolean dev) {
        this.soundModule = soundModule;
//        addControl( new ParticleSizeSliderControl( soundModule.getSoundWaveGraphic() ) );
        addControl( new MeasurementControlPanel( soundModule.getMeasurementToolSet() ) );
        addControl( new DetectorSetControlPanel( WIStrings.getString( "readout.pressure" ), soundModule.getIntensityReaderSet(), soundModule.getSoundSimulationPanel(), soundModule.getWaveModel(), soundModule.getLatticeScreenCoordinates(), soundModule.getClock() ) );

        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );
        addControl( new ResetModuleControl( soundModule ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        addControl( new WaveRotateControl3D( soundModule.getWaveInterferenceModel(), soundModule.getRotationWaveGraphic() ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

//        addControl( new SoundWaveGraphicRadioControl( soundModule.getSoundWaveGraphic() ) );
//        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        addControl( new ShowMarkersControl( soundModule.getSoundWaveGraphic(), soundModule.getSoundWaveGraphic().getPressureWaveGraphic() ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        multiOscillatorControlPanel = new MultiOscillatorControlPanel( soundModule.getMultiOscillator(), WIStrings.getString( "controls.one-speaker" ), WIStrings.getString( "controls.two-speakers" ),soundModule.getScreenUnits() );
        addControl( multiOscillatorControlPanel );
        addControl( new SoundAudioControlPanel( soundModule.getAudioSubsystem() ) );
//        addVerticalSpace();

        slitControlPanel = new SlitControlPanel( soundModule.getSlitPotential(), soundModule.getScreenUnits() );
        addControl( slitControlPanel );

        addControl( new AddWallPotentialButton( soundModule.getWaveInterferenceModel() ) );

        if (dev){
            addDeveloperControls();
        }
    }

    private void addDeveloperControls() {
        final PressureWaveGraphic pressureWaveGraphic=soundModule.getSoundWaveGraphic().getPressureWaveGraphic();
//        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, pressureWaveGraphic.getDistBetweenCells() );
//        cellDim.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                int dim = (int) cellDim.getValue();
//                pressureWaveGraphic.setSpaceBetweenCells( dim );
//                waveModelGraphic.setCellDimensions( dim, dim );
//            }
//        } );
//        final JCheckBox showWave = new JCheckBox( "Show Lattice", waveModelGraphic.getVisible() );
//        showWave.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                waveModelGraphic.setVisible( showWave.isSelected() );
//            }
//        } );
        final ModelSlider imageSize = new ModelSlider( "Particle Size", "pixels", 1, 36, pressureWaveGraphic.getImageSize() );
        imageSize.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setParticleImageSize( (int) imageSize.getValue() );
            }
        } );

        final ModelSlider acceleration = new ModelSlider( "Particle Acceleration", "", 0, 10, pressureWaveGraphic.getParticleAcceleration() );
        acceleration.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setParticleAcceleration( acceleration.getValue() );
            }
        } );
        final ModelSlider maxVelocity = new ModelSlider( "Particle Max Velocity", "pixels/sec", 0, 30, pressureWaveGraphic.getMaxVelocity() );
        maxVelocity.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setMaxVelocity( maxVelocity.getValue() );
            }
        } );
        final ModelSlider friction = new ModelSlider( "Friction", "scale", 0, 1, pressureWaveGraphic.getFriction() );
        friction.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setFriction( friction.getValue() );
            }
        } );

//        addControl( cellDim );
//        addControl( showWave );
        addControl( imageSize );
        addControl( acceleration );
        addControl( maxVelocity );
        addControl( friction );
    }

    public void setAsymmetricFeaturesEnabled( boolean b ) {
        multiOscillatorControlPanel.setEnabled( b );
        slitControlPanel.setEnabled( b );
    }
}
