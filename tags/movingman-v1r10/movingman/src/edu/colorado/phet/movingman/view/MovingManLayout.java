/*PhET, 2004.*/
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.movingman.MMUtil;
import edu.colorado.phet.movingman.common.LinearTransform1d;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 11:09:56 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class MovingManLayout {
    private int walkwayHeight = 150;
    private int topInset = 20;
    private int walkwayBottomInset = 0;
    private int spaceBetweenPlots = MMUtil.isHighScreenResolution() ? 10 : 10;

    private MMVerticalLayout verticalLayout;
    private MovingManApparatusPanel movingManApparatusPanel;
    private LayoutItem layoutItemX;
    private LayoutItem layoutItemV;
    private LayoutItem layoutItemA;
    private int walkwayInsetX = 55;

    public MovingManLayout( MovingManApparatusPanel movingManApparatusPanel ) {
        this.movingManApparatusPanel = movingManApparatusPanel;

        verticalLayout = new MMVerticalLayout( this.movingManApparatusPanel );
        verticalLayout.addSpacer( walkwayHeight );
        verticalLayout.addSpacer( walkwayBottomInset );
        verticalLayout.addSpacer( topInset );
        verticalLayout.addSpacer( spaceBetweenPlots );
        layoutItemX = movingManApparatusPanel.getPlotSet().getPositionPlotSuite();
        verticalLayout.addLayoutItem( layoutItemX );
        verticalLayout.addSpacer( spaceBetweenPlots );
        layoutItemV = movingManApparatusPanel.getPlotSet().getVelocityPlotSuite();
        verticalLayout.addLayoutItem( layoutItemV );
        verticalLayout.addSpacer( spaceBetweenPlots );
        layoutItemA = movingManApparatusPanel.getPlotSet().getAccelerationPlotSuite();
        verticalLayout.addLayoutItem( layoutItemA );
        verticalLayout.addSpacer( spaceBetweenPlots );
        relayout();
    }

    public int getWalkWayInsetX() {
        return walkwayInsetX;
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

        public int getConstantHeight() {
            int size = 0;
            for( int i = 0; i < layoutItems.size(); i++ ) {
                LayoutItem layoutItem = (LayoutItem)layoutItems.get( i );
                if( layoutItem.isVariable() ) {
                }
                else {
                    size += layoutItem.getLayoutHeight();
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
                    y += layoutItem.getLayoutHeight();
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

    public static interface LayoutItem {
        public boolean isVariable();

        public int getLayoutHeight();

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

        public int getLayoutHeight() {
            return height;
        }

        public void setVerticalParameters( int y, int height ) {
        }

        public void setY( int y ) {
        }
    }

    public void relayout() {
        verticalLayout.layout();
        LinearTransform1d oldTransform = movingManApparatusPanel.getManPositionTransform();
        LinearTransform1d manGraphicTransform = new LinearTransform1d( oldTransform.getMinInput(), oldTransform.getMaxInput(),
                                                                       walkwayInsetX, movingManApparatusPanel.getWidth() - walkwayInsetX );
        movingManApparatusPanel.setManTransform( manGraphicTransform );
        movingManApparatusPanel.getManGraphic().setY( walkwayHeight - movingManApparatusPanel.getManGraphic().getHeight() );
        double outputRange = manGraphicTransform.getMaxOutput() - manGraphicTransform.getMinOutput();
        movingManApparatusPanel.getWalkwayGraphic().setSize( (int)outputRange + 200, walkwayHeight );
        movingManApparatusPanel.repaintBackground();
    }

}
