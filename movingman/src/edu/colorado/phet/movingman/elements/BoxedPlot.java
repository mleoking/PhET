/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.math.transforms.BoxToBoxInvertY;
import edu.colorado.phet.movingman.application.MovingManModule;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class BoxedPlot implements ObservingGraphic {
    private MovingManModule module;
    DataSeries mh;
    private Timer recordingTimer;
    private Color color;
    private Stroke stroke;
    private Rectangle2D.Double inputBox;
    private BufferedGraphicForComponent buffer;
    private double xShift;
    boolean showValue = true;
    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    private boolean started;
    private BoxToBoxInvertY transform;
    private boolean visible = true;
    ArrayList rawData = new ArrayList(100);
    ArrayList transformedData = new ArrayList(100);
    float lastTime;
    double dt;
    private Graphics2D bufferGraphic;
    private Rectangle2D.Double outputBox;

    public BoxedPlot(MovingManModule module, DataSeries mh, Timer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox, BufferedGraphicForComponent buffer, double xShift) {
        this.module = module;
        this.mh = mh;
        this.recordingTimer = timer;
        this.color = color;
        this.stroke = stroke;
        this.inputBox = inputBox;
        this.buffer = buffer;
        this.xShift = xShift;
        mh.addObserver(this);
        mh.updateObservers();
        timer.addObserver(this);
        this.outputBox = new Rectangle2D.Double();
    }

    public void setInputBox(Rectangle2D.Double inputBox) {
        this.inputBox = inputBox;
        setOutputBox(outputBox);//redraws everything.
    }

    public void setOutputBox(Rectangle2D.Double outputBox) {
        this.outputBox = outputBox;
        started = false;
        this.transform = new BoxToBoxInvertY(inputBox, outputBox);
        //Update plot based on existing data
        path.reset();
        if (rawData.size() <= 1)
            return;
        Point2D.Double pt0 = (Point2D.Double) rawData.get(1);
        pt0 = transform.transform(pt0);
        path.moveTo((float) pt0.x, (float) pt0.y);
        for (int i = 2; i < rawData.size(); i++) {
            Point2D.Double data = (Point2D.Double) rawData.get(i);
            data = transform.transform(data);
            path.lineTo((float) data.x, (float) data.y);
            started = true;
        }
    }

    public void paint(Graphics2D g) {
        if (started && visible) {
            g.setStroke(stroke);
            g.setColor(color);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.draw(path);
        }
    }

    public void update(Observable o, Object arg) {
        if (transform == null)
            return;
        float time = (float) recordingTimer.getTime();
        if (time == lastTime)
            return;
        dt = time - lastTime;
        lastTime = time;
        if (mh.size() <= 1) {
            path.reset();
            started = false;
            rawData = new ArrayList();
        } else {
            float position = (float) mh.getLastPoint();// * scale + yoffset;
            Point2D.Double pt = new Point2D.Double(time - xShift, position);
            rawData.add(pt);
            pt = transform.transform(pt);
//            pt.x+=xShift;
            transformedData.add(pt);
            if (mh.size() >= 2 && !started) {
                path.moveTo((float) pt.x, (float) pt.y);
                started = true;
            } else if (started) {
                if (visible && buffer.getImage() != null) {
                    Point2D.Double a = (Point2D.Double) transformedData.get(transformedData.size() - 2);
                    Point2D.Double b = (Point2D.Double) transformedData.get(transformedData.size() - 1);
                    this.bufferGraphic = (Graphics2D) buffer.getImage().getGraphics();
                    bufferGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    bufferGraphic.setColor(color);
//                    this.clipArea = stroke.createStrokedShape(new Line2D.Double(b.x, b.y, a.x, a.y));
                    bufferGraphic.setStroke(stroke);
//                    if (outputBox.contains(a) && outputBox.contains(b))
                    bufferGraphic.setClip(outputBox);
                    bufferGraphic.drawLine((int) b.x, (int) b.y, (int) a.x, (int) a.y);
                }
                try {
                    path.lineTo((float) pt.x, (float) pt.y);
                } catch (IllegalPathStateException ipse) {
                    ipse.printStackTrace();
                }
            }
        }
    }

    public Rectangle2D.Double getInputBounds() {
        return transform.getInputBounds();
    }

    public BoxToBoxInvertY getTransform() {
        return transform;
    }

    public double getxShift() {
        return xShift;
    }

    public Rectangle2D.Double getOutputBox() {
        return transform.getOutputBounds();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setRangeX(double time) {
        Rectangle2D.Double input = transform.getInputBounds();
        Rectangle2D.Double newInput = new Rectangle2D.Double(input.x, input.y, time, input.height);
        transform.setInputBounds(newInput);
        Rectangle2D.Double outputBounds = transform.getOutputBounds();
        outputBounds.width = time;
        transform.setOutputBounds(outputBounds);
    }

    public void setShift(double x) {
        this.xShift = x;
    }
}
