package edu.colorado.phet.common.view.graphics.positioned;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 6:14:53 PM
 * To change this template use Options | File Templates.
 */
public class CenteredCircleGraphic implements PositionedGraphic {
    int width;
    int height;
    Color c;

    public CenteredCircleGraphic( int width, int height, Color c ) {
        this.width = width;
        this.height = height;
        this.c = c;
    }

    public void paint( Graphics2D g, int x, int y ) {
        int left = x - width / 2;
        int top = y - height / 2;
        g.setColor( c );
        g.fillOval( left, top, width, height );
    }

    public void setColor( Color color ) {
        this.c = color;
    }

}
