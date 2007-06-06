package org.reid.particles.view;

import org.reid.particles.model.ParticleModel;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 10:14:16 PM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */
public interface IParticleApp {
    ParticleModel getParticleModel();

    void setNumberParticles( int numParticles );
}
