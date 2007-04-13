/**
 * Class: StarField
 * Package: edu.colorado.phet.distanceladder.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.phet.distanceladder.model;

import edu.colorado.phet.common.model.CompositeModelElement;
import edu.colorado.phet.distanceladder.Config;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that, at this point, doesn't need to add
 * any behavior to its parent. It may later
 */
public class StarField extends CompositeModelElement {

    private ArrayList stars = new ArrayList();
    private Rectangle2D.Double bounds;

    public StarField() {
        this.bounds = new Rectangle2D.Double( -Config.universeWidth * 0.51, -Config.universeWidth * 0.51,
                                              Config.universeWidth * 1.02, Config.universeWidth * 1.02 );
    }

    public void addStar( Star star ) {
        stars.add( star );
    }

    public List getStars() {
        return stars;
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public void reset() {
        stars.clear();
    }
}
