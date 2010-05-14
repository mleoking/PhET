package edu.colorado.phet.recordandplayback.gui;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.recordandplayback.model.RecordModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Timeline<T> extends PNode {
    RecordModel<T> model;
    PhetPCanvas canvas;
    Color timelineColor;
    double maxTime;

    int pathOffsetY = 4;
    int pathHeight = 6;
    int ellipseWidth = 10;
    int ellipseHeight = 8;
    int insetX = 10;
    final double scale = 1.0;
    private PhetPPath shaded;
    private PhetPPath background;
    private PImage handle;

    public static class Track extends PhetPPath {
        private PhetPPath topShade;
        private PhetPPath bottomShade;
        private PhetPPath leftShade;
        private PhetPPath rightShade;

        public Track(Color backgroundColor) {
            super(backgroundColor);
            topShade = new PhetPPath(new BasicStroke(2), darker(backgroundColor, 55));
            addChild(topShade);
            bottomShade = new PhetPPath(new BasicStroke(1), darker(backgroundColor, 20));
            addChild(bottomShade);
            leftShade = new PhetPPath(new BasicStroke(2), darker(backgroundColor, 50));
            addChild(leftShade);
            rightShade = new PhetPPath(new BasicStroke(1), darker(backgroundColor, 20));
            addChild(rightShade);
        }

        @Override
        public void setPathTo(Shape aShape) {
            super.setPathTo(aShape);    //To change body of overridden methods use File | Settings | File Templates.
            Rectangle2D b = aShape.getBounds2D();
            topShade.setPathTo(new Line2D.Double(b.getX(), b.getY(), b.getMaxX(), b.getY()));
            bottomShade.setPathTo(new Line2D.Double(b.getX(), b.getMaxY(), b.getMaxX(), b.getMaxY()));
            leftShade.setPathTo(new Line2D.Double(b.getX(), b.getY(), b.getX(), b.getMaxY()));
            rightShade.setPathTo(new Line2D.Double(b.getMaxX(), b.getY(), b.getMaxX(), b.getMaxY()));
        }
    }

    public Timeline(final RecordModel<T> model, PhetPCanvas canvas, Color timelineColor, double maxTime) {
        this.model = model;
        this.canvas = canvas;
        this.timelineColor = timelineColor;
        this.maxTime = maxTime;

        shaded = new PhetPPath(timelineColor);
        Color backgroundColor = new Color(190, 195, 195);

        background = new Track(backgroundColor);

        BufferedImage img = null;
        try {
            img = ImageLoader.loadBufferedImage("piccolo-phet/images/button-template.png");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        BufferedImage scaledImage = BufferedImageUtils.getScaledInstance(img, 20, 10, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
        handle = new PImage(scaledImage);

        addChild(background);
        addChild(shaded);
        addChild(handle);

        canvas.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateSelf();
            }
        });

        handle.addInputEventListener(new CursorHandler());
        handle.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseDragged(PInputEvent event) {
                handleDrag(event);
            }
        });

        shaded.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mousePressed(PInputEvent event) {
                //todo: should put model in playback mode?
                double x = event.getCanvasPosition().getX();
                double t = x / scale;
                model.setPlaybackTime(MathUtil.clamp(model.getMinRecordedTime(), t, model.getMaxRecordedTime()));
            }

            @Override
            public void mouseDragged(PInputEvent event) {
                handleDrag(event);
            }
        });


        model.addObserver(new SimpleObserver() {
            public void update() {
                updateSelf();
            }
        });
        updateSelf();
    }


    private static Color darker(Color c, int delta) {
        return new Color(c.getRed() - delta, c.getGreen() - delta, c.getBlue() - delta);
    }

    private void handleDrag(PInputEvent event) {
        model.setPaused(true);
        double dx = event.getCanvasDelta().width;
        double t = model.getTime() + dx / scale;//todo: what is t doing here?  it seems unused
        model.setPlaybackTime(MathUtil.clamp(model.getMinRecordedTime(), model.getFloatTime() + dx / scale, model.getMaxRecordedTime()));
//        model.setPlaybackTime(((model.getFloatTime() + dx / scale)max model.getMinRecordedTime())
//        min(model.getMaxRecordedTime()));
    }

    private void updateSelf() {
        double scale = (canvas.getWidth() - insetX * 2) / maxTime;

        shaded.setPathTo(new Rectangle(insetX, pathOffsetY + 1, (int) (model.getRecordedTimeRange() * scale), pathHeight - 1));
        background.setPathTo(new Rectangle(insetX, pathOffsetY, (int) (maxTime * scale), pathHeight));
        handle.setVisible(model.isPlayback());
        double elapsed = model.getTime() - model.getMinRecordedTime();
        handle.setOffset(elapsed * scale - handle.getFullBounds().getWidth() / 2 + insetX, pathOffsetY - 2);
    }
}