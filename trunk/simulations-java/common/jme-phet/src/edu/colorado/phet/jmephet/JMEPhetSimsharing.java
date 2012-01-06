// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;

/**
 * @author Sam Reid
 */
public class JMEPhetSimsharing {
    public enum Objects implements SimSharingConstants.System.SystemObject {
        jmePhetApplication
    }

    public enum Actions implements SimSharingConstants.System.SystemAction {
        erred,
    }
}
