/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.movingman.elements.BoxedPlot;
import edu.colorado.phet.movingman.elements.DataSeries;
import edu.colorado.phet.movingman.elements.Timer;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 10:55:09 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class ValueGraphic implements ObservingGraphic {
    private MovingManModule module;
    Timer recordingTimer;
    private Timer playbackTimer;
    private DataSeries series;
    private String pre;
    private String unitsString;
    DecimalFormat format = new DecimalFormat("#0.00");
    private String output;
    Font font = new Font("Lucida Sans", 0, 20);
    Color color = Color.black;
    int x;
    int y;
    private BoxedPlot offsetSource;
    private boolean visible = true;

    public ValueGraphic(MovingManModule module, Timer timer, Timer playbackTimer, DataSeries series, String pre, String units, int x, int y, BoxedPlot offsetSource) {
        this.module = module;
        this.recordingTimer = timer;
        this.playbackTimer = playbackTimer;
        this.series = series;
        this.pre = pre;
        this.unitsString = units;
        this.x = x;
        this.y = y;
        this.offsetSource = offsetSource;
        timer.addObserver(this);
        playbackTimer.addObserver(this);
    }

    public void paint(Graphics2D g) {
        if (output != null && visible) {
            g.setFont(font);
            g.setColor(color);
            g.drawString(output, x, y);
        }
    }

    public void update(Observable o, Object arg) {
        int index = 0;
        if (module.isRecording() || module.isMotionMode())
            index = series.size() - 1;
        else {
            double time = playbackTimer.getTime() + offsetSource.getxShift();
//            offsetSource.get
            index = (int) (time / MovingManModule.TIMER_SCALE);
        }
        if (series.indexInBounds(index)) {
            double value = series.pointAt(index);
            String valueString = format.format(value);
            if (valueString.equals("-0.00"))
                valueString = "0.00";
            this.output = pre + valueString + " " + unitsString;

        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
