/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.view;

import edu.colorado.phet.common.examples.hellophet.model.Message;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.view.graphics.positioned.CenteredCircleGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 8:38:25 PM
 * Copyright (c) May 18, 2003 by Sam Reid
 */
public class MessageView implements InteractiveGraphic, ObservingGraphic {
    private DragHandler dragHandler;
    Message m;
    int painty;
    int paintx;
    private BaseModel model;
    double yoffset;
    Font f = new Font("dialog", 0, 24);
    String str = "Hello there.";
    private FontRenderContext frc;
    boolean init = false;
    CenteredCircleGraphic circleGraphic = new CenteredCircleGraphic(40, 40, Color.blue);

    public MessageView(BaseModel model, Message m, int yoffset) {
        this.model = model;
        this.yoffset = yoffset;
        this.m = m;
        m.addObserver(this);
    }

    public synchronized void paint(Graphics2D graphics2D) {
        if (!init)
            return;//we haven't got an layoutPane event yet, so cannot paint yet.
        /**We could rewrite to fire an layoutPane event in the construction of the part of the model that this observes...
         * Or, just call m.updateObservers() right now..?  (I have a bad feeling about this one.)
         */
        if (frc == null)
            frc = graphics2D.getFontRenderContext();
        Rectangle2D bounds = getTextBounds();
        graphics2D.setColor(Color.green);
        graphics2D.fillRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());

        graphics2D.setColor(Color.blue);
        graphics2D.setFont(f);
        graphics2D.drawString(str, paintx, painty);
        circleGraphic.paint(graphics2D, paintx, painty);
    }

    public synchronized void update(Observable o, Object arg) {
        Message sm = (Message) o;
        this.painty = (int) (sm.getY() + yoffset);
        this.paintx = (int) sm.getX();
        init = true;
    }

    Rectangle2D getTextBounds() {
        if (frc == null) {
            return new Rectangle2D.Double();
        }
        Rectangle2D bounds = f.getStringBounds(str, frc);
        bounds = new Rectangle2D.Double(bounds.getX() + paintx, bounds.getY() + painty, bounds.getWidth(), bounds.getHeight());
        return bounds;
    }

    public boolean canHandleMousePress(MouseEvent event) {
        Rectangle2D bounds = getTextBounds();
        return bounds.contains(event.getX(), event.getY());
    }

    public void mousePressed(MouseEvent event) {
        Point current = new Point((int) m.getX(), (int) m.getY());
        dragHandler = new DragHandler(event.getPoint(), current);
    }

    public void mouseDragged(MouseEvent event) {
        final Point rel = dragHandler.getNewLocation(event.getPoint());
        model.execute(new Command() {
            public void doIt() {
                m.setY(rel.y);
            }
        });
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseEntered( MouseEvent event ) {
        circleGraphic.setColor(Color.green);
    }

    public void mouseExited( MouseEvent event ) {
        circleGraphic.setColor(Color.blue);
    }
}
