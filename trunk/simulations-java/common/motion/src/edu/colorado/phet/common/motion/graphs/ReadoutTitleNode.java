package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 8:59:23 PM
 */
public class ReadoutTitleNode extends PNode {
    private ShadowHTMLNode titlePText;
    private ControlGraphSeries series;
    private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
    private PhetPPath background;
    private double insetX = 2;
    private double insetY = 2;

    public ReadoutTitleNode( ControlGraphSeries series ) {
        this.series = series;
        titlePText = new ShadowHTMLNode();
        titlePText.setFont( getTitleFont() );
        titlePText.setColor( series.getColor() );
//        background = new PhetPPath( null, Color.white, new BasicStroke(), Color.black );
        background = new PhetPPath( Color.white );
        addChild( background );
        addChild( titlePText );
        background.translate( insetX, insetY );
        titlePText.translate( insetX, insetY );
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
        setValueText( decimalFormat.format( getValueToDisplay() ) );
    }

    private void setValueText( String valueText ) {
        titlePText.setHtml( "<html>" + series.getAbbr() + "<sub>" + series.getCharacterName() + "</sub>=" + valueText + " " + series.getUnits() );
        background.setPathTo( RectangleUtils.expand( titlePText.getFullBounds(), insetX, insetY ) );//todo: avoid setting identical shapes here for performance considerations
    }

    public double getPreferredWidth() {
        setValueText( "MMM.MM" );
        double width = getFullBounds().getWidth();
        updateText();
        return width;
    }

    protected double getValueToDisplay() {
        return series.getSimulationVariable().getValue();
    }

}
