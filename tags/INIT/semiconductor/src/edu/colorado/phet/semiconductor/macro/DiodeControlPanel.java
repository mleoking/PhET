package edu.colorado.phet.semiconductor.macro;

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
    SemiconductorModule module;
    ButtonGroup bg = new ButtonGroup();
    private JPanel pan = new JPanel();

    public DiodeControlPanel( final SemiconductorModule module ) {
        this.module = module;
        pan.setLayout( new BoxLayout( pan, BoxLayout.Y_AXIS ) );

        addJButton( "One (1)", new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSingleSection();
            }
        }, false );
        addJButton( "Two (2)", new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setDoubleSection();
            }
        }, true );
        addJButton( "Three (3)", new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setTripleSection();
            }
        }, false );
        Border b = BorderFactory.createTitledBorder( "Segments" );
        pan.setBorder( b );

        setBackground( new Color( 240, 230, 210 ) );
        add( pan );
    }

    private void addJButton( String s, ActionListener actionListener, boolean selected ) {
        JRadioButton button = new JRadioButton( s );
        button.addActionListener( actionListener );
        button.setSelected( selected );
        bg.add( button );
        pan.add( button );
    }
}
