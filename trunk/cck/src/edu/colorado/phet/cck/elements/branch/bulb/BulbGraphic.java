/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch.bulb;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.common.SimpleObserver;
import edu.colorado.phet.cck.elements.branch.ConnectibleImageGraphic;
import edu.colorado.phet.cck.elements.branch.components.Bulb;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 27, 2003
 * Time: 12:59:59 AM
 * Copyright (c) Oct 27, 2003 by Sam Reid
 */
public class BulbGraphic extends ConnectibleImageGraphic {
//        ImageBranchGraphic {
    private Bulb bulbBranch;
    BulbBrightness bb = new BulbBrightness();
    private BufferedImage originalImage;
    private double intensity;
    private Rectangle bounds;
//    public static boolean showJunctionHoles = false;

    public BulbGraphic(final Circuit circuit, final ModelViewTransform2D transform, Bulb branch,
                       CCK2Module module, final BufferedImage image,
                       Stroke highlightStroke, Color highlightColor) {
        super(circuit, transform, branch, module, image, highlightStroke, highlightColor);
        this.bulbBranch = branch;
        this.originalImage = image;
        transform.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2D ModelViewTransform2D) {
                updateGap(image);
            }
        });
        bulbBranch.addIntensityObserver(new SimpleObserver() {
            public void update() {
                intensityChanged();
            }
        });
        super.addGraphic(new Graphic() {
            public void paint(Graphics2D g) {
                paintBrightieLines(g, intensity);
            }
        }, 10);
        updateGap(image);
        intensityChanged();
    }


/*    protected Rectangle getImageRectangle() {
        //Half a bulb image...
        Rectangle imageRect = new Rectangle(imagePortion.getImage().getWidth(), imagePortion.getImage().getHeight() / 2);
        return imageRect;
    }*/

    public void update() {
        PhetVector start = branch.getStart();
//        PhetVector end = branch.getEnd();
        double length = getBranchLength();
        PhetVector dir = branch.getDirection();
        double modelWidthForImage = transform.viewToModelDifferentialX(imagePortion.getImageWidth());
        double modelHeightForImage = transform.viewToModelDifferentialY(imagePortion.getImage().getHeight());
        double segmentLength = (length - modelWidthForImage) / 2;

//        PhetVector preEndVector = start.getAddedInstance(dir.getScaledInstance(segmentLength));
//        pre.getTarget().setState(start.getX(), start.getY(), preEndVector.getX(), preEndVector.getY());

//        PhetVector secondStartPoint = getSecondStartPoint(start, dir, segmentLength, modelWidthForImage);
//        post.getTarget().setState(secondStartPoint.getX(), secondStartPoint.getY(), end.getX(), end.getY());
//        PhetVector imageCenterModel = start.getAddedInstance(dir.getScaledInstance(segmentLength + modelWidthForImage / 2));
        PhetVector imageCenterModel = branch.getEnd().getAddedInstance(.02, -modelHeightForImage / 2.0 + .14);
        Point imCtr = transform.modelToView(imageCenterModel);
//        angle = Math.atan2(imCtr.y - pre.getTarget().getStartPoint().y, imCtr.x - pre.getTarget().getStartPoint().x);
//        angle = Math.atan2(imCtr.y - startJunctionGraphic.getY(), imCtr.x - startJunctionGraphic.getX());
        angle = 0;
        AffineTransform imageTransform = getImageTransform(imagePortion.getImage(), angle, imCtr.getX(), imCtr.getY());
        AffineTransform flameTransform = getImageTransform(imagePortion.getFlameImage(), angle, imCtr.getX(), imCtr.getY());
        imagePortion.setImageTransform(imageTransform, flameTransform);

//        Rectangle imageRect = new Rectangle(imagePortion.image.getWidth(), imagePortion.image.getHeight());
//        this.imagePortion.setImageShape(imagePortion.imageTransform.createTransformedShape(imageRect));

        currentOrVoltageChanged(branch);
        bounds = imagePortion.getImageShape().getBounds();
        textDisplay.setLocation(bounds.x, bounds.y);
    }

    private void updateGap(BufferedImage image) {
        double imageWidth = image.getWidth();
        double imageWidthModelCoords = transform.viewToModelDifferentialX(imageWidth);
        double imageHeight = image.getHeight();
        double height = Math.abs(transform.viewToModelDifferentialY(imageHeight));
        bulbBranch.setImageParametersModelCoords(imageWidthModelCoords, height);
    }

//    protected double getBranchLength() {
//        return branch.getStartJunction().distance(branch.getEndJunction().getVector());
//    }

//    protected PhetVector getSecondStartPoint(PhetVector start, PhetVector dir, double segmentLength, double modelWidthForImage) {
//        if (bulbBranch == null) {//constructor's 1st line
//            this.bulbBranch = (Bulb) branch;
//        }
//        return bulbBranch.getSecondStartPoint();
//    }

    public void intensityChanged() {
        this.intensity = bulbBranch.getIntensity();
        BufferedImage image = bb.operate(originalImage, this.intensity);
        super.setImage(image);
    }

    int MAX_BRIGHTIE_LINES = 30;

    private void paintBrightieLines(Graphics2D g2, double intensity) {
        AffineTransform save = g2.getTransform();
        BufferedImage image = originalImage;
        AffineTransform at = super.getImageTransform();//getTransform(branch, image);
        g2.setTransform(at);
//        Rectangle rect = sh.getBounds();
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.yellow);
        int maxLines = MAX_BRIGHTIE_LINES;
        int numLines = (int) (maxLines * intensity);

        Point center = new Point(image.getWidth() / 2, image.getHeight() / 4);//(int) (rect.getX() + rect.getWidth()), (int) (rect.getY() + rect.getHeight()));
        double angle = 0;
//        angle=rand.nextDouble()*Math.PI/2;
        double dtheta = Math.PI * 2 / numLines;

        int originMagnitude = 50;
        int magnitude = (int) (intensity * 100) + originMagnitude;
        if (numLines > 1) {
            for (int i = 0; i < numLines; i++) {
                Point startAt = new Point(center.x + (int) (originMagnitude * Math.cos(angle)), center.y + (int) (originMagnitude * Math.sin(angle)));
                Point dest = new Point(center.x + (int) (magnitude * Math.cos(angle)), center.y + (int) (magnitude * Math.sin(angle)));
                g2.drawLine(startAt.x, startAt.y, dest.x, dest.y);
                angle += dtheta;
            }
        }
        g2.setTransform(save);
    }

}
