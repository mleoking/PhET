package edu.colorado.phet.common.view.graphics.positioned;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 6:14:53 PM
 * To change this template use Options | File Templates.
 */
public class PositionedRect3DGraphic implements PositionedGraphic {
    int width;
    int height;
    Color c;
    boolean raised;

    public PositionedRect3DGraphic( int width, int height, Color c, boolean raised ) {
        this.width = width;
        this.height = height;
        this.c = c;
        this.raised = raised;
    }

    public void paint( Graphics2D g, int x, int y ) {
        int left = x - width / 2;
        int top = y - height / 2;
        g.setColor( c );
        g.fill3DRect( left, top, width, height, raised );
    }

    public Rectangle getRectangle( int x, int y ) {
        int left = x - width / 2;
        int top = y - height / 2;

        return new Rectangle( left, top, width, height );
    }
}
