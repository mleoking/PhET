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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JFrame;


/**
 * JComponentFollower
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class JComponentFollower implements IFollower, ComponentListener {

    private AbstractHelpItem _helpItem;
    private JComponent _helpPane;
    private JComponent _targetComponent;
    private JFrame _targetFrame;
    
    public JComponentFollower( AbstractHelpItem helpItem, JComponent helpPane, JComponent targetComponent, JFrame targetFrame ) {
        _helpItem = helpItem;
        _helpPane = helpPane;
        _targetComponent = targetComponent;
        _targetFrame = targetFrame;
        setFollowEnabled( true );
    }
    
    public void setFollowEnabled( boolean enabled ) {
        if ( enabled ) {
            _targetComponent.addComponentListener( this );
            _targetFrame.addComponentListener( this );
            _helpItem.updateDisplay();
        }
        else {
            _targetComponent.removeComponentListener( this );
            _targetFrame.removeComponentListener( this );
        }
    }
 
    public void componentResized( ComponentEvent e ) {
        follow();
    }

    public void componentMoved( ComponentEvent e ) {
        if ( e.getSource() == _targetComponent ) {
            follow();
        }
    }

    public void componentShown( ComponentEvent e ) {
        _helpItem.setVisible( _targetComponent.isVisible() );
    }

    public void componentHidden( ComponentEvent e ) {
        _helpItem.setVisible( _targetComponent.isVisible() );
    }
    
    private void follow() {
        _helpItem.updateDisplay();
        Point2D p = _helpItem.mapLocation( _targetComponent );
        _helpItem.setOffset( p.getX(), p.getY() );
    }
}
