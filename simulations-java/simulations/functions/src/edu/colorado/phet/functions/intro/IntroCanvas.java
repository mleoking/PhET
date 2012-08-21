package edu.colorado.phet.functions.intro;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.functions.buildafunction.AbstractFunctionsCanvas;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionPrototype2Canvas;
import edu.colorado.phet.functions.buildafunction.ValueContext;
import edu.colorado.phet.functions.buildafunction.ValueNode;
import edu.colorado.phet.functions.intro.view.NavigationBar;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends AbstractFunctionsCanvas implements ValueContext, SceneContext {

    private Scene scene;
    private final ResetAllButtonNode resetAllButtonNode;
    private final HTMLImageButtonNode nextButton;
    private static final long ANIMATION_DELAY = 200;
    public final IntegerProperty level = new IntegerProperty( 0 );

    public IntroCanvas() {

        setBackground( BuildAFunctionPrototype2Canvas.BACKGROUND_COLOR );
        addChild( new NavigationBar() {{
            setOffset( INSET, INSET );
        }} );
        resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
            }
        }, this, new PhetFont( 18, true ), Color.black, Color.orange ) {{
            setOffset( 1024 - this.getFullWidth() - INSET - INSET - INSET, 768 - this.getFullHeight() - INSET - 40 - 40 - 10 - INSET );
        }};
        addScreenChild( resetAllButtonNode );

        scene = Scenes.scenes.index( level.get() ).f( this );
        addChild( scene );

        nextButton = new HTMLImageButtonNode( "Next" ) {{
            setFont( resetAllButtonNode.getFont() );
            setBackground( Color.green );
            setOffset( resetAllButtonNode.getFullBounds().getX(), resetAllButtonNode.getFullBounds().getMinY() - getFullBounds().getHeight() - 10 );
        }};
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                level.increment();
                Integer level = IntroCanvas.this.level.get();
                if ( level >= Scenes.scenes.length() ) {
                    level = Scenes.scenes.length() - 1;
                }
                animateToNewScene( Scenes.scenes.index( level ).f( IntroCanvas.this ) );
            }
        } );
        addChild( nextButton );
        nextButton.setVisible( false );
    }

    public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
        scene.mouseDragged( valueNode, delta );
    }

    public void mouseReleased( final ValueNode valueNode ) {
        scene.mouseReleased( valueNode );
    }

    public void showNextButton() {
        nextButton.setVisible( true );
    }

    private void animateToNewScene( final Scene newScene ) {
        nextButton.setVisible( false );
        addChild( newScene );
        newScene.setOffset( STAGE_SIZE.width, 0 );
        newScene.animateToPositionScaleRotation( 0, 0, 1, 0, ANIMATION_DELAY );
        scene.animateToPositionScaleRotation( -STAGE_SIZE.width, 0, 1, 0, ANIMATION_DELAY ).setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                IntroCanvas.this.removeChild( scene );
                IntroCanvas.this.scene = newScene;
            }
        } );
    }
}