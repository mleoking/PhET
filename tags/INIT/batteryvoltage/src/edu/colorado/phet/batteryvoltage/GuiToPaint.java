package edu.colorado.phet.batteryvoltage;

import electron.paint.Painter;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jan 18, 2003
 * Time: 5:53:35 PM
 * To change this template use Options | File Templates.
 */
public class GuiToPaint implements Painter {
    electron.gui.Painter painter;

    public GuiToPaint( electron.gui.Painter painter ) {
        this.painter = painter;
    }

    public void paint( Graphics2D graphics2D ) {
        painter.paint( graphics2D );
    }
}
