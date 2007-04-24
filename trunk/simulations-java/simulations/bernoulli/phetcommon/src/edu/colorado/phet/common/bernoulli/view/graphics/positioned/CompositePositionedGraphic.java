/*, 2003.*/
package edu.colorado.phet.common.bernoulli.view.graphics.positioned;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 21, 2003
 * Time: 7:53:40 PM
 *
 */
public class CompositePositionedGraphic implements PositionedGraphic {
    ArrayList a = new ArrayList();

    public void addLocalPaintable(PositionedGraphic lp) {
        a.add(lp);
    }

    public int numPaints() {
        return a.size();
    }

    public PositionedGraphic paintAt(int i) {
        return (PositionedGraphic) a.get(i);
    }

    public void paint(Graphics2D g, int x, int y) {
        for (int i = 0; i < numPaints(); i++) {
            paintAt(i).paint(g, x, y);
        }
    }
}
