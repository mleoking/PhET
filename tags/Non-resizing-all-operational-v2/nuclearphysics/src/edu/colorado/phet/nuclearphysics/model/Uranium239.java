/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;

import java.awt.geom.Point2D;

public class Uranium239 extends Nucleus {

    private Neutron fissionInstigatingNeutron;
    private BaseModel model;

    public Uranium239( Point2D position, BaseModel model ) {
        super( position, 92, 147 );
        this.model = model;
    }
}
