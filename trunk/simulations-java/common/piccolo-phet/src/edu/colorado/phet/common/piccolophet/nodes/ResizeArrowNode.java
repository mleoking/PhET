// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This node represents a double-headed arrow that can be grabbed by the user
 * and moved in order to resize something in the view.
 *
 * @author John Blanco
 */
public class ResizeArrowNode extends PhetPNode {

    // Attributes of the arrow.
    private final DoubleArrowNode m_adjusterArrow;
    private final Color m_normalFillColor;
    private final Color m_highlightedFillColor;

    // Control the flashing, which can be done to make arrow more eye catching.
    private final FlashController flashController;

    /**
     * This constructor allows the user to specify the size and orientation
     * of the resizing arrow.  The arrow that is created will be centered
     * around 0,0 so that the user can position it without having to worry
     * about offsetting it.
     *
     * @param width - In terms of canvas coordinates.
     * @param angle - In radians.  A value of 0 means a horizontal arrow.
     */
    public ResizeArrowNode( double width, double angle, Color normalFillColor, Color highlightedFillColor ) {

        m_normalFillColor = normalFillColor;
        m_highlightedFillColor = highlightedFillColor;

        // Create and add the child node that will represent the double-
        // headed arrow.
        m_adjusterArrow = new DoubleArrowNode( new Point2D.Double( -width / 2, 0 ),
                                               new Point2D.Double( width / 2, 0 ),
                                               width * 0.3,
                                               width * 0.7,
                                               width * 0.25 );
        m_adjusterArrow.rotate( angle );
        m_adjusterArrow.setPaint( m_normalFillColor );
        m_adjusterArrow.setPickable( true );
        addChild( m_adjusterArrow );

        // Add the handler that will change the cursor when the user moves
        // the mouse over this node.
        m_adjusterArrow.addInputEventListener( new CursorHandler() );

        // Add the handler that will highlight the node when the user moves
        // the mouse over.
        m_adjusterArrow.addInputEventListener( new PBasicInputEventHandler() {
            private boolean m_mouseIsPressed;
            private boolean m_mouseIsInside;

            public void mousePressed( PInputEvent event ) {
                m_mouseIsPressed = true;
                m_adjusterArrow.setPaint( m_highlightedFillColor );
            }

            public void mouseReleased( PInputEvent event ) {
                m_mouseIsPressed = false;
                if ( !m_mouseIsInside ) {
                    m_adjusterArrow.setPaint( m_normalFillColor );
                }
            }

            public void mouseEntered( PInputEvent event ) {
                m_mouseIsInside = true;
                m_adjusterArrow.setPaint( m_highlightedFillColor );
            }

            public void mouseExited( PInputEvent event ) {
                m_mouseIsInside = false;
                if ( !m_mouseIsPressed ) {
                    m_adjusterArrow.setPaint( m_normalFillColor );
                }
            }
        } );

        // Add the timer that will control flashing (if used).
        flashController = new FlashController( m_adjusterArrow, m_normalFillColor, m_highlightedFillColor );
    }

    public void setStroke( Stroke aStroke ) {
        m_adjusterArrow.setStroke( aStroke );
    }

    /**
     * Flash between a couple of different colors in order to make this
     * arrow more noticeable.
     */
    public void flash() {
        flashController.restart();
    }

    /**
     * Class that controls timed flashing.
     */
    private static class FlashController {
        // Constants that control the frequency and duty cycle of the flashing.
        private static final int PRE_FLASH_TIME = 300; // In milliseconds.
        private static final int FLASH_ON_TIME = 500; // In milliseconds.
        private static final int FLASH_OFF_TIME = 500; // In milliseconds.
        private static final int NUM_FLASHES = 3;

        // Variables used to implement the flashing behavior.
        private boolean flashOn = false;
        private int flashCount = 0;
        private Timer flashTimer;

        /**
         * Constructor.
         *
         * @param flashingNode
         * @param normalColor
         * @param flashColor
         */
        private FlashController( final PPath flashingNode, final Color normalColor, final Color flashColor ) {
            flashTimer = new Timer( PRE_FLASH_TIME, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( flashOn ) {
                        // Turn flash off.
                        flashOn = false;
                        flashingNode.setPaint( normalColor );
                        flashCount++;
                        if ( flashCount < NUM_FLASHES ) {
                            flashTimer.setDelay( FLASH_OFF_TIME );
                            flashTimer.restart();
                        }
                        else {
                            flashTimer.stop();
                        }
                    }
                    else {
                        // Turn flash on.
                        flashOn = true;
                        flashingNode.setPaint( flashColor );
                        flashTimer.setDelay( FLASH_ON_TIME );
                        flashTimer.restart();
                    }
                }
            } );
        }

        public void restart() {
            flashTimer.stop();
            flashCount = 0;
            flashOn = false;
            flashTimer.setDelay( PRE_FLASH_TIME );
            flashTimer.restart();
        }
    }
}
