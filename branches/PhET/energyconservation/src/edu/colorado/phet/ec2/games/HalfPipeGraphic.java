/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.games;

import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.ec2.elements.spline.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 25, 2003
 * Time: 7:45:29 PM
 * Copyright (c) Jul 25, 2003 by Sam Reid
 */
public class HalfPipeGraphic extends SplineGraphic {
    private int numControlPts;
    private LinkedVertexGraphic magicPoint;

    public HalfPipeGraphic( ModelViewTransform2d transform, ModuleSplineInterface moduleInterface, Spline spline, int numControlPts ) {
        super( transform, moduleInterface, spline );

        curve = new CurveGraphic( transform, this, spline ) {

            public boolean canHandleMousePress( MouseEvent event ) {
                return false;
            }
        };
        this.numControlPts = numControlPts;
    }

    public void pointStructureChanged( Spline source ) {
        removeAllGraphics();
        addGraphic( curve, 0 );

        curve.update( source, null );
        Point[] pts = curve.getPoints();
        vertices = new ArrayList();
        for( int i = 0; i < pts.length && i < numControlPts; i++ ) {
            Point pt = pts[i];

            LinkedVertexGraphic vg = new LinkedVertexGraphic( this, spline, i, pt.x, pt.y );
            addGraphic( vg, 1 );
            vertices.add( vg );
            vg.viewChanged( transform );
        }
        if( pts.length > numControlPts ) {
            Point pt = pts[numControlPts];
            magicPoint = new LinkedVertexGraphic( this, spline, numControlPts, pt.x, pt.y ) {
                public void doMouseDrag( MouseEvent event ) {
                    if( dragger == null ) {
                        return;
                    }
                    Point rel = dragger.getNewLocation( event.getPoint() );
                    rel.x = 0;
                    boolean ok = false;

                    if( magicPoint != null && magicPoint.getModelLocation().y > 3 ) {
                        ok = true;
                    }
                    if( rel.y < 0 ) {
                        ok = true;
                    }
                    if( ok ) {
                        Point2D.Double modelPt = CurveGraphic.transformLocalViewToModel( transform, rel.x, rel.y );//transform.viewToModel(rel.x,rel.y);

                        spline.translatePoint( index, modelPt.x, modelPt.y * .65 );
                        dragger = new DragHandler( event.getPoint(), new Point() );
                    }

                }
            };
            addGraphic( magicPoint, 1 );
            vertices.add( magicPoint );
            magicPoint.viewChanged( transform );
        }
    }

    class LinkedVertexGraphic extends VertexGraphic {
        public LinkedVertexGraphic( final SplineGraphic parent, final Spline source, final int index, int x, int y ) {
            super( parent, source, index, x, y );
        }


        public void doMousePress( MouseEvent event ) {
            super.mousePressed( event );
        }

        public void mousePressed( MouseEvent event ) {
            for( int k = 0; k < vertices.size(); k++ ) {
                LinkedVertexGraphic vge = (LinkedVertexGraphic)vertexGraphicAt( k );
                vge.doMousePress( event );
            }
        }

        public void doMouseDrag( MouseEvent event ) {
            if( dragger == null ) {
                return;
            }
            Point rel = dragger.getNewLocation( event.getPoint() );
            rel.x = 0;
            boolean ok = false;

            if( magicPoint != null && magicPoint.getModelLocation().y > 3.4 ) {
                ok = true;
            }
            if( rel.y < 0 ) {
                ok = true;
            }
            if( ok ) {
                Point2D.Double modelPt = CurveGraphic.transformLocalViewToModel( transform, rel.x, rel.y );//transform.viewToModel(rel.x,rel.y);

                spline.translatePoint( index, modelPt.x, modelPt.y );
                dragger = new DragHandler( event.getPoint(), new Point() );
            }
        }

        public void mouseDragged( MouseEvent event ) {
            for( int k = 0; k < vertices.size(); k++ ) {
                LinkedVertexGraphic vge = (LinkedVertexGraphic)vertexGraphicAt( k );
                vge.doMouseDrag( event );
            }

        }

    }
}
