/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import java.awt.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

/**
 * KspControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class KspControl extends JPanel {

    private double minValue = 0;
    private double maxValue = 3E-16;
    private ModelSlider kspSlider;
    private JSpinner mantissaSpinner;
    private JSpinner oomSpinner;
    //    private OrderOfMagnitudeSpinner oomSpinner;
    private SolubleSaltsModel model;
    private double maxExponent = SolubleSaltsConfig.DEFAULT_CONFIGURABLE_KSP;
    private double minExponent = -40;
    //    private double minExponent = 1E-40;
    private double defaultExponent = SolubleSaltsConfig.DEFAULT_CONFIGURABLE_KSP_EXP;

    /**
     * @param model
     */
    public KspControl( final SolubleSaltsModel model ) {
        this.model = model;

        kspSlider = createSlider( model, 1E-15 );

        mantissaSpinner = new JSpinner( new SpinnerNumberModel( 1, 0, 10, 0.1 ) );
        mantissaSpinner.setPreferredSize( new Dimension( 45, (int) mantissaSpinner.getPreferredSize().getHeight() ) );
//        mantissaSpinner.setPreferredSize( new Dimension( 10, (int)mantissaSpinner.getPreferredSize().getHeight() ) );
        mantissaSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setKsp();
            }
        } );

        oomSpinner = new JSpinner( new SpinnerNumberModel( defaultExponent, minExponent, maxExponent, 1 ) );
        oomSpinner.setPreferredSize( new Dimension( 45, (int) oomSpinner.getPreferredSize().getHeight() ) );
        oomSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setKsp();
//                sliderChanger( model, oomSpinner, gbc );
            }
        } );
//
//        oomSpinner = new OrderOfMagnitudeSpinner( defaultExponent, minExponent, maxExponent, "0E00" );
//        oomSpinner.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                setKsp();
////                sliderChanger( model, oomSpinner, gbc );
//            }
//        } );

        // Initialize the model
        setKsp();


        setBorder( new TitledBorder( "Ksp" ) );
        setLayout( new GridBagLayout() );

        final GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add( mantissaSpinner, gbc );
        gbc.gridx = 1;
        add( new JLabel( " E " ), gbc );
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add( oomSpinner, gbc );
    }

    private void setKsp() {
        model.setKsp( getKsp() );
    }

    private ModelSlider createSlider( final SolubleSaltsModel model, double maxValue ) {
        final ModelSlider kspSlider = new ModelSlider( "", "", 0, maxValue, maxValue / 2 );
        final DecimalFormat kspFormat = new DecimalFormat( "0.00E00" );
        kspSlider.setSliderLabelFormat( kspFormat );
        kspSlider.setTextFieldFormat( kspFormat );
        kspSlider.setNumMajorTicks( 3 );
        kspSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setKsp( kspSlider.getValue() );
            }
        } );
        model.setKsp( kspSlider.getValue() );
        return kspSlider;
    }

    /**
     * Returns the Ksp value specified by the controls in this panel.
     *
     * @return Ksp
     */
    public double getKsp() {
        double mantissa = ( (Double) mantissaSpinner.getValue() ).doubleValue();
        double exponent = ( (Double) oomSpinner.getValue() ).doubleValue();
        double ksp = mantissa * Math.pow( 10, exponent );
        return ksp;
//        System.out.println( "ksp = " + ksp );
//        double mantissa = ((Double)mantissaSpinner.getValue()).doubleValue();
//        double exponent = ((Double)oomSpinner.getValue()).doubleValue();
//
//        return mantissa * exponent;
    }
}
