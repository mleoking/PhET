/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import javax.swing.*;
import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 1:33:46 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class TutorialTextArea extends JTextArea {
    public TutorialTextArea() {

        setWrapStyleWord( true );
        setBackground( new Color( 255, 255, 255, 75 ) );
        setOpaque( false );
        setEditable( false );
        setForeground( Color.blue );
        setFont( new PhetDefaultFont( 18, true ) );
        setLineWrap( true );
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
//                g2.drawImage( Page.backgroundImage, new AffineTransform(), this );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        super.paintComponent( g );
    }
}
