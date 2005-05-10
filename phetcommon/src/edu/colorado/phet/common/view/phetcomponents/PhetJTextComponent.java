/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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
    private Timer timer;

    /**
     * Clients should use PhetJComponent.newInstance().
     * If the client is making this, assume it's top level.
     */
    public PhetJTextComponent( Component apparatusPanel, JTextComponent component ) {
        this( apparatusPanel, component, true );
    }

    public PhetJTextComponent( Component apparatusPanel, JTextComponent component, boolean topLevel ) {
        super( apparatusPanel, component, topLevel );
        this.textComponent = component;
        caret = component.getCaret();
        int blinkRate = caret.getBlinkRate();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( hasKeyFocus ) {
                    repaint();//todo just redraw the caret region.
                }
            }
        };
        timer = new Timer( blinkRate, actionListener );
        if( autorepaintCaret ) {
            timer.start();
        }
        caret.setVisible( false );
        caret.setSelectionVisible( true );
        component.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                gainedKeyFocus();
            }

            public void focusLost( FocusEvent e ) {
                lostKeyFocus();
            }
        } );
        repaint();
    }

    public void gainedKeyFocus() {
        this.hasKeyFocus = true;
        super.gainedKeyFocus();
        textComponent.setFocusable( true );
        textComponent.requestFocus();
        caret.setVisible( true );
        repaint();
    }

    public void lostKeyFocus() {
        this.hasKeyFocus = false;
        super.lostKeyFocus();
        caret.setVisible( false );
        repaint();
    }


}
