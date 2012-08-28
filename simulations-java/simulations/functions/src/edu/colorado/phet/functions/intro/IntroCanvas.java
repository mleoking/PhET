package edu.colorado.phet.functions.intro;

import fj.F;
import fj.data.List;

import edu.colorado.phet.functions.buildafunction.ValueContext;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.colorado.phet.functions.intro.view.NavigationBar;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends ChallengeProgressionCanvas implements ValueContext, SceneContext {

    private Scene scene;
    private List<F<IntroCanvas, Scene>> scenes;

    public IntroCanvas( List<F<IntroCanvas, Scene>> scenes ) {
        this.scenes = scenes;

        addChild( new NavigationBar() {{
            setOffset( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2, INSET );
        }} );

        scene = scenes.index( level.get() ).f( this );
        addChild( scene );
    }

    @Override protected void nextButtonPressed() {
        final Integer index = Math.min( scenes.length() - 1, level.get() );
        animateToNewScene( scenes.index( index ).f( this ) );
    }

    @Override protected void finishAnimation( final PNode newScene ) {
        scene.animateToPositionScaleRotation( -STAGE_SIZE.width, 0, 1, 0, ANIMATION_DELAY ).setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                IntroCanvas.this.removeChild( scene );
                IntroCanvas.this.scene = (Scene) newScene;
            }
        } );
    }

    public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        scene.mouseDragged( valueNode, delta );
    }

    public void mouseReleased( final ValueNode valueNode ) {
        scene.mouseReleased( valueNode );
    }

}