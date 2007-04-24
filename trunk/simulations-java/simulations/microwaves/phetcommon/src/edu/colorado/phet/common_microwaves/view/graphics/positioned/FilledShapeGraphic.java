/*, 2003.*/
package edu.colorado.phet.common_microwaves.view.graphics.positioned;

import edu.colorado.phet.common_microwaves.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 8, 2003
 * Time: 11:53:10 PM
 *
 */
public class FilledShapeGraphic implements Graphic {
    Shape shape;
    private Paint color;

    public FilledShapeGraphic(Shape shape,Paint paint) {
        this.shape = shape;
        this.color = paint;
    }

    public void paint(Graphics2D g) {
        g.setPaint(color);
        g.fill(shape);
    }

    public void setShape( Shape shape ) {
        this.shape = shape;
    }
}
