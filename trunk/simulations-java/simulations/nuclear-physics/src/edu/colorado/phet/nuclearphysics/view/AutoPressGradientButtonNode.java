package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class extends the gradient button in order to add the capability of
 * doing an "auto-press", which means that it is told to look like it just
 * got pressed for a bit and then released.
 * 
 * @author John Blanco
 */
public class AutoPressGradientButtonNode extends GradientButtonNode{

	private static final int HIGHLIGHT_TIME = 250; // In milliseconds.
	private static final int PRESS_TIME = 500; // In milliseconds.
	
	// Possible states for the button.
	private static final int BUTTON_IDLE = 1;
	private static final int BUTTON_PRE_PRESS_HIGHLIGHTED = 2;
	private static final int BUTTON_PRESSED = 3;
	private static final int BUTTON_POST_PRESS_HIGHLIGHTED = 3;
	
	// Timers
    private static final Timer PRE_PRESS_HIGHLIGHT_TIMER = new Timer( HIGHLIGHT_TIME, null );
    private static final Timer PRESS_TIMER = new Timer( PRESS_TIME, null );
    private static final Timer POST_PRESS_HIGHLIGHT_TIMER = new Timer( HIGHLIGHT_TIME, null );
    
    // State variable for button state.
	private int buttonState;
	
	/**
	 * Constructor.
	 */
	public AutoPressGradientButtonNode(String label, int fontSize,
			Color buttonColor) {
		super(label, fontSize, buttonColor);
		buttonState = BUTTON_IDLE;

		PRE_PRESS_HIGHLIGHT_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            	// Make the button look like it is pressed.
    			PPath button = getButton();
        		button.setPaint( getArmedGradient() );
    			button.setOffset(SHADOW_OFFSET, SHADOW_OFFSET);
        		buttonState = BUTTON_PRESSED;
        		PRE_PRESS_HIGHLIGHT_TIMER.stop();
        		PRESS_TIMER.start();
            }
        } );

		PRESS_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            	// Make the button look like it is highlighted but not pressed.
    			PPath button = getButton();
        		button.setPaint( getMouseOverGradient() );
    			button.setOffset(0, 0);
        		buttonState = BUTTON_POST_PRESS_HIGHLIGHTED;
        		PRESS_TIMER.stop();
        		POST_PRESS_HIGHLIGHT_TIMER.start();
            }
        } );

		POST_PRESS_HIGHLIGHT_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            	// Make the button look like it is neither pressed nor highlighted.
    			PPath button = getButton();
        		button.setPaint( getMouseNotOverGradient() );
    			button.setOffset(0, 0);
        		buttonState = BUTTON_IDLE;
        		POST_PRESS_HIGHLIGHT_TIMER.stop();
            }
        } );
	}

	public void autoPress(){
		if (buttonState == BUTTON_IDLE){
			// Make the button look like it is highlighted.
			PPath button = getButton();
			button.setPaint( getMouseOverGradient() );
			
			// Start the timer for the next step.
	        PRE_PRESS_HIGHLIGHT_TIMER.start();
	        buttonState = BUTTON_PRE_PRESS_HIGHLIGHTED;
		}
	}
}
