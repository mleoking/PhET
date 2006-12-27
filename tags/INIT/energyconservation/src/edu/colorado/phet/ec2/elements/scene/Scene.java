/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.scene;

import edu.colorado.phet.coreadditions.graphics.ImageFlip;
import edu.colorado.phet.coreadditions.graphics.RescaleOp2;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.elements.car.Car;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


/**
 * User: Sam Reid
 * Date: Aug 3, 2003
 * Time: 12:59:25 AM
 * Copyright (c) Aug 3, 2003 by Sam Reid
 */
public class Scene {
    BufferedImage background;
    private BufferedImage leftInit;
    double gravity;
    String name;
    private BufferedImage leftImage;
    private BufferedImage rightImage;
    private EC2Module module;
    private ModelViewTransform2d transform;

    public Scene( BufferedImage background, BufferedImage leftInit, double gravity, String name, final ModelViewTransform2d transform, final EC2Module module ) {
        this.background = background;
        this.leftInit = leftInit;

        this.gravity = gravity;
        this.name = name;
        this.transform = transform;
        this.module = module;

        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
//                        updateCarViewBounds();
                if( module.getCurrentScene() == Scene.this ) {
                    synchronizeCar();
                }
            }
        } );
//        updateCarViewBounds();
    }

    public void synchronizeCar() {
        Point2D.Double bounds = getCarModelBounds();
        module.getCar().setBounds( bounds.x, bounds.y );
        updateCarViewBounds();
    }

    public void updateCarViewBounds() {
        Car car = module.getCar();
        int carHeight = -transform.modelToViewDifferentialY( car.getBoundsHeight() );
        int carWidth = transform.modelToViewX( car.getBoundsWidth() );
        leftImage = RescaleOp2.rescale( leftInit, carWidth, carHeight );
        rightImage = ImageFlip.flipX( leftImage );
        module.getCarGraphic().setCarImage( getLeftImage(), getRightImage() );
    }

    public BufferedImage getLeftImage() {
        return leftImage;
    }

    public String toString() {
        return name;
    }

    public BufferedImage getRightImage() {
        return rightImage;
    }

    public double getGravity() {
        return gravity;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getBackground() {
        return background;
    }

    public BufferedImage getCarImage() {
        return leftInit;
    }

    public Point2D.Double getCarModelBounds() {
        double requestedWidth = module.getCar().getBoundsWidth();
        double appropriateHeight = leftInit.getHeight() * requestedWidth / leftInit.getWidth();
        return new Point2D.Double( requestedWidth, appropriateHeight );
    }

    public void setup() {
        Car car = module.getCar();
        car.setGravity( getGravity() );
        module.setBackground( background, transform );
        synchronizeCar();
        module.setCurrentScene( this );
        module.setChartRange();
        module.updateBuffer();
        module.getApparatusPanel().repaint();
    }

}
