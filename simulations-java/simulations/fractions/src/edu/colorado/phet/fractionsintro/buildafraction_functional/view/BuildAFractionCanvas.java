package edu.colorado.phet.fractionsintro.buildafraction_functional.view;

import fj.Effect;
import fj.F;
import fj.F2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction_functional.model.Mode;
import edu.colorado.phet.fractionsintro.buildafraction_functional.view.numbers.NumberScene;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.view.UpdateNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;

/**
 * Main simulation canvas for "build a fraction" tab
 * TODO: duplicated code in pieceTool pieceGraphic DraggablePieceNode
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas {
    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    private final BuildAFractionModel model;

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );
    public final PictureScene pictureScene;
    public final NumberScene numberScene;

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        setBackground( Color.white );

        final SettableProperty<Mode> mode = model.toProperty(
                new F<BuildAFractionState, Mode>() {
                    @Override public Mode f( final BuildAFractionState s ) {
                        return s.mode;
                    }
                },
                new F2<BuildAFractionState, Mode, BuildAFractionState>() {
                    @Override public BuildAFractionState f( final BuildAFractionState s, final Mode mode ) {
                        return s.withMode( mode );
                    }
                }
        );

        //View to show when the user is guessing numbers (by creating pictures)
        pictureScene = new PictureScene( model, mode, this );
        numberScene = new NumberScene( model, mode, this );

        //When the mode changes, update the toolboxes
        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode node ) {
                node.addChild( mode.get() == Mode.PICTURES ? pictureScene : numberScene );
            }
        }, mode ) );

        //Reset all button
        addChild( new ResetAllButtonNode( model, this, 18, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
            setOffset( STAGE_SIZE.width - this.getFullWidth() - INSET, STAGE_SIZE.height - this.getFullHeight() - INSET );
        }} );
    }

    public static PNode createModeControlPanel( final SettableProperty<Mode> mode ) {
        return new HBox( radioButton( Components.picturesRadioButton, Strings.PICTURES, mode, Mode.PICTURES ),
                         radioButton( Components.numbersRadioButton, Strings.NUMBERS, mode, Mode.NUMBERS ) ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
    }

    public static PNode emptyFractionGraphic( boolean showNumeratorOutline, boolean showDenominatorOutline ) {
        final VBox box = new VBox( box( showNumeratorOutline ), divisorLine(), box( showDenominatorOutline ) );

        //Show a background behind it to make the entire shape draggable
        final PhetPPath background = new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), TRANSPARENT );
        return new RichPNode( background, box );
    }

    private static PNode divisorLine() { return new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black ); }

    private static PhetPPath box( boolean showOutline ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40, 50 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : TRANSPARENT );
    }

    public static PNode radioButton( IUserComponent component, final String text, final SettableProperty<Mode> mode, Mode value ) {
        return new PSwing( new PropertyRadioButton<Mode>( component, text, mode, value ) {{
            setOpaque( false );
            setFont( AbstractFractionsCanvas.CONTROL_FONT );
        }} );
    }
}