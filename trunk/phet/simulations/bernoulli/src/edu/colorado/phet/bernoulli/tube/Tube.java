/**
 * Class: Tube
 * Package: edu.colorado.phet.bernoulli.tube
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli.tube;

import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Tube extends SimpleObservable {
    ArrayList points = new ArrayList();
    double diameter = .20;

    public void addPoint( Point2D.Double point ) {
        points.add( point );
    }

    public List getPoints() {
        return points;
    }

    public double getDiameter() {
        return diameter;
    }

    public void clear() {
        points.clear();
    }
}
