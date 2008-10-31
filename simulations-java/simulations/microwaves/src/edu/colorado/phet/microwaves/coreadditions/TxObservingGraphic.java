/**
 * Class: TxObservingGraphic
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 24, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observer;

import edu.colorado.phet.common_microwaves.view.graphics.Graphic;
import edu.colorado.phet.common_microwaves.view.graphics.ModelViewTransform2D;
import edu.colorado.phet.common_microwaves.view.graphics.TransformListener;

public abstract class TxObservingGraphic implements Graphic, Observer, TransformListener {

    private ModelViewTransform2D modelViewTx;

    protected TxObservingGraphic( ModelViewTransform2D tx ) {
        this.modelViewTx = tx;
        modelViewTx.addTransformListener( this );
    }

    protected void setBoundsMapping( Rectangle2D.Double modelBounds,
                                     Rectangle viewBounds,
                                     edu.colorado.phet.common_microwaves.view.ApparatusPanel apparatusPanel ) {
        modelViewTx = new ModelViewTransform2D( modelBounds, viewBounds );
//        modelViewTx = new ModelViewTransform2D( modelBounds, viewBounds );
        ApparatusPanelModelViewTxAdapter adapter = new ApparatusPanelModelViewTxAdapter( modelViewTx, apparatusPanel );
        apparatusPanel.addComponentListener( adapter );
    }

    protected void setTxViewBounds( Dimension proposedSize ) {
        modelViewTx.setViewBounds( new Rectangle( (int) modelViewTx.getViewBounds().getMinX(),
                                                  (int) modelViewTx.getViewBounds().getMinY(),
                                                  (int) proposedSize.getWidth(),
                                                  (int) proposedSize.getHeight() ) );
    }

    protected int modelToViewX( double x ) {
        return modelViewTx.modelToViewX( x );
    }

    protected int modelToViewY( double y ) {
        return modelViewTx.modelToViewY( y );
    }

    protected Point modelToView( Point2D.Double inputPt ) {
        return modelViewTx.modelToView( inputPt );
    }

    protected int modelToViewDifferentialX( double x ) {
        return modelViewTx.modelToViewDifferentialX( x );
    }

    protected int modelToViewDifferentialY( double y ) {
        return modelViewTx.modelToViewDifferentialY( y );
    }

    protected ModelViewTransform2D getTx() {
        return modelViewTx;
    }

    public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
        this.update( null, null );
    }

    //
    // Inner classes
    //
    class ApparatusPanelModelViewTxAdapter extends ComponentAdapter {
        private double aspectRatio;

        ApparatusPanelModelViewTxAdapter( ModelViewTransform2D tx, edu.colorado.phet.common_microwaves.view.ApparatusPanel apparatusPanel ) {
            aspectRatio = tx.getModelBounds().height / tx.getModelBounds().width;
            apparatusPanel.addComponentListener( this );
        }

        public void componentResized( ComponentEvent e ) {
            Component c = (Component) e.getSource();
            Dimension newSize = c.getSize();
            double newAspectRatio = (double) newSize.height / (double) newSize.width;
            if ( newAspectRatio < aspectRatio ) {
                newSize.setSize( (int) ( newSize.height / aspectRatio ), newSize.height );
            }
            else if ( newAspectRatio > aspectRatio ) {
                newSize.setSize( newSize.width, newSize.width * aspectRatio );
            }
            setTxViewBounds( newSize );
        }
    }

    private abstract class Attribute {
        private int value;

        public int getValue() {
            return value;
        }

        protected void setValue( int value ) {
            this.value = value;
        }

        abstract protected void setValue( double modelValue );
    }

    public class XAttribute extends Attribute {
        public void setValue( double modelValue ) {
            super.setValue( modelToViewX( modelValue ) );
        }
    }

    public class YAttribute extends Attribute {
        public void setValue( double modelValue ) {
            super.setValue( modelToViewY( modelValue ) );
        }
    }

    public class XAttributeRelative extends Attribute {
        public void setValue( double modelValue ) {
            super.setValue( modelToViewDifferentialX( modelValue ) );
        }
    }

    public class YAttributeRelative extends Attribute {
        public void setValue( double modelValue ) {
            super.setValue( modelToViewDifferentialY( modelValue ) );
        }
    }

    public class PointAttribute {
        Point point = new Point();

        public void setValue( Point2D.Double modelPoint ) {
            point = modelToView( modelPoint );
        }

        public void setValue( double x, double y ) {
            point.setLocation( modelToViewX( x ), modelToViewY( y ) );
        }

        public Point getValue() {
            return point;
        }

        public void setUpperLeftCornerFromCenter( double modelDx, double modelDy ) {
            point.setLocation( point.getX() - modelToViewDifferentialX( modelDx ),
                               point.getY() + modelToViewDifferentialY( modelDy ) );
        }
    }


}
