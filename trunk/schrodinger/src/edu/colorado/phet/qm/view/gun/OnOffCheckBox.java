/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.phetcommon.LucidaSansFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 11:27:46 PM
 * Copyright (c) Mar 2, 2006 by Sam Reid
 */

public class OnOffCheckBox extends JCheckBox {
    public OnOffCheckBox( final OnOffItem onOffItem ) {
        super( "On", onOffItem.isOn() );
        setVerticalTextPosition( AbstractButton.BOTTOM );
        setHorizontalTextPosition( AbstractButton.CENTER );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onOffItem.setOn( isSelected() );
            }
        } );
        setFont( new LucidaSansFont( 13, true ) );
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
    }
}
