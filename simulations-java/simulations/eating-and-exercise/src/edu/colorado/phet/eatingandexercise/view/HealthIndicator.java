package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;

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
        heartStrainIndicator = new HeartStrainIndicatorBar( human );

        addChild( heartStrengthIndicator );
        addChild( heartStrainIndicator );

        updateLayout();
    }

    private void updateLayout() {
        double inset = 4;
        heartStrainIndicator.setOffset( heartStrengthIndicator.getFullBounds().getMaxX() + inset, 0 );
    }

    private static class HeartStrengthIndicatorBar extends IndicatorHealthBar {
        private Human human;

        public HeartStrengthIndicatorBar( final Human human ) {
            super( "<html>Heart Strength</html>", 0, 1, 250 / 1000.0, 1000 / 1000.0, INDICATOR_BAR_HEIGHT, Color.red, Color.green );
            this.human = human;
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

    private static class HeartStrainIndicatorBar extends IndicatorHealthBar {
        private Human human;

        public HeartStrainIndicatorBar( Human human ) {
            super( "<html>Heart Strain</html>", 0, 1, 16 / 100.0, 31 / 100.0, INDICATOR_BAR_HEIGHT, Color.green, Color.red );
            this.human = human;
            human.addListener( new Human.Adapter() {
                public void heartStrainChanged() {
                    updateValue();
                }
            } );
            updateValue();
        }

        private void updateValue() {
            setValue( human.getHeartStrain() );
        }
    }

    public static void main( String[] args ) {
        PiccoloTestFrame piccoloTestFrame = new PiccoloTestFrame( HealthIndicator.class.getName() );
        HealthIndicator indicator = new HealthIndicator( new Human() );
        indicator.setOffset( 200, 200 );
        piccoloTestFrame.addNode( indicator );
        piccoloTestFrame.setVisible( true );
    }
}
