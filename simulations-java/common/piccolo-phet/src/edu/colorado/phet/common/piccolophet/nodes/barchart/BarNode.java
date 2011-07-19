// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.barchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class depicts a single bar in a BarChartNode.
 * It is still under development and subject to change.
 *
 * @author Sam Reid
 */
public class BarNode extends PNode {
    private double scale;
    private double value;
    private int x;
    private int width;
    private int y;
    private VerticalShadowHTMLNode label;
    private PPath rectanglePath;
    private double labelWidth;

    public BarNode( String text, double scale, double value, int x, int width, int y, Color color, Font barFont ) {
        this.scale = scale;
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;

        rectanglePath = new PPath( null );
        rectanglePath.setPaint( color );
        rectanglePath.setStroke( new BasicStroke( 1 ) );
        rectanglePath.setStrokePaint( Color.black );

        label = new VerticalShadowHTMLNode( barFont, text, color, Color.black );
        addChild( rectanglePath );

        addChild( label );
        labelWidth = label.getWidth();
        updateBar();
    }

    private void updateBar() {
        double height = computeHeight();
        Rectangle2D.Double rect = new Rectangle2D.Double( x, y - height, width, height );
        label.setOffset( rect.x + 2 - labelWidth, (int) ( y + label.getFullBounds().getHeight() + 14 ) );
        rectanglePath.setPathTo( rect );
    }

    public void setValue( double value ) {
        if ( value != this.value ) {
            this.value = value;
            update();
        }
    }

    private void update() {
        if ( value < 0 ) {
            rectanglePath.setOffset( 0, -computeHeight() );//a big hack to make negative values work.
        }
        else {
            rectanglePath.setOffset( 0, 0 );
        }
        this.value = Math.abs( value );
        updateBar();
    }

    private double computeHeight() {
        return scale * value;
    }

    public void setBarHeight( double baselineY ) {
        this.y = (int) baselineY;
        updateBar();
    }

    public void setBarScale( double scale ) {
        if ( this.scale != scale ) {
            this.scale = scale;
            update();
        }
    }
}
