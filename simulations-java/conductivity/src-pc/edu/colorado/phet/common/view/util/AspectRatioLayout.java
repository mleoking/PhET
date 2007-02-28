/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.util;

import edu.colorado.phet.common.view.ApparatusPanel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 5, 2004
 * Time: 12:14:40 PM
 * Copyright (c) Jan 5, 2004 by Sam Reid
 */
public class AspectRatioLayout implements LayoutManager {
    Component target;
    private int insetX;
    private int insetY;
    double aspectRatio;

    public AspectRatioLayout( Component target, int insetX, int insetY ) {
        this( target, insetX, insetY, 1 );
    }

    public AspectRatioLayout( Component target, int insetX, int insetY, double aspectRatio ) {
        this.aspectRatio = aspectRatio;
        this.target = target;
        this.insetX = insetX;
        this.insetY = insetY;
    }

    public void addLayoutComponent( String name, Component comp ) {
    }

    public void removeLayoutComponent( Component comp ) {
    }

    /**
     * The code for aspect ratio =1 is slightly clearer.
     * <p/>
     * int width = parent.getWidth();
     * int height = parent.getHeight();
     * <p/>
     * int min = Math.min(width, height);
     * int desiredWidth = min - insetX * 2;
     * int desiredHeight = min - insetY * 2;
     * return new Dimension(desiredWidth, desiredHeight);
     */
    public Dimension preferredLayoutSize( Container parent ) {
        //find out the constraint.
        double availWidth = parent.getWidth() - insetX * 2;
        double availHeight = parent.getHeight() - insetY * 2;
        double availAR = availHeight / availWidth;
        if( availAR == aspectRatio ) {
            return new Dimension( (int)availWidth, (int)availHeight );
        }
        else if( availAR < aspectRatio ) {
            //width is too high

            double reducedWidth = availHeight / aspectRatio;
            return new Dimension( (int)reducedWidth, (int)availHeight );
        }
        else {

            double reducedHeight = availWidth * aspectRatio;
            return new Dimension( (int)availWidth, (int)reducedHeight );
        }
    }

    public Dimension minimumLayoutSize( Container parent ) {
        return new Dimension();
    }

    public void layoutContainer( Container parent ) {
        Dimension des = preferredLayoutSize( parent );
        int desiredWidth = des.width;
        int desiredHeight = des.height;
        target.setSize( desiredWidth, desiredHeight );

        int x = ( parent.getWidth() - desiredWidth ) / 2;
        int y = ( parent.getHeight() - desiredHeight ) / 2;
        target.setLocation( x, y );
    }

}

