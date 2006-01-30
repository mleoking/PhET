/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.help;

import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

import javax.swing.JComponent;


/**
 * JComponentFollower
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class JComponentFollower extends ComponentAdapter implements IFollower {

    private AbstractHelpItem _helpItem;
    private JComponent _target;
    
    public JComponentFollower( AbstractHelpItem helpItem, JComponent target ) {
        _helpItem = helpItem;
        _target = target;
        setFollowEnabled( true );
    }
    
    public void setFollowEnabled( boolean enabled ) {
        if ( enabled ) {
            _target.addComponentListener( this );
            updatePosition();
        }
        else {
            _target.removeComponentListener( this );
        }
    }
 
    public void componentResized( ComponentEvent e ) {
        updatePosition();
    }

    public void componentMoved( ComponentEvent e ) {
        updatePosition();
    }
    
    public void componentHidden( ComponentEvent e ) {
        _helpItem.setVisible( _target.isVisible() );
    }
    
    public void componentShown( ComponentEvent e ) {
        _helpItem.setVisible( _target.isVisible() );
        updatePosition();
    }
    
    public void updatePosition() {
        Point2D p = _helpItem.mapLocation( _target );
        _helpItem.setOffset( p.getX(), p.getY() );
    }
}
