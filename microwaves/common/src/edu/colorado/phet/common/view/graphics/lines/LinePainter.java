package edu.colorado.phet.common.view.graphics.lines;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 18, 2003
 * Time: 12:30:02 AM
 * Copyright (c) Apr 18, 2003 by Sam Reid
 */
public interface LinePainter {
    public void drawLine(Graphics2D g, int x, int y, int x2, int y2);
}
