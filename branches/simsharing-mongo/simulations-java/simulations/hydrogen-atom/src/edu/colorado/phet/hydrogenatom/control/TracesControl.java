// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Font;

import javax.swing.JCheckBox;

import edu.colorado.phet.hydrogenatom.HAResources;

/**
 * TracesControl is the control used to turn traces on/off in the Gun control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TracesControl extends JCheckBox {

    public TracesControl( Font font ) {
        super();
        setText( HAResources.getString( "label.showTraces" ) );
        setFont( font );
        setOpaque( false );
    }
}
