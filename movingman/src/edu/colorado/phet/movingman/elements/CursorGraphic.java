/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.math.transforms.BoxToBoxInvertY;
import edu.colorado.phet.movingman.application.MovingManModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 9:02:00 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class CursorGraphic implements ObservingGraphic, InteractiveGraphic {
    MovingManModule module;
    Timer timer;
    private Color color;
    private BoxToBoxInvertY transform;
    int x = 0;
    int width = 8;
    int height;
    int y;
    private DragHandler dragHandler;
    private BoxToBoxInvertY inversion;
    boolean visible = false;
    private Stroke stroke = new BasicStroke(3.0f);

    public CursorGraphic(MovingManModule module, Timer timer, Color color, BoxToBoxInvertY transform, int y, int height) {
        this.module = module;
        this.timer = timer;
        this.color = color;
        this.transform = transform;
        this.y = y;
        this.height = height;
        timer.addObserver(this);
        if (transform != null)
            inversion = new BoxToBoxInvertY(transform.getOutputBounds(), transform.getInputBounds());
        update(timer, null);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBounds(BoxToBoxInvertY transform) {
        this.transform = transform;
        this.inversion = new BoxToBoxInvertY(transform.getOutputBounds(), transform.getInputBounds());
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void paint(Graphics2D g) {
        if (!visible || transform == null)
            return;
        g.setColor(color);
        g.setStroke(stroke);
        g.drawRect(x, y, width, height);
    }

    public void update(Observable o, Object arg) {
        if (transform == null)
            return;
        double time = timer.getTime();
        double coordinate = transform.transform(new Point2D.Double(time, 0)).x;
        this.x = (int) coordinate - width / 2;
    }

    public boolean canHandleMousePress(MouseEvent event) {
        if (!visible || transform == null)
            return false;
        Rectangle r = new Rectangle(x, y, width, height);
        return r.contains(event.getPoint());
    }

    public void mousePressed(MouseEvent event) {
        Point start = new Point(x, y);
        this.dragHandler = new DragHandler(event.getPoint(), start);
    }

    public void mouseDragged(MouseEvent event) {
        Point newPoint = dragHandler.getNewLocation(event.getPoint());
        double xCoord = newPoint.x;
        Point2D.Double input = new Point2D.Double(xCoord, 0);
        double requestedTime = inversion.transform(input).x;
        module.cursorMovedToTime(requestedTime);
    }

    public void mouseReleased(MouseEvent event) {
        dragHandler = null;
    }

    public void mouseEntered(MouseEvent event) {
        Window w = SwingUtilities.getWindowAncestor(event.getComponent());
        w.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent event) {
        Window w = SwingUtilities.getWindowAncestor(event.getComponent());
        w.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public boolean isVisible() {
        return visible;
    }

    public void updateYourself() {
        update(timer, null);
    }
}
