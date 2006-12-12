/**
 * Class: ScalarObservable
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 28, 2004
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.util.SimpleObservable;

public abstract class ScalarObservable extends SimpleObservable {

    public void addObserver( ScalarObserver observer ) {
        super.addObserver( observer );
    }

    public abstract double getValue();
}
