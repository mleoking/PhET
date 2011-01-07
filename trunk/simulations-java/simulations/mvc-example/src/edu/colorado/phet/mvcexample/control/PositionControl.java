// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.control;

import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.mvcexample.MVCExampleApplication;

/**
 * PositionControl is a control that displays (but does not edit) position.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositionControl extends JPanel {
    
    private static final String LABEL = MVCExampleApplication.RESOURCE_LOADER.getLocalizedString( "PositionControl.label" );

    private JLabel _label;
    
    public PositionControl() {
        super();
        _label = new JLabel();
        add( _label );
    }
    
    public void setValue( Point2D p ) {
        String s = LABEL + " (" + (int)p.getX() + "," + (int)p.getY() + ")";
        _label.setText( s );
    }
}
