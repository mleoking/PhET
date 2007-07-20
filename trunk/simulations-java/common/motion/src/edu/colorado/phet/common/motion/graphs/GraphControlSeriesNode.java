package edu.colorado.phet.common.motion.graphs;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.motion.model.ISimulationVariable;

import java.awt.*;

/**
 * Author: Sam Reid
* Jul 20, 2007, 11:47:17 AM
*/
public class GraphControlSeriesNode extends PNode {
    private ShadowPText shadowPText;
    private PSwing textBox;
    private GraphControlTextBox boxGraphControl;

    public GraphControlSeriesNode( String title, String abbr, Color color, ISimulationVariable simulationVariable ) {
        shadowPText = new ShadowPText( title );
        Font labelFont = new Font( "Lucida Sans", Font.BOLD, 14 );
        shadowPText.setFont( labelFont );
        shadowPText.setTextPaint( color );
        shadowPText.setShadowColor( Color.black );
        addChild( shadowPText );

        boxGraphControl = new GraphControlTextBox( abbr, simulationVariable, color );
        textBox = new PSwing( boxGraphControl );
        addChild( textBox );
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
