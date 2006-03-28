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
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

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
//public class KspControl extends ModelSlider {

    private double minValue = 0;
    private double maxValue = 3E-16;
    private ModelSlider kspSlider;

    public KspControl( final SolubleSaltsModel model ) {
        final GridBagConstraints gbc = new DefaultGridBagConstraints();

        kspSlider = createSlider( model, 1E-15 );

        final OrderOfMagnitudeSpinner oomSpinner = new OrderOfMagnitudeSpinner( 1E-15, 1E-20, 1E-15, "0E00" );
        oomSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "ConfigurableSaltControlPanel.stateChanged" );
                sliderChanger( model, oomSpinner, gbc );
            }
        } );


        setBorder( new TitledBorder( "Ksp") );
        setLayout( new GridBagLayout() );
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;
        gbc.gridy = 1;
        add( kspSlider, gbc );

        gbc.gridwidth = 1;
        gbc.weightx = .5;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add( new JLabel( "Max Ksp: "), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        add( oomSpinner, gbc );
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
