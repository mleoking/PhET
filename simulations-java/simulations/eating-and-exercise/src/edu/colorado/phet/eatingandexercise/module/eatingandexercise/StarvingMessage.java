package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.view.LabelNode;

/**
 * Created by: Sam
 * Jun 24, 2008 at 11:48:26 AM
 */
public class StarvingMessage extends LabelNode {
    private Human human;

    public StarvingMessage( final Human human ) {
        super( "<html>Body is in starvation mode.<br>Death will occur soon.<html>" );
        this.human = human;

        human.addListener( new Human.Adapter() {
            public void ageChanged() {
                updateVisibility();
            }
        } );
        updateVisibility();
    }

    protected void updateVisibility() {//todo: remove awkwardness
        setVisible( human.getStarvingTimeDays() > 30 );
    }


}