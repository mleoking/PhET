// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 26, 2008 at 4:12:57 PM
 */
public class HumanAudioPlayer {
    private Human human;

    public HumanAudioPlayer( final Human human ) {
        this.human = human;
        human.addListener( new Human.Adapter() {
            public void aliveChanged() {
                if ( !human.isAlive() ) {
                    EatingAndExerciseResources.getResourceLoader().getAudioClip( "killed.wav" ).play();
                }
            }
        } );
    }

    public void start() {
    }
}
