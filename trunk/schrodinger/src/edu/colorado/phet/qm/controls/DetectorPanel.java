/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.model.QWIModel;

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
    private QWIModule module;

    public DetectorPanel( final QWIModule module ) {
        this.module = module;
        setFillNone();
        setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "detection" ) ) );
        JButton removeAll = new JButton( QWIStrings.getString( "remove.all" ) );
        removeAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.removeAllDetectors();
            }
        } );

        JButton newDetector = new JButton( QWIStrings.getString( "add.detector" ) );
        newDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addDetector();
            }
        } );
        add( newDetector );

//        final JCheckBox repeats = new JCheckBox( "Repeats", getDiscreteModel().getDetectorSet().isRepeats() );
//        repeats.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                getDiscreteModel().getDetectorSet().setRepeats( repeats.isSelected() );
//            }
//        } );
//        final JCheckBox oneshot = new JCheckBox( "One-Shot", getDiscreteModel().getDetectorSet().isOneShot() );
//        oneshot.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                getDiscreteModel().getDetectorSet().setOneShot( oneshot.isSelected() );
//            }
//        } );
        final JCheckBox repeatDetect = new JCheckBox( QWIStrings.getString( "repeat.detect" ), !getDiscreteModel().getDetectorSet().isOneShot() );
        repeatDetect.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().getDetectorSet().setOneShot( !repeatDetect.isSelected() );
            }
        } );

        final JCheckBox autodetect = new JCheckBox( QWIStrings.getString( "autodetect" ), getDiscreteModel().isAutoDetect() );
        autodetect.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setAutoDetect( autodetect.isSelected() );
//                repeatDetect.setEnabled( !autodetect.isSelected() );
            }
        } );
        //*"Autodetect" has to be deselected before "Repeat Detect" can be checked.
//        repeatDetect.setEnabled( !autodetect.isSelected() );
        add( autodetect );
//        add( repeats );
        add( repeatDetect );

        final JButton detect = new JButton( QWIStrings.getString( "detect" ) );
        detect.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().detect();
            }
        } );
        add( detect );

        final JButton enableAll = new JButton( QWIStrings.getString( "enable.all" ) );
        enableAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().enableAllDetectors();
            }
        } );
//        add( enableAll );
        add( removeAll );
    }

    private QWIModel getDiscreteModel() {
        return module.getQWIModel();
    }
}
