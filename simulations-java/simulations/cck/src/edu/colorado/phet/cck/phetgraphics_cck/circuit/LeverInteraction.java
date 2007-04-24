package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common_cck.view.graphics.Graphic;

/**
 * User: Sam Reid
 * Date: Jun 21, 2004
 * Time: 12:51:20 AM
 *
 */
public interface LeverInteraction extends Graphic {
    CircuitComponent getComponent();

    void delete();
}
