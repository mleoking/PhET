package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 8:59:23 PM
 */
public class ReadoutTitleNode extends PNode {
    //8-13-2007: Rotation sim's performance has 50% memory allocation and 30% processor usage in HTMLNode.update
    //Therefore, we just set the HTMLNode once, and update the text in piccolo without swing 
    private ShadowHTMLNode titleNode;
    private ShadowPText valueNode;

    private ControlGraphSeries series;
    private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
    private PhetPPath background;
    private double insetX = 2;
    private double insetY = 2;

    public ReadoutTitleNode( ControlGraphSeries series ) {
        this.series = series;

        titleNode = new ShadowHTMLNode();
        titleNode.setFont( getTitleFont() );
        titleNode.setColor( series.getColor() );

        valueNode = new ShadowPText();
        valueNode.setFont( getTitleFont() );
        valueNode.setTextPaint( series.getColor() );

        background = new PhetPPath( Color.white );
        addChild( background );
        addChild( titleNode );
        addChild( valueNode );
        background.translate( insetX, insetY );
        titleNode.translate( insetX, insetY );
        series.getSimulationVariable().addListener( new IVariable.Listener() {
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
        titleNode.setHtml( "<html>" + series.getAbbr() + "<sub>" + series.getCharacterName() + "</sub>= " );
//        Image im=titleNode.toImage();
//        PImage p=new PImage( im );
//        addChild( p);
        
        valueNode.setOffset( titleNode.getFullBounds().getWidth() + 3, 3 );
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
        valueNode.setText( valueText + " " + series.getUnits() );
        background.setPathTo( RectangleUtils.expand( titleNode.getFullBounds().createUnion( valueNode.getFullBounds() ), insetX, insetY ) );//todo: avoid setting identical shapes here for performance considerations
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
