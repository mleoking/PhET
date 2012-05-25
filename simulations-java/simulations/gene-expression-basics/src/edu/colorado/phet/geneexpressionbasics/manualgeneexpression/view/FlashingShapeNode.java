// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * PNode that has a shape and that can be set up to flash in a number of
 * different ways.
 *
 * @author John Blanco
 */
class FlashingShapeNode extends PNode {

    private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );

    private final FlashController flashController;

    public FlashingShapeNode( Shape shape, Color flashColor, int onTime, int offTime, int numFlashes, boolean visibleAtStart, boolean visibleAtEnd ) {
        PPath flashingNode = new PhetPPath( shape, visibleAtStart ? flashColor : INVISIBLE_COLOR );
        addChild( flashingNode );
        flashController = new FlashController( flashingNode, INVISIBLE_COLOR, flashColor, onTime, offTime, numFlashes, visibleAtStart, visibleAtEnd );
    }

    public void startFlashing() {
        flashController.restart();
    }

    public void stopFlashing() {
        flashController.stop();
    }

    public void forceFlashOff() {
        flashController.forceFlashOff();
    }

    /**
     * Class that controls timed flashing.
     */
    private static class FlashController {

        // Variables used to implement the flashing behavior.
        private int transitionCountdown = 0;
        private Timer flashTimer;
        private final PPath flashingNode;
        private final Color normalColor;
        private final Color flashColor;
        private final boolean flashOnAtStart;
        private final boolean flashOnAtEnd;
        private final int numFlashes;

        /**
         * Constructor.
         *
         * @param flashingNode
         * @param normalColor
         * @param flashColor
         * @param onTime       - in milliseconds
         * @param offTime      - in milliseconds
         */
        private FlashController( final PPath flashingNode, final Color normalColor, final Color flashColor,
                                 final int onTime, final int offTime, final int numFlashes, boolean flashOnAtStart, boolean flashOnAtEnd ) {

            this.flashingNode = flashingNode;
            this.flashColor = flashColor;
            this.normalColor = normalColor;
            this.flashOnAtStart = flashOnAtStart;
            this.flashOnAtEnd = flashOnAtEnd;
            this.numFlashes = numFlashes;

            // Set up the timer.
            flashTimer = new Timer( Integer.MAX_VALUE, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    int time;
                    if ( flashingNode.getPaint() == flashColor ) {
                        // Flash is on, so turn flash off.
                        flashingNode.setPaint( normalColor );
                        time = offTime;
                    }
                    else {
                        // Flash is off, so turn flash on.
                        flashingNode.setPaint( flashColor );
                        time = onTime;
                    }
                    transitionCountdown--;
                    if ( transitionCountdown > 0 ) {
                        // Set timer for next transition.
                        flashTimer.setInitialDelay( time );
                        flashTimer.restart();
                    }
                    else {
                        // Done flashing.
                        flashTimer.stop();
                    }
                }
            } );
            flashTimer.setRepeats( false );
        }

        public void restart() {
            flashTimer.stop();
            setFlashOn( flashOnAtStart );
            transitionCountdown = numFlashes * 2;
            if ( flashOnAtStart != flashOnAtEnd ) {
                transitionCountdown -= 1;
            }
            flashTimer.setInitialDelay( 0 );
            flashTimer.restart();
        }

        public boolean isFlashing() {
            return flashTimer.isRunning();
        }

        public void stop() {
            flashTimer.stop();
        }

        public void forceFlashOff() {
            if ( isFlashing() ) {
                stop();
            }
            setFlashOn( false );
        }

        private void setFlashOn( boolean flashOn ) {
            flashingNode.setPaint( flashOn ? flashColor : normalColor );
        }
    }
}
