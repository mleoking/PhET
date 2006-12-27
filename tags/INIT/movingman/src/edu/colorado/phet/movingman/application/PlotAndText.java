package edu.colorado.phet.movingman.application;

import edu.colorado.phet.movingman.elements.BoxedPlot;
import edu.colorado.phet.movingman.elements.GridLineGraphic;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 12:22:35 AM
 * To change this template use Options | File Templates.
 */
public class PlotAndText {
    BoxedPlot plot;
    ValueGraphic text;
    GridLineGraphic grid;
    boolean visible = true;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        plot.setVisible(visible);
        text.setVisible(visible);
        grid.setVisible(visible);
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

    public PlotAndText(BoxedPlot plot, ValueGraphic text, GridLineGraphic grid) {
        this.plot = plot;
        this.text = text;
        this.grid = grid;
    }
}
