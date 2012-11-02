// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.SlopeModel;
import edu.colorado.phet.linegraphing.common.view.EquationControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for interacting with a line's equation in slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeEquationControls extends EquationControls {

    public SlopeEquationControls( SlopeModel model, LineFormsViewProperties viewProperties ) {
        this( model.interactiveLine, model.savedLines, model.x1Range, model.y1Range,
              viewProperties.interactiveEquationVisible, viewProperties.linesVisible );
    }

    public SlopeEquationControls( Property<Line> interactiveLine, ObservableList<Line> savedLines,
                                  Property<DoubleRange> xRange, Property<DoubleRange> yRange,
                                  Property<Boolean> maximized, Property<Boolean> linesVisible ) {
        super( SlopeEquationNode.createGeneralFormNode(),
               interactiveLine,
               savedLines,
               maximized,
               linesVisible,
               new SlopeEquationNode( interactiveLine, xRange, yRange,
                                      LGConstants.INTERACTIVE_EQUATION_FONT, LGConstants.STATIC_EQUATION_FONT,
                                      LGColors.STATIC_EQUATION_ELEMENT ) );
    }

    // test
    public static void main( String[] args ) {

        // model properties
        Property<Line> interactiveLine = new Property<Line>( new Line( 1, 3, 2, 4, Color.BLACK ) );
        ObservableList<Line> savedLines = new ObservableList<Line>();
        Property<DoubleRange> xRange = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );
        Property<DoubleRange> yRange = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );
        Property<Boolean> maximized = new Property<Boolean>( true );
        Property<Boolean> linesVisible = new Property<Boolean>( true );

        // control panel
        PNode controlPanelNode = new SlopeEquationControls( interactiveLine, savedLines, xRange, yRange, maximized, linesVisible );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );
        canvas.getLayer().addChild( controlPanelNode );
        controlPanelNode.setOffset( 100, 100 );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
