package edu.colorado.phet.common.piccolophet.nodes;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PanZoomWorldKeyHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDebug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 4:23:15 PM
 */
public class IncrementalPPath extends PPath {
    private PCanvas pCanvas;
    private boolean doingLineTo = false;

    public IncrementalPPath( PCanvas pCanvas ) {
        this.pCanvas = pCanvas;
    }

    public void lineTo( float x, float y ) {
        Point2D currentPoint = getPathReference().getCurrentPoint();
        doingLineTo = true;
        super.lineTo( x, y );
        doingLineTo = false;
        Point2D newPoint = getPathReference().getCurrentPoint();

        Line2D.Double line = new Line2D.Double( currentPoint, newPoint );
        Rectangle2D bounds = getStroke().createStrokedShape( line ).getBounds2D();
        Rectangle2D.Double localBounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        localToGlobal( bounds );

//        System.out.println( "line=" + toString(line) + ", stroke=" + toString( getStroke() ) + ", localBounds=" + localBounds + ", globalBounds=" + bounds );
        System.out.println( "line=" + toString(line) + ", stroke=" + toString( getStroke() ) + ", globalBounds=" + bounds );
        pCanvas.repaint( new PBounds( bounds ) );
    }

    private String toString( Line2D.Double line ) {
        return line.getP1()+" to "+line.getP2();
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
