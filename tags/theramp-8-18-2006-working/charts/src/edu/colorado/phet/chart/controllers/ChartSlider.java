/** Sam Reid*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    private PhetGraphic sliderGraphic;
    private int preferredWidth;
    private ChartSliderUI sliderUI;

    public ChartSlider( Component apparatusPanel, final Chart chart, Color foregroundColor ) {
        this( apparatusPanel, chart, null, foregroundColor );
    }

    public ChartSlider( Component apparatusPanel, final Chart chart, String imageLoc, Color foregroundColor ) {
        super( apparatusPanel );
        this.chart = chart;
        slider = new JSlider( JSlider.VERTICAL, 0, numTicks, numTicks / 2 ) {
            public GraphicsConfiguration getGraphicsConfiguration() {
                return super.getGraphicsConfiguration();
            }
        };
        slider.setOpaque( false );

        preferredWidth = slider.getPreferredSize().width;

        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( isDragging() ) {
                    fireChange();
                }
            }
        } );
        BufferedImage image = null;
        if( imageLoc != null ) {

            try {
                image = ImageLoader.loadBufferedImage( imageLoc );
//            image = ImageLoader.loadBufferedImage( "images/thumb2.png" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        if( image != null ) {
            UIManager.put( "Slider.trackWidth", new Integer( image.getWidth() * 2 ) );
            UIManager.put( "Slider.majorTickLength", new Integer( 6 ) );
            UIManager.put( "Slider.highlight", Color.white );
            UIManager.put( "Slider.verticalThumbIcon", new ImageIcon( image ) );
            UIManager.put( "Slider.horizontalThumbIcon", new ImageIcon( image ) );

//        slider.setUI( new MetalSliderUI());
            sliderUI = new ChartSliderUI( this, image, foregroundColor );
        }
        slider.setUI( sliderUI );
//        sliderGraphic = new PhetJComponent( apparatusPanel, slider );
        sliderGraphic = PhetJComponent.newInstance( apparatusPanel, slider );
        addGraphic( sliderGraphic );
        updateLocation();
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLocation();
            }
        } );
    }

    private boolean isDragging() {
        return slider.getValueIsAdjusting();
    }

    private void fireChange() {
        double modelValue = getValue();
//        System.out.println( "ChartSlider.fireChange: " + modelValue );
        proxy.valueChanged( modelValue );
        changed = true;
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
//        System.out.println( "viewBounds = " + viewBounds );
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
//        System.out.println( "sliderGraphic = " + sliderGraphic.getBounds() );
    }

    public void setBounds( int x, int y, int width, int height ) {
        slider.setPreferredSize( new Dimension( width, height ) );
        setLocation( x, y );
        sliderGraphic.repaint();
    }

    private Dimension getIconDimension() {
        Icon vert = UIManager.getIcon( "Slider.verticalThumbIcon" );
        String str = vert != null ? vert.getIconHeight() + "" : "null";
//        System.out.println( "vert.getIconHeight() = " + str );
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
        int val = slider.getValue();
//        System.out.println( "val = " + val+", tick="+tick );
        boolean changed = false;
        if( val != tick ) {
//                    System.out.println( "val = " + val+", tick="+tick );
            slider.setValue( tick );
            changed = true;
        }
        for( int i = 0; i < s.length; i++ ) {
            ChangeListener changeListener = s[i];
            slider.addChangeListener( changeListener );
        }
        if( changed ) {
            sliderGraphic.repaint();
        }
    }

    public boolean hasMoved() {
        return changed;
    }

    public void setSelected( boolean selected ) {
        sliderUI.setSelected( selected );
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
