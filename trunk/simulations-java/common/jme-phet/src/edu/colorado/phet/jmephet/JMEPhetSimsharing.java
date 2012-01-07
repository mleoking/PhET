// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.SystemObject;

/**
 * @author Sam Reid
 */
public class JMEPhetSimsharing {
    public enum Objects implements SystemObject {
        jmePhetApplication
    }

    public enum Actions implements SystemAction {
        erred,
    }
}
