// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.view;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;

public interface IParticleApp {
    ParticleModel getParticleModel();

    void setNumberParticles( int numParticles );
}
