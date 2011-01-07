// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.quantumwaveinterference.view.colorgrid.ColorGrid;
import edu.colorado.phet.quantumwaveinterference.view.colorgrid.ColorMap;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Dec 8, 2005
 * Time: 12:42:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColorGridNode extends PImage {
    private ColorGrid colorGrid;

    public ColorGridNode( ColorGrid colorGrid ) {
        this.colorGrid = colorGrid;
    }

    public void setGridDimensions( int nx, int ny ) {
        colorGrid.setModelSize( nx, ny );
    }

    public void paint( ColorMap colorMap ) {
        colorGrid.colorize( colorMap );
        setImage( colorGrid.getBufferedImage() );
        repaint();
    }

    public BufferedImage getBufferedImage() {
        return colorGrid.getBufferedImage();
    }

    public int getCellWidth() {
        return colorGrid.getCellWidth();
    }

    public int getCellHeight() {
        return colorGrid.getCellHeight();
    }

    public ColorGrid getColorGrid() {
        return colorGrid;
    }

    public void setCellDimensions( int dx, int dy ) {
        colorGrid.setCellDimensions( dx, dy );
    }
}
