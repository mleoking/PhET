/** Sam Reid*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.math.LinearTransform1d;

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

    public VerticalChartSlider( final Chart chart ) {
        this.chart = chart;
        final int numTicks = 1000;
        slider = new JSlider( JSlider.VERTICAL, 0, numTicks, numTicks / 2 );
        Rectangle viewBounds = chart.getViewBounds();
        int x = viewBounds.x;
        int y = viewBounds.y;
        slider.setLocation( x, y );

        setVisible( true );
//        if( component instanceof JComponent ) {
//            JComponent jc = (JComponent)component;
//            jc.add( slider );

        update();
        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
        int insetGuess = vert.getIconHeight() / 2;
        int dx = vert.getIconWidth();
        slider.reshape( x - dx, y - insetGuess, slider.getPreferredSize().width, viewBounds.height + insetGuess * 2 );
        slider.setBackground( new Color( 255, 255, 255, 0 ) );
        slider.setOpaque( false );
//            slider.setSnapToTicks( true );

        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = slider.getValue();
                Rectangle r = chart.getViewBounds();
                LinearTransform1d transform1d = new LinearTransform1d( 0, numTicks, r.y + r.height, r.y );
                double output = transform1d.operate( value );
                double modelValue = chart.getTransform().viewToModelY( (int)output );
                for( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener)listeners.get( i );
                    listener.valueChanged( modelValue );
                }
            }
        } );
    }

    public void update() {
        Rectangle viewBounds = chart.getViewBounds();
        int x = viewBounds.x;
        int y = viewBounds.y;
        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
        int insetGuess = vert.getIconHeight() / 2;
        int dx = vert.getIconWidth();
        slider.reshape( x - dx, y - insetGuess, slider.getPreferredSize().width, viewBounds.height + insetGuess * 2 );
    }

    public void setVisible( boolean b ) {
        if( b != visible ) {
            visible = b;
            if( visible ) {
                JComponent jc = (JComponent)chart.getComponent();
                jc.add( slider );
            }
            else {
                JComponent jc = (JComponent)chart.getComponent();
                jc.remove( slider );
            }
        }
//        JComponent jc = (JComponent)component;
//        jc.add( slider );
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
