package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.common_cck.view.graphics.Graphic;

/**
 * User: Sam Reid
 * Date: May 27, 2004
 * Time: 9:41:20 AM
 * Copyright (c) May 27, 2004 by Sam Reid
 */
public interface HasJunctionGraphic extends Graphic {
    public JunctionGraphic getJunctionGraphic();

    void delete();
}
