package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;

public class MediumColorDialog extends JDialog {

    public MediumColorDialog( PhetFrame frame, final LRRModel thisisMyModel ) {
        super( frame, false );

        setContentPane( new JTabbedPane() {
            final JColorChooser airChooser;
            final JColorChooser waterChooser;
            final JColorChooser glassChooser;
            final JColorChooser diamondChooser;

            {
                airChooser = createChooser( Color.BLACK );
                addTab( "Air", airChooser );
                waterChooser = createChooser( new Color( 31, 40, 75 ) );
                addTab( "Water", waterChooser );
                glassChooser = createChooser( new Color( 72, 72, 72 ) );
                addTab( "Glass", glassChooser );
                diamondChooser = createChooser( new Color( 144, 144, 123 ) );
                addTab( "Diamond", diamondChooser );
            }

            public JColorChooser createChooser( Color initialColor ) {
                return new JColorChooser( initialColor ) {{
                    getSelectionModel().addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            updateColorFunction();
                        }
                    } );
                }};
            }

            public void updateColorFunction() {
                final Color airColor = airChooser.getColor();
                final Color waterColor = waterChooser.getColor();
                final Color glassColor = glassChooser.getColor();
                final Color diamondColor = diamondChooser.getColor();
                thisisMyModel.colorMappingFunction.setValue( new Function1<Double, Color>() {
                    public Color apply( Double value ) {
                        if ( value < LRRModel.N_WATER ) {
                            double ratio = new Function.LinearFunction( 1.0, LRRModel.N_WATER, 0, 1 ).evaluate( value );
                            return colorBlend( airColor, waterColor, ratio );
                        }
                        else if ( value < LRRModel.N_GLASS ) {
                            double ratio = new Function.LinearFunction( LRRModel.N_WATER, LRRModel.N_GLASS, 0, 1 ).evaluate( value );
                            return colorBlend( waterColor, glassColor, ratio );
                        }
                        else if ( value < LRRModel.N_DIAMOND ) {
                            double ratio = new Function.LinearFunction( LRRModel.N_GLASS, LRRModel.N_DIAMOND, 0, 1 ).evaluate( value );
                            return colorBlend( glassColor, diamondColor, ratio );
                        }
                        else {
                            return diamondColor;
                        }
                    }
                } );
            }

            public Color colorBlend( Color a, Color b, double ratio ) {
                return new Color(
                        (int) ( ( (float) a.getRed() ) * ( 1 - ratio ) + ( (float) b.getRed() ) * ratio ),
                        (int) ( ( (float) a.getGreen() ) * ( 1 - ratio ) + ( (float) b.getGreen() ) * ratio ),
                        (int) ( ( (float) a.getBlue() ) * ( 1 - ratio ) + ( (float) b.getBlue() ) * ratio )
                );
            }
        } );

        pack();
    }

}
