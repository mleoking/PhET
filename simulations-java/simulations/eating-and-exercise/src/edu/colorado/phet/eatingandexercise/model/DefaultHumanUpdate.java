package edu.colorado.phet.eatingandexercise.model;

/**
 * Created by: Sam
 * Jul 14, 2008 at 8:07:33 PM
 */
public class DefaultHumanUpdate implements HumanUpdate {
    private CompositeHumanUpdate compositeHumanUpdate = new CompositeHumanUpdate();

    public DefaultHumanUpdate() {
        compositeHumanUpdate.add( new MuscleAndFatMassLoss2() );
    }

    public void update( Human human, double dt ) {
        compositeHumanUpdate.update( human, dt );
    }
}
