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

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;


/**
 * AbstractHelpItem
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractHelpItem extends PNode {

    JComponent _helpPane;
    IFollower _follower;
    
    public AbstractHelpItem( JComponent helpPane ) {
        _helpPane = helpPane;
    }
    
    public void cleanup() {
        if ( _follower != null ) {
            _follower.setFollowEnabled( false );
            _follower = null;
        }
    }
    
    public void pointAt( Point2D p ) {
        setOffset( p.getX(), p.getY() );
    }
    
    public void pointAt( double x, double y ) {
        setOffset( x, y );
    }
    
    public void pointAt( JComponent targetComponent, JFrame targetFrame ) {
        if ( _follower != null ) {
            _follower.setFollowEnabled( false );
        }
        _follower = new JComponentFollower( this, _helpPane, targetComponent, targetFrame );
    }
    
    public void pointAt( PNode targetNode, PCanvas targetCanvas ) {
        if ( _follower != null ) {
            _follower.setFollowEnabled( false );
        }
        _follower = new PNodeFollower( this, _helpPane, targetNode, targetCanvas );
    }
    
    public abstract void updateDisplay();
    
    public Point2D mapLocation( PNode targetNode, PCanvas targetCanvas ) {
        Rectangle2D globalBounds = targetNode.getGlobalBounds();
        Point globalPoint = new Point( (int) globalBounds.getX(), (int) globalBounds.getY() );
        Point helpPanePoint = SwingUtilities.convertPoint( targetCanvas.getParent(), globalPoint, _helpPane );
        return helpPanePoint;
    }
    
    public Point2D mapLocation( JComponent targetComponent ) {
        return SwingUtilities.convertPoint( targetComponent.getParent(), targetComponent.getLocation(), _helpPane );
    }
}
