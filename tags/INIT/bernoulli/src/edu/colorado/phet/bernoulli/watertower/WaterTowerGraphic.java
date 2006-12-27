package edu.colorado.phet.bernoulli.watertower;

//import edu.colorado.phet.bernoulli.common.DifferentialDragHandler;

import edu.colorado.phet.bernoulli.pump.RectangleGraphic;
import edu.colorado.phet.bernoulli.valves.HorizontalValveGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 2:47:13 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class WaterTowerGraphic implements InteractiveGraphic, SimpleObserver, TransformListener {
    WaterTower tower;
    private Component target;
    private Color backgroundColor;
    TankWaterGraphic twg;
    ModelViewTransform2d transform;
    RectangleGraphic pumpContainer;
    ArrayList struts = new ArrayList( 6 );
    private Stroke strutStroke = new BasicStroke( 4.0f );
//    private VerticalValveGraphic bottomValveGraphic;
    private HorizontalValveGraphic horizontalValveGraphic;

    public WaterTowerGraphic( WaterTower tower, ModelViewTransform2d transform, ApparatusPanel target, Color backgroundColor ) {
        this.transform = transform;
        this.tower = tower;
        this.target = target;
        this.backgroundColor = backgroundColor;
        pumpContainer = new RectangleGraphic( getRectangle(), transform );

        twg = new TankWaterGraphic( tower.getTank(), transform );
        transform.addTransformListener( this );
        tower.addObserver( this );

//        this.bottomValveGraphic = new VerticalValveGraphic(tower.getBottomValve(), transform, target, false,backgroundColor);
        this.horizontalValveGraphic = new HorizontalValveGraphic( tower.getRightValve(), transform, target, false, backgroundColor );

    }

    public Rectangle2D.Double getRectangle() {
        return new Rectangle2D.Double( tower.getX(), tower.getY(), tower.getWidth(), tower.getHeight() );
    }

    public void paint( Graphics2D g ) {
        twg.paint( g );
        pumpContainer.paint( g );
        g.setColor( Color.black );
        g.setStroke( strutStroke );
        for( int i = 0; i < struts.size(); i++ ) {
            Line2D.Double strut = (Line2D.Double)struts.get( i );
            g.drawLine( (int)strut.getX1(), (int)strut.getY1(), (int)strut.getX2(), (int)strut.getY2() );
        }
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return pumpContainer.getRectangle().contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
//        event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        dragHandler = new DifferentialDragHandler( event.getPoint() );
    }

    DifferentialDragHandler dragHandler;

    public void mouseDragged( MouseEvent event ) {
        Point pt = dragHandler.getDifferentialLocationAndReset( event.getPoint() );
        Point2D.Double modeldx = transform.viewToModelDifferential( pt.x, pt.y );
        tower.translate( modeldx.y );
        target.repaint();
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void update() {
        twg.setModelRectangle( getRectangle() );
        pumpContainer.setModelRectangle( getRectangle() );
//        bottomValveGraphic.update();
        buildStruts();
    }

    private void buildStruts() {
        struts = new ArrayList();
        Rectangle rect = pumpContainer.getRectangle();
        double groundHeight = this.tower.getGroundHeight();
        int groundHeightView = transform.modelToViewY( groundHeight );

        double fanout = 5;
        Line2D.Double leftLeg = new Line2D.Double( rect.x, rect.y + rect.height, rect.x - fanout, groundHeightView );
        struts.add( leftLeg );

        Line2D.Double rightLeg = new Line2D.Double( rect.x + rect.width, rect.y + rect.height, rect.x + rect.width + fanout, groundHeightView );
        struts.add( rightLeg );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        update();
    }

//    public VerticalValveGraphic getBottomValveGraphic() {
//        return bottomValveGraphic;
//    }

    public HorizontalValveGraphic getRightValveGraphic() {
        return horizontalValveGraphic;
    }

}
