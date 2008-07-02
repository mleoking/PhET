package edu.colorado.phet.eatingandexercise.view;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 27, 2008 at 8:36:43 AM
 */
public class HealthIndicator extends PNode {
    private IndicatorHealthBar heartStrengthIndicator;
    private IndicatorHealthBar heartStrainIndicator;
    private Human human;
    private static final int INDICATOR_BAR_HEIGHT = 150;

    public HealthIndicator( final Human human ) {
        this.human = human;
        heartStrengthIndicator = new HeartStrengthIndicatorBar( human );
        heartStrainIndicator = new IndicatorHealthBar( "<html>Heart Strain</html>", 0, 2000, 1000, INDICATOR_BAR_HEIGHT );

        addChild( heartStrengthIndicator );
        addChild( heartStrainIndicator );


        heartStrainIndicator.setValue( 0.5 );

//        human.addListener( new Human.Adapter() {
//            public void fatPercentChanged() {
//                updateBodyFat();
//            }
//        } );
//        human.addListener( new Human.Adapter() {
//            public void exerciseChanged() {
//                updateExerciseIndicator();
//            }
//        } );
        updateBodyFat();
        updateLayout();
    }

//    private void updateExerciseIndicator() {
//        heartStrainIndicator.setValue( human.getExercise().getValue() );
//    }

    private void updateBodyFat() {
        heartStrengthIndicator.setValue( human.getFatMassPercent() );
    }

    private void updateLayout() {
        double inset = 4;
        heartStrainIndicator.setOffset( heartStrengthIndicator.getFullBounds().getMaxX() + inset, 0 );
    }

    public static void main( String[] args ) {
        PiccoloTestFrame piccoloTestFrame = new PiccoloTestFrame( HealthIndicator.class.getName() );
        HealthIndicator indicator = new HealthIndicator( new Human() );
        indicator.setOffset( 200, 200 );
        piccoloTestFrame.addNode( indicator );
        piccoloTestFrame.setVisible( true );
    }

    private static class HeartStrengthIndicatorBar extends IndicatorHealthBar {
        private Human human;

        public HeartStrengthIndicatorBar( final Human human ) {
            super( "<html>Heart Strength</html>", 0, 2000, 500, INDICATOR_BAR_HEIGHT );
            this.human = human;
//            setValue( 20 );
            human.addListener( new Human.Adapter() {
                public void heartStrengthChanged() {
                    updateValue();
                }
            } );
            updateValue();
        }

        private void updateValue() {
            setValue( human.getHeartStrength() );
        }
    }
}
