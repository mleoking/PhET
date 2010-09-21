package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 26, 2008 at 7:02:15 PM
 */
public class HeartAttackMessage extends WarningMessage {
    private Human human;

    public HeartAttackMessage( Human human ) {
        super( "" );
        this.human = human;
        human.addListener( new Human.Adapter() {
            public void heartAttackProbabilityChanged() {
                updateMessage();
            }
        } );
        updateMessage();
    }

    private void updateMessage() {
        setText( getWarningMessage() );
        setVisible( human.getHeartAttackProbabilityPerDay() > 0 );
    }

    private String getWarningMessage() {
        return "<html>Increased risk of heart attack.</html>";
    }

    private String getLevel() {
        double heartAttackProbabilityPerDay = human.getHeartAttackProbabilityPerDay();
        if ( heartAttackProbabilityPerDay < 0.1 ) {
            return "moderate";
        }
        else if ( heartAttackProbabilityPerDay < 0.3 ) {
            return "high";
        }
        else {
            return "extreme";
        }
    }
}
