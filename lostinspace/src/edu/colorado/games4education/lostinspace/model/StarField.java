/**
 * Class: StarField
 * Package: edu.colorado.games4education.lostinspace.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.games4education.lostinspace.model;

import edu.colorado.phet.common.model.CompositeModelElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that, at this point, doesn't need to add
 * any behavior to its parent. It may later
 */
public class StarField extends CompositeModelElement {

    private ArrayList stars = new ArrayList();

    public void addStar( Star star ) {
        stars.add( star );
    }

    public List getStars() {
        return stars;
    }
}
