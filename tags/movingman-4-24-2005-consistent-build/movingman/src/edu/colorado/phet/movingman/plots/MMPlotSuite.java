/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.DefaultDecimalFormat;
import edu.colorado.phet.movingman.common.HelpItem2;
import edu.colorado.phet.movingman.model.MMTimer;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.view.GoPauseClearPanel;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;
import edu.colorado.phet.movingman.view.MovingManLayout;

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

public class MMPlotSuite extends GraphicLayerSet implements MovingManLayout.LayoutItem {
    private MMPlot plot;
    private PhetGraphic maximizeButton;
    private boolean plotVisible = true;
    private int plotInsetX = 100;
    private ArrayList listeners = new ArrayList();
    private PhetGraphic goPauseClearGraphic;
    private PhetGraphic textBoxGraphic;
    private TextBox textBox;
    private ShadowHTMLGraphic titleGraphic;
    private HelpItem2 sliderHelpItem;
    private GoPauseClearPanel goPauseClearPanel;
    private MovingManApparatusPanel movingManApparatusPanel;
    private MovingManModule module;
    private DefaultDecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );

    public MMPlotSuite( final MovingManModule module, MovingManApparatusPanel movingManApparatusPanel, final MMPlot plot ) {
        super( movingManApparatusPanel );
        this.module = module;
        this.movingManApparatusPanel = movingManApparatusPanel;
        this.plot = plot;
        plot.addListener( new PlotDeviceListenerAdapter() {
            public void minimizePressed() {
                setPlotVisible( false );
            }
        } );
        JButton maxButton = new JButton( plot.getName() );
        maxButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setPlotVisible( true );
            }
        } );
        maximizeButton = PhetJComponent.newInstance( movingManApparatusPanel, maxButton );
        maximizeButton.setVisible( false );
        addGraphic( plot );
//        plot.setLocation( 0,1);
        addGraphic( maximizeButton );
        goPauseClearPanel = new GoPauseClearPanel( movingManApparatusPanel.getModule() );
        goPauseClearGraphic = PhetJComponent.newInstance( movingManApparatusPanel, goPauseClearPanel );

        PhetGraphic goButtonGraphic = getGoButtonGraphic();
        addGraphic( goPauseClearGraphic );

        textBox = new TextBox( movingManApparatusPanel.getModule(), 4, plot.getVarname() + "=" );

        module.getTimeModel().getPlaybackTimer().addListener( new MMTimer.Listener() {
            public void timeChanged() {
                setPlaybackTime( module.getTimeModel().getPlaybackTimer().getTime() );
            }
        } );

        textBoxGraphic = PhetJComponent.newInstance( movingManApparatusPanel, textBox );
        addGraphic( textBoxGraphic );

        plot.dataSeriesAt( 0 ).getRawData().addObserver( new TimeSeries.Observer() {
            public void dataAdded( TimeSeries timeSeries ) {
                setTextBoxText( timeSeries.getLastPoint().getValue() );
            }

            public void cleared( TimeSeries timeSeries ) {
//                textBox.setText( "" );
            }
        } );

        titleGraphic = new ShadowHTMLGraphic( movingManApparatusPanel, plot.getName(),
//                                              new Font( "Lucida Sans", Font.BOLD, 12 ),
                                              MMFontManager.getFontSet().getTitleFont(),
                                              plot.dataSeriesAt( 0 ).getColor(), 1, 1, Color.black );
        addGraphic( titleGraphic );

        SliderHelpItem sliderHelpItem = new SliderHelpItem( movingManApparatusPanel, goButtonGraphic, this );
        addGraphic( sliderHelpItem );

//        plot.addListener( new PlotDeviceListenerAdapter() {
//            public void sliderDragged( double dragValue ) {
//                setTextBoxText( dragValue );
//                plot.setTextValue( dragValue );
//            }
//        } );
//        setBackground( Color.blue );
    }

    private void setTextBoxText( double value ) {
        String text = decimalFormat.format( value );
//        if( Double.parseDouble( text ) == 0 ) {
//            if( text.startsWith( "-" ) ) {
//                text = text.substring( 1 );
//            }
//        }
        textBox.setText( text );
    }

    private void setPlaybackTime( double time ) {
        if( plot.getNumDataSeries() > 0 ) {
            TimePoint tp = plot.dataSeriesAt( 0 ).getRawData().getValueForTime( time );
            setTextBoxText( tp.getValue() );
//            textBox.setText( decimalFormat.format( tp.getValue() ) );
        }
    }


    public boolean isPaused() {
        return movingManApparatusPanel.getModule().isPaused();
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

    private PhetGraphic getGoButtonGraphic() {
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

    public MMPlot getPlotDevice() {
        return plot;
    }

    public boolean isPlotVisible() {
        return plotVisible;
    }

    public Rectangle getBorderRectangle() {
        if( isPlotVisible() ) {
            setBoundsDirty();
            Rectangle rect = textBoxGraphic.getBounds();
//            rect = rect.union( plot.getBounds() );
            rect = rect.union( plot.getVisibleBounds() );
            rect = rect.intersection( new Rectangle( getComponent().getSize() ) );
            return rect;
        }
        else {
            return null;
        }
    }

    public void valueChanged( double value ) {
        plot.setTextValue( value );
        setTextBoxText( value );
    }

    public static interface Listener {
        void plotVisibilityChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void setPlotVisible( boolean plotVisible ) {
        if( plotVisible != this.plotVisible ) {
            this.plotVisible = plotVisible;
            plot.setVisible( plotVisible );
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

    public void setVerticalParameters( int y, int height ) {
        titleGraphic.setLocation( 2, y );
        textBoxGraphic.setLocation( 2, y + 20 );
        goPauseClearGraphic.setLocation( 10, textBoxGraphic.getHeight() + textBoxGraphic.getY() + 5 );

        if( getComponent().getWidth() > 0 && height > 0 ) {
            int plotInsetDX = 10;
            int offsetX = plot.getChartSlider().getWidth() * 2;
            int plotWidth = getComponent().getWidth() - textBoxGraphic.getWidth() - plotInsetDX * 2 - offsetX;
            plot.setChartSize( plotWidth, height - 12 );
            plot.setLocation( (int)( textBoxGraphic.getBounds().getMaxX() + plotInsetDX + offsetX ), y );
        }
        setBoundsDirty();
        autorepaint();
        notifyChanged();
    }


    public void setY( int y ) {
        maximizeButton.setLocation( plotInsetX, y );
    }
}
