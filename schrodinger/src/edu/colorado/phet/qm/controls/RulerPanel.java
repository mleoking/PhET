/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:15:10 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class RulerPanel extends HorizontalLayoutPanel {
    private SchrodingerPanel schrodingerPanel;

    public RulerPanel( SchrodingerPanel schrodingerPanel ) throws IOException {
        this.schrodingerPanel = schrodingerPanel;

        final HorizontalLayoutPanel rulerPanel = this;

        final JCheckBox ruler = new JCheckBox( "Ruler" );
        ImageIcon icon = new ImageIcon( ImageLoader.loadBufferedImage( "images/ruler-thumb.jpg" ) );
        rulerPanel.add( ruler );
        rulerPanel.add( new JLabel( icon ) );
        ruler.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setRulerVisible( ruler.isSelected() );
            }
        } );
//        addControl( rulerPanel );
        new Timer( 500, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ruler.setSelected( getSchrodingerPanel().isRulerVisible() );
                rulerPanel.setEnabled( !getSchrodingerPanel().isPhotonMode() );
                ruler.setEnabled( !getSchrodingerPanel().isPhotonMode() );
            }
        } ).start();
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }
}
