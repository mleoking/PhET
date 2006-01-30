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
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;


/**
 * HelpItem
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PNodeFollower implements IFollower, PropertyChangeListener {

    private AbstractHelpItem _helpItem;
    private PNode _target;
    private PCanvas _targetContainer;
    
    public PNodeFollower( AbstractHelpItem helpItem, PNode target, PCanvas targetContainer ) {
        _helpItem = helpItem;
        _target = target;
        _targetContainer = targetContainer;
        setFollowEnabled( true );
    }
    
    public void setFollowEnabled( boolean enabled ) {
        if ( enabled ) {
            _target.addPropertyChangeListener( this );
            updatePosition();
        }
        else {
            _target.removePropertyChangeListener( this );
        }
    }
    
    public void propertyChange( PropertyChangeEvent event ) {  
        if ( PNode.PROPERTY_VISIBLE.equals( event.getPropertyName() ) ) {
            _helpItem.setVisible( _target.getVisible() );
        }
        updatePosition();
    }
    
    public void updatePosition() {
        Point2D p = _helpItem.mapLocation( _target, _targetContainer );
        _helpItem.setOffset( p );
    }
}
