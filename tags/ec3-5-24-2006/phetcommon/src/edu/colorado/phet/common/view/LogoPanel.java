/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.view;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * LogoPanel is the panel used to show the PhET logo.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LogoPanel extends JPanel {

    private ImageIcon imageIcon;
    private JLabel titleLabel;

    public LogoPanel() {
        imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.gif" ) );
        titleLabel = new JLabel( imageIcon );
        titleLabel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
        add( titleLabel );
    }
}
