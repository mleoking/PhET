package edu.colorado.phet.forces1d.charts.controllers;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.forces1d.phetcommon.math.Function;
import edu.colorado.phet.forces1d.phetcommon.util.EventChannel;
import edu.colorado.phet.forces1d.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.forces1d.phetcommon.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.charts.Chart;

/**
 * User: Sam Reid
 * Date: Oct 18, 2004
 * Time: 10:35:14 PM
 */
public class VerticalChartSlider2 extends GraphicLayerSet {
    private Chart chart;
    private JSlider slider;
    private EventChannel eventChannel = new EventChannel( Listener.class );
    private Listener proxy = (Listener) eventChannel.getListenerProxy();
    private int numTicks = 1000;
    public int offsetX = 0;
    private boolean changed;
    private PhetGraphic sliderGraphic;
    private int preferredWidth;

    public VerticalChartSlider2( ApparatusPanel apparatusPanel, final Chart chart ) {
        super( apparatusPanel );
        this.chart = chart;
        slider = new JSlider( JSlider.VERTICAL, 0, numTicks, numTicks / 2 );

        preferredWidth = slider.getPreferredSize().width;
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double modelValue = getValue();
                proxy.valueChanged( modelValue );
                changed = true;
            }
        } );
        sliderGraphic = PhetJComponent.newInstance( apparatusPanel, slider );
        addGraphic( sliderGraphic );
        update();
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                update();
            }
        } );
    }

    public double getValue() {
        int value = slider.getValue();
        Rectangle r = chart.getViewBounds();
        Function.LinearFunction transform1d = new Function.LinearFunction( 0, numTicks, r.y + r.height, r.y );
        double output = transform1d.evaluate( value );
        double modelValue = chart.getModelViewTransform().viewToModelY( (int) output );
        return modelValue;
    }

    public void setOffsetX( int offsetX ) {
        this.offsetX = offsetX;
    }

    public void update() {
        Rectangle viewBounds = chart.getViewBounds();
        int x = viewBounds.x;
        int y = viewBounds.y;
        Dimension iconDim = getIconDimension();

        int insetYGuess = iconDim.height / 2;
        int dx = iconDim.width;

        Rectangle newShape = new Rectangle( x - dx - offsetX, y - insetYGuess, slider.getPreferredSize().width, viewBounds.height + insetYGuess * 2 );
        slider.setPreferredSize( new Dimension( newShape.width, newShape.height ) );
        slider.setOpaque( false );
        setLocation( newShape.x, newShape.y );
        sliderGraphic.repaint();
    }

    private Dimension getIconDimension() {
        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
        if ( vert == null ) {
            return new Dimension( preferredWidth, preferredWidth );
        }
        return new Dimension( vert.getIconWidth(), vert.getIconHeight() );
    }

    public void setValue( double y ) {
        double viewY = chart.getModelViewTransform().modelToViewY( y );
        Rectangle r = chart.getViewBounds();
        Function.LinearFunction transform1d = new Function.LinearFunction( r.y + r.height, r.y, 0, numTicks );
        int tick = (int) transform1d.evaluate( viewY );
        ChangeListener[] s = slider.getChangeListeners();
        for ( int i = 0; i < s.length; i++ ) {
            ChangeListener changeListener = s[i];
            slider.removeChangeListener( changeListener );
        }
        slider.setValue( tick );
        for ( int i = 0; i < s.length; i++ ) {
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
