/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.components;

import edu.colorado.phet.common.view.ApparatusPanel;

import javax.swing.*;
import java.awt.*;

/**
 * This LayoutManager maintains the specified aspectRatio for the target component.
 * <p/>
 * Here's an example usage:
 * AspectRatioLayout cl = new AspectRatioLayout(apparatusPanel, insetX, insetY, aspectRatio);
 * container.setLayout(cl);
 * container.add(apparatusPanel);
 */
class AspectRatioLayout implements LayoutManager {
    Component target;
    private int insetX;
    private int insetY;
    double aspectRatio;

    public AspectRatioLayout( Component target, int insetX, int insetY ) {
        this( target, insetX, insetY, 1 );
    }

    public AspectRatioLayout( Component target, int insetX, int insetY, double aspectRatio ) {
        if( target == null ) {
            throw new RuntimeException( "Null target component." );
        }
        this.aspectRatio = aspectRatio; //TODO account for asr=width/height.
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
        double availAR = availWidth / availHeight;
        if( availAR == aspectRatio ) {
            return new Dimension( (int)availWidth, (int)availHeight );
        }
        else if( availAR > aspectRatio ) {
            //width is too big
            double reducedWidth = availHeight * aspectRatio;
            return new Dimension( (int)reducedWidth, (int)availHeight );
        }
        else {

            double reducedHeight = availWidth / aspectRatio;
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

    public static void main( String[] args ) {
        final JPanel container = new JPanel();
        final ApparatusPanel apparatusPanel = new ApparatusPanel();
        final int insetY = 30;
        final int insetX = 30;
        apparatusPanel.setBackground( Color.green );

        container.setBackground( Color.yellow );
        double aspectRatio = 1.5;//height/width

        AspectRatioLayout cl = new AspectRatioLayout( apparatusPanel, insetX, insetY, aspectRatio );
        container.setLayout( cl );
        container.add( apparatusPanel );

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( container );
        jf.pack();
        jf.setVisible( true );
    }

    public void setAspectRatio( double aspectRatio ) {
        this.aspectRatio = aspectRatio;
    }
}

