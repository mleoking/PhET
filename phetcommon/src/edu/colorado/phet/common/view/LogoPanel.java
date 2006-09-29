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

import javax.swing.*;
import java.awt.*;


/**
 * LogoPanel is the panel used to show the PhET logo.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LogoPanel extends JPanel {

    public static final String IMAGE_PHET_LOGO = PhetLookAndFeel.PHET_LOGO_120x50;

    private ImageIcon imageIcon;
    private JLabel titleLabel;

    public LogoPanel() {
        imageIcon = new ImageIcon( getClass().getClassLoader().getResource( IMAGE_PHET_LOGO ) );
        titleLabel = new JLabel( imageIcon );
        titleLabel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
        add( titleLabel );
    }
}
