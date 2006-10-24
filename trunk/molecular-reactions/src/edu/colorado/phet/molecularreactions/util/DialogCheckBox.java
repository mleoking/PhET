/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.util;

import javax.swing.*;
import java.awt.event.*;

/**
 * DialogCheckBox
 * A check box that opens/closes a dialog, and unchecks if the dialog is
 * closed independently
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DialogCheckBox extends JCheckBox implements ComponentListener {
    public DialogCheckBox( String text ) {
        super( text );
    }

    public void componentHidden( ComponentEvent e ) {
        setSelected( false );
    }

    public void componentMoved( ComponentEvent e ) {
        // noop
    }

    public void componentResized( ComponentEvent e ) {
        // noop
    }

    public void componentShown( ComponentEvent e ) {
        // noop
    }
}
