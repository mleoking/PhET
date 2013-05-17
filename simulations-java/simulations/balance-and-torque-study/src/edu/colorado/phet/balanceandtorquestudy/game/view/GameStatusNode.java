package edu.colorado.phet.balanceandtorquestudy.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Indicates game status, such as how far through the game the user has
 * progressed.
 *
 * @author John Blanco
 */
public class GameStatusNode extends PNode {

    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final PhetFont FONT = new PhetFont( 24 );
    private static final Dimension2D SIZE = new PDimension( 800, 50 );

    public GameStatusNode( final int totalChallenges, final Property<Integer> challengesCompleted ) {
        final PNode backgroundRect = new PhetPPath( new Rectangle2D.Double( 0, 0, SIZE.getWidth(), SIZE.getHeight() ),
                                                    BACKGROUND_FILL_COLOR,
                                                    BACKGROUND_STROKE,
                                                    BACKGROUND_STROKE_COLOR );
        addChild( backgroundRect );
        final PText statusMessage = new PhetPText( "X", FONT );
        backgroundRect.addChild( statusMessage );

        challengesCompleted.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer integer ) {
                statusMessage.setText( "You have completed " + challengesCompleted.get() + " of " + totalChallenges + " challenges." );
                statusMessage.centerFullBoundsOnPoint( SIZE.getWidth() / 2, SIZE.getHeight() / 2 );
            }
        } );
    }
}
