// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharingtestsim;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.state.SerializableBufferedImage;
import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;

/**
 * @author Sam Reid
 */
public class SimSharingTestSimState implements SimState {
    private long time;
    private SerializableBufferedImage image;
    public final Vector2D position;
    private int index;

    public SimSharingTestSimState( long time,
                                   SerializableBufferedImage image,
                                   Vector2D position,
                                   int index ) {
        this.time = time;
        this.image = image;
        this.position = position;
        this.index = index;
    }

    public long getTime() {
        return time;
    }

    public SerializableBufferedImage getImage() {
        return image;
    }

    public int getIndex() {
        return index;
    }
}