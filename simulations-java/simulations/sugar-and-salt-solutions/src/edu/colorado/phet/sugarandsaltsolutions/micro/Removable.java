// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Signifies that a model object (such as a water molecule) can be observed for when it leaves the model.
 *
 * @author Sam Reid
 */
public interface Removable {
    public void addRemovalListener( VoidFunction0 voidFunction0 );
}
