package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.movingman.ValueGraphic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 12:22:35 AM
 * To change this template use Options | File Templates.
 */
public class PlotAndText {
    private MMPlot plot;
    private ValueGraphic text;
    private boolean visible = true;

    public PlotAndText( final MMPlot plot, ValueGraphic text ) {
        this.plot = plot;
        this.text = text;
        plot.setCloseHandler( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                plot.getModule().setPlotVisible( PlotAndText.this, false );
            }
        } );
        plot.setValueGraphic( text );
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        plot.setVisible( visible );
        text.setVisible( visible );
    }

    public MMPlot getPlot() {
        return plot;
    }

    public ValueGraphic getText() {
        return text;
    }

    public void setPaintYLines( double[] doubles ) {
        plot.setPaintYLines( doubles );
    }

    public void updateSlider() {
        plot.updateSlider();
    }

    public void cursorMovedToTime( double time ) {
        plot.cursorMovedToTime( time );
    }

    public void setCursorVisible( boolean visible ) {
        plot.setCursorVisible( visible );
    }
}
