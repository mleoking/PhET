/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 10:12:30 AM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class ConfigureHorizontalSlitPanel extends AdvancedPanel {
    private HorizontalDoubleSlit slit;

    public ConfigureHorizontalSlitPanel( HorizontalDoubleSlit doubleSlitPotential ) {
//        setBorder( BorderFactory.createTitledBorder( "Configure Slits" ) );
        super( "Configure Slits>>", "Hide Slit Config<<" );
        this.slit = doubleSlitPotential;
        double pow = doubleSlitPotential.getPotential();

        SlitSpinner potentialSpinner = new SlitSpinner( "Potential",
                                                        new SpinnerNumberModel( pow, 0, Double.POSITIVE_INFINITY, pow / 10 ),
                                                        new ChangeHandler() {
                                                            public void valueChanged( Number value ) {
                                                                slit.setPotential( value.doubleValue() );
                                                            }
                                                        } );
        addControlFullWidth( potentialSpinner );


        SlitSpinner sizeSpinner = new SlitSpinner( "Size", new SpinnerNumberModel( doubleSlitPotential.getSlitSize(), 0, doubleSlitPotential.getGridWidth() / 2, 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setSlitSize( value.intValue() );
            }
        } );
        addControlFullWidth( sizeSpinner );

        SlitSpinner sepSpinner = new SlitSpinner( "Separation", new SpinnerNumberModel( doubleSlitPotential.getSlitSeparation(), 0, doubleSlitPotential.getGridHeight(), 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setSlitSeparation( value.intValue() );
            }
        } );
        addControlFullWidth( sepSpinner );

        SlitSpinner depth = new SlitSpinner( "Depth", new SpinnerNumberModel( doubleSlitPotential.getHeight(), 0, doubleSlitPotential.getGridHeight(), 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setHeight( value.intValue() );
            }
        } );
        addControlFullWidth( depth );

        SlitSpinner yval = new SlitSpinner( "Y", new SpinnerNumberModel( doubleSlitPotential.getY(), 0, doubleSlitPotential.getGridHeight(), 1 ), new ChangeHandler() {
            public void valueChanged( Number value ) {
                slit.setY( value.intValue() );
            }
        } );

        addControlFullWidth( yval );
    }

    private static interface ChangeHandler {
        public void valueChanged( Number value );
    }

    private static class SlitSpinner extends JPanel {
        public JSpinner spinner;

        public SlitSpinner( String name, SpinnerModel spinnerModel, final ChangeHandler changeHandler ) {
            spinner = new JSpinner( spinnerModel );
            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Number val = (Number)spinner.getValue();
                    changeHandler.valueChanged( val );
                }
            } );
//            setBorder( BorderFactory.createTitledBorder( name ) );
            spinner.setPreferredSize( new Dimension( 150, spinner.getPreferredSize().height ) );
            setLayout( new BorderLayout() );
            JLabel label = new JLabel( name );
            add( label, BorderLayout.WEST );
            add( spinner, BorderLayout.EAST );
        }
    }
}
