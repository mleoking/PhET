/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
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

public class MinimizablePlot extends GraphicLayerSet implements MovingManLayout.LayoutItem {
    private PlotDevice plot;
    private PhetGraphic maximizeButton;
    private boolean plotVisible = true;
    private int plotInsetX = 100;
    private int plotInsetRight = 13;
    private ArrayList listeners = new ArrayList();

    public MinimizablePlot( Component component, final MMPlot plot ) {
        super( component );
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
        maximizeButton = PhetJComponent.newInstance( component, maxButton );
        maximizeButton.setVisible( false );
        addGraphic( plot );
        addGraphic( maximizeButton );
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
        if( getComponent().getWidth() > 0 && height > 0 ) {
            int width = getComponent().getWidth() - plotInsetX - plotInsetRight - 30;
            plot.setChartViewBounds( plotInsetX, y, width, height - 12 );
        }
    }

    public void setY( int y ) {
        maximizeButton.setLocation( plotInsetX, y );
    }
}
