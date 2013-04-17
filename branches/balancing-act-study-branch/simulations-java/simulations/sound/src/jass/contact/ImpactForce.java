// Copyright 2002-2011, University of Colorado
package jass.contact;

import jass.engine.Source;

public interface ImpactForce extends Source {
    void setImpactGain( float gain );

    public void setImpactDuration( float dur );

    public void bang( float force );
}


