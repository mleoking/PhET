/*PhET, 2004.*/
package edu.colorado.phet.movingman;

//import edu.colorado.phet.common.math.transforms.functions.RangeToRange;

import edu.colorado.phet.movingman.common.math.RangeToRange;
import edu.colorado.phet.movingman.plots.BoxedPlot;
import edu.colorado.phet.movingman.plots.CursorGraphic;
import edu.colorado.phet.movingman.plots.PlotAndText;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 11:09:56 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class MovingManLayout {
    private int panelHeight;
    private int panelWidth;
    private int numPlots;
    private int walkwayHeight = 100;
    private int topInset = 20;
    private int walkwayBottomInset = 55;
    private int spaceBetweenPlots = 20;
    private int bottomInset = 20;
    private int offsetIntoPlotForString = 40;
    private int textOffsetX = 50;
    private int plotHeight;
    private int plotsStartAtY;
    private int plotInsetX = 90;

    public void setApparatusPanelHeight( int panelHeight ) {
        this.panelHeight = panelHeight;
    }

    public void setApparatusPanelWidth( int panelWidth ) {
        this.panelWidth = panelWidth;
    }

    public void setNumPlots( int numPlots ) {
        this.numPlots = numPlots;
    }

    public int getPlotY( int plotIndex ) {
        if( plotIndex == 0 ) {
            return plotsStartAtY;
        }
        else {
            return ( plotsStartAtY + plotIndex * ( plotHeight + spaceBetweenPlots ) );
        }
    }

    public int getPlotHeight() {
        return plotHeight;
    }

    public void relayout() {

        int heightForPlots = panelHeight - walkwayHeight - walkwayBottomInset - topInset - bottomInset;
        int numPlotSpacers = numPlots - 1;
        if( numPlots == 0 ) {
            this.plotHeight = heightForPlots;
        }
        else {
            this.plotHeight = ( heightForPlots - numPlotSpacers * spaceBetweenPlots ) / numPlots;
        }
        plotsStartAtY = walkwayHeight + topInset + walkwayBottomInset;

    }

    public int getTotalPlotHeight() {
        return numPlots * ( plotHeight + spaceBetweenPlots );
    }

    public Point getTextCoordinates( int index ) {
        int y = getPlotY( index );
        return new Point( textOffsetX + plotInsetX, y + this.offsetIntoPlotForString );
    }

    public void relayout( MovingManModule module ) {
        JPanel app = module.getApparatusPanel();
        int panelHeight = app.getHeight();
        setApparatusPanelHeight( panelHeight );
        int panelWidth = app.getWidth();
        setApparatusPanelWidth( app.getWidth() );
        relayout();

        //time scales as width.

        PlotAndText pos = module.getPositionPlot();
        PlotAndText vel = module.getVelocityPlot();
//        PlotAndText acc = module.getAccelerationPlot();
        BoxedPlot smoothedPositionGraphic = module.getPositionGraphic();
        BoxedPlot velocityGraphic = module.getVelocityGraphic();
        BoxedPlot accelGraphic = module.getAccelerationGraphic();

        int index = 0;
        relayoutBoxedPlot( smoothedPositionGraphic, index, module, module.getPositionString() );
        if( pos.isVisible() ) {
            index++;
        }
        relayoutBoxedPlot( velocityGraphic, index, module, module.getVelocityString() );
        if( vel.isVisible() ) {
            index++;
        }
        relayoutBoxedPlot( accelGraphic, index, module, module.getAccelString() );

        CursorGraphic cursorGraphic = module.getCursorGraphic();
        cursorGraphic.setBounds( smoothedPositionGraphic.getTransform() );
        cursorGraphic.setHeight( getTotalPlotHeight() );

        int manInset = 50;
        RangeToRange oldTransform = module.getManPositionTransform();
        RangeToRange manGraphicTransform = new RangeToRange( oldTransform.getLowInputPoint(), oldTransform.getHighInputPoint(), manInset, panelWidth - manInset );
        module.getManGraphic().setTransform( manGraphicTransform );
        module.setManTransform( manGraphicTransform );
    }

    private void relayoutBoxedPlot( BoxedPlot plot, int index, MovingManModule module, ValueGraphic vg ) {
        int insetX = plotInsetX;
        int insetXRightSide = 20;

        Rectangle rectangle = new Rectangle( insetX, getPlotY( index ), panelWidth - insetX - insetXRightSide, getPlotHeight() );
        if( rectangle.width > 0 && rectangle.height > 0 ) {

            plot.setViewBounds( rectangle );
            Point textCoord = getTextCoordinates( index );
            vg.setPosition( textCoord.x, textCoord.y );
        }
    }
}
