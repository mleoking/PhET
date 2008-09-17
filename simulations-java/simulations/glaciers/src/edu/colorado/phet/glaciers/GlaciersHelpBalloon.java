/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers;

import javax.swing.JComponent;

import edu.colorado.phet.common.piccolophet.help.HelpBalloon;

/**
 * GlaciersHelpBalloon encapsulates the look of help ballons for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersHelpBalloon extends HelpBalloon {

    public GlaciersHelpBalloon( JComponent helpPanel, String text, Attachment arrowTailPosition, double arrowLength ) {
        super( helpPanel, text, arrowTailPosition, arrowLength, 0 /* arrowRotation */ );
    }
}
