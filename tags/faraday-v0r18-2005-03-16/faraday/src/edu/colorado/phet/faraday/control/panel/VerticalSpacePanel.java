/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.panel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;


/**
 * VerticalSpacePanel is used to put vertical space in the control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VerticalSpacePanel extends JPanel {

    public VerticalSpacePanel( int space ) {
        super();
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( Box.createVerticalStrut( space ) );
    }
}
