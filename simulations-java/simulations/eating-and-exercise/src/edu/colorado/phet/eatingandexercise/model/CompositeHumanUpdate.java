package edu.colorado.phet.eatingandexercise.model;

import java.util.ArrayList;

/**
 * Created by: Sam
 * Jul 14, 2008 at 8:07:51 PM
 */
public class CompositeHumanUpdate implements HumanUpdate {
    private ArrayList list = new ArrayList();

    public void add( HumanUpdate humanUpdate ) {
        list.add( humanUpdate );
    }

    public void update( Human human, double dt ) {
        for ( int i = 0; i < list.size(); i++ ) {
            ( (HumanUpdate) list.get( i ) ).update( human, dt );
        }
    }
}
