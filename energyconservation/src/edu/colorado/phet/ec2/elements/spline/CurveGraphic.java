/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.spline;

import edu.colorado.phet.common.view.graphics.DragHandler;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.coreadditions.graphics.arrows.Arrow;
import edu.colorado.phet.coreadditions.graphics.positioned.CenteredCircleGraphic2;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.ec2.DefaultLandingEvent;
import edu.colorado.phet.ec2.common.util.CursorHandler;
import edu.colorado.phet.ec2.elements.car.SplineMode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jul 25, 2003
 * Time: 8:17:57 PM
 * Copyright (c) Jul 25, 2003 by Sam Reid
 */
public class CurveGraphic implements InteractiveGraphic, ObservingGraphic {
    private NatCubic narc;

    NatCubic cubic = new NatCubic();
    Spline spline;
    ModelViewTransform2d transform;
    ArrayList points = new ArrayList();
    private Color color = Color.black;
    private Stroke stroke = new BasicStroke( 8 );
    CursorHandler cursorHandler = new CursorHandler();
    private boolean paintSplineData = false;
    JPopupMenu popup;
    private SplineGraphic parent;
    private Point popupLocation;
    private boolean showShape = false;
    private boolean showChickenPox = false;
    public static boolean SHOW_NORMALS = false;

    public CurveGraphic( final ModelViewTransform2d transform, final SplineGraphic parentGraphic, final Spline spline ) {
        this.transform = transform;
        this.parent = parentGraphic;
        this.spline = spline;
        popup = newJPopupMenu( "Curve" );
        JMenuItem remove = newMenuItem( "Remove", new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                parent.remove();
            }
        } );
        JMenuItem addControlPoint = newMenuItem( "Add Control Point", new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Point pt = popupLocation;
                int index = determineNewControlPointIndex( pt );
                Point2D.Double modelCoords = transform.viewToModel( pt.x, pt.y );
                spline.insertControlPoint( index, modelCoords.x, modelCoords.y );
            }
        } );
        JMenuItem copySpline = newMenuItem( "Copy", new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Spline copy = spline.copySpline();
                for( int i = 0; i < copy.numPoints(); i++ ) {
                    copy.translate( 1, 0 );
                }
                parentGraphic.addNewSpline( copy );
            }
        } );

        popup.add( addControlPoint );
        popup.add( copySpline );
        popup.addSeparator();
        popup.add( remove );
        update( null, null );
    }

    public static JPopupMenu newJPopupMenu( String title ) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorderPainted( true );
        popup.setBackground( Color.green );
        TitledBorder tb = new TitledBorder( title );
        tb.setTitleFont( new Font( "Lucida", 0, 20 ) );
        popup.setBorder( tb );
        popup.setBackground( new Color( 100, 100, 200 ) );
        popup.setForeground( Color.yellow );
        popup.setOpaque( true );
        return popup;
    }

    private int determineNewControlPointIndex( Point pt ) {
        Point2D.Double modelCoord = transform.viewToModel( pt.x, pt.y );

        int numTestLocations = 100;
        double requestedScalar = DefaultLandingEvent.getSplineLocation( spline.getSegmentPath(), modelCoord, numTestLocations );
//    O.d("Requested Scalar="+requestedScalar);
        for( int i = 0; i < spline.numPoints(); i++ ) {
            Point2D.Double coord = spline.pointAt( i );
            double controlPointScalar = DefaultLandingEvent.getSplineLocation( spline.getSegmentPath(), coord, numTestLocations );
//            O.d("ControlScalar="+controlPointScalar);
            if( requestedScalar < controlPointScalar ) {
//                O.d("Returning i="+i);
                return i;
            }
        }
//        O.d("No find, return spline.numpoints()");
        return spline.numPoints();
    }

    static Font buttonFont = new Font( "Lucida", 0, 24 );

    public static JMenuItem newMenuItem( String name, ActionListener actionListener ) {
        JMenuItem jb = new JMenuItem( name );
        jb.setFont( buttonFont );
        jb.addActionListener( actionListener );
        return jb;
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        if( narc != null ) {
            GeneralPath path = narc.getPath();
            Shape sh = stroke.createStrokedShape( path );
            return sh.contains( event.getX(), event.getY() );
        }
        return false;
    }

    public void setTransform( ModelViewTransform2d transform ) {
        this.transform = transform;
    }

    public void mousePressed( MouseEvent event ) {
        if( SwingUtilities.isRightMouseButton( event ) ) {
            popup.setLocation( event.getPoint() );
            popup.show( event.getComponent(), event.getX(), event.getY() );
            popupLocation = event.getPoint();
        }
        else {
            dragger = new DragHandler( event.getPoint(), new Point() );
//            color = Color.gray;
            SplineMode.healEnergy = false;
        }
    }

    DragHandler dragger;

    public void mouseDragged( MouseEvent event ) {
//        O.d("Dragging."+System.currentTimeMillis());
        if( dragger == null ) {
            return;
        }
        Point rel = dragger.getNewLocation( event.getPoint() );
        Point2D.Double modelPt = transformLocalViewToModel( transform, rel.x, rel.y );//transform.viewToModel(rel.x,rel.y);
        spline.translate( modelPt.x, modelPt.y );
        dragger = new DragHandler( event.getPoint(), new Point() );
    }

    public ModelViewTransform2d getTransform() {
        return transform;
    }

    public static Point2D.Double transformLocalViewToModel( ModelViewTransform2d transform, int x, int y ) {
        double xout = transform.viewToModel( x, 0 ).x;
        Rectangle2D.Double modelBounds = transform.getModelBounds();
        Rectangle viewBounds = transform.getViewBounds();
        double m = modelBounds.height / viewBounds.height;
        double yout = m * ( y - viewBounds.y ) + modelBounds.y;
        return new Point2D.Double( xout, -yout );
    }

    public void mouseReleased( MouseEvent event ) {
        dragger = null;
        color = Color.black;
        SplineMode.healEnergy = true;
    }

    public void mouseEntered( MouseEvent event ) {
//        color = Color.darkGray;
        cursorHandler.mouseEntered( event );
    }

    public void mouseExited( MouseEvent event ) {
        color = Color.black;
        cursorHandler.mouseExited( event );
    }

    public void paint( Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        if( narc != null ) {
            g.setColor( color );
            g.setStroke( stroke );
            narc.paint( g );
        }
        //Test out the spline.
        paintSplineData = true;
        if( spline != null && transform != null && spline.numPoints() >= 2 && paintSplineData ) {
            GeneralPath path = spline.getPath();
            if( path != null && showShape ) {
                Shape shape = transform.toAffineTransform().createTransformedShape( path );
                g.setColor( Color.green );
                g.setStroke( new BasicStroke( 4 ) );
                g.fill( shape );
            }

            boolean showSegments = false;
            if( showSegments ) {
                Arrow arrow = new Arrow( Color.red, 3 );
                SegmentPath segpath = spline.getSegmentPath();
                for( int i = 0; i < segpath.numSegments(); i++ ) {
                    Segment seg = segpath.segmentAt( i );
                    g.setColor( Color.yellow );
                    g.setStroke( new BasicStroke( 2 ) );
                    Line2D.Double line = new Line2D.Double( seg.getStartPoint(), seg.getFinishPoint() );
                    Shape sh = transform.toAffineTransform().createTransformedShape( line );
                    g.draw( sh );
                }
            }

            if( SHOW_NORMALS ) {
                Arrow arrow = new Arrow( Color.green, 5 );
                SegmentPath segpath = spline.getSegmentPath();
                for( int i = 0; i < segpath.numSegments(); i++ ) {
                    Segment seg = segpath.segmentAt( i );
                    PhetVector vector = seg.getDirectionVector().getNormalVector().getScaledInstance( 4 );
                    PhetVector dirVector = seg.getDirectionVector().getNormalVector();
                    Point dvector = transform.modelToViewDifferential( dirVector.getX(), dirVector.getY() );
                    Point startPoint = transform.modelToView( seg.getStartPoint().getX(), seg.getStartPoint().getY() );
                    arrow.drawLine( g, startPoint.x, startPoint.y, startPoint.x + dvector.x, startPoint.y - dvector.y );
                }
            }

            if( showChickenPox ) {
                SegmentPath segpath = spline.getSegmentPath();
                int numSteps = 17;
                double dx = segpath.getLength() / numSteps;
                double x = 0;
                CenteredCircleGraphic2 ccg = new CenteredCircleGraphic2( 5, Color.red );
                for( int i = 0; i < numSteps; i++ ) {
                    Point2D.Double pt = segpath.getPosition( x );
//                O.d("SegmentPath Position["+i+"]="+pt);
                    Point viewPoint = transform.modelToView( pt.x, pt.y );
                    ccg.paint( g, viewPoint.x, viewPoint.y );
                    x += dx;
                }
            }
        }
    }

    public Point[] getPoints() {
        return (Point[])points.toArray( new Point[0] );
    }

    public void update( Observable o, Object arg ) {
        if( transform == null ) {
            return;
        }
        narc = new NatCubic();
        points = new ArrayList();
        for( int i = 0; i < spline.numPoints(); i++ ) {
            Point2D.Double pt = spline.pointAt( i );
            Point xy = transform.modelToView( pt.x, pt.y );
            narc.addPoint( xy.x, xy.y );
            points.add( xy );
        }
        narc.computePaintState( spline.getNumSegments() );
    }
}
