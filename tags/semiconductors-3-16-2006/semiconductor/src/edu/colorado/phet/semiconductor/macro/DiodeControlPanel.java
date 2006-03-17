package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 7:13:50 PM
 * Copyright (c) Feb 7, 2004 by Sam Reid
 */
public class DiodeControlPanel extends JPanel {
    ButtonGroup bg = new ButtonGroup();
    private JPanel pan = new JPanel();

    public DiodeControlPanel( final SemiconductorModule module ) {
        pan.setLayout( new BoxLayout( pan, BoxLayout.Y_AXIS ) );

        addJButton( SimStrings.get( "DiodeControlPanel.OneButton" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSingleSection();
            }
        }, false );
        addJButton( SimStrings.get( "DiodeControlPanel.TwoButton" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setDoubleSection();
            }
        }, true );
        addJButton( SimStrings.get( "DiodeControlPanel.ThreeButton" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setTripleSection();
            }
        }, false );
        Border b = BorderFactory.createTitledBorder( SimStrings.get( "DiodeControlPanel.SegmentBorder" ) );
        pan.setBorder( b );

        setBackground( new Color( 240, 230, 210 ) );
        add( pan );
        final JCheckBox gate = new JCheckBox( SimStrings.get( "DiodeControlPanel.GateCheckBox" ) );
        gate.setSelected( true );
        gate.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getMagnetGraphic().setVisible( gate.isSelected() );
                if( !gate.isSelected() ) {
                    module.releaseGate();
                }
            }
        } );
        add( gate );
    }

    private void addJButton( String s, ActionListener actionListener, boolean selected ) {
        JRadioButton button = new JRadioButton( s );
        button.addActionListener( actionListener );
        button.setSelected( selected );
        bg.add( button );
        pan.add( button );
    }
}
