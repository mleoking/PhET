package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.common_cck.view.graphics.InteractiveGraphic;

/**
 * User: Sam Reid
 * Date: Jun 10, 2004
 * Time: 12:51:25 AM
 * Copyright (c) Jun 10, 2004 by Sam Reid
 */
public interface CircuitGraphicListener {
    void graphicAdded( Branch branch, InteractiveGraphic graphic );

    void graphicRemoved( Branch branch, InteractiveGraphic graphic );
}
