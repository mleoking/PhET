/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.movingman.common.RangeToRange;
import edu.colorado.phet.movingman.plots.MMPlot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 11:09:56 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class MovingManLayout {
    private int walkwayHeight = 100;
    private int topInset = 20;
    private int walkwayBottomInset = 55;
    private int spaceBetweenPlots = 20;
    private int plotInsetX = 90;

    private MMVerticalLayout verticalLayout;
    private MovingManModule module;
    private JComponent panel;
    private ChartLayoutItem layoutItemX;
    private ChartLayoutItem layoutItemV;
    private ChartLayoutItem layoutItemA;

    public MovingManLayout( MovingManModule module ) {
        this.module = module;
        this.panel = module.getApparatusPanel();

        verticalLayout = new MMVerticalLayout( panel );
        verticalLayout.addSpacer( walkwayHeight );
        verticalLayout.addSpacer( walkwayBottomInset );
        verticalLayout.addSpacer( topInset );
        verticalLayout.addSpacer( spaceBetweenPlots );
        layoutItemX = new ChartLayoutItem( panel, module.getPositionPlot() );
        verticalLayout.addLayoutItem( layoutItemX );
        verticalLayout.addSpacer( spaceBetweenPlots );
        layoutItemV = new ChartLayoutItem( panel, module.getVelocityPlot() );
        verticalLayout.addLayoutItem( layoutItemV );
        verticalLayout.addSpacer( spaceBetweenPlots );
        layoutItemA = new ChartLayoutItem( panel, module.getAccelerationPlot() );
        verticalLayout.addLayoutItem( layoutItemA );
        verticalLayout.addSpacer( spaceBetweenPlots );
        relayout();
    }

    class ChartLayoutItem implements LayoutItem {
        private MMPlot plot;
        private JComponent component;
        private int plotInsetRight = 5;
        private MMPlot.ChartButton chartButton;

        public ChartLayoutItem( JComponent component, MMPlot plot ) {
            this.component = component;
            this.plot = plot;
            this.chartButton = plot.getShowButton();
        }

        public boolean isVariable() {
            if( plot.isVisible() ) {
                return true;
            }
            else {
                return false;
            }
        }

        public int getHeight() {
            return chartButton.getPreferredSize().height;
        }

        public void setVerticalParameters( int y, int height ) {
            if( component.getWidth() > 0 && height > 0 ) {
                int width = component.getWidth() - plotInsetX - plotInsetRight;
                plot.setViewBounds( plotInsetX, y, width, height );
            }
        }

        public void setY( int y ) {
            chartButton.setLocation( chartButton.getX(), y );
        }

//        public void relayout() {
//            if( !plot.isVisible() ) {
//                component.add( chartButton );
//            }
//            else {
//                component.remove( chartButton );
//            }
//        }
    }

    static class MMVerticalLayout {
        ArrayList layoutItems = new ArrayList();
        Component component;

        public MMVerticalLayout( Component component ) {
            this.component = component;
        }

        public void addLayoutItem( LayoutItem layoutItem ) {
            layoutItems.add( layoutItem );
        }

        public void clear() {
            layoutItems.clear();
        }

        public int getConstantHeight() {
            int size = 0;
            for( int i = 0; i < layoutItems.size(); i++ ) {
                LayoutItem layoutItem = (LayoutItem)layoutItems.get( i );
                if( layoutItem.isVariable() ) {

                }
                else {
                    size += layoutItem.getHeight();
                }
            }
            return size;
        }

        /**
         * Shares the remaining space equally among all VariableLayoutItems.
         */
        public void layout() {
            int ch = getConstantHeight();
            int numVariableItems = numVariableItems();

            int availableHeight = component.getHeight() - ch;
            int heightPerVariableItem = 0;
            if( numVariableItems > 0 ) {
                heightPerVariableItem = availableHeight / numVariableItems;
            }

            int y = 0;
            for( int i = 0; i < layoutItems.size(); i++ ) {
                LayoutItem layoutItem = (LayoutItem)layoutItems.get( i );
                if( layoutItem.isVariable() ) {
                    layoutItem.setVerticalParameters( y, heightPerVariableItem );
                    y += heightPerVariableItem;
                }
                else {
                    layoutItem.setY( y );
                    y += layoutItem.getHeight();
                }
            }
        }

        private int numVariableItems() {
            int num = 0;
            for( int i = 0; i < layoutItems.size(); i++ ) {
                LayoutItem layoutItem = (LayoutItem)layoutItems.get( i );
                if( layoutItem.isVariable() ) {
                    num++;
                }
            }
            return num;
        }

        public void addSpacer( int height ) {
            addLayoutItem( new Spacer( height ) );
        }
    }

    static interface LayoutItem {
        public boolean isVariable();

        public int getHeight();

        public void setVerticalParameters( int y, int height );

        public void setY( int y );
    }

    static class Spacer implements LayoutItem {
        private int height;

        public Spacer( int height ) {
            this.height = height;
        }

        public boolean isVariable() {
            return false;
        }

        public int getHeight() {
            return height;
        }

        public void setVerticalParameters( int y, int height ) {
        }

        public void setY( int y ) {
        }

    }

    public void relayout() {
        verticalLayout.layout();
        int manInset = 50;
        RangeToRange oldTransform = module.getManPositionTransform();
        RangeToRange manGraphicTransform = new RangeToRange( oldTransform.getLowInputPoint(), oldTransform.getHighInputPoint(), manInset, panel.getWidth() - manInset );
        module.getManGraphic().setTransform( manGraphicTransform );
        module.setManTransform( manGraphicTransform );
    }

}
