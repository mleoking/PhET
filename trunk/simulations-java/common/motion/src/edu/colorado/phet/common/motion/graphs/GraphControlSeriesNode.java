package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.*;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 11:47:17 AM
 */
public class GraphControlSeriesNode extends PNode {
    private ShadowPText shadowPText;
    private PSwing textBox;
    private GraphControlTextBox boxGraphControl;

    public GraphControlSeriesNode( ControlGraphSeries series ) {
        shadowPText = new ShadowPText( series.getTitle() );
        Font labelFont = new Font( "Lucida Sans", Font.BOLD, 14 );
        shadowPText.setFont( labelFont );
        shadowPText.setTextPaint( series.getColor() );
        shadowPText.setShadowColor( Color.black );
        addChild( shadowPText );

        boxGraphControl = createGraphControlTextBox( series );
        textBox = new PSwing( boxGraphControl );
        addChild( textBox );
    }

    protected GraphControlTextBox createGraphControlTextBox( ControlGraphSeries series ) {
        return new GraphControlTextBox( series );
    }

    public void relayout( double dy ) {
        shadowPText.setOffset( 0, 0 );
        textBox.setOffset( 0, shadowPText.getFullBounds().getMaxY() + dy );
    }

    public void setEditable( boolean editable ) {
        boxGraphControl.setEditable( editable );
    }

    public GraphControlTextBox getTextBox() {
        return boxGraphControl;
    }
}
