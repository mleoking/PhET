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

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.OrderOfMagnitudeSpinner;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.text.DecimalFormat;
import java.awt.*;

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
    private OrderOfMagnitudeSpinner oomSpinner;
    private SolubleSaltsModel model;
    private double maxExponent = SolubleSaltsConfig.DEFAULT_CONFIGURABLE_KSP;
    private double minExponent = 1E-40;
    private double defaultExponent = SolubleSaltsConfig.DEFAULT_CONFIGURABLE_KSP;

    /**
     *
     * @param model
     */
    public KspControl( final SolubleSaltsModel model ) {
        final GridBagConstraints gbc = new DefaultGridBagConstraints();
        this.model = model;

        kspSlider = createSlider( model, 1E-15 );

        mantissaSpinner = new JSpinner( new SpinnerNumberModel(1, 0, 10, 0.1 ));
        mantissaSpinner.setPreferredSize( new Dimension( 45, (int)mantissaSpinner.getPreferredSize().getHeight() ) );
//        mantissaSpinner.setPreferredSize( new Dimension( 10, (int)mantissaSpinner.getPreferredSize().getHeight() ) );
        mantissaSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setKsp();
            }
        } );

        oomSpinner = new OrderOfMagnitudeSpinner( defaultExponent, minExponent, maxExponent, "0E00" );
        oomSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setKsp();
//                sliderChanger( model, oomSpinner, gbc );
            }
        } );


        // Initialize the model
        setKsp();


        setBorder( new TitledBorder( "Ksp") );
        setLayout( new GridBagLayout() );

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add( mantissaSpinner, gbc );
        gbc.gridx = 1;
        add( new JLabel( "X"), gbc );
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add( oomSpinner, gbc );




//        gbc.anchor = GridBagConstraints.CENTER;
//        gbc.gridwidth = 2;
//        gbc.gridy = 1;
//        add( kspSlider, gbc );
//
//        gbc.gridwidth = 1;
//        gbc.weightx = .5;
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.EAST;
//        add( new JLabel( SimStrings.get("ControlLabels.MaxKsp") + ": "), gbc);
//        gbc.anchor = GridBagConstraints.WEST;
//        gbc.gridx = 1;
//        add( oomSpinner, gbc );
    }

    private void setKsp() {
        double mantissa = ((Double)mantissaSpinner.getValue()).doubleValue();
        double exponent = ((Double)oomSpinner.getValue()).doubleValue();
        model.setKsp( mantissa * exponent );
    }

    private void sliderChanger( SolubleSaltsModel model, OrderOfMagnitudeSpinner oomSpinner, GridBagConstraints gbc ) {
        remove( kspSlider );
        kspSlider = createSlider( model, ((Double)oomSpinner.getValue()).doubleValue() );
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add( kspSlider, gbc );
        revalidate();
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
}
