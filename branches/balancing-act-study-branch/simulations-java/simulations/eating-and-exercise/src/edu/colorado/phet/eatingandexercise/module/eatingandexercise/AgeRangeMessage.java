// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 24, 2008 at 11:48:26 AM
 */
public class AgeRangeMessage extends TimeoutWarningMessage {
    private Human human;

    public AgeRangeMessage( final Human human ) {
        super( "<html>This simulation is based on data<br>from 20-60 year olds.<html>" );
        this.human = human;

        human.addListener( new Human.Adapter() {
            public void ageChanged() {
                update();
            }
        } );
    }

    protected void update() {
        if ( human.getAge() < EatingAndExerciseUnits.yearsToSeconds( 20 ) || human.getAge() > EatingAndExerciseUnits.yearsToSeconds( 60 ) ) {
            resetVisibleTime();
        }
    }
}