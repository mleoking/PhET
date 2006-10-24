/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 1:23:37 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */
public class AtomShapeControlPanel extends VerticalLayoutPanel {
    public AtomShapeControlPanel( final DGModule dgModule ) {
        setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "atom.shape" ) ) );
        JRadioButton circular = new JRadioButton( QWIStrings.getString( "circular" ), dgModule.isAtomShapeCircular() );
        JRadioButton squares = new JRadioButton( QWIStrings.getString( "square" ), dgModule.isAtomShapeSquare() );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( circular );
        buttonGroup.add( squares );
        circular.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.setAtomShapeCircular();
            }
        } );
        squares.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.setAtomShapeSquare();
            }
        } );
        if( circular.isSelected() ) {
            add( circular );
            add( squares );
        }
        else {
            add( squares );
            add( circular );
        }
    }
}
