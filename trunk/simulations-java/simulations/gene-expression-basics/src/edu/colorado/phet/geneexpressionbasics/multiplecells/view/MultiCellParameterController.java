// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control panel that allows the user to alter various parameters of cell
 * operation within a population of cells.
 *
 * @author John Blanco
 */
public class MultiCellParameterController extends PNode {

    private static final Font TITLE_LABEL_FONT = new PhetFont( 16, true );
    private static final Color BACKGROUND_COLOR = new Color( 162, 205, 90 );

    public MultiCellParameterController( MultipleCellsModel model ) {
        // Create the content for this panel.
        PNode content = new VBox(
                20,
                new PText( "Cell Parameters" ) {{setFont( TITLE_LABEL_FONT );}},
                new IntegerParameterSliderNode( CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMin(),
                                                CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMax(),
                                                model.transcriptionFactorLevel,
                                                "<center>Transcription Factor<br>Level</center>" ),
                new DoubleParameterSliderNode( CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                               model.transcriptionFactorAssociationProbability,
                                               "<center>Transcription Factor<br>Affinity</center>" ),
                new DoubleParameterSliderNode( CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                               model.polymeraseAssociationProbability,
                                               "<center>Polymerase<br>Affinity</center>" ),
                new DoubleParameterSliderNode( CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMax(),
                                               model.proteinDegradationRate,
                                               "<center>Protein<br>Degradation Rate</center>" )
        );

        // Add the content to a control panel.
        addChild( new ControlPanelNode( content, BACKGROUND_COLOR ) );
    }

    // Class that defines the slider and its labels.
    private static class DoubleParameterSliderNode extends PNode {

        private static final Font TITLE_FONT = new PhetFont( 14 );
        private static final double SLIDER_TRACK_WIDTH = 100;
        private static final double SLIDER_TRACK_HEIGHT = 4;
        private static final Font LABEL_FONT = new PhetFont( 12 );

        private DoubleParameterSliderNode( double min, double max, final SettableProperty<Double> settableProperty, String htmlLabelText ) {

            // Create the label.
            PNode labelNode = new HTMLNode( htmlLabelText ) {{
                setFont( TITLE_FONT );
            }};

            // Create the slider node.
            HSliderNode sliderNode = new HSliderNode( min, max, SLIDER_TRACK_WIDTH, SLIDER_TRACK_HEIGHT, settableProperty, new BooleanProperty( true ) ) {
                @Override protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
                    return Color.BLACK;
                }
            };

            // Add the labels to the slider node.
            // TODO: i18n
            sliderNode.addLabel( min, new PText( "Low" ) {{
                setFont( LABEL_FONT );
            }} );
            sliderNode.addLabel( max, new PText( "High" ) {{
                setFont( LABEL_FONT );
            }} );

            // Add the label and slider to a vertical box.
            addChild( new VBox( 0, labelNode, sliderNode ) );
        }
    }

    // Class that wraps DoubleParameterSliderNode in such a way that it can be
    // used to control an integer property.
    private static class IntegerParameterSliderNode extends PNode {

        private IntegerParameterSliderNode( int min, int max, final SettableProperty<Integer> settableProperty, String htmlLabelText ) {

            // Create a property of type double and hook it to the integer
            // property.
            Property<Double> doubleProperty = new Property<Double>( (double) settableProperty.get() );
            doubleProperty.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    settableProperty.set( (int) Math.round( value ) );
                }
            } );

            // Create the slider node.
            addChild( new DoubleParameterSliderNode( min, max, doubleProperty, htmlLabelText ) );
        }
    }
}
