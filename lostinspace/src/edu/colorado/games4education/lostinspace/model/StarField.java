/**
 * Class: StarField
 * Package: edu.colorado.games4education.lostinspace.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.games4education.lostinspace.model;

import edu.colorado.phet.common.model.CompositeModelElement;
import edu.colorado.games4education.lostinspace.Config;

import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Rectangle2D;

/**
 * This is a class that, at this point, doesn't need to add
 * any behavior to its parent. It may later
 */
public class StarField extends CompositeModelElement {

    private ArrayList stars = new ArrayList();
    private Rectangle2D.Double bounds;

    public StarField( Rectangle2D.Double bounds ) {
        this.bounds = bounds;
        this.bounds = new Rectangle2D.Double( -Config.fixedStarDistance * 2 / 3, -Config.fixedStarDistance * 2 / 3,
                                              Config.fixedStarDistance * 2 / 3, Config.fixedStarDistance * 2 / 3);
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
}
