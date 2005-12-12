/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.swing;

import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.qm.controls.ConfigureHorizontalSlitPanel;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 24, 2005
 * Time: 5:25:53 PM
 * Copyright (c) Jul 24, 2005 by Sam Reid
 */

public class ConfigureSlitButton extends JButton {
    private HorizontalDoubleSlit horizontalDoubleSlit;
    private ConfigureHorizontalSlitPanel configureHorizontalSlitPanel;
    private JDialog frame;

    public ConfigureSlitButton( JFrame parent, HorizontalDoubleSlit horizontalDoubleSlit ) {
        super( "Configure" );
        configureHorizontalSlitPanel = new ConfigureHorizontalSlitPanel( horizontalDoubleSlit );
        frame = new JDialog( parent, "Double Slits Configuration", false );
        frame.setContentPane( configureHorizontalSlitPanel );
        frame.pack();
        SwingUtils.centerWindowOnScreen( frame );
        this.horizontalDoubleSlit = horizontalDoubleSlit;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                frame.setVisible( true );
            }
        } );

    }
}
