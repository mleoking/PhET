/**
 * Class: SingleSourceWithBoxModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 19, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.sound.model.AttenuationFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.SoundControlPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class SingleSourceWithBoxModule extends SingleSourceListenModule {
    private AirBoxGraphic boxInteriorGraphic;

    protected SingleSourceWithBoxModule( ApplicationModel appModel ) {
        super( appModel, "<html>Listen with<br>Varying Air Pressure</html>" );
        init();
    }

    private void init() {
        SoundModel soundModel = (SoundModel)getModel();
        WaveMedium waveMedium = soundModel.getWaveMedium();

        Shape box = createBox();
        VariableWaveMediumAttenuationFunction attenuationFunction = new VariableWaveMediumAttenuationFunction();
        attenuationFunction.setVariableRegion( box );
        waveMedium.setAttenuationFunction( attenuationFunction );
        PhetShapeGraphic boxGraphic = new PhetShapeGraphic( getApparatusPanel(), box, new BasicStroke( 4f ), new Color( 200, 200, 120 ) );
        addGraphic( boxGraphic, 8 );
        boxInteriorGraphic = new AirBoxGraphic( getApparatusPanel(), box );
        addGraphic( boxInteriorGraphic, 6 );

        SoundControlPanel controlPanel = (SoundControlPanel)getControlPanel();
        controlPanel.addPanel( new BoxAirDensityControlPanel( attenuationFunction ) );
    }

    /**
     * Creates a shape that is a box with a round wall on the front
     * @return
     */
    private Shape createBox() {
        int boxWidth = 180;
        int boxHeight = 350;

        float b = 50;
        GeneralPath box = new GeneralPath();
        float x0 = SoundConfig.s_wavefrontBaseX - b;
        float y0 = SoundConfig.s_wavefrontBaseY - boxHeight / 2;
        double r = boxWidth - b;
        double alpha = Math.atan( ( boxHeight / 2 ) / ( boxWidth - b ));
        float d = (float)( boxWidth + ( boxHeight / 2 ) * Math.tan( alpha ));
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

    static class AirBoxGraphic extends PhetShapeGraphic {
        static Color[] grayLevels = new Color[256];
        private int grayLevel;

        static {
            for( int i = 0; i < 256; i++ ) {
                grayLevels[i] = new Color( i, i, i );
            }
        }

        AirBoxGraphic( Component component, Shape shape ) {
            super( component, shape, new Color( 128, 128, 128 ), new BasicStroke( 4f ), new Color( 200, 200, 100 ) );
        }

        void setAirDensity( double density ) {
            grayLevel = 255 - (int)( 128 * density );
            this.setPaint( grayLevels[grayLevel] );
        }

        int getGrayLevel() {
            return grayLevel;
        }
    }

    class BoxAirDensityControlPanel extends JPanel {
        VariableWaveMediumAttenuationFunction attenuationFunction;

        public BoxAirDensityControlPanel( final VariableWaveMediumAttenuationFunction attenuationFunction ) {
            this.attenuationFunction = attenuationFunction;
            final int maxValue = 100;
            final JSlider densitySlider = new JSlider( JSlider.HORIZONTAL, 0, maxValue, maxValue );
            densitySlider.setSnapToTicks( true );
            densitySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setAirDensity( densitySlider, maxValue, attenuationFunction );
                }
            } );
            setAirDensity( densitySlider, maxValue, attenuationFunction );
            this.setLayout( new GridLayout( 2, 1 ) );
            this.setBorder( new TitledBorder( "Air Density" ) );
            this.setPreferredSize( new Dimension( 120, 80 ) );
            this.add( densitySlider );
        }

        private void setAirDensity( final JSlider densitySlider, final int maxValue, final VariableWaveMediumAttenuationFunction attenuationFunction ) {
            double airDensity = ( (double)densitySlider.getValue() ) / maxValue;
            attenuationFunction.setVariableRegionAttenuation( airDensity );
            boxInteriorGraphic.setAirDensity( airDensity );
        }

    }

    class VariableWaveMediumAttenuationFunction implements AttenuationFunction {
        private Shape variableRegion;
        private double variableRegionAttenuation = 1;

        public void setVariableRegion( Shape region ) {
            this.variableRegion = region;
        }

        /**
         * This computes the attenuation of the signal given distance from the source.
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
