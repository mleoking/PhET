/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import edu.colorado.phet.common.view.ApparatusPanel;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
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
    private Caret caret;
    private boolean hasKeyFocus = false;

    public PhetJTextComponent( ApparatusPanel apparatusPanel, JTextComponent component ) {
        super( apparatusPanel, component );
        this.textComponent = component;
        caret = component.getCaret();
        int blinkRate = caret.getBlinkRate();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( hasKeyFocus ) {
                    redraw();//todo just redraw the caret region.
                }
            }
        };
        Timer timer = new Timer( blinkRate, actionListener );
        if( autorepaintCaret ) {
            timer.start();
        }
        caret.setVisible( false );
        caret.setSelectionVisible( true );

        redraw();
    }

    public void gainedKeyFocus() {
        this.hasKeyFocus = true;
        super.gainedKeyFocus();
        textComponent.setFocusable( true );
        textComponent.requestFocus();
        caret.setVisible( true );
        redraw();
    }

    public void lostKeyFocus() {
        this.hasKeyFocus = false;
        super.lostKeyFocus();
        caret.setVisible( false );
        redraw();
    }
}
