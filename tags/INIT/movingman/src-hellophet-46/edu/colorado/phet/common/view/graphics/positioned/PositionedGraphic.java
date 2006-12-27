package edu.colorado.phet.common.view.graphics.positioned;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 5:26:55 PM
 * To change this template use Options | File Templates.
 */
public interface PositionedGraphic {
    /**Paints centered at the specified positioned.*/
    public void paint(Graphics2D g, int x, int y);
}
