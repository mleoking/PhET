package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 8:59:23 PM
 */
public class ReadoutTitleNode extends PNode {
    private ShadowPText titlePText;
    private ControlGraphSeries series;
    private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
    private PhetPPath background;

    public ReadoutTitleNode( ControlGraphSeries series ) {
        this.series = series;
        titlePText = new ShadowPText();
        titlePText.setFont( getTitleFont() );
        titlePText.setTextPaint( series.getColor() );
        background = new PhetPPath( null, Color.white, new BasicStroke(), Color.black );
        addChild( background );
        addChild( titlePText );
        series.getSimulationVariable().addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                updateText();
            }
        } );
        series.addListener( new ControlGraphSeries.Adapter() {
            public void unitsChanged() {
                updateText();
            }
        } );
        updateText();
    }

    private Font getTitleFont() {
        if( Toolkit.getDefaultToolkit().getScreenSize().width <= 1024 ) {
            return new Font( "Lucida Sans", Font.BOLD, 11 );
        }
        else {
            return new Font( "Lucida Sans", Font.BOLD, 14 );
        }
    }

    public ControlGraphSeries getSeries() {
        return series;
    }

    protected void updateText() {
        String valueText = decimalFormat.format( getValueToDisplay() );
        while( valueText.length() < "-10.00".length() ) {
            valueText = " " + valueText;
        }
        titlePText.setText( series.getTitle() + " " + valueText + " " + series.getUnits() );
        background.setPathTo( RectangleUtils.expand( titlePText.getFullBounds(), 2, 2 ) );//todo: avoid setting identical shapes here for performance considerations
    }

    protected double getValueToDisplay() {
        return series.getSimulationVariable().getValue();
    }

}
