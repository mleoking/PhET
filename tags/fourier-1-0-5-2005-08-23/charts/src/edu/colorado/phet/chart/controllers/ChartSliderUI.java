package edu.colorado.phet.chart.controllers;

// MyMetalScrollBarUI.java
// A simple extension of MetalScrollBarUI that draws the thumb as a solid
// black rectangle.
//

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ChartSliderUI extends MetalSliderUI {
    private ChartSlider chartSlider;
    private BufferedImage image;
    private ImageIcon icon;
    private Color foregroundColor;
    private Border lineBorder;

    // Create our own scrollbar UI!
    public ChartSliderUI( ChartSlider chartSlider, BufferedImage image, Color foregroundColor ) {
        this.chartSlider = chartSlider;
        this.image = image;
        this.foregroundColor = foregroundColor;
        this.icon = new ImageIcon( image );
        lineBorder = new DottedLineBorder( foregroundColor, new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{5, 10}, 0 ) );
    }


    public void paintThumb( Graphics g ) {
        Rectangle knobBounds = thumbRect;

        g.translate( knobBounds.x, knobBounds.y );

        if( slider.getOrientation() == JSlider.HORIZONTAL ) {
            icon.paintIcon( slider, g, 0, 0 );
        }
        else {
            icon.paintIcon( slider, g, 0, 0 );
        }

        g.translate( -knobBounds.x, -knobBounds.y );
    }

    public void setSelected( boolean selected ) {
        if( selected ) {
//            slider.setBackground( foregroundColor );
            slider.setBorder( lineBorder );
        }
        else {
//            slider.setBackground( new PhetLookAndFeel().getBackgroundColor() );
            slider.setBorder( null );
        }
    }
}