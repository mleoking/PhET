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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;


/**
 * JComponentFollower
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class JComponentFollower implements IFollower, ComponentListener {

    private AbstractHelpItem _helpItem;
    private Component _helpPane;
    private JComponent _target;
    private Container _targetContainer;
    
    public JComponentFollower( AbstractHelpItem helpItem, Component helpPane, JComponent target, Container targetContainer ) {
        _helpItem = helpItem;
        _helpPane = helpPane;
        _target = target;
        _targetContainer = targetContainer;
        setFollowEnabled( true );
    }
    
    public void setFollowEnabled( boolean enabled ) {
        if ( enabled ) {
            _target.addComponentListener( this );
            _targetContainer.addComponentListener( this );
            _helpItem.updateDisplay();
        }
        else {
            _target.removeComponentListener( this );
            _targetContainer.removeComponentListener( this );
        }
    }
 
    public void componentResized( ComponentEvent e ) {
        follow();
    }

    public void componentMoved( ComponentEvent e ) {
        if ( e.getSource() == _target ) {
            follow();
        }
    }

    public void componentShown( ComponentEvent e ) {
        _helpItem.setVisible( _target.isVisible() );
    }

    public void componentHidden( ComponentEvent e ) {
        _helpItem.setVisible( _target.isVisible() );
    }
    
    private void follow() {
        _helpItem.updateDisplay();
        Point2D p = _helpItem.mapLocation( _target );
        _helpItem.setOffset( p.getX(), p.getY() );
    }
}
