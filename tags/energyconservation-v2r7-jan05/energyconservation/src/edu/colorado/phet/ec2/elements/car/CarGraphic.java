/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.car;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.view.graphics.positioned.PositionedRect3DGraphic;
import edu.colorado.phet.coreadditions.graphics.arrows.Arrow;
import edu.colorado.phet.coreadditions.graphics.positioned.CenteredImageGraphic2;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.common.RotatableImageGraphic;
import edu.colorado.phet.ec2.common.util.CursorHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Observable;


/**
 * User: Sam Reid
 * Date: May 31, 2003
 * Time: 11:46:48 PM
 * Copyright (c) May 31, 2003 by Sam Reid
 */
public class CarGraphic implements ObservingGraphic, InteractiveGraphic {
    double angle = 0;
    private EC2Module module;
    private Car c;
    int x;
    int y;
    PositionedRect3DGraphic paint;
    CenteredImageGraphic2 carGraphic;
    BufferedImage currentImage;
    BufferedImage carImage;
    BufferedImage leftImage;
    BufferedImage rightImage;
    Point vector;
    CursorHandler cursorHandler = new CursorHandler();
    private boolean showRect = false;
    private PhetVector velocityVector;
    RotatableImageGraphic graphic;
    private AffineTransform affineTransform;
    public static boolean OFFSET = true;
    private AngleChooser DEFAULT_ANGLE_CHOOSER = new AngleChooser() {
        public double getAngle( Car c ) {
            return -velocityVector.getAngle() + Math.PI;
        }
    };

    public CarGraphic( EC2Module module ) {
        this.module = module;
        this.c = module.getCar();

        this.ac = DEFAULT_ANGLE_CHOOSER;
        this.currentImage = module.getCarImage();
        carImage = currentImage;
        leftImage = carImage;
        rightImage = carImage;
        carGraphic = new CenteredImageGraphic2( currentImage );

        int width = 40;
        int height = 40;
        paint = new PositionedRect3DGraphic( width, height, Color.blue, true );
        c.addObserver( this );
        update( c, null );
        module.getTransform().addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                viewChanged( modelViewTransform2d );
            }
        } );
        graphic = new RotatableImageGraphic( 0, currentImage, x, y );
    }

    public synchronized void paint( Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        graphic.paint( g );
        if( vector != null ) {
            Arrow a2 = new Arrow( Color.red, Color.red, 4.0f, 20, 26 );
            a2.drawLine( g, x, y, vector.x, vector.y );

            Arrow arrowx = new Arrow( Color.green, 6 );
            arrowx.drawLine( g, x, y, vector.x, y );
            Arrow arrowy = new Arrow( Color.green, 6 );
            arrowy.drawLine( g, x, y, x, vector.y );
        }
        if( showRect ) {
            g.setStroke( new BasicStroke( 2 ) );
            g.setColor( Color.orange );
            Rectangle2D.Double rect = c.getRectangle();
            Shape trf = module.getTransform().toAffineTransform().createTransformedShape( rect );
            g.draw( trf );
        }
    }

    private void chooseCarImageDir() {
        double vel = c.getVelocityVector().getX();
        if( vel <= 0 ) {
            currentImage = leftImage;
        }
        else {
            currentImage = rightImage;
        }
        carGraphic.setBufferedImage( currentImage );
    }

    AngleChooser ac;

    public synchronized void update( Observable o, Object arg ) {
//        Shape preShape=getShape();
        Point view = module.getTransform().modelToView( c.getX(), c.getY() );

        this.x = view.x;
        this.y = view.y;

//        Shape modelShape = c.getShape();
        if( affineTransform == null ) {
            affineTransform = module.getTransform().toAffineTransform();
        }
//        this.viewShape = affineTransform.createTransformedShape(modelShape);
        velocityVector = c.getVelocityVector();

        this.angle = ac.getAngle( c );
//        O.d("velmag="+velocityVector.getMagnitude());
//        O.d("Cary="+c.getHeightAboveGround());
        /**Set the car to be level above the ground.*/
        if( c.getHeightAboveGround() < .05 ) {
            angle = 0;
            if( c.getVelocityVector().getX() > 0 ) {
                angle = Math.PI;
            }
        }
        if( graphic != null ) {
            /**Make sure it's upside up.*/
            rectifyAndChooseImage();
            graphic.setState( angle, x, y );
        }

//        Shape postShape=getShape();
//        module.getApparatusPanel().repaint( preShape.getBounds());
//        module.getApparatusPanel().repaint( postShape.getBounds());
    }

    private void rectifyAndChooseImage() {
        if( module.getCar().isFalling() ) {
            if( angle > Math.PI / 2 && angle <= 3 * Math.PI / 2 ) {
                angle -= Math.PI;
                graphic.setBufferedImage( rightImage );
            }
            else {
                graphic.setBufferedImage( leftImage );
            }
        }
        else {        //attached to a spline.
//            O.d(System.currentTimeMillis() + ", top=" + module.getCar().getSplineMode().isTop());
            boolean top = module.getCar().getSplineMode().isTop();
            double velocity = module.getCar().getSplineMode().getVelocity();

            if( top ) {
                if( velocity > 0 ) {
                    graphic.setBufferedImage( rightImage );
                    angle += Math.PI;
                }
                else {
                    graphic.setBufferedImage( leftImage );
                }
            }
            else {
                if( velocity > 0 ) {
                    graphic.setBufferedImage( leftImage );
                }
                else {
                    graphic.setBufferedImage( rightImage );
                    angle += Math.PI;
                }
            }
        }
    }

    public Shape getShape() {
        return module.getTransform().toAffineTransform().createTransformedShape( module.getCar().getRectangle() );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        boolean val = getShape().contains( event.getPoint() );
        return val;
    }

    public void mousePressed( MouseEvent event ) {
        c.getMode().mousePressed( this, event );
    }

    public void mouseDragged( MouseEvent event ) {
        c.getMode().mouseDragged( this, event );
        setDefaultAngleChooser();
    }

    public void mouseReleased( MouseEvent event ) {
        c.getMode().mouseReleased( this, event );
    }

    public void mouseEntered( MouseEvent event ) {
        CursorHandler.setCursor( Cursor.HAND_CURSOR, event.getComponent() );
    }

    public void mouseExited( MouseEvent event ) {
        CursorHandler.setCursor( Cursor.DEFAULT_CURSOR, event.getComponent() );
    }

    public void viewChanged( ModelViewTransform2d transform ) {
        carGraphic.setBufferedImage( currentImage );
        graphic.setBufferedImage( currentImage );
        this.affineTransform = module.getTransform().toAffineTransform();
        chooseCarImageDir();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setCarImage( BufferedImage leftImage, BufferedImage rightImage ) {
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.currentImage = leftImage;
        graphic.setBufferedImage( leftImage );
//        module.getApparatusPanel().repaint( );
    }

    public boolean isRectVisible() {
        return showRect;
    }

    public void setRectVisible( boolean showRect ) {
        this.showRect = showRect;
    }

    public Point getCarViewLocation() {
        return new Point( x, y );
    }

    public Car getCar() {
        return module.getCar();
    }

    public ModelViewTransform2d getTransform() {
        return module.getTransform();
    }

    public void setInitialVelocityVector( Point vector ) {
        this.vector = vector;
    }

    public Point getInitialVelocityVector() {
        return vector;
    }

    public void setAngleChooser( AngleChooser ac ) {
        this.ac = ac;
    }

    public void setDefaultAngleChooser() {
        this.ac = DEFAULT_ANGLE_CHOOSER;
    }

}
