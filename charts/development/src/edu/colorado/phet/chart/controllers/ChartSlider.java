/** Sam Reid*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.EventListener;

/**
 * User: Sam Reid
 * Date: Oct 18, 2004
 * Time: 10:35:14 PM
 * Copyright (c) Oct 18, 2004 by Sam Reid
 */
public class ChartSlider extends GraphicLayerSet {
    private Chart chart;
    private JSlider slider;
    private EventChannel eventChannel = new EventChannel( Listener.class );
    private Listener proxy = (Listener)eventChannel.getListenerProxy();
    private int numTicks = 1000;
    public int offsetX = 0;
    private boolean changed;
    private PhetJComponent sliderGraphic;
    private int preferredWidth;

    public ChartSlider( Component apparatusPanel, final Chart chart ) {
        super( apparatusPanel );
        this.chart = chart;
        slider = new JSlider( JSlider.VERTICAL, 0, numTicks, numTicks / 2 );
        slider.setOpaque( false );

        preferredWidth = slider.getPreferredSize().width;
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double modelValue = getValue();
                proxy.valueChanged( modelValue );
                changed = true;
            }
        } );
        sliderGraphic = new PhetJComponent( apparatusPanel, slider );
        addGraphic( sliderGraphic );
        updateLocation();
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLocation();
            }
        } );

    }

    public double getValue() {
        int value = slider.getValue();
        Rectangle r = chart.getChartBounds();
        Function.LinearFunction transform1d = new Function.LinearFunction( 0, numTicks, r.y + r.height, r.y );
        double output = transform1d.evaluate( value );
        double modelValue = chart.getModelViewTransform().viewToModelY( (int)output );
        return modelValue;
    }

    public void setOffsetX( int offsetX ) {
        this.offsetX = offsetX;
    }

    public void updateLocation() {
        Rectangle viewBounds = chart.getChartBounds();
        viewBounds.x -= chart.getVerticalTicks().getWidth() - 5;
        System.out.println( "viewBounds = " + viewBounds );
        int x = viewBounds.x;
        int y = viewBounds.y;
        Dimension iconDim = getIconDimension();

        int insetYGuess = iconDim.height / 2;
//        int insetYGuess = 0;
        int dx = iconDim.width;

        Rectangle newShape = new Rectangle( x - dx - offsetX, y - insetYGuess,
                                            slider.getPreferredSize().width, viewBounds.height + insetYGuess * 2 );
        slider.setPreferredSize( new Dimension( newShape.width, newShape.height ) );

        setLocation( newShape.x, newShape.y );
        sliderGraphic.repaint();
        System.out.println( "sliderGraphic = " + sliderGraphic.getBounds() );
    }

    public void setBounds( int x, int y, int width, int height ) {
        slider.setPreferredSize( new Dimension( width, height ) );
        setLocation( x, y );
        sliderGraphic.repaint();
    }

    private Dimension getIconDimension() {
        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
        String str = vert != null ? vert.getIconHeight() + "" : "null";
        System.out.println( "vert.getIconHeight() = " + str );
        if( vert == null ) {
            return new Dimension( preferredWidth, preferredWidth );
        }
        return new Dimension( vert.getIconWidth(), vert.getIconHeight() );
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
        sliderGraphic.repaint();
    }

    public boolean hasMoved() {
        return changed;
    }

    public interface Listener extends EventListener {
        public void valueChanged( double value );
    }

    public void addListener( Listener listener ) {
        eventChannel.addListener( listener );
    }

    public PhetGraphic getSliderGraphic() {
        return sliderGraphic;
    }

    public JSlider getSlider() {
        return slider;
    }
}
