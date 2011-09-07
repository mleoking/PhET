// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharingtestsim;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SerializableBufferedImage;
import edu.colorado.phet.common.phetcommon.simsharing.SimState;

/**
 * @author Sam Reid
 */
public class SimSharingTestSimState implements SimState {
    private long time;
    //    private SerializableBufferedImage image;
    public final ImmutableVector2D position;

    public SimSharingTestSimState( long time,
//                                   SerializableBufferedImage image,
                                   ImmutableVector2D position ) {
        this.time = time;
//        this.image = image;
        this.position = position;
    }

    public long getTime() {
        return time;
    }

    public SerializableBufferedImage getImage() {
        return null;
    }
}