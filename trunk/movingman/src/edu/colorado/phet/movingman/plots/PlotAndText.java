package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.movingman.ValueGraphic;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 12:22:35 AM
 * To change this template use Options | File Templates.
 */
public class PlotAndText {
    private BoxedPlot plot;
    private ValueGraphic text;
    private GridLineGraphic grid;
    private boolean visible = true;

    public PlotAndText( BoxedPlot plot, ValueGraphic text, GridLineGraphic grid ) {
        this.plot = plot;
        this.text = text;
        this.grid = grid;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        plot.setVisible( visible );
        text.setVisible( visible );
        grid.setVisible( visible );
    }

    public BoxedPlot getPlot() {
        return plot;
    }

    public ValueGraphic getText() {
        return text;
    }

    public GridLineGraphic getGrid() {
        return grid;
    }

}
