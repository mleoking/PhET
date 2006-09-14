package edu.colorado.phet.cck3.phetgraphics_cck.circuit;

import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.common_cck.view.graphics.Graphic;

/**
 * User: Sam Reid
 * Date: Jun 21, 2004
 * Time: 12:51:20 AM
 * Copyright (c) Jun 21, 2004 by Sam Reid
 */
public interface LeverInteraction extends Graphic {
    CircuitComponent getComponent();

    void delete();
}
