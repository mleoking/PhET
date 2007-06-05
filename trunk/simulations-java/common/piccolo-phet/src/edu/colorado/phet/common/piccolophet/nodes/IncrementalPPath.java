package edu.colorado.phet.common.piccolophet.nodes;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
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

/**
 * Author: Sam Reid
 * Jun 5, 2007, 4:23:15 PM
 */
public class IncrementalPPath extends PNode {
    private GeneralPath path = new GeneralPath();
    static float x;
    static float y;
    private Stroke stroke = new BasicStroke( 1 );
    private Paint strokePaint = Color.blue;
    private boolean updatingBoundsFromPath;
    private PCanvas pCanvas;

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test IncrementalPPath" );
        PhetPCanvas contentPane = new PhetPCanvas();
        PDebug.debugRegionManagement = true;
//        final PPath path = new PPath();
        final IncrementalPPath path = new IncrementalPPath( contentPane );
        path.moveTo( 0, 0 );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                x += 1;
                y = 100 * (float)Math.sin( x / 30.0 ) + 150;
                System.out.println( "x = " + x + ", y=" + y );
                path.lineTo( x, y );
            }
        } );
        contentPane.addScreenChild( path );
        frame.setContentPane( contentPane );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
        timer.start();
    }

    public IncrementalPPath( PCanvas pCanvas ) {
        this.pCanvas = pCanvas;
    }

    private void lineTo( double x, double y ) {
        Point2D currentPoint = path.getCurrentPoint();
        path.lineTo( (float)x, (float)y );
        Point2D newPoint = path.getCurrentPoint();
        pathChanged();
        Rectangle2D bounds = stroke.createStrokedShape( new Line2D.Double( currentPoint, newPoint ) ).getBounds2D();
        localToGlobal( bounds );
        pCanvas.repaint( new PBounds( bounds ) );
    }

    private void moveTo( double x, double y ) {
        path.moveTo( (float)x, (float)y );
        pathChanged();
    }

    private void pathChanged() {
        firePropertyChange( PPath.PROPERTY_CODE_PATH, PPath.PROPERTY_PATH, null, path );
        updateBoundsFromPath();
//        invalidatePaint();
    }

    private void updateBoundsFromPath() {
        updatingBoundsFromPath = true;
        if( path == null ) {
            resetBounds();
        }
        else {
            Rectangle2D b = getPathBoundsWithStroke();
            setBounds( b.getX(), b.getY(), b.getWidth(), b.getHeight() );
        }
        updatingBoundsFromPath = false;
    }

    //override to avoid repainting full bounds
    public boolean setBounds( double x, double y, double width, double height ) {
        PBounds bounds = getBoundsReference();
        if( bounds.x != x || bounds.y != y || bounds.width != width || bounds.height != height ) {
            bounds.setRect( x, y, width, height );

            if( width <= 0 || height <= 0 ) {
                bounds.reset();
            }

            internalUpdateBounds( x, y, width, height );
//            invalidatePaint();
            signalBoundsChanged();
            return true;
        }
        // Don't put any invalidating code here or else nodes with volatile bounds will
        // create a soft infinite loop (calling Swing.invokeLater()) when they validate
        // their bounds.
        return false;
    }

    public Rectangle2D getPathBoundsWithStroke() {
        if( stroke != null ) {
            return stroke.createStrokedShape( path ).getBounds2D();
        }
        else {
            return path.getBounds2D();
        }
    }

    public boolean intersects( Rectangle2D aBounds ) {
        if( super.intersects( aBounds ) ) {
            if( getPaint() != null && path.intersects( aBounds ) ) {
                return true;
            }
            else if( stroke != null && strokePaint != null ) {
                return stroke.createStrokedShape( path ).intersects( aBounds );
            }
        }
        return false;
    }

    //****************************************************************
    // Painting
    //****************************************************************

    protected void paint( PPaintContext paintContext ) {
        Paint p = getPaint();
        Graphics2D g2 = paintContext.getGraphics();

        if( p != null ) {
            g2.setPaint( p );
            g2.fill( path );
        }

        if( stroke != null && strokePaint != null ) {
            g2.setPaint( strokePaint );
            g2.setStroke( stroke );
            g2.draw( path );
        }
    }
}
