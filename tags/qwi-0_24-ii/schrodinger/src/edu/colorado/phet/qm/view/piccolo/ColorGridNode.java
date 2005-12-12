package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.qm.view.colormaps.ColorGrid;
import edu.colorado.phet.qm.view.colormaps.ColorMap;
import edu.umd.cs.piccolo.nodes.PImage;

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
}
