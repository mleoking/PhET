package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;

import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractions.FractionsResources.Strings.MY_FRACTIONS;
import static fj.data.List.range;

/**
 * Main simulation canvas for "build a fraction" tab
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas {
    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        final Stroke stroke = new BasicStroke( 2 );
        final VBox radioButtonControlPanel = new VBox( 0, VBox.LEFT_ALIGNED,
                                                       radioButton( "Numbers" ),
                                                       radioButton( "Pictures" ) );

        //IDEA: show the target in the box but grayed out and dotted line.  When the user has a match, it turns red dotted line.  When they drop it in, it fills in.
        //Would this have worked for build a molecule?
        List<PNode> scoreBoxes = range( 0, 4 ).map( new F<Integer, PNode>() {
            @Override public PNode f( final Integer integer ) {
                return new PhetPPath( new RoundRectangle2D.Double( 0, 0, 120, 120, 30, 30 ), stroke, Color.darkGray );
            }
        } );
        final Collection<PNode> nodes = scoreBoxes.toCollection();
        final VBox rightControlPanel = new VBox( radioButtonControlPanel, new Spacer( 0, 0, 10, 10 ), new PhetPText( MY_FRACTIONS, CONTROL_FONT ), new VBox( nodes.toArray( new PNode[nodes.size()] ) ) ) {{
            setOffset( STAGE_SIZE.width - getFullWidth() - INSET, INSET );
        }};
        addChild( rightControlPanel );

        //Add a piece size control node
        //Make it look like radio buttons so it doesn't look draggable?
        //But I'm worried about sharing "representation" change toggle buttons from the rest of the sim.
        addChild( new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 125, 30, 30 ), stroke, Color.darkGray );
            addChild( border );
            final F<Integer, PNode> toBar = new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return bar( i + 1, i % 2, i / 2 );
                }
            };
            addChild( new FNode( range( 0, 8 ).map( toBar ) ) {{
                centerFullBoundsOnPoint( border.getCenterX(), border.getCenterY() );
            }} );
            setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, INSET );
        }} );

        addChild( new RichPNode() {{
            final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 700, 150, 30, 30 ), stroke, Color.darkGray );
            addChild( border );

            BucketView bucketView = new BucketView( new Bucket( 150, -50, new Dimension2DDouble( 200, 100 ), Color.blue, "pieces" ), ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 1, -1 ) );
            addChild( bucketView.getHoleNode() );
            addChild( bucketView.getFrontNode() );

            setOffset( ( STAGE_SIZE.width - rightControlPanel.getFullWidth() ) / 2 - this.getFullWidth() / 2, STAGE_SIZE.height - INSET - this.getFullHeight() );
        }} );
    }

    public PNode bar( final int numSegments, final int row, final int column ) {
        return new PNode() {{

            double width = 120;
            final double height = 25;

            final double sliceWidth = width / numSegments;
            List<PNode> nodes = range( 0, numSegments ).map( new F<Integer, PNode>() {
                @Override public PNode f( final Integer i ) {
                    return new PhetPPath( new Rectangle2D.Double( i * sliceWidth, 0, sliceWidth, height ), new BasicStroke( 1 ), Color.black );
                }
            } );
            addChild( new FNode( nodes ) );

            final int spacingX = 15;
            final int spacingY = 15;
            setOffset( column * ( width + spacingX ), row * ( height + spacingY ) );
        }};
    }

    private PNode radioButton( final String text ) {
        return new PSwing( new JRadioButton( text ) {{
            setOpaque( false );
            setFont( AbstractFractionsCanvas.CONTROL_FONT );
        }} );
    }
}