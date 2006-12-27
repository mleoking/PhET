/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 10:53:48 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public class MacroControlPanel extends JPanel {
    private MacroModule macroModule;
    private JCheckBox lightOn;
    private JCheckBox dopants;

    public MacroControlPanel( final MacroModule macroModule ) {
        this.macroModule = macroModule;
        JRadioButton conductor = new JRadioButton( "Metal" );
        JRadioButton insulator = new JRadioButton( "Plastic" );
        JRadioButton photoconductor = new JRadioButton( "Photoconductor" );
        JRadioButton diode = new JRadioButton( "Diode" );
        conductor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                macroModule.setConductor();
            }
        } );
        insulator.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                macroModule.setInsulator();
            }
        } );
        photoconductor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                macroModule.setPhotoconductor();
            }
        } );
        diode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                macroModule.setDiode();
            }
        } );

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        JPanel typePanel = new JPanel();
        typePanel.setLayout( new BoxLayout( typePanel, BoxLayout.Y_AXIS ) );
        ButtonGroup bg = new ButtonGroup();
        bg.add( conductor );
        bg.add( insulator );
        bg.add( photoconductor );
        bg.add( diode );
        conductor.setSelected( true );

        typePanel.add( conductor );
        typePanel.add( insulator );
        typePanel.add( photoconductor );
        typePanel.add( diode );

        Border b = BorderFactory.createRaisedBevelBorder();
        b = BorderFactory.createTitledBorder( b, "Materials" );
        typePanel.setBorder( b );

        add( typePanel );

//        lightOn = new JCheckBox("Shine The Light");
//        lightOn.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                macroModule.setFlashlightOn(lightOn.isSelected());
//            }
//        });
//        add(lightOn);

        dopants = new JCheckBox( "Show Dopants" );
        dopants.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                macroModule.setDopantPanelVisible( dopants.isSelected() );
            }
        } );
        add( dopants );

        final JCheckBox showPlusses = new JCheckBox( "Show + Charges" );
        showPlusses.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                macroModule.setShowPlusses( showPlusses.isSelected() );
            }
        } );
        add( showPlusses );

    }

    public void disableSemiconductor() {
        lightOn.setEnabled( false );
        dopants.setEnabled( false );
        repaint();
    }

    public void enableSemiconductor() {
        lightOn.setEnabled( true );
        dopants.setEnabled( true );
        repaint();
    }

//    public void turnOffLight() {
//        lightOn.setSelected(false);
//        macroModule.setFlashlightOn(lightOn.isSelected());
//    }
}
