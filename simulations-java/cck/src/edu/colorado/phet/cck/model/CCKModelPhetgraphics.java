package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;

/**
 * CCKModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CCKModelPhetgraphics extends CCKModel {
    protected Circuit createCircuit( CircuitChangeListener circuitChangeListener ) {
        return new Circuit( circuitChangeListener ) {
            protected void removeNeighborJunctions( Branch branch ) {
                //no-op, handled elsewhere in phetgraphics implementation
            }
        };
    }

}
