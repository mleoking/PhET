package edu.colorado.phet.buildafraction.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.view.SpinnerButtonNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LevelSelectionScreen extends PNode {
    private double initialX;
    private double initialY;

    public void animateHome( int duration ) {
        animateToPositionScaleRotation( initialX, initialY, 1, 0, duration );
    }

    public static interface Context {
        void homeButtonPressed();
    }

    public LevelSelectionScreen( final String text, final Context context ) {

        addChild( new SpinnerButtonNode( Images.LEFT_BUTTON_UP, Images.LEFT_BUTTON_PRESSED, Images.LEFT_BUTTON_GRAY, new VoidFunction1<Boolean>() {
            public void apply( final Boolean spinning ) {
                context.homeButtonPressed();
            }
        } ) );
        //Title text, only shown when the user is choosing a level
        PNode titleText = new PNode() {{
            addChild( new PhetPText( text, new PhetFont( 38, true ) ) );
        }};

//        WorldSelectionScreen worldSelectionScreen = new WorldSelectionScreen( BuildAFractionCanvas2.this ) {{
//            centerFullBoundsOnPoint( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 );
//        }};
//        addChild( worldSelectionScreen );
        titleText.centerFullBoundsOnPoint( AbstractFractionsCanvas.STAGE_SIZE.width / 2, AbstractFractionsCanvas.INSET + titleText.getFullBounds().getHeight() / 2 );
        addChild( titleText );
    }

    public void setInitialPosition( double x, double y ) {
        this.initialX = x;
        this.initialY = y;
        setOffset( x, y );
    }
}