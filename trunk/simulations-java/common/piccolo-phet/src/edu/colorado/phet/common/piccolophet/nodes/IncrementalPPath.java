package edu.colorado.phet.common.piccolophet.nodes;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 4:23:15 PM
 */
public class IncrementalPPath extends PPath {
    private PCanvas pCanvas;
    private boolean doingLineTo = false;
//    private Point2D prevPt;
    //    private Point2D currPt;
    ArrayList pts = new ArrayList();

    public IncrementalPPath( PCanvas pCanvas ) {
        this.pCanvas = pCanvas;
        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                pts.clear();
                repaint();
            }
        };
//        addPropertyChangeListener( PROPERTY_BOUNDS, pcl);
//        addPropertyChangeListener( PROPERTY_FULL_BOUNDS, pcl);
        addPropertyChangeListener( PROPERTY_TRANSFORM, pcl );
        addPropertyChangeListener( PROPERTY_STROKE, pcl );
//        addPropertyChangeListener( pcl);

    }

    public void lineTo( float x, float y ) {
        pts.add( new Point2D.Double( x, y ) );
//        this.prevPt = getPathReference().getCurrentPoint();
//        this.currPt = ;
        Point2D prevPoint = getPathReference().getCurrentPoint();
        doingLineTo = true;
        localLineTo( x, y, prevPoint );
        doingLineTo = false;
        Point2D newPoint = getPathReference().getCurrentPoint();

        Line2D.Double line = new Line2D.Double( prevPoint, newPoint );
        Rectangle2D bounds = getStroke().createStrokedShape( line ).getBounds2D();
//        Rectangle2D.Double localBounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        localToGlobal( bounds );

//        System.out.println( "line=" + toString(line) + ", stroke=" + toString( getStroke() ) + ", localBounds=" + localBounds + ", globalBounds=" + bounds );
//        System.out.println( "line=" + toString(line) + ", stroke=" + toString( getStroke() ) + ", globalBounds=" + bounds );
        globalBoundsChanged( bounds );

    }

    private void localLineTo( float x, float y, Point2D prevPoint ) {
        getPathReference().lineTo( x, y );
        firePropertyChange( PROPERTY_CODE_PATH, PROPERTY_PATH, null, getPathReference() );
        localUpdateBoundsFromPath( prevPoint );
        invalidatePaint();
    }

    protected void paint( PPaintContext paintContext ) {
        Paint p = getPaint();
        Graphics2D g2 = paintContext.getGraphics();

        if( p != null ) {
            g2.setPaint( p );
            g2.fill( getPathReference() );
        }

        if( getStroke() != null && getStrokePaint() != null ) {
            g2.setPaint( getStrokePaint() );
            g2.setStroke( getStroke() );
            int numPtsToUse = 30;
            if( pts.size() < numPtsToUse ) {
                g2.draw( getPathReference() );
            }
            else {
                GeneralPath path = new GeneralPath();
                path.moveTo( (float)getPreviousPoint( 0 ).getX(), (float)getPreviousPoint( 0 ).getY() );
                for( int i = 1; i < numPtsToUse; i++ ) {
                    path.lineTo( (float)getPreviousPoint( i ).getX(), (float)getPreviousPoint( i ).getY() );
                }
                g2.draw( path );
            }
        }
    }

    private Point2D getPreviousPoint( int i ) {
        return (Point2D)pts.get( pts.size() - 1 - i );
    }

    private boolean updatingBoundsFromPath = false;

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        if( updatingBoundsFromPath ) {
            return;
        }
        else {
            super.internalUpdateBounds( x, y, width, height );
        }
    }

    private void localUpdateBoundsFromPath( Point2D prevPoint ) {
        updatingBoundsFromPath = true;
        if( getPathReference() == null ) {
            resetBounds();
        }
        else {
//            Rectangle2D b = getPathBoundsWithStroke();
            Rectangle2D b = getBounds().createUnion( getLastSegmentBoundsWithStroke( prevPoint ) );
            setBounds( b.getX(), b.getY(), b.getWidth(), b.getHeight() );
        }
        updatingBoundsFromPath = false;
    }

    private Rectangle2D getLastSegmentBoundsWithStroke( Point2D prevPoint ) {
        if( getStroke() != null && prevPoint != null ) {
            Line2D.Double line = new Line2D.Double( getPathReference().getCurrentPoint(), prevPoint );
            return getStroke().createStrokedShape( line ).getBounds2D();

//            return newBounds;
        }
        else {
            return getPathReference().getBounds2D();
        }
    }

    protected void globalBoundsChanged( Rectangle2D bounds ) {
        repaintGlobalBounds( bounds );
    }

    protected void repaintGlobalBounds( Rectangle2D bounds ) {
        pCanvas.repaint( new PBounds( bounds ) );
    }

    protected void repaintGlobalBoundsImmediately( Rectangle2D bounds ) {
        pCanvas.paintImmediately( bounds.getBounds() );
    }

    private String toString( Line2D.Double line ) {
        return line.getP1() + " to " + line.getP2();
    }

    private String toString( Stroke stroke ) {
        if( stroke instanceof BasicStroke ) {
            BasicStroke basicStroke = (BasicStroke)stroke;
            return "width=" + basicStroke.getLineWidth();
        }
        else {
            return stroke.toString();
        }
    }

    public void invalidatePaint() {
        if( !doingLineTo ) {
            super.invalidatePaint();
        }
    }

    /**
     * Demonstration for IncrementalPPath.
     *
     * @param args
     */
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test IncrementalPPath" );
        PhetPCanvas contentPane = new PhetPCanvas();
        contentPane.setZoomEventHandler( new PZoomEventHandler() );
        PDebug.debugRegionManagement = true;
//        final PPath path = new PPath();
        final IncrementalPPath path = new IncrementalPPath( contentPane );
        Timer timer = new Timer( 30, new ActionListener() {

            float x_var;
            float y_var;
            boolean firstTime = true;

            public void actionPerformed( ActionEvent e ) {
                x_var += 1;
                y_var = 100 * (float)Math.sin( x_var / 30.0 ) + 150;
//                y_var = 100;
                System.out.println( "x = " + x_var + ", y=" + y_var );
                if( firstTime ) {
                    path.moveTo( x_var, y_var );
                    firstTime = false;
                }
                else {
                    path.lineTo( x_var, y_var );
                }
            }
        } );
        contentPane.addWorldChild( path );
        frame.setContentPane( contentPane );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
        timer.start();
        contentPane.requestFocus();
    }
}
