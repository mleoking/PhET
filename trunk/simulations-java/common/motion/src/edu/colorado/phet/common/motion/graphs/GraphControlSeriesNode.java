// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.graphs;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 11:47:17 AM
 */
public class GraphControlSeriesNode extends PNode {
    private ShadowPText shadowPText;
    private PSwing textBox;
    private GraphControlTextBox boxGraphControl;
    public static final Font LABEL_FONT = new PhetFont( Font.BOLD, 18 );

    public GraphControlSeriesNode( ControlGraphSeries series ) {
        shadowPText = new ShadowPText( series.getTitle() );
        shadowPText.setFont( LABEL_FONT );
        shadowPText.setTextPaint( series.getColor() );
        shadowPText.setShadowColor( Color.black );
        addChild( shadowPText );

        boxGraphControl = createGraphControlTextBox( series );
        textBox = new PSwing( boxGraphControl );
        addChild( textBox );
    }

    public void addListener( GraphControlTextBox.Listener listener ) {
        boxGraphControl.addListener( listener );
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
