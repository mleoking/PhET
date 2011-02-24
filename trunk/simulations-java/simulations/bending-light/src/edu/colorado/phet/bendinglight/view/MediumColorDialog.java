// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.model.LRRModel;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

public class MediumColorDialog extends JDialog {

    public MediumColorDialog( PhetFrame frame, final LRRModel thisisMyModel ) {
        super( frame, false );

        setContentPane( new JTabbedPane() {
            final JColorChooser airChooser;
            final JColorChooser waterChooser;
            final JColorChooser glassChooser;
            final JColorChooser diamondChooser;

            {
                airChooser = createChooser( LRRModel.AIR_COLOR );
                addTab( "Air", airChooser );
                waterChooser = createChooser( LRRModel.WATER_COLOR );
                addTab( "Water", waterChooser );
                glassChooser = createChooser( LRRModel.GLASS_COLOR );
                addTab( "Glass", glassChooser );
                diamondChooser = createChooser( LRRModel.DIAMOND_COLOR );
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
                        if ( value < LRRModel.WATER.index ) {
                            double ratio = new Function.LinearFunction( 1.0, LRRModel.WATER.index, 0, 1 ).evaluate( value );
                            return colorBlend( airColor, waterColor, ratio );
                        }
                        else if ( value < LRRModel.GLASS.index ) {
                            double ratio = new Function.LinearFunction( LRRModel.WATER.index, LRRModel.GLASS.index, 0, 1 ).evaluate( value );
                            return colorBlend( waterColor, glassColor, ratio );
                        }
                        else if ( value < LRRModel.DIAMOND.index ) {
                            double ratio = new Function.LinearFunction( LRRModel.GLASS.index, LRRModel.DIAMOND.index, 0, 1 ).evaluate( value );
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
