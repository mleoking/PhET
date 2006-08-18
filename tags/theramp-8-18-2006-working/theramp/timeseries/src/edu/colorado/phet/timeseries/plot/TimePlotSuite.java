/* Copyright 2004, Sam Reid */
package edu.colorado.phet.timeseries.plot;

import edu.colorado.phet.common.util.DefaultDecimalFormat;
import edu.colorado.phet.common.view.help.HelpItem3;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicCriteria;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.timeseries.TimePoint;
import edu.colorado.phet.timeseries.TimeSeries;
import edu.colorado.phet.timeseries.TimeSeriesModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 4, 2005
 * Time: 9:28:16 PM
 * Copyright (c) Apr 4, 2005 by Sam Reid
 */

public class TimePlotSuite extends GraphicLayerSet {
    private TimePlot timePlot;
    private PhetGraphic maximizeButton;
    private boolean plotVisible = true;
    private int plotInsetX = 100;
    private ArrayList listeners = new ArrayList();
    private PhetGraphic goPauseClearGraphic;
    private PhetGraphic textBoxGraphic;
    private TextBox textBox;
    private ShadowHTMLGraphic titleGraphic;
    private HelpItem3 sliderHelpItem;
    private GoPauseClearPanel goPauseClearPanel;
    private DefaultDecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
    private TimeSeriesModel module;

    public TimePlotSuite( final TimeSeriesModel module, Component component, final TimePlot timePlot ) {
        super( component );
        this.module = module;
        this.timePlot = timePlot;
        timePlot.addListener( new PlotDeviceListenerAdapter() {
            public void minimizePressed() {
                setPlotVisible( false );
            }
        } );
        JButton maxButton = new JButton( timePlot.getName() );
        maxButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setPlotVisible( true );
            }
        } );
        maximizeButton = PhetJComponent.newInstance( component, maxButton );
        maximizeButton.setVisible( false );
        addGraphic( timePlot );
        addGraphic( maximizeButton );
        goPauseClearPanel = new GoPauseClearPanel( module );
        goPauseClearGraphic = PhetJComponent.newInstance( component, goPauseClearPanel );
        addGraphic( goPauseClearGraphic );

        textBox = new TextBox( module, 4, timePlot.getVarName() + "=" );

        module.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                setPlaybackTime( module.getPlaybackTime() );
            }
        } );

        textBoxGraphic = PhetJComponent.newInstance( component, textBox );
        addGraphic( textBoxGraphic );

        if( timePlot.numPlotDeviceData() > 0 ) {
            timePlot.dataSeriesAt( 0 ).getRawData().addObserver( new TimeSeries.Observer() {
                public void dataAdded( TimeSeries timeSeries ) {
                    setTextBoxText( timeSeries.getLastPoint().getValue() );
                    notifyValueChanged( timeSeries.getLastPoint().getValue() );
                }

                public void cleared( TimeSeries timeSeries ) {
                }
            } );

        }
        titleGraphic = new ShadowHTMLGraphic( component, timePlot.getName(),
                                              getTitleFont(),
                                              getTitleColor(), 1, 1, Color.black );
        addGraphic( titleGraphic );
        titleGraphic.setLocation( 2, 0 );

        textBoxGraphic.setLocation( 2, 20 );
        goPauseClearGraphic.setLocation( 10, textBoxGraphic.getHeight() + textBoxGraphic.getY() + 5 );
    }

    private Color getTitleColor() {
        if( timePlot.numPlotDeviceData() > 0 ) {
            return timePlot.dataSeriesAt( 0 ).getColor();
        }
        else {
            return Color.blue;
        }
    }

    private Font getTitleFont() {
        return new Font( "Lucida Sans", Font.BOLD, 14 );
    }

    private void setTextBoxText( double value ) {
        String text = decimalFormat.format( value );
        textBox.setText( text );
    }

    public PhetGraphic getTextBoxGraphic() {
        return textBoxGraphic;
    }

    private void setPlaybackTime( double time ) {
        if( timePlot.getNumDataSeries() > 0 ) {
            TimePoint tp = timePlot.dataSeriesAt( 0 ).getRawData().getValueForTime( time );
            setTextBoxText( tp.getValue() );//todo factor into listener system.
            notifyValueChanged( tp.getValue() );
        }
    }

    private void notifyValueChanged( double value ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.valueChanged( value );
        }
    }

    public boolean isPaused() {
        return module.isPaused();
    }

    private PhetGraphic search( PhetGraphic source, GraphicCriteria criteria ) {
        if( criteria.isSatisfied( source ) ) {
            return source;
        }
        else if( source instanceof GraphicLayerSet ) {
            GraphicLayerSet gls = (GraphicLayerSet)source;
            PhetGraphic[] children = gls.getGraphics();
            for( int i = 0; i < children.length; i++ ) {
                PhetGraphic child = children[i];
                PhetGraphic result = search( child, criteria );
                if( result != null ) {
                    return result;
                }
            }
        }
        return null;
    }

    public PhetGraphic getGoButtonGraphic() {
        GraphicCriteria gc = new GraphicCriteria() {
            public boolean isSatisfied( PhetGraphic phetGraphic ) {
                if( phetGraphic instanceof PhetJComponent ) {
                    PhetJComponent pj = (PhetJComponent)phetGraphic;
                    if( pj.getSourceComponent() == goPauseClearPanel.getGoPauseButton() ) {
                        return true;
                    }
                }
                return false;
            }
        };
        return search( goPauseClearGraphic, gc );
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public TimePlot getPlotDevice() {
        return timePlot;
    }

    public boolean isPlotVisible() {
        return plotVisible;
    }

    public Rectangle getBorderRectangle() {
        if( isPlotVisible() ) {
            setBoundsDirty();
            Rectangle rect = textBoxGraphic.getBounds();
            rect = rect.union( timePlot.getVisibleBounds() );
            rect = rect.intersection( new Rectangle( getComponent().getSize() ) );
            return rect;
        }
        else {
            return null;
        }
    }

    public void valueChanged( double value ) {
        timePlot.setTextValue( value );
        setTextBoxText( value );
        notifyValueChanged( value );
    }

    public void reset() {
        timePlot.reset();
        valueChanged( 0.0 );
    }

    public void addPlotDeviceData( PlotDeviceSeries plotDeviceData ) {
        getPlotDevice().addPlotDeviceData( plotDeviceData );
    }

    public static interface Listener {
        void plotVisibilityChanged();

        void valueChanged( double value );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setPlotVisible( boolean plotVisible ) {
        if( plotVisible != this.plotVisible ) {
            this.plotVisible = plotVisible;
            timePlot.setVisible( plotVisible );
            goPauseClearGraphic.setVisible( plotVisible );
            textBoxGraphic.setVisible( plotVisible );
            titleGraphic.setVisible( plotVisible );

            maximizeButton.setVisible( !plotVisible );

            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.plotVisibilityChanged();
            }
            fireVisibilityChanged();
        }
    }

    public boolean isVariable() {
        return plotVisible;
    }

    public int getLayoutHeight() {
        return maximizeButton.getHeight();
    }

    public void setSize( int width, int height ) {
//        int plotDXInternal=timePlot.getBounds().x-timePlot.
        int decorationInsetX = timePlot.getDecorationInsetX();
        int plotInsetDX = 10;
        int offsetX = (int)Math.max( textBoxGraphic.getBounds().getMaxX(), goPauseClearGraphic.getBounds().getMaxX() );
        int plotWidth = width - offsetX - plotInsetDX * 2 - decorationInsetX;
        timePlot.setChartSize( plotWidth, height );


//        System.out.println( "bounds = " + bounds );
        timePlot.setLocation( plotInsetDX + offsetX + decorationInsetX, 0 );
        setBoundsDirty();
        autorepaint();
        notifyChanged();
    }

    public void setPlotVerticalParameters( int y, int height ) {
        int insetX = 10;
        setSize( getComponent().getWidth() - insetX * 2, height );
        setLocation( insetX, y );
    }

    public void setButtonY( int y ) {
        maximizeButton.setLocation( plotInsetX, y );
    }

    public void paint( Graphics2D g2 ) {
        super.paint( g2 );
    }
}
