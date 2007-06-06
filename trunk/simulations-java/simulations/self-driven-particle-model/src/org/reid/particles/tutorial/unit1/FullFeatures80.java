/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import org.reid.particles.tutorial.BasicTutorialCanvas;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 12:23:38 AM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class FullFeatures80 extends FullFeatureBaseClass {
    boolean usedRandomness = false;
    boolean usedRange = false;
    boolean usedNumber = false;

    public FullFeatures80( final BasicTutorialCanvas basicPage ) {
        super( basicPage );
        setText( "The free parameters for the Self-Driven Particle Model are: number of particles, " +
                 "universe length, particle speed, randomness, visual range, and time increment. " +
                 "Experiment with the three provided numerical controls before moving on." );
        getRadiusControlGraphic().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                usedRange = true;
                checkFinish();
            }

        } );
        getRandomnessGraphic().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                usedRandomness = true;
                checkFinish();
            }

        } );
        getParticleCountGraphic().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                usedNumber = true;
                checkFinish();
            }

        } );

    }

    private void checkFinish() {
        if( usedRandomness && usedNumber && usedRange ) {
            advance();
        }
    }
}
