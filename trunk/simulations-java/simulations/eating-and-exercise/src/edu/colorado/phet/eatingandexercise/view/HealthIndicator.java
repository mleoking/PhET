package edu.colorado.phet.eatingandexercise.view;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 27, 2008 at 8:36:43 AM
 */
public class HealthIndicator extends PNode {
    private IndicatorHealthBar bodyFatIndicator;
    private IndicatorHealthBar exerciseIndicator;
    private Human human;
    private final int INDICATOR_BAR_HEIGHT = 150;

    public HealthIndicator( final Human human ) {
        this.human = human;
        bodyFatIndicator = new IndicatorHealthBar( "<html>Body Fat %</html>", 0, 100, 20, INDICATOR_BAR_HEIGHT );
        exerciseIndicator = new IndicatorHealthBar( "<html>Exercise</html>", 0, 2000, 1000, INDICATOR_BAR_HEIGHT );

        addChild( bodyFatIndicator );
        addChild( exerciseIndicator );

        bodyFatIndicator.setValue( 20 );
        exerciseIndicator.setValue( 0.5 );

        human.addListener( new Human.Adapter() {
            public void fatPercentChanged() {
                updateBodyFat();
            }
        } );
        human.addListener( new Human.Adapter() {
            public void exerciseChanged() {
                updateExerciseIndicator();
            }
        } );
        updateBodyFat();
        updateLayout();
    }

    private void updateExerciseIndicator() {
        exerciseIndicator.setValue( human.getExercise().getValue() );
    }

    private void updateBodyFat() {
        bodyFatIndicator.setValue( human.getFatMassPercent() );
    }

    private void updateLayout() {
        double inset = 4;
        exerciseIndicator.setOffset( bodyFatIndicator.getFullBounds().getMaxX() + inset, 0 );
    }

    public static void main( String[] args ) {
        PiccoloTestFrame piccoloTestFrame = new PiccoloTestFrame( HealthIndicator.class.getName() );
        HealthIndicator indicator = new HealthIndicator( new Human() );
        indicator.setOffset( 200, 200 );
        piccoloTestFrame.addNode( indicator );
        piccoloTestFrame.setVisible( true );
    }
}
