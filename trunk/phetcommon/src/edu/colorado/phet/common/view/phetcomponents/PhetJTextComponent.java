/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import edu.colorado.phet.common.view.ApparatusPanel;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 9, 2005
 * Time: 3:35:04 PM
 * Copyright (c) Mar 9, 2005 by Sam Reid
 */

public class PhetJTextComponent extends PhetJComponent {
    private JTextComponent textComponent;
    private boolean autorepaintCaret = true;

    public PhetJTextComponent( ApparatusPanel apparatusPanel, JTextComponent component ) {
        super( apparatusPanel, component );
        this.textComponent = component;
        Caret caret = component.getCaret();
        int blinkRate = caret.getBlinkRate();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                redraw();//todo just redraw the caret region.
            }
        };
        Timer timer = new Timer( blinkRate, actionListener );
        if( autorepaintCaret ) {
            timer.start();
        }
        redraw();
    }

    protected void redraw() {
        if( textComponent != null ) {
            super.redraw();
            Graphics2D g2 = createGraphics();
            /**How to handle text..?*/
            Caret caret = textComponent.getCaret();
            caret.setVisible( true );
            caret.setSelectionVisible( true );
            int blinkie = caret.getBlinkRate();
            long time = System.currentTimeMillis();
            long remainder = time % ( blinkie * 2 );
            if( remainder <= 500 ) {
                caret.paint( g2 );
            }
        }
    }
}
