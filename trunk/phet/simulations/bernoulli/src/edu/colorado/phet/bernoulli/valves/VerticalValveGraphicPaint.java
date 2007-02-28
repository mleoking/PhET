package edu.colorado.phet.bernoulli.valves;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 1:09:19 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class VerticalValveGraphicPaint implements InteractiveGraphic, SimpleObserver, TransformListener {
    Color color = Color.red;
    boolean open;
    Valve model;
    private ModelViewTransform2d transform;
    private ApparatusPanel target;
    private boolean up;
    private Color backgroundColor;
    private Point location;
    int width = 40;
    int height = 30;
    int tankStrokeWidth = 4;
    int barrierWidth = 4;
    double scaleBarrierWidth = 5;
    private Rectangle rect;

    public VerticalValveGraphicPaint( Valve model, ModelViewTransform2d transform, ApparatusPanel target, boolean up, Color backgroundColor ) {
        this.model = model;
        this.transform = transform;
        this.target = target;
        this.up = up;
        this.backgroundColor = backgroundColor;
        model.addObserver( this );
        transform.addTransformListener( this );
        update();
        color = color.red;
    }

    public void valveChanged() {
        this.open = model.isOpen();
        this.location = transform.modelToView( model.getX(), model.getY() );
        if( !up ) {
            this.rect = new Rectangle( location.x - width / 2, location.y - tankStrokeWidth / 2, width, height + tankStrokeWidth / 2 );
        }
        else {
            this.rect = new Rectangle( location.x - width / 2, location.y + tankStrokeWidth / 2 - height, width, height + tankStrokeWidth / 2 );
        }
        target.repaint();
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return rect.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        model.setOpen( !model.isOpen() );
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void paint( Graphics2D g ) {
        if( rect != null ) {
            if( open ) {
                g.setColor( backgroundColor );
                g.fillRect( rect.x, rect.y, rect.width, rect.height );
                g.setColor( color );
                g.fillRect( rect.x, rect.y, barrierWidth, rect.height );
                g.fillRect( rect.x + rect.width - barrierWidth, rect.y, barrierWidth, rect.height );
            }
            else {
                g.setColor( color );
                g.fillRect( rect.x, rect.y, rect.width, rect.height );
            }
        }
    }

    public void update() {
        valveChanged();
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        this.height = Math.abs( transform.modelToViewDifferentialY( model.getHeight() ) );
        this.width = Math.abs( transform.modelToViewDifferentialX( model.getWidth() ) );
        this.barrierWidth = Math.abs( transform.modelToViewDifferentialX( model.getWidth() / scaleBarrierWidth ) );
        update();
    }

    public Rectangle getRectangle() {
        return rect;
    }
}
