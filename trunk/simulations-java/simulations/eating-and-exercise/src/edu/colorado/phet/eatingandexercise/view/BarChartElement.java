package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by: Sam
 * Aug 18, 2008 at 10:13:26 AM
 */
public class BarChartElement {
    private String name;
    private Paint paint;
    private double value;
    private BufferedImage image;
    private Color textColor;

    public BarChartElement( String name, Paint paint, double value ) {
        this( name, paint, value, null );
    }

    public BarChartElement( String name, Paint paint, double value, BufferedImage image, Color textColor ) {
        this.name = name;
        this.paint = paint;
        this.value = value;
        this.image = image;
        this.textColor = textColor;
    }

    public BarChartElement( String name, Paint paint, double value, BufferedImage image ) {
        this( name, paint, value, image, Color.black );
    }

    public Paint getPaint() {
        return paint;
    }

    public double getValue() {
        return value;
    }

    public void setValue( double value ) {
        this.value = value;
        notifyListener();
    }

    public String getName() {
        return name;
    }

    public void setPaint( Paint color ) {
        this.paint = color;
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.paintChanged();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public Color getTextColor() {
        return textColor;
    }

    public static interface Listener {
        void valueChanged();

        void paintChanged();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).valueChanged();
        }
    }
}
