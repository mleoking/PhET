/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 4, 2003
 * Time: 10:40:01 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.graphics.util;

import java.awt.*;
import java.awt.geom.Point2D;

public class ShadowedHelpText extends HelpItem {

    public ShadowedHelpText( String text, int x, int y, Color color ) {
        super( text, x, y, color );
    }

    public ShadowedHelpText( String text, Point2D.Float location, Color color ) {
        super( text, location, color );
    }

    public void paint( Graphics2D g ) {

        Paint paint = getColor();
        g.setPaint( Color.black );
        g.drawString( getText(),
                      (int)getLocation().getX() + 1,
                      (int)getLocation().getY() + 2 );

        // Draw the text
        super.paint( g );
    }
}
