// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

public class PButton extends PNode {
    private JButton button;

    public PButton( PSwingCanvas pSwingCanvas, String label ) {
        button = new JButton( label ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        PSwing pSwing = new PSwing( button );
        addChild( pSwing );
    }

    public void addActionListener( ActionListener actionListener ) {
        button.addActionListener( actionListener );
    }

    public String toString() {
        return "PButton[" + button.getText() + "]: " + super.toString();
    }

}
