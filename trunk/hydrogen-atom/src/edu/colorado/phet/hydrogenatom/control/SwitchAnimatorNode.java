/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.HAClock;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * SwitchAnimatorNode manages animation of the AtomicModelSelector panel.
 * As the ModeSwitch is changed, the AtomicModelSelector panel will slide up and down.
 * The ModeSwitch and AtomicModelSelector are vertically left aligned.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SwitchAnimatorNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double Y_SPACING = 10;
    private static final double Y_DELTA = 4;
    private static final long ANIMATION_DT = 1; // milliseconds
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ModeSwitch _modeSwitch;
    private AtomicModelSelector _atomicModelSelector;
    private PClip _clipNode;
    private Thread _thread;
    private boolean _runThread;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param modeSwitch
     * @param atomicModelSelector
     */
    public SwitchAnimatorNode( ModeSwitch modeSwitch, AtomicModelSelector atomicModelSelector ) {
        super();
        
        _modeSwitch = modeSwitch;
        _modeSwitch.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleModeChange();
            }
        } );
        
        _atomicModelSelector = atomicModelSelector;
        
        // Because the panels have rounded corners, the clipping path starts half-way down the ModeSwitch.
        _clipNode = new PClip();
        double width = Math.max( _modeSwitch.getFullBounds().getWidth(), _atomicModelSelector.getFullBounds().getWidth() );
        double height = ( _modeSwitch.getFullBounds().getHeight() / 2 ) +  Y_SPACING + _atomicModelSelector.getFullBounds().getHeight();
        Rectangle2D clipShape = new Rectangle2D.Double( 0, 0, width, height );
        _clipNode.setPathTo( clipShape );
        _clipNode.setPaint( HAConstants.COLOR_TRANSPARENT );
        _clipNode.setStroke( null );

        _clipNode.addChild( _atomicModelSelector );
        addChild( _clipNode );
        addChild( _modeSwitch );
        
        _modeSwitch.setOffset( 0, 0 );
        _clipNode.setOffset( 0, _modeSwitch.getFullBounds().getHeight() / 2 );
        fixAtomicModelSelectorOffset();
    }
    
    /*
     * Sets the proper "final" offset for the AtomicModelSelector, based on the ModeSwitch state.
     */
    private void fixAtomicModelSelectorOffset() {
        if ( _modeSwitch.isExperimentSelected() ) {
            _atomicModelSelector.setOffset( 0, -( _atomicModelSelector.getFullBounds().getHeight() + Y_SPACING ) );
        }
        else {
            _atomicModelSelector.setOffset( 0, ( _modeSwitch.getFullBounds().getHeight() / 2 ) + Y_SPACING );
        }  
    }
    
    //----------------------------------------------------------------------------
    // Animation
    //----------------------------------------------------------------------------
    
    /*
     * Handles a change to the "mode" switch.
     */
    private void handleModeChange() {
        
        interruptAnimation();
        
        if ( _modeSwitch.isExperimentSelected() ) {
            animateUp();
        }
        else {
           animateDown();
        }
    }
    
    /*
     * Sets the flag that will cause any existing animation to complete,
     * and waits for the animation thread to complete.
     */
    private void interruptAnimation() {
        if ( _thread != null && _thread.isAlive() ) {
            _runThread = false;
            try {
                _thread.join();
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
    
    /*
     * Makes the AtomicModelSelector panel slide up.
     */
    private void animateUp() {
        Runnable runnable = new Runnable() {
            public void run() {
                _runThread = true;
                boolean done = false;
                final double x = 0;
                final double yMin = ( _modeSwitch.getFullBounds().getHeight() / 2 );
                while ( !done && _runThread ) {
                    double y = _atomicModelSelector.getFullBounds().getY() - Y_DELTA;
                    _atomicModelSelector.setOffset( x, y );
                    repaint();
                    done = ( _atomicModelSelector.getFullBounds().getMaxY() <= yMin );
                    try {
                        Thread.sleep( ANIMATION_DT );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                        fixAtomicModelSelectorOffset();
                    }
                }
            }
        };
        
        _thread = new Thread( runnable );
        _thread.start();
    }
    
    /*
     * Makes the AtomicModelSelector panel slide down.
     */
    private void animateDown() {
        Runnable runnable = new Runnable() {
            public void run() {
                _runThread = true;
                boolean done = false;
                final double yMax = ( _modeSwitch.getFullBounds().getHeight() / 2 ) + Y_SPACING;
                while ( !done && _runThread ) {
                    double x = 0;
                    double y = Math.min( _atomicModelSelector.getFullBounds().getY() + Y_DELTA, yMax );
                    _atomicModelSelector.setOffset( x, y );
                    repaint();
                    done = ( _atomicModelSelector.getFullBounds().getY() >= yMax );
                    try {
                        Thread.sleep( ANIMATION_DT );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                        fixAtomicModelSelectorOffset();
                    }
                }
            }
        };
        
        _thread = new Thread( runnable );
        _thread.start();
    }

}
