package edu.colorado.phet.movingman.view;

import java.awt.*;

import edu.colorado.phet.common.motion.charts.TemporalChart;
import edu.colorado.phet.common.motion.charts.TextBox;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This is the panel to the left side of a chart with readouts and controls pertaining to the chart variables.
 *
 * @author Sam Reid
 */
public class MovingManChartControl extends PNode {
    private static final PhetFont TITLE_FONT = new PhetFont( 16, true );
    public static final PhetFont TEXT_BOX_FONT = new PhetFont( 14 );
    private static final PhetFont UNITS_FONT = TEXT_BOX_FONT;

    public MovingManChartControl( String title, Color color, TextBoxListener textBoxDecorator, final TemporalChart chart, String units ) {
        PText titleNode = new PText( title );
        {
            titleNode.setFont( TITLE_FONT );
            titleNode.setTextPaint( color );
        }
        this.addChild( titleNode );

        final TextBox textBox = new TextBox( TEXT_BOX_FONT );
        {
            textBoxDecorator.addListeners( textBox );
            textBox.setOffset( 15, titleNode.getFullBounds().getHeight() );
        }
        this.addChild( textBox );

        PText unitsReadout = new PText( units );
        {
            unitsReadout.setFont( UNITS_FONT );
            unitsReadout.setTextPaint( color );
            unitsReadout.setOffset( textBox.getFullBounds().getMaxX() + 2, textBox.getFullBounds().getCenterY() - unitsReadout.getFullBounds().getHeight() / 2 );
        }
        this.addChild( unitsReadout );
    }
}
