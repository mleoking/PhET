/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 2:55:19 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class PButton extends PNode {
    private JButton button;

    public PButton( PSwingCanvas pSwingCanvas, String label ) {
        button = new JButton( label ){
            protected void paintComponent( Graphics g ) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        PSwing pSwing = new PSwing( pSwingCanvas, button );
        addChild( pSwing );
    }

    public void addActionListener( ActionListener actionListener ) {
        button.addActionListener( actionListener );
    }

    public String toString() {
        return "PButton["+button.getText()+"]: "+super.toString();
    }

}
