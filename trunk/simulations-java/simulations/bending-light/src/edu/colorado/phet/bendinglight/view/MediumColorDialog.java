// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

public class MediumColorDialog extends JDialog {

    public MediumColorDialog( PhetFrame frame, final BendingLightModel thisisMyModel ) {
        super( frame, false );

        setContentPane( new JTabbedPane() {
            final JColorChooser airChooser;
            final JColorChooser waterChooser;
            final JColorChooser glassChooser;
            final JColorChooser diamondChooser;

            {
                airChooser = createChooser( BendingLightModel.AIR_COLOR );
                addTab( "Air", airChooser );
                waterChooser = createChooser( BendingLightModel.WATER_COLOR );
                addTab( "Water", waterChooser );
                glassChooser = createChooser( BendingLightModel.GLASS_COLOR );
                addTab( "Glass", glassChooser );
                diamondChooser = createChooser( BendingLightModel.DIAMOND_COLOR );
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
                        if ( value < BendingLightModel.WATER.index ) {
                            double ratio = new Function.LinearFunction( 1.0, BendingLightModel.WATER.index, 0, 1 ).evaluate( value );
                            return colorBlend( airColor, waterColor, ratio );
                        }
                        else if ( value < BendingLightModel.GLASS.index ) {
                            double ratio = new Function.LinearFunction( BendingLightModel.WATER.index, BendingLightModel.GLASS.index, 0, 1 ).evaluate( value );
                            return colorBlend( waterColor, glassColor, ratio );
                        }
                        else if ( value < BendingLightModel.DIAMOND.index ) {
                            double ratio = new Function.LinearFunction( BendingLightModel.GLASS.index, BendingLightModel.DIAMOND.index, 0, 1 ).evaluate( value );
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
