/* Copyright 2006, University of Colorado */

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

import edu.colorado.phet.common.view.util.SimStrings;

/**
 * TransitionMarksControl is the control used to turn transition marks on/off
 * on the Gun's wavelength control.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TransitionMarksControl extends JCheckBox {

    public TransitionMarksControl( Font font ) {
        super();
        setText( SimStrings.get( "label.transitionMarks" ) );
        setFont( font );
        setOpaque( false );
    }
}
