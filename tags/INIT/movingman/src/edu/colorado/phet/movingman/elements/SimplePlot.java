/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.view.graphics.ObservingGraphic;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class SimplePlot implements ObservingGraphic {
    DataSeries mh;
    private Timer timer;
    private Color color;
    private Stroke stroke;
    private int yoffset;
    private float scale;
    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    float t = 0;
    private boolean started;

    public SimplePlot(DataSeries mh, Timer timer, Color color, Stroke stroke, int yoffset, float scale) {
        this.mh = mh;
        this.timer = timer;
        this.color = color;
        this.stroke = stroke;
        this.yoffset = yoffset;
        this.scale = scale;
        mh.addObserver(this);
        mh.updateObservers();
        timer.addObserver(this);
    }

    public void paint(Graphics2D g) {
        if (started) {
            g.setStroke(stroke);
            g.setColor(color);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.draw(path);
        }
    }

    public void update(Observable o, Object arg) {
        float time = (float) timer.getTime();
        if (mh.size() <= 1) {
            path.reset();
            started = false;
        } else if (mh.size() == 2) {
            float position = (float) mh.getLastPoint() * scale + yoffset;
            path.moveTo(time, position);
            started = true;
        } else {
            float position = (float) mh.getLastPoint() * scale + yoffset;
            path.lineTo(time, position);
        }
        t++;
    }
}
