// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * @author Sam Reid
 */
public interface SimsharingApplicationState extends Serializable, IProguardKeepClass {
    long getTime();

    SerializableBufferedImage getImage();
}