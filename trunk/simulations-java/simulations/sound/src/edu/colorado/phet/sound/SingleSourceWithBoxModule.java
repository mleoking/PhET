/**
 * Class: SingleSourceWithBoxModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 19, 2004
 */
package edu.colorado.phet.sound;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.sound.coreadditions.ScalarObservable;
import edu.colorado.phet.sound.model.AttenuationFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.DialGauge;
import edu.colorado.phet.sound.view.SoundApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;

public class SingleSourceWithBoxModule extends SingleSourceListenModule {
    private AirBoxGraphic boxInteriorGraphic;
    private PhetShapeGraphic boxGraphic;
    private double airDensity;
    private ScalarObservable airDensityObservable;
    private DialGauge pressureGauge;
    final int maxDensity = 200;

    protected SingleSourceWithBoxModule() {
        super( SoundResources.getString( "ModuleTitle.SingelSourceWithBox" ) );
        init();
    }

    private void init() {

        // Move the speaker listener out in front of the speaker a bit so it
        // it gets an attenuated wavefront. If we leave it at (0,0), the sound
        // is never attenuated
        getSpeakerListener().setLocation( new Point2D.Double( 10, 0 ) );

        // Add a pressure pressureGauge
        airDensityObservable = new ScalarObservable() {
            public double getValue() {
                return airDensity;
            }
        };
        double x = 150;
        double y = 80;
        double diam = 100;
        pressureGauge = new DialGauge( airDensityObservable, getApparatusPanel(), x, y, diam, 0, 1,
                                       SoundResources.getString( "SingleSourceWithBoxModule.Pressure" ),
                                       SoundResources.getString( "SingleSourceWithBoxModule.ATM" ) );
        Rectangle2D.Double gaugeStem = new Rectangle2D.Double( x - 5, y + diam / 2, 10, 20 );

        // todo: updated 1/21/06
        pressureGauge.addGraphic( new PhetShapeGraphic( getSimulationPanel(), gaugeStem, Color.black ), 6 );

        SoundModel soundModel = getSoundModel();
        WaveMedium waveMedium = soundModel.getWaveMedium();

        Shape box = createBox();

        VariableWaveMediumAttenuationFunction attenuationFunction = new VariableWaveMediumAttenuationFunction();
        attenuationFunction.setVariableRegion( box );
        waveMedium.setAttenuationFunction( attenuationFunction );
        boxGraphic = new PhetShapeGraphic( getSimulationPanel(), box, new BasicStroke( 8f ), new Color( 124, 80, 10 ) );
        boxInteriorGraphic = new AirBoxGraphic( getApparatusPanel(), box );

        getApparatusPanel().addGraphic( boxGraphic, 8 );
        getApparatusPanel().addGraphic( boxInteriorGraphic, 6 );
        getApparatusPanel().addGraphic( pressureGauge, 5 );

        // Control Panel
        SoundControlPanel controlPanel = (SoundControlPanel)getControlPanel();
        controlPanel.addPanel( new BoxAirDensityControlPanel( attenuationFunction ) );

        // Make the listener the audio source
        getAudioControlPanel().setAudioSource( SoundApparatusPanel.LISTENER_SOURCE );
//        getAudioControlPanel().setSpeakerRBEnabled( false );

        // Make the ListenerGraphic non-movable
        getListenerGraphic().setMovable( false );
    }

    /**
     * Creates a shape that is a box with a round wall on the front
     *
     * @return
     */
    private Shape createBox() {
        int boxWidth = 180;
        int boxHeight = 350;

        float b = 50;
        GeneralPath box = new GeneralPath();
        float x0 = SoundConfig.s_wavefrontBaseX - b;
        float y0 = SoundConfig.s_wavefrontBaseY - boxHeight / 2;
        double alpha = Math.atan( ( boxHeight / 2 ) / ( boxWidth - b ) );
        float d = (float)( boxWidth + ( boxHeight / 2 ) * Math.tan( alpha ) );
        box.moveTo( x0, y0 );
        box.lineTo( x0 + boxWidth, y0 );
        box.quadTo( x0 + d * 0.85f, SoundConfig.s_wavefrontBaseY,
                    x0 + boxWidth, y0 + boxHeight );
        box.lineTo( x0, y0 + boxHeight );
        box.closePath();
        return box;
    }

    public int rgbAt( int x, int y ) {
        // todo: figure out how to get rid of the hard-coded 80.
        if( boxInteriorGraphic.contains( SoundConfig.s_wavefrontBaseX + 80 + x,
                                         SoundConfig.s_wavefrontBaseY + y ) ) {
            return boxInteriorGraphic.getGrayLevel();
        }
        else {
            return super.rgbAt( x, y );
        }
    }

    public void setAudioSource( int source ) {
        super.setAudioSource( source );
    }

    private void showBoxAndGauge( boolean show ) {
        if( show ) {
            getApparatusPanel().addGraphic( boxGraphic, 8 );
            getApparatusPanel().addGraphic( boxInteriorGraphic, 6 );
            getApparatusPanel().addGraphic( pressureGauge, 5 );
        }
        else {
            getApparatusPanel().removeGraphic( boxGraphic );
            getApparatusPanel().removeGraphic( boxInteriorGraphic );
            getApparatusPanel().removeGraphic( pressureGauge );
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private static class AirBoxGraphic extends PhetShapeGraphic {
        static Color[] grayLevels = new Color[256];
        private int grayLevel;

        static {
            for( int i = 0; i < 256; i++ ) {
                grayLevels[i] = new Color( i, i, i );
            }
        }

        AirBoxGraphic( Component component, Shape shape ) {
            super( component, shape, new Color( 128, 128, 128 ), new BasicStroke( 1f ), new Color( 138, 100, 70 ) );
        }

        void setAirDensity( double density ) {
            // This gives a background that is black when the air is completely evacuated from the
            // box. To get a white background instead, use the second, commented line.
            grayLevel = (int)( 128 * density );
//            grayLevel = 255 - (int)( 128 * density );
            this.setPaint( grayLevels[grayLevel] );
        }

        int getGrayLevel() {
            return grayLevel;
        }
    }

    private class BoxAirDensityControlPanel extends JPanel {
        VariableWaveMediumAttenuationFunction attenuationFunction;
        String evacuateLabel = SoundResources.getString( "SingleSourceWithBoxModule.RemoveAir" );
        String addLabel = SoundResources.getString( "SingleSourceWithBoxModule.AddAir" );
        boolean evacuateToggle = true;
        private JButton airButton;
        private JSlider densitySlider;
        private BoxEvacuator boxEvacuator;
        private Color airButtonEnabledBackground;

        public BoxAirDensityControlPanel( final VariableWaveMediumAttenuationFunction attenuationFunction ) {

            this.attenuationFunction = attenuationFunction;

            airButton = new JButton( evacuateLabel );
            airButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    boxEvacuator = new BoxEvacuator();
                    boxEvacuator.start();
                }
            } );

            final int maxValue = 200;
            densitySlider = new JSlider( JSlider.HORIZONTAL, 0, maxValue, maxValue );
            densitySlider.setSnapToTicks( true );
            densitySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setAirDensity( densitySlider, maxValue, attenuationFunction );
                }
            } );
            setAirDensity( densitySlider, maxValue, attenuationFunction );
            densitySlider.setEnabled( false );

            JButton resetBtn = new JButton( SoundResources.getString( "ClockPanelLarge.Reset" ) );
            resetBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( boxEvacuator != null ) {
                        boxEvacuator.kill();
                    }
                }
            } );

            this.setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 0, 0, 0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL,
                                                             insets, 0, 0 );
            this.add( airButton, gbc );
            airButtonEnabledBackground = new Color( 100, 200, 100 );
            airButton.setBackground( airButtonEnabledBackground );
//            gbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
//                                          GridBagConstraints.CENTER,
//                                          GridBagConstraints.HORIZONTAL,
//                                          insets, 0, 0 );
            //            this.add( densitySlider, gbc );
            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            add( resetBtn, gbc );
            this.setBorder( new TitledBorder( SoundResources.getString( "SingleSourceWithBoxModule.BorderTitle" ) ) );
            this.setPreferredSize( new Dimension( 120, 120 ) );
        }

        private void setAirDensity( final JSlider densitySlider, final int maxValue, final VariableWaveMediumAttenuationFunction attenuationFunction ) {
            airDensity = ( (double)densitySlider.getValue() ) / maxValue;
            attenuationFunction.setVariableRegionAttenuation( airDensity );
            boxInteriorGraphic.setAirDensity( airDensity );
            airDensityObservable.notifyObservers();
        }

        private void setAirDensity( double density, final int maxValue, final VariableWaveMediumAttenuationFunction attenuationFunction ) {
            airDensity = ( density ) / maxDensity;
            //            airDensity = ( density ) / maxValue;
            attenuationFunction.setVariableRegionAttenuation( airDensity );
            boxInteriorGraphic.setAirDensity( airDensity );
            airDensityObservable.notifyObservers();
        }

        class BoxEvacuator extends Thread {
            private int maxValue;
            private int minValue;
            private Boolean kill = Boolean.FALSE;

            BoxEvacuator() {
                this.maxValue = densitySlider.getMaximum();
                this.minValue = densitySlider.getMinimum();
            }

            public void run() {
                kill = Boolean.FALSE;
                try {
                    // Disable the button so it can't be clicked while were doing out thing
                    airButton.setEnabled( false );
                    Color buttonBackground = airButton.getBackground();
                    airButton.setBackground( Color.gray );

                    // Pump air out or in
                    int incr = evacuateToggle ? -1 : 1;
                    int value = evacuateToggle ? maxValue : minValue;
                    int stop = evacuateToggle ? minValue : maxValue;
                    while( value != stop ) {

                        value += incr;
                        densitySlider.setValue( value );
                        setAirDensity( value, maxValue, attenuationFunction );
                        Thread.sleep( 100 );

                        // Check to see if we got a kill message
                        synchronized( kill ) {
                            if( kill.booleanValue() ) {
                                return;
                            }
                        }
                    }

                    // Enable the button and set its text
                    evacuateToggle = !evacuateToggle;
                    airButton.setText( evacuateToggle ? evacuateLabel : addLabel );
                    airButton.setEnabled( true );
                    airButton.setBackground( buttonBackground );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }

            synchronized void kill() {
                kill = Boolean.TRUE;
                reset();
            }

            void reset() {
                evacuateToggle = true;
                airButton.setText( evacuateLabel );
                airButton.setEnabled( true );
                airButton.setBackground( airButtonEnabledBackground );
                setAirDensity( maxDensity, maxDensity, attenuationFunction );
            }
        }
    }

    /**
     *
     */
    private class VariableWaveMediumAttenuationFunction implements AttenuationFunction {
        private Shape variableRegion;
        private double variableRegionAttenuation = 1;

        public void setVariableRegion( Shape region ) {
            this.variableRegion = region;
        }

        /**
         * This computes the attenuation of the signal given distance from the source.
         *
         * @param variableRegionAttenuation
         */
        public void setVariableRegionAttenuation( double variableRegionAttenuation ) {
            // The function is an arc of a circle
            this.variableRegionAttenuation = Math.sqrt( 1 - ( variableRegionAttenuation - 1 ) * ( variableRegionAttenuation - 1 ) );
        }

        public double getAttenuation( double x, double y ) {
            if( variableRegion != null && variableRegion.contains( x + SoundConfig.s_speakerBaseX, y + 201 ) ) {
                return variableRegionAttenuation;
            }
            else {
                return 1;
            }
        }
    }
}
