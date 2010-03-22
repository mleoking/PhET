package edu.colorado.phet.acidbasesolutions.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.umd.cs.piccolo.PNode;


/**
 * Animates the scaling of a node.
 * This is used to make the terms grow/shrink when scaling is turned on/off.
 */
public class ScalingAnimator implements ActionListener {
    
    private static final int ANIMATION_STEPS = 10;
    private static final int TIMER_DELAY = 50; // ms
    
    private final PNode node; // the node whose scale will be animated
    private final double finalScale; // animate to this scale value
    private final double deltaScale; // how much to change the scale each time the timer fires
    private final Timer timer; // Swing timer
    
    public ScalingAnimator( PNode node, double finalScale ) {
        
        this.node = node;
        this.finalScale = finalScale;
        this.deltaScale = ( finalScale - node.getScale() ) / ANIMATION_STEPS;
        
        this.timer = new Timer( 0, this );
        this.timer.setDelay( TIMER_DELAY );
        this.timer.setRepeats( true );
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }

    // called each time the timer fires, changes the scale by deltaScale
    public void actionPerformed( ActionEvent e ) {
        double newScale = node.getScale() + deltaScale;
        if ( ( deltaScale < 0 && newScale < finalScale ) || ( deltaScale > 0 && newScale > finalScale ) ) {
            newScale = finalScale;
        }
        node.setScale( newScale );
        if ( newScale == finalScale ) {
            timer.stop();
        }
    }
}
