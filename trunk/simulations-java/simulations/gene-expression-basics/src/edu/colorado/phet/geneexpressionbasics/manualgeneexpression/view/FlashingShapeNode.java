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
 * PNode that spends most of its time being invisible, but can be made to
 * flash upon command.
 *
 * @author John Blanco
 */
public class FlashingShapeNode extends PNode {

    private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );

    private final FlashController flashController;

    public FlashingShapeNode( Shape shape, Color flashColor, int onTime, int offTime, int numFlashes ) {
        PPath flashingNode = new PhetPPath( shape, INVISIBLE_COLOR );
        addChild( flashingNode );
        flashController = new FlashController( flashingNode, INVISIBLE_COLOR, flashColor, onTime, offTime, numFlashes );
    }

    public void flash() {
        flashController.restart();
    }

    /**
     * Class that controls timed flashing.
     */
    private static class FlashController {

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
         * @param onTime       - in milliseconds
         * @param offTime      - in milliseconds
         */
        private FlashController( final PPath flashingNode, final Color normalColor, final Color flashColor,
                                 final int onTime, final int offTime, final int numFlashes ) {
            // Set up the timer.
            flashTimer = new Timer( Integer.MAX_VALUE, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( flashOn ) {
                        // Turn flash off.
                        flashOn = false;
                        flashingNode.setPaint( normalColor );
                        flashCount++;
                        if ( flashCount < numFlashes ) {
                            flashTimer.setInitialDelay( offTime );
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
                        flashTimer.setInitialDelay( onTime );
                        flashTimer.restart();
                    }
                }
            } );
            flashTimer.setRepeats( false );
        }

        public void restart() {
            flashTimer.stop();
            flashOn = false;
            flashCount = 0;
            flashTimer.setInitialDelay( 0 );
            flashTimer.restart();
        }

        public boolean isFlashing() {
            return flashTimer.isRunning();
        }

        public void stop() {
            flashTimer.stop();
        }
    }
}
