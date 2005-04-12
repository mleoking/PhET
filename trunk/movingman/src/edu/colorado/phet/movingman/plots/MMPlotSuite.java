/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.movingman.common.HelpItem2;
import edu.colorado.phet.movingman.model.TimeListenerAdapter;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.view.GoPauseClearPanel;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;
import edu.colorado.phet.movingman.view.MovingManLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 4, 2005
 * Time: 9:28:16 PM
 * Copyright (c) Apr 4, 2005 by Sam Reid
 */

public class MMPlotSuite extends GraphicLayerSet implements MovingManLayout.LayoutItem {
    private PlotDevice plot;
    private PhetGraphic maximizeButton;
    private boolean plotVisible = true;
    private int plotInsetX = 100;
    private int plotInsetRight = 13;
    private ArrayList listeners = new ArrayList();
    private PhetGraphic goPauseClearGraphic;
    private PhetGraphic textBoxGraphic;
    private TextBox textBox;
    private ShadowHTMLGraphic titleGraphic;
    private HelpItem2 sliderHelpItem;
    private GoPauseClearPanel goPauseClearPanel;
    private MovingManApparatusPanel movingManApparatusPanel;

    public MMPlotSuite( MovingManApparatusPanel movingManApparatusPanel, final MMPlot plot ) {
        super( movingManApparatusPanel );
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
        addGraphic( maximizeButton );
        goPauseClearPanel = new GoPauseClearPanel( movingManApparatusPanel.getModule() );
        goPauseClearGraphic = PhetJComponent.newInstance( movingManApparatusPanel, goPauseClearPanel );

        PhetGraphic goButtonGraphic = getGoButtonGraphic();
        addGraphic( goPauseClearGraphic );

        textBox = new TextBox( movingManApparatusPanel.getModule(), 4, plot.getVarname() + "=" );
        textBoxGraphic = PhetJComponent.newInstance( movingManApparatusPanel, textBox );
        addGraphic( textBoxGraphic );
        final DecimalFormat df = new DecimalFormat( "0.0#" );
        plot.dataSeriesAt( 0 ).getRawData().addObserver( new TimeSeries.Observer() {
            public void dataAdded( TimeSeries timeSeries ) {
                textBox.setText( df.format( timeSeries.getLastPoint().getValue() ) );
            }

            public void cleared( TimeSeries timeSeries ) {
//                textBox.setText( "" );
            }
        } );

        titleGraphic = new ShadowHTMLGraphic( movingManApparatusPanel, plot.getName(),
                                              new Font( "Lucida Sans", Font.BOLD, 12 ), plot.dataSeriesAt( 0 ).getColor(), 1, 1, Color.black );
        addGraphic( titleGraphic );
        plot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                if( isPaused() ) {
                    showSliderHelp();
                }
            }
        } );
        sliderHelpItem = new HelpItem2( movingManApparatusPanel, "Press Go!" );
        sliderHelpItem.setVisible( false );

        sliderHelpItem.pointLeftAt( goButtonGraphic, 30 );
        addGraphic( sliderHelpItem );
        movingManApparatusPanel.getModule().getMovingManModel().getTimeModel().addListener( new TimeListenerAdapter() {
            public void recordingStarted() {
                hideGoHelp();
            }

            public void playbackStarted() {
                hideGoHelp();
            }

            public void rewind() {
                hideGoHelp();
            }

            public void reset() {
                hideGoHelp();
            }
        } );
//        movingManApparatusPanel.getModule().getMovingManModel().getTimeModel().addListener( new TimeListenerAdapter() {
//            public void recordingPaused() {
//                focusTextBox();
//            }
//
//            public void playbackPaused() {
//                focusTextBox();
//            }
//
//            public void rewind() {
//                focusTextBox();
//            }
//
//            public void reset() {
//                focusTextBox();
//            }
//        } );
    }
//
//    private void focusTextBox() {
////        textBox.setEnabled( false );
//        PhetGraphic textGraphic = search( textBoxGraphic, new GraphicCriteria() {
//            public boolean isSatisfied( PhetGraphic phetGraphic ) {
//                return phetGraphic
//            }
//        } );
//        textBox.set
//    }

    private boolean isPaused() {
        return movingManApparatusPanel.getModule().isPaused();
    }

    public void hideGoHelp() {//todo call this function.
        sliderHelpItem.setVisible( false );
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
                    if( pj.getSourceComponent() == goPauseClearPanel.getGoButton() ) {
                        return true;
                    }
                }
                return false;
            }
        };
        return search( goPauseClearGraphic, gc );
    }

    private void showSliderHelp() {
        sliderHelpItem.setVisible( true );
    }

    public TextBox getTextBox() {
        return textBox;
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
            int offsetX = plot.getSlider().getWidth() * 2;
//            int offsetX = plot.getSlider().getBounds().x-plot.getChart().getBounds().x;
            int plotWidth = getComponent().getWidth() - textBoxGraphic.getWidth() - plotInsetDX * 2 - offsetX;
            plot.setChartSize( plotWidth, height - 12 );
            plot.setLocation( (int)( textBoxGraphic.getBounds().getMaxX() + plotInsetDX + offsetX ), y );
        }
    }

    public void setY( int y ) {
        maximizeButton.setLocation( plotInsetX, y );
    }
}
