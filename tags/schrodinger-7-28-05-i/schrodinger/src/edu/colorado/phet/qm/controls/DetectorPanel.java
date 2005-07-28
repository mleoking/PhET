/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
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
        JButton newDetector = new JButton( "Add Detector" );
        newDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addDetector();
            }
        } );
        add( newDetector );

//        final JCheckBox causeCollapse = new JCheckBox( "Causes Collapse", true );
//        causeCollapse.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                getDiscreteModel().setDetectionCausesCollapse( causeCollapse.isSelected() );
//            }
//        } );
//        add( causeCollapse );

        final JCheckBox oneShot = new JCheckBox( "One-Shot" );
        oneShot.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setOneShotDetectors( oneShot.isSelected() );
            }
        } );
        oneShot.setSelected( getDiscreteModel().isOneShotDetectors() );
        add( oneShot );

        final JCheckBox autodetect = new JCheckBox( "Autodetect", false );
        autodetect.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setAutoDetect( autodetect.isSelected() );
            }
        } );
        add( autodetect );

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
    }

    private DiscreteModel getDiscreteModel() {
        return module.getDiscreteModel();
    }
}
