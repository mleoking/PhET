/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:23:30 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class ManGraphic extends PhetImageGraphic {
    private Force1DPanel force1DPanel;

    public ManGraphic( Force1DPanel force1DPanel ) {
        super( force1DPanel, "images/left-ii.gif" );
        this.force1DPanel = force1DPanel;

    }
}
