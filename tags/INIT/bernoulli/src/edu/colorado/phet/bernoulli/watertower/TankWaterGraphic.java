package edu.colorado.phet.bernoulli.watertower;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Graphic for a rectangle of water.
 */
public class TankWaterGraphic implements Graphic, TransformListener, SimpleObserver {
    RectangularTank tank;
    ModelViewTransform2d transform;
    private Rectangle waterRect;

    public TankWaterGraphic( RectangularTank tank, ModelViewTransform2d transform ) {
        this.tank = tank;
        this.transform = transform;
        tank.addObserver( this );
        transform.addTransformListener( this );
        update();
    }

    public void update() {
        double waterVolume = tank.getWaterVolume();
        double volume = tank.getVolume();
        double waterHeight = Math.abs( waterVolume / volume * tank.getHeight() );
        Rectangle2D.Double waterModelRectangle = new Rectangle2D.Double( tank.getX(),
                                                                         tank.getY(),
                                                                         tank.getWidth(),
                                                                         waterHeight );

//        Point topleft = transform.modelToView(tank.getX(), tank.getY() + waterHeight - tank.getHeight());
//        Point bottomRight = transform.modelToView(tank.getX() + tank.getWidth(), tank.getY() - tank.getHeight());
//        waterRect = new Rectangle(topleft.x, topleft.y, bottomRight.x - topleft.x, bottomRight.y - topleft.y);
//        waterRect=transform.modelToView(new Rectangle2D.Double(tank.getX(),tank.getY()+waterHeight-tank.getHeight(),tank.getWidth(),tank.getHeight()));
        waterRect = transform.modelToView( waterModelRectangle );
    }

    Stroke stroke = new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

    public void paint( Graphics2D g ) {
        //Do the water volume.
        g.setColor( Color.blue );
        g.fill( waterRect );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        update();
    }

    public boolean contains( MouseEvent event ) {
        return waterRect.contains( event.getPoint() );
    }

    public void setModelRectangle( Rectangle2D.Double rectangle ) {
        tank.setRectangle( rectangle );
    }

}