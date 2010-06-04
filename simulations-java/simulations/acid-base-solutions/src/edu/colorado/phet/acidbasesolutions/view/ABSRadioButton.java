/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;


/**
 * Convenience class, for creating a radio button that is 
 * part of a button group and has an optional ActionListener.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSRadioButton extends JRadioButton {
    
    public ABSRadioButton( String label, ButtonGroup group ) {
        this( label, group, null );
    }
    
    public ABSRadioButton( String label, ButtonGroup group, ActionListener listener ) {
        super( label );
        group.add( this );
        if ( listener != null ) {
            addActionListener( listener );
        }
    }
}
