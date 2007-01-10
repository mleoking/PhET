/** Sam Reid*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.math.Function;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 18, 2004
 * Time: 10:35:14 PM
 * Copyright (c) Oct 18, 2004 by Sam Reid
 */
public class VerticalChartSlider {
    private Chart chart;
    private JSlider slider;
    private ArrayList listeners = new ArrayList();
    private boolean visible = false;
    private int numTicks = 1000;
    public int offsetX = 0;
    private boolean changed;

    public VerticalChartSlider( final Chart chart ) {
        this.chart = chart;
        slider = new JSlider( JSlider.VERTICAL, 0, numTicks, numTicks / 2 );
        Rectangle viewBounds = chart.getChartBounds();
        int x = viewBounds.x;
        int y = viewBounds.y;
        slider.setLocation( x, y );
        setVisible( true );

//        Dimension dim = getIconDimension();
//        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
//        int insetGuess = dim.height / 2;
//        int dx = dim.width;
//        slider.reshape( x - dx, y - insetGuess, slider.getPreferredSize().width, viewBounds.height + insetGuess * 2 );
        slider.setBackground( new Color( 255, 255, 255, 0 ) );
        slider.setOpaque( false );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double modelValue = getValue();
                for( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener)listeners.get( i );
                    listener.valueChanged( modelValue );
                }
                changed = true;
            }
        } );
        update();
    }

    public double getValue() {
        int value = slider.getValue();
        Rectangle r = chart.getChartBounds();
        Function.LinearFunction transform1d = new Function.LinearFunction( 0, numTicks, r.y + r.height, r.y );
//        LinearTransform1d transform1d = new LinearTransform1d( 0, numTicks, r.y + r.height, r.y );
        double output = transform1d.evaluate( value );
        double modelValue = chart.getModelViewTransform().viewToModelY( (int)output );
        return modelValue;
    }

    public void setOffsetX( int offsetX ) {
        this.offsetX = offsetX;
    }

    public void update() {
        Rectangle viewBounds = chart.getBounds();
        int x = viewBounds.x;
        int y = viewBounds.y;
//        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
        Dimension dim = getIconDimension();
        int insetYGuess = dim.height / 2;
        //TODO this crashed once with vert=null.
        int dx = dim.width;
        slider.reshape( x - dx - offsetX, y - insetYGuess, slider.getPreferredSize().width, viewBounds.height + insetYGuess * 2 );
    }

    private Dimension getIconDimension() {
        int preferredWidth = slider.getPreferredSize().width;
        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
        if( vert == null ) {
            return new Dimension( preferredWidth, preferredWidth );
        }
        return new Dimension( vert.getIconWidth(), vert.getIconHeight() );
    }

    public void setVisible( boolean b ) {
        if( b != visible ) {
            visible = b;
            if( visible ) {
                JComponent jc = (JComponent)chart.getComponent();
                slider.setVisible( visible );
                jc.add( slider );
            }
            else {
                JComponent jc = (JComponent)chart.getComponent();

                jc.remove( slider );
                slider.setVisible( visible );
            }
        }
    }

    public void setValue( double y ) {
        double viewY = chart.getModelViewTransform().modelToViewY( y );
        Rectangle r = chart.getChartBounds();
        Function.LinearFunction transform1d = new Function.LinearFunction( r.y + r.height, r.y, 0, numTicks );
        int tick = (int)transform1d.evaluate( viewY );
        ChangeListener[] s = slider.getChangeListeners();
        for( int i = 0; i < s.length; i++ ) {
            ChangeListener changeListener = s[i];
            slider.removeChangeListener( changeListener );
        }
        slider.setValue( tick );
        for( int i = 0; i < s.length; i++ ) {
            ChangeListener changeListener = s[i];
            slider.addChangeListener( changeListener );
        }
    }

    public boolean hasMoved() {
        return changed;
    }

    public interface Listener {
        public void valueChanged( double value );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public JSlider getSlider() {
        return slider;
    }
}
