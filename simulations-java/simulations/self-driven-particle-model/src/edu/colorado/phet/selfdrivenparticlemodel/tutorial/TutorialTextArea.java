// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class TutorialTextArea extends JTextArea {
    public TutorialTextArea() {
        setWrapStyleWord( true );
        setBackground( new Color( 255, 255, 255, 75 ) );
        setOpaque( false );
        setEditable( false );
        setForeground( Color.blue );
        setFont( new PhetFont( 18, true ) );
        setLineWrap( true );
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        super.paintComponent( g );
    }
}
