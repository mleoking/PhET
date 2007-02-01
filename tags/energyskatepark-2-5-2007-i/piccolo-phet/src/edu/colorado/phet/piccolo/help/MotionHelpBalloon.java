/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.help;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PAffineTransform;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MotionHelpBalloon is a help ballon that animates from a start position
 * to an end position.  The start position is set using setOffset.
 * The end position can be either a point or a node; see the animateTo
 * methods.  When animateTo is called, the animation begins immediately.
 * If you prefer to define your own custom motion, don't call an
 * animateTo method. Instead, create your own Piccolo activity and
 * attach it to the node.
 * <p>
 * See the Javadoc for HelpBallon for methods related to the 
 * visual attributes (text, balloon, arrow, etc.)
 *
 * @author Sam Reid / Chris Malley
 * @version $Revision$
 */
public class MotionHelpBalloon extends HelpBalloon {
    
    public static final int DEFAULT_DURATION = 3000;
    
    private PCanvas canvas;
    private boolean started;

    /**
     * Constructor.
     * By default, there is no arrow, no ballon behind the text,
     * and the location is relative to the upper-left corner.
     * 
     * @param canvas
     * @param s
     */
    public MotionHelpBalloon( PCanvas canvas, String s ) {
        super( canvas, s, HelpBalloon.TOP_LEFT, 100, 0 );
        setBalloonVisible( false );
        setArrowVisible( false );
        setEnabled( true );
        this.canvas = canvas;
        this.started = false;
    }
    
    /**
     * Animates to a point using the default duration.
     * Animation begins immediately.
     * The animation path is a straight line.
     * 
     * @param x
     * @param y
     * @throws IllegalStateException if this node is not yet in the Piccolo tree
     */
    public void animateTo( final double x, final double y ) {
        animateTo( x, y, DEFAULT_DURATION );
    }
    
    /**
     * Animates to a point using a specified duration.
     * Animation begins immediately.
     * The animation path is a straight line.
     * 
     * @param x
     * @param y
     * @param duration duration in milliseconds
     * @throws IllegalStateException if this node is not yet in the Piccolo tree
     */
    public void animateTo( final double x, final double y, final long duration ) {
        if ( !started ) {
            if ( getRoot() == null ) {
                throw new IllegalStateException( "node has no root" );
            }
            PActivity activity = animateToPositionScaleRotation( x, y, 1 /*scale*/, 0 /*theta*/, duration );
            getRoot().addActivity( activity );
        }
    }
    
    /**
     * Animates to a node using the default duration.
     * Animation begins immediately.
     * The animation path is a straight line, but will be adjusted
     * if the destination node is moving.
     * 
     * @param node
     * @throws IllegalStateException if this node is not yet in the Piccolo tree
     */
    public void animateTo( final PNode node ) {
        animateTo( node, DEFAULT_DURATION );
    }
    
    /**
     * Animates to a node using a specified duration.
     * Animation begins immediately.
     * The animation path is a straight line, but will be adjusted 
     * if the destination node is moving.
     * 
     * @param node
     * @param duration duration in milliseconds
     * @throws IllegalStateException if this node is not yet in the Piccolo tree
     */
    public void animateTo( final PNode node, long duration ) {
        if ( !started ) {
            if ( getRoot() == null ) {
                throw new IllegalStateException( "node has no root" );
            }
            Point2D loc = mapLocation( node, canvas );
            final PTransformActivity activity = animateToPositionScaleRotation( loc.getX(), loc.getY(), 1 /*scale*/, 0 /*theta*/, duration );
            node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    PAffineTransform t = getTransform();
                    Point2D loc = mapLocation( node, canvas );
                    t.setOffset( loc.getX(), loc.getY() );
                    t.setScale( 1.0 );
                    t.setRotation( 0.0 );
                    double[] m = new double[6];
                    t.getMatrix( m );
                    activity.setDestinationTransform( m );
                }
            } );
            getRoot().addActivity( activity );
        }
    }
    
    /**
     * Override setVisible to also control pickability.
     * If this node is visible, then it (and its children) are pickable.
     * The rationale for doing this is:
     * Normally if something is higher in the Z-Ordering and obscuring something
     * below then mouse events should not pass through.  This is certainly true
     * for opaque things, but also a true for semi-transparent nodes.  
     * Or you might want to add a MouseListener to this node
     * that makes it disappear when the user clicks on it. 
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        setPickable( visible );
        setChildrenPickable( visible );
    }
}