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

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

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
    private JComponent _helpPane;
    private PNode _targetNode;
    private PCanvas _targetCanvas;
    
    public PNodeFollower( AbstractHelpItem helpItem, JComponent helpPane, PNode targetNode, PCanvas targetCanvas ) {
        _helpItem = helpItem;
        _helpPane = helpPane;
        _targetNode = targetNode;
        _targetCanvas = targetCanvas;
        setFollowEnabled( true );
    }
    
    public void setFollowEnabled( boolean enabled ) {
        if ( enabled ) {
            _targetNode.addPropertyChangeListener( this );
            _helpItem.updateDisplay();
        }
        else {
            _targetNode.removePropertyChangeListener( this );
        }
    }
    
    public void propertyChange( PropertyChangeEvent event ) {  
        if ( PNode.PROPERTY_VISIBLE.equals( event.getPropertyName() ) ) {
            _helpItem.setVisible( _targetNode.getVisible() );
        }
        _helpItem.updateDisplay();
        Point2D p = _helpItem.mapLocation( _targetNode, _targetCanvas );
        _helpItem.setOffset( p );
    }
}
