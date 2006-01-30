/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.help;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;


/**
 * AbstractHelpItem is the base class for help items.
 * It is concerned with positioning help items on
 * the parent help pane.  The position of a help item may 
 * be static, or it may track some other object (eg, a 
 * JComponent or PNode).
 * <p>
 * Note that this class contains no information about the 
 * "look" of a help item.  You must handle the display details
 * in your subclass.
 * <p>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractHelpItem extends PNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JComponent _helpPane; // the parent help pane
    private IFollower _follower; // tracks the position of some object
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param helpPane the help pane that contains this help item
     */
    public AbstractHelpItem( JComponent helpPane ) {
        _helpPane = helpPane;
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        if ( _follower != null ) {
            _follower.setFollowEnabled( false );
            _follower = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets a reference to the parent help pane.
     * 
     * @return parent help pane
     */
    public JComponent getHelpPane() {
        return _helpPane;
    }  
    
    /**
     * Updates the position of the help item.
     * This task is delegated to an IFollower object.
     */
    public void updatePosition() {
        if ( _follower != null ) {
            _follower.updatePosition();
        }
    }
    
    //----------------------------------------------------------------------------
    // Specify something to point at
    //----------------------------------------------------------------------------
    
    /**
     * Makes the help item point at point on the screen.
     * The point is in the parent help pane's coordinate system.
     * 
     * @param p the point
     */
    public void pointAt( Point2D p ) {
        setOffset( p.getX(), p.getY() );
    }
    
    /**
     * Makes the help item point at point on the screen.
     * The point is in the parent help pane's coordinate system.
     * 
     * @param x
     * @param y
     */
    public void pointAt( double x, double y ) {
        setOffset( x, y );
    }
    
    /**
     * Makes the help item point at JComponent.
     * Responsibility for following the JComponent is delegated
     * to a JComponentFollower.
     * 
     * @param component
     */
    public void pointAt( JComponent component ) {
        if ( _follower != null ) {
            _follower.setFollowEnabled( false );
        }
        _follower = new JComponentFollower( this, component );
    }
    
    /**
     * Makes the help item point at PNode on a specified PCanvas.
     * Responsibility for following the PNode is delegated
     * to a PNodeFollower.
     * 
     * @param node
     * @param canvas
     */
    public void pointAt( PNode node, PCanvas canvas ) {
        if ( _follower != null ) {
            _follower.setFollowEnabled( false );
        }
        _follower = new PNodeFollower( this, node, canvas );
    }
    
    //----------------------------------------------------------------------------
    // Coordinate mapping
    //----------------------------------------------------------------------------
    
    /**
     * Maps the location (upper left corner) of a PNode on a specified PCanvas to 
     * the coordinate system of the parent help pane.
     * 
     * @param targetNode
     * @param targetCanvas
     * @return
     */
    protected Point2D mapLocation( PNode targetNode, PCanvas targetCanvas ) {
        Rectangle2D globalBounds = targetNode.getGlobalBounds();
        Point globalPoint = new Point( (int) globalBounds.getX(), (int) globalBounds.getY() );
        Point helpPanePoint = SwingUtilities.convertPoint( targetCanvas.getParent(), globalPoint, _helpPane );
        return helpPanePoint;
    }
    
    /**
     * Maps the location (upper left corner) of a JComponent to 
     * the coordinate system of the parent help pane.
     * 
     * @param targetComponent
     * @return
     */
    protected Point2D mapLocation( JComponent targetComponent ) {
        return SwingUtilities.convertPoint( targetComponent.getParent(), targetComponent.getLocation(), _helpPane );
    }
    
    //----------------------------------------------------------------------------
    // IFollower
    //----------------------------------------------------------------------------
    
    /**
     * IFollower synchronizes the visibility and position of a help item
     * with some other object.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private interface IFollower {

        /**
         * Turns following on and off.
         * @param enabled
         */
        public void setFollowEnabled( boolean enabled );
         
        /** Synchronizes visibility. */
        public void updateVisibility();
        
        /** Synchronizes position. */
        public void updatePosition();
    }
    
    //----------------------------------------------------------------------------
    // JComponentFollower
    //----------------------------------------------------------------------------
    
    /**
     * JComponentFollower synchronizes position and visibility with a JComponent.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class JComponentFollower implements IFollower, ComponentListener {

        private AbstractHelpItem _helpItem;
        private JComponent _target;
        
        /**
         * Constructor.
         * 
         * @param helpItem the help item
         * @param target the JComponent to follow
         */
        public JComponentFollower( AbstractHelpItem helpItem, JComponent target ) {
            _helpItem = helpItem;
            _target = target;
            setFollowEnabled( true );
        }
        
        /* Turns following on and off. */
        public void setFollowEnabled( boolean enabled ) {
            if ( enabled ) {
                _target.addComponentListener( this );
                updateVisibility();
                updatePosition();
            }
            else {
                _target.removeComponentListener( this );
            }
        }
        
        /* Synchronizes visibility. */
        public void updateVisibility() {
            _helpItem.setVisible( _target.isVisible() );
        }
        
        /* Synchronizes position. */
        public void updatePosition() {
            if ( _helpItem.getVisible() ) {
                Point2D p = _helpItem.mapLocation( _target );
                _helpItem.setOffset( p.getX(), p.getY() );
            }
        }
        
        /* Synchronizes position when the JComponent is resized. */
        public void componentResized( ComponentEvent e ) {
            updatePosition();
        }

        /* Synchronizes position when the JComponent is moved. */
        public void componentMoved( ComponentEvent e ) {
            updatePosition();
        }
        
        /* Synchronizes visibility when the JComponent is hidden. */
        public void componentHidden( ComponentEvent e ) {
            updateVisibility();
        }
        
        /* Synchronizes visibility when the JComponent is shown. */
        public void componentShown( ComponentEvent e ) {
            updateVisibility();
            updatePosition();
        }
    }
    
    //----------------------------------------------------------------------------
    // PNodeFollower
    //----------------------------------------------------------------------------
    
    /**
     * PNodeFollower synchronizes position and visibility with a PNode.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class PNodeFollower implements IFollower, PropertyChangeListener {

        private AbstractHelpItem _helpItem;
        private PNode _target;
        private PCanvas _targetContainer;
        
        /**
         * Constructor.
         * 
         * @param helpItem the help item
         * @param target the PNode to follow
         * @param targetContainer the PCanvas that contains the PNode
         */
        public PNodeFollower( AbstractHelpItem helpItem, PNode target, PCanvas targetContainer ) {
            _helpItem = helpItem;
            _target = target;
            _targetContainer = targetContainer;
            setFollowEnabled( true );
        }
        
        /* Turns following on and off. */
        public void setFollowEnabled( boolean enabled ) {
            if ( enabled ) {
                _target.addPropertyChangeListener( this );
                updateVisibility();
                updatePosition();
            }
            else {
                _target.removePropertyChangeListener( this );
            }
        }  
        
        /* Synchronizes visibility. */
        public void updateVisibility() {
            _helpItem.setVisible( _target.getVisible() );
        }
        
        /* Synchronizes position. */
        public void updatePosition() {
            if ( _helpItem.getVisible() ) {
                Point2D p = _helpItem.mapLocation( _target, _targetContainer );
                _helpItem.setOffset( p );
            }
        }
        
        /* Synchronizes position and visibility when a relevant property of the PNode changes. */
        public void propertyChange( PropertyChangeEvent event ) {  
            if ( PNode.PROPERTY_VISIBLE.equals( event.getPropertyName() ) ) {
                updateVisibility();
            }
            updatePosition();
        }
    }
}
