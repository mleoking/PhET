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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;


/**
 * AbstractHelpItem is the base class for help items.
 * It is concerned with positioning help items on the parent help pane.
 * The position of a help item may be static, or it may track some other
 * object (eg, a JComponent or PNode).  Responsibility for following an
 * object is delegated to an IFollower.
 * <p>
 * Note that this class contains no information about the "look" of a help item.
 * You must handle the display details in your subclass.
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
    private boolean _enabled; // whether this help item is enabled, see setEnabled
    
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
        setEnabled( false ); // disabled by default
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
    
    /**
     * Enables and disables the help item.
     * When disabled, a help item is invisible and its "follower" does nothing.
     * When enabled, a help item's visibility and position are determined by its follower.
     * If the help item is not following anything, then its enabled state determines
     * its visibility.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
        
        if ( _follower != null ) {
            _follower.setFollowEnabled( enabled );
        }
        else {
            // if no follower, then simply set visibility
            setVisible( enabled );
        }
        
        if ( !enabled ) {
            setVisible( false );
        }
    }
    
    //----------------------------------------------------------------------------
    // Specify something to point at
    //----------------------------------------------------------------------------
    
    /**
     * Puts a help item at a specified location, in help pane coordinates.
     * Use this for help items that have no arrow and don't point at an object.
     * 
     * @param p location
     */
    public void setLocation( Point2D p ) {
        setLocation( p.getX(), p.getY() );
    }
    
    /**
     * Puts a help item at a specified location, in help pane coordinates.
     * Use this for help items that have no arrow and don't point at an object.
     * 
     * @param x
     * @param y
     */
    public void setLocation( double x, double y ) {
        pointAt( x, y );
    }
    
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
        _follower.setFollowEnabled( _enabled );
    }
    
    /**
     * Makes the help item point at PNode on a specified PCanvas.
     * Responsibility for following the PNode is delegated
     * to a PNodeFollower.
     * 
     * @param node
     * @param canvas
     * @throws IllegalArgumentException if node is not a descendent of canvas
     */
    public void pointAt( PNode node, PCanvas canvas ) {
        if ( ! node.isDescendentOf( canvas.getLayer()  ) ) {
            throw new IllegalArgumentException( "node is not a descendent of canvas" );
        }
        if ( _follower != null ) {
            _follower.setFollowEnabled( false );
        }
        _follower = new PNodeFollower( this, node, canvas );
        _follower.setFollowEnabled( _enabled );
    }
    
    //----------------------------------------------------------------------------
    // Coordinate mapping
    //----------------------------------------------------------------------------
    
    /**
     * Maps the location (upper-left corner) of a PNode on a specified PCanvas to 
     * the coordinate system of the parent help pane.
     * 
     * @param node
     * @param canvas
     * @return
     */
    protected Point2D mapLocation( PNode node, PCanvas canvas ) {
        // Get the node's global location - above the root node's transform, but below the canvas's view transform.
        Rectangle2D globalFullBounds = node.getParent().localToGlobal( node.getFullBounds() );
        // Apply the canvas' view transform to get a point in the canvas' coordinate system.
        PCamera camera = canvas.getCamera();
        PAffineTransform transform = camera.getViewTransformReference();
        Rectangle2D bounds = transform.transform( globalFullBounds, null );
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();
        // Convert the canvas location to a point in the help pane.
        Point2D helpPanePoint = SwingUtilities.convertPoint( canvas, x, y, _helpPane );
        return helpPanePoint;
    }
    
    /**
     * Maps the location (upper-left corner) of a JComponent to 
     * the coordinate system of the parent help pane.
     * 
     * @param component
     * @return
     */
    protected Point2D mapLocation( JComponent component ) {
        return SwingUtilities.convertPoint( component.getParent(), component.getLocation(), _helpPane );
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
        private JComponent _component;
        
        /**
         * Constructor.
         * 
         * @param helpItem the help item
         * @param component the JComponent to follow
         */
        public JComponentFollower( AbstractHelpItem helpItem, JComponent component ) {
            _helpItem = helpItem;
            _component = component;
            setFollowEnabled( false ); // disabled by default
        }
        
        /* Turns following on and off. */
        public void setFollowEnabled( boolean enabled ) {
            if ( enabled ) {
                _component.addComponentListener( this );
                updateVisibility();
                updatePosition();
            }
            else {
                _component.removeComponentListener( this );
            }
        }
        
        /* Synchronizes visibility. */
        public void updateVisibility() {
            _helpItem.setVisible( _component.isVisible() );
        }
        
        /* Synchronizes position. */
        public void updatePosition() {
            if ( _helpItem.getVisible() ) {
                Point2D p = _helpItem.mapLocation( _component );
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
        private PNode _node;
        private PCanvas _canvas;
        private ArrayList _watchList; // array of PNodes to watch, includes the target node and all ancestors
        
        /**
         * Constructor.
         * 
         * @param helpItem the help item
         * @param node the PNode to follow
         * @param canvas the PCanvas that contains the PNode
         */
        public PNodeFollower( AbstractHelpItem helpItem, PNode node, PCanvas canvas ) {
            _helpItem = helpItem;
            _node = node;
            _canvas = canvas;
            _watchList = new ArrayList();
            setFollowEnabled( false ); // disabled by default
        }
        
        /* Turns following on and off. */
        public void setFollowEnabled( boolean enabled ) {
            if ( enabled ) {
                updateWatchList();
                updateVisibility();
                updatePosition();
            }
            else {
                clearWatchList();
            }
        }  
        
        /* Synchronizes visibility. */
        public void updateVisibility() {
            boolean visible = isNodeVisible( _node );
            _helpItem.setVisible( visible );
//            System.out.println( "AbstractHelpItem.updateVisibility " + _target.getAttribute( "name" ) );//XXX
        }
        
        /* Synchronizes position. */
        public void updatePosition() {
            if ( _helpItem.getVisible() ) {
                Point2D p = _helpItem.mapLocation( _node, _canvas );
                _helpItem.setOffset( p );
//                System.out.println( "AbstractHelpItem.updatePosition " + _target.getAttribute( "name" ) );//XXX
            }
        }
        
        /* Synchronizes position and visibility when a relevant property of the PNode changes. */
        public void propertyChange( PropertyChangeEvent event ) {
            String propertyName =  event.getPropertyName();
            if ( PNode.PROPERTY_PARENT.equals( propertyName ) ) {
                // the node hierachy has changed
                updateWatchList();
                updateVisibility();
                updatePosition();
            }
            else if ( PNode.PROPERTY_VISIBLE.equals( propertyName ) ) {
                // the visibility of some node in the hierachy has changed
                updateVisibility();
                updatePosition();
            }
            else {
                // the position of some node in the hierachy has changed
                updatePosition();
            }
        }
        
        /*
         * Creates a list of nodes that we are watching.
         * For each of these nodes, we register as a PropertyChangeListener.
         */
        private void updateWatchList() {
            clearWatchList();
            PNode node = _node;
            while ( node != null ) {
                node.addPropertyChangeListener( this );
                _watchList.add( node );
                node = node.getParent();
            }
        }
        
        /*
         * Clears the watch list, deregistering as a PropertyChangeListener.
         */
        private void clearWatchList() {
            Iterator i = _watchList.iterator();
            while ( i.hasNext() ) {
                ((PNode)i.next()).removePropertyChangeListener( this );
            }
            _watchList.clear();
        }
        
        /*
         * Determines if a node is visible.
         * A node is visible if it and and all of its ancestors is visible.
         * 
         * @param node
         * @return true or value
         */
        private static boolean isNodeVisible( final PNode node ) {
            boolean visible = node.getVisible();
            PNode aNode = node;
            while ( visible && aNode != null ) {
                aNode = aNode.getParent();
                if ( aNode != null ) {
                    visible = aNode.getVisible();
                }
            }
            return visible;
        }
    }
}
