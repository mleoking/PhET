// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.spline.CubicSpline2D;
import edu.colorado.phet.common.spline.ParametricFunction2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Mar 4, 2007
 * Time: 12:27:25 AM
 */

public class CubicSpline2DNode extends ParametricFunction2DNode {
    private CubicSpline2D parametricFunction2D;
    private PNode controlPointLayer = new PNode();

    private ParametricFunction2D.Listener listener = new ParametricFunction2D.Listener() {
        public void trackChanged() {
            update();
        }
    };
    private boolean constructed = false;

    public CubicSpline2DNode( CubicSpline2D parametricFunction2D ) {
        super( parametricFunction2D );
        this.parametricFunction2D = parametricFunction2D;
        parametricFunction2D.addListener( listener );
        addChild( controlPointLayer );
        constructed = true;
        updateControlPoints();
    }

    protected void updateControlPoints() {

        super.updateControlPoints();
        if( constructed ) {
            while( controlPointLayer.getChildrenCount() < parametricFunction2D.getNumControlPoints() ) {
                addControlPointNode();
            }
            while( controlPointLayer.getChildrenCount() > parametricFunction2D.getNumControlPoints() ) {
                removeControlPointNode();
            }
            for( int i = 0; i < controlPointLayer.getChildrenCount(); i++ ) {
                ControlPointNode controlPointNode = (ControlPointNode)controlPointLayer.getChild( i );
                controlPointNode.setIndex( i );
            }
        }
    }

    public void setCubicSpline2D( CubicSpline2D cubicSpline2D ) {
        if( this.parametricFunction2D != null ) {
            this.parametricFunction2D.removeListener( listener );
        }
        this.parametricFunction2D = cubicSpline2D;
        cubicSpline2D.addListener( listener );
        update();
    }

    private void removeControlPointNode() {
        controlPointLayer.removeChild( controlPointLayer.getChildrenCount() - 1 );
    }

    class ControlPointNode extends PNode {
        private int index;
        private PhetPPath controlPoint;

        public ControlPointNode( int index_ ) {
            this.index = index_;

            controlPoint = new PhetPPath( new Color( 255, 50, 50, 128 ), new BasicStroke( 0.01f ), Color.black );
            double w = 0.10;
            controlPoint.setPathTo( new Ellipse2D.Double( -w / 2, -w / 2, w, w ) );

            addChild( controlPoint );

            controlPoint.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    parametricFunction2D.translateControlPoint( index, event.getDeltaRelativeTo( CubicSpline2DNode.this ).width,
                                                                event.getDeltaRelativeTo( CubicSpline2DNode.this ).height );
                }
            } );

            addChild( controlPoint );
            update();
        }

        private void update() {
            controlPoint.setOffset( parametricFunction2D.getControlPoints()[index] );
        }

        public void setIndex( int i ) {
            this.index = i;
            update();
        }
    }

    private void addControlPointNode() {
        controlPointLayer.addChild( new ControlPointNode( 0 ) );
        update();
    }
}
