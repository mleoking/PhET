// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.common.view.Colors;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.view.NumberLineNode.Horizontal;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.CakeIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.HorizontalBarIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.NumberLineIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.PieIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RadioButtonStrip;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.VerticalBarIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.WaterGlassIcon;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.intro.view.Representation.*;
import static java.util.Arrays.asList;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends AbstractFractionsCanvas {

    //Flag to enable debugging water glasses and cake representations
    private static final boolean debugRepresentations = false;

    public FractionsIntroCanvas( final FractionsIntroModel model ) {

        final RadioButtonStrip<Representation> representationControlPanel =
                new RadioButtonStrip<Representation>( model.representation, asList(
                        new Pair<PNode, Representation>( new PieIcon( model.representation, Colors.CIRCLE_COLOR ), PIE ),
                        new Pair<PNode, Representation>( new HorizontalBarIcon( model.representation, Colors.HORIZONTAL_SLICE_COLOR ), HORIZONTAL_BAR ),
                        new Pair<PNode, Representation>( new VerticalBarIcon( model.factorySet.verticalSliceFactory, Colors.VERTICAL_SLICE_COLOR ).getNode(), VERTICAL_BAR ),
                        new Pair<PNode, Representation>( new WaterGlassIcon( model.representation, Colors.CUP_COLOR ), WATER_GLASSES ),
                        new Pair<PNode, Representation>( new CakeIcon( model.representation ), CAKE ),
                        new Pair<PNode, Representation>( new NumberLineIcon( model.representation ), NUMBER_LINE ) ), 0 ) {{
                    setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() / 2, INSET );
                }};
        addChild( representationControlPanel );

        //Show the fraction text icon on the right to keep it far away from the spinner fraction (which is to the left)
        boolean iconTextOnTheRight = true;

        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( model.representation, PIE, new PieSetNode( model.pieSet, rootNode, iconTextOnTheRight ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( model.representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode, iconTextOnTheRight ) ) );

        //For vertical bars
        addChild( new RepresentationNode( model.representation, VERTICAL_BAR, new PieSetNode( model.verticalBarSet, rootNode, iconTextOnTheRight ) ) );

        //For debugging water glasses region management
        if ( debugRepresentations ) {
            addChild( new RepresentationNode( model.representation, WATER_GLASSES, new PieSetNode( model.waterGlassSet, rootNode, iconTextOnTheRight ) ) );
        }

        //For water glasses
        final Rectangle2D b = model.factorySet.waterGlassSetFactory.createEmptyPies( 1, 1 ).head().cells.head().getShape().getBounds2D();
        addChild( new RepresentationNode( model.representation, WATER_GLASSES, new WaterGlassSetNode( model.waterGlassSet, rootNode, Colors.CUP_COLOR, b.getWidth(), b.getHeight(), iconTextOnTheRight ) ) );

        //For draggable cakes
        addChild( new RepresentationNode( model.representation, CAKE, new CakeSetNode( model.cakeSet, rootNode, iconTextOnTheRight ) ) );

        //For debugging cakes
        if ( debugRepresentations ) {
            addChild( new RepresentationNode( model.representation, CAKE, new PieSetNode( model.cakeSet, rootNode, iconTextOnTheRight ) ) );
        }

        //Number line
        addChild( new NumberLineNode( model.numerator, model.numerator, model.denominator, model.representation.valueEquals( NUMBER_LINE ), model.maximum, new Horizontal(), 32, Colors.NUMBER_LINE, false ) {{
            setOffset( INSET + 10, representationControlPanel.getFullBounds().getMaxY() + 100 + 15 );
        }} );

        //The fraction control node
        addChild( new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator, model.maximum, 8 ) ) {{
            setOffset( 73, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );

        final ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, STAGE_SIZE.height - getFullBounds().getHeight() - INSET );
        }};
        addChild( resetAllButtonNode );

        //Spinner to change the maximum allowed value
        MaxSpinner maxSpinner = new MaxSpinner( model.maximum ) {{

            //Center above reset all button
            setOffset( ( STAGE_SIZE.getWidth() + representationControlPanel.getMaxX() ) / 2 - getFullWidth() / 2, representationControlPanel.getCenterY() - getFullHeight() / 2 );
        }};
        addChild( maxSpinner );
    }
}