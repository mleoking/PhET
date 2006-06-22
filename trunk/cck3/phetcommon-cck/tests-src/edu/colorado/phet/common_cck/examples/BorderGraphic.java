/**
 * Class: BorderGraphic
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 26, 2004
 */
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.view.graphics.Graphic;

import javax.swing.border.Border;
import java.awt.*;

public class BorderGraphic implements Graphic {
    Border border;
    Component c;
    int x;
    int y;
    int width;
    int height;

    public BorderGraphic( Border border, Component c, int x, int y, int width, int height ) {
        this.border = border;
        this.c = c;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setBorder( Border border ) {
        this.border = border;
    }

    public void setX( int x ) {
        this.x = x;
    }

    public void setY( int y ) {
        this.y = y;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    public void paint( Graphics2D g ) {
        border.paintBorder( c, g, x, y, width, height );
    }

    public static void main( String[] args ) {
        //        BorderGraphic bg=new BorderGraphic( BorderFactory.createTitledBorder( "My PhET frame"),apparatus,0,0,100,100);

    }
}
