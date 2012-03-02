// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.view.NumberLineNode.Horizontal;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.CakeIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.HorizontalBarIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.NumberLineIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.PieIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationControlPanel;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.VerticalBarIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.WaterGlassIcon;

import static edu.colorado.phet.fractionsintro.intro.view.Representation.*;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends AbstractFractionsCanvas {

    public FractionsIntroCanvas( final FractionsIntroModel model ) {

        final RepresentationControlPanel representationControlPanel = new RepresentationControlPanel( model.representation, new RepresentationIcon[] {
                new PieIcon( model.representation, LightGreen ),
                new HorizontalBarIcon( model.representation, LightGreen ),
                new VerticalBarIcon( model.representation, LightGreen ),
                new WaterGlassIcon( model.representation, LightGreen ),
                new CakeIcon( model.representation ),
                new NumberLineIcon( model.representation ),
        } ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() / 2, INSET );
        }};
        addChild( representationControlPanel );

        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( model.representation, PIE, new PieSetNode( model.pieSet, rootNode ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( model.representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode ) ) );

        //For vertical bars
        addChild( new RepresentationNode( model.representation, VERTICAL_BAR, new PieSetNode( model.verticalBarSet, rootNode ) ) );

        //For debugging water glasses region management
//        addChild( new RepresentationNode( model.representation, WATER_GLASSES, new PieSetNode( model.waterGlassSet, rootNode ) ) );

        //For water glasses
        final Rectangle2D b = model.factorySet.waterGlassSetFactory.createEmptyPies( 1, 1 ).head().cells.head().shape().getBounds2D();
        addChild( new RepresentationNode( model.representation, WATER_GLASSES, new WaterGlassSetNode( model.waterGlassSet, rootNode, LightGreen, b.getWidth(), b.getHeight() ) ) );

        //For draggable cakes
        addChild( new RepresentationNode( model.representation, CAKE, new CakeSetNode( model.cakeSet, rootNode ) ) );

        //For debugging cakes
//        addChild( new RepresentationNode( model.representation, CAKE, new PieSetNode( model.cakeSet, rootNode ) ) );

        //Number line
        addChild( new NumberLineNode( model.numerator, model.numerator, model.denominator, model.representation.valueEquals( NUMBER_LINE ), model.maximum, new Horizontal(), 32, LightGreen ) {{
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
        }};
        addChild( resetAllButtonNode );

        resetAllButtonNode.setOffset( STAGE_SIZE.width - resetAllButtonNode.getFullBounds().getWidth() - INSET, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET );

        //Spinner to change the maximum allowed value
        MaxSpinner maxSpinner = new MaxSpinner( model.maximum ) {{

            //Center above reset all button
            setOffset( ( STAGE_SIZE.getWidth() + representationControlPanel.getMaxX() ) / 2 - getFullWidth() / 2, representationControlPanel.getCenterY() - getFullHeight() / 2 );
        }};
        addChild( maxSpinner );
    }
}