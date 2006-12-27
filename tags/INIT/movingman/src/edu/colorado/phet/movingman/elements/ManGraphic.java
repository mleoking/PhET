/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.ImageFlip;
import edu.colorado.phet.common.view.graphics.arrows.ArrowWithFixedSizeArrowhead;
import edu.colorado.phet.common.math.CircularBuffer;
import edu.colorado.phet.common.math.transforms.functions.RangeToRange;
import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.common.RescaleOp3;
import edu.colorado.phet.movingman.common.ImageFlip3;
import edu.colorado.phet.movingman.common.tests.IdeaGraphic2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:25:37 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class ManGraphic implements ObservingGraphic, InteractiveGraphic {
    private BufferedImage standingMan;
    private BufferedImage leftMan;
    private BufferedImage rightMan;
    int x;
    int y;
    private RangeToRange transform;//from man to graphics device.
    private MovingManModule module;
    Man m;
    private DragHandler dragHandler;
    BufferedImage currentImage;
    private RangeToRange inversion;
    CircularBuffer cb = new CircularBuffer(10);
    IdeaGraphic2 ideaGraphic;
    private boolean showIdea = true;
    private ArrowWithFixedSizeArrowhead arrow;
    private IdeaGraphic2 motionIdea;
    private ArrowWithFixedSizeArrowhead motionArrow;
//    private Rectangle newRect;

    public ManGraphic(MovingManModule module, Man m, int y, RangeToRange transform) {
        this.module = module;
        this.m = m;
        this.y = y;
        this.transform = transform;
        ImageLoader loader = new ImageLoader();
        standingMan = loader.loadBufferedImage("images/stand-150.gif");
        leftMan = loader.loadBufferedImage("images/left-150.gif");
        int height = 120;
//        int height=600;
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        }
        standingMan = RescaleOp3.rescaleYMaintainAspectRatio(standingMan, height);
        leftMan = RescaleOp3.rescaleYMaintainAspectRatio(leftMan, height);
        rightMan = ImageFlip3.flipX(leftMan);

        currentImage = standingMan;
        m.addObserver(this);
        m.updateObservers();
        inversion = transform.invert();
//        module.getVelocityData().addDataSeriesListener(this);
    }

    double lastx = 0;

    public void setShowIdea(boolean showIdea) {
        this.showIdea = showIdea;
    }

    public void paint(Graphics2D g) {

//        Shape clip=g.getClip();
//        g.setClip(newRect);
        g.drawImage(currentImage, x - currentImage.getWidth() / 2, y, null);

        if (showIdea) {
            if (ideaGraphic == null) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color lightBlue = module.getPurple();
                Font ideaFont = new Font("Lucida", 0, 28);

                BufferedImage ideaImage = new ImageLoader().loadBufferedImage("images/icons/TipOfTheDay24.gif");
                int ideaX = module.getApparatusPanel().getWidth() / 8;
                ideaGraphic = new IdeaGraphic2(true, ideaX, y + 250, new String[]{"Drag the man",
                                                                                  "and graph his position,",
                                                                                  " velocity and acceleration."},
                        g.getFontRenderContext(), ideaFont, Color.black, ideaImage, lightBlue);
                int motionIdeaX = (int) (module.getApparatusPanel().getWidth() * .6);
                arrow = new ArrowWithFixedSizeArrowhead(Color.black, 10);
                motionIdea = new IdeaGraphic2(true, motionIdeaX, y + 270, new String[]{"Or choose a premade motion."}, g.getFontRenderContext(),
                        ideaFont, Color.black, ideaImage, lightBlue);
                motionArrow = new ArrowWithFixedSizeArrowhead(Color.black, 10);
            }
            ideaGraphic.paint(g);
            motionIdea.paint(g);
            Point ideaCenter = ideaGraphic.getImageCenter();
            arrow.drawLine(g, ideaCenter.x, ideaCenter.y, x - currentImage.getWidth() / 2, y + currentImage.getHeight() / 2);

            Point motionCenter = motionIdea.getImageCenter();
            motionArrow.drawLine(g, motionCenter.x, motionCenter.y, module.getApparatusPanel().getWidth(), 25);

        }
        cb.addPoint(x - lastx);
        lastx = x;
//        System.out.println("cb.toString() = " + cb.toString());
        double velocity = cb.average();
        if (velocity == 0 && currentImage != this.standingMan) {
            currentImage = this.standingMan;
        } else if (velocity < 0 && currentImage != this.leftMan) {
            currentImage = this.leftMan;
        } else if (velocity > 0 && currentImage != this.rightMan) {
            currentImage = this.rightMan;
        }
//        g.setClip(clip);
    }

    public void update(Observable o, Object arg) {
        double output = transform.evaluate(m.getX());
        this.x = (int) output;
    }

    public boolean canHandleMousePress(MouseEvent event) {
        if (module.isRecording()) {
            BufferedImage im = currentImage;
            Rectangle r = new Rectangle(x - im.getWidth() / 2, y, im.getWidth(), im.getHeight());
            return r.contains(event.getPoint());
        } else
            return false;
    }

    public void mousePressed(MouseEvent event) {
        dragHandler = new DragHandler(event.getPoint(), new Point(x, y));
        module.getModel().execute(new Command() {
            public void doIt() {
                m.setGrabbed(true);
            }
        });
    }

    public void mouseDragged(MouseEvent event) {
        final Point newPt = dragHandler.getNewLocation(event.getPoint());
//        module.getModel().execute(new Command() {
//            public void doIt() {
        Rectangle curRect = new Rectangle(x - currentImage.getWidth() / 2, y, currentImage.getWidth(), currentImage.getHeight());
        int graphicsPt = newPt.x;
        double manPoint = inversion.evaluate(graphicsPt);
        m.setX(manPoint);
        Rectangle newRect = new Rectangle(x - currentImage.getWidth() / 2, y, currentImage.getWidth(), currentImage.getHeight());
//        this.newRect=newRect;
        Rectangle total = newRect.union(curRect);

        module.getApparatusPanel().paintImmediately(total);
//            }
//        });
        setShowIdea(false);
    }

    public void mouseReleased(MouseEvent event) {
        module.getModel().execute(new Command() {
            public void doIt() {
                m.setGrabbed(false);
            }
        });
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

    public void setTransform(RangeToRange transform) {
        this.transform = transform;
        this.inversion = transform.invert();
        update(null, null);
    }

    public Shape getShape() {
        Rectangle newRect = new Rectangle(x - currentImage.getWidth() / 2, y, currentImage.getWidth(), currentImage.getHeight());
        return newRect;
//        return null;
    }

    public Area getClipArea() {
        return new Area(getShape());
    }

}
