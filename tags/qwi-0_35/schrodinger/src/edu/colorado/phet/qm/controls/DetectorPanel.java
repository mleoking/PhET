/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 29, 2005
 * Time: 11:41:25 AM
 * Copyright (c) Jun 29, 2005 by Sam Reid
 */

public class DetectorPanel extends VerticalLayoutPanel {
    private SchrodingerModule module;

    public DetectorPanel( final SchrodingerModule module ) {
        this.module = module;
        setFillNone();
        setBorder( BorderFactory.createTitledBorder( "Detection" ) );
        JButton removeAll = new JButton( "Remove All" );
        removeAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.removeAllDetectors();
            }
        } );

        JButton newDetector = new JButton( "Add Detector" );
        newDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addDetector();
            }
        } );
        add( newDetector );

        final JCheckBox repeats = new JCheckBox( "Repeats", getDiscreteModel().getDetectorSet().isRepeats() );
        repeats.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().getDetectorSet().setRepeats( repeats.isSelected() );
            }
        } );

        final JCheckBox autodetect = new JCheckBox( "Autodetect", getDiscreteModel().isAutoDetect() );
        autodetect.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setAutoDetect( autodetect.isSelected() );
                repeats.setEnabled( autodetect.isSelected() );
            }
        } );

        add( autodetect );
        add( repeats );

        final JButton detect = new JButton( "Detect!" );
        detect.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().detect();
            }
        } );
        add( detect );

        final JButton enableAll = new JButton( "Enable all" );
        enableAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().enableAllDetectors();
            }
        } );
        add( enableAll );
        add( removeAll );
    }

    private DiscreteModel getDiscreteModel() {
        return module.getDiscreteModel();
    }
}
