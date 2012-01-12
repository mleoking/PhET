// Copyright 2002-2011, University of Colorado
package jass.contact;

import jass.engine.Source;

public interface RollForce extends Source {
    // ref values correspond to normal loop speed in wav file, or to
    // reference setting of AR filter, etc.
    void setRollForceReference( float val );

    void setRollSpeedReference( float val );

    void setRollGain( float gain );
}


