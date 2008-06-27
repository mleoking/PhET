package edu.colorado.phet.eatingandexercise.view;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 27, 2008 at 8:36:43 AM
 */
public class HealthIndicator extends PNode {
    private HealthBar bodyFatIndicator;
    private HealthBar exerciseIndicator;
    private Human human;

    public HealthIndicator( Human human ) {
        this.human = human;
        bodyFatIndicator = new HealthBar( "<html>Body Fat %</html>" );
        exerciseIndicator = new HealthBar( "<html>Exercise</html>" );

        addChild( bodyFatIndicator );
        addChild( exerciseIndicator );

        updateLayout();
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
