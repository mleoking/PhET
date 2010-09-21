package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 24, 2008 at 11:48:26 AM
 */
public class StarvingMessage extends WarningMessage {
    private Human human;

    public StarvingMessage( final Human human ) {
        super( "<html>Starving!<html>" );

        this.human = human;

        human.addListener( new Human.Adapter() {
            public void starvingChanged() {
                updateVisibility();
            }
        } );
        updateVisibility();
    }

    protected void updateVisibility() {
        setVisible( human.isStarving() );
    }
}