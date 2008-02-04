package edu.colorado.phet.curvefitting;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 4, 2008
 * Time: 10:50:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CurveFittingSimulationPanel extends PhetPCanvas {
    private PPath path = new PPath( new Line2D.Double( 0, 0, 100, 100 ) );
    //    private PPath point1;
    //    private PPath point2;
    private ArrayList points = new ArrayList();

    public CurveFittingSimulationPanel() {
//        PPath path = new PPath( new Line2D.Double( 0, 0, 100, 100 ) );


        points.add( createPoint( 200, 200, Color.black ) );
        points.add( createPoint( 300, 320, Color.blue ) );
        points.add( createPoint( 300, 150, Color.blue ) );

        addScreenChild( path );
        for( int i = 0; i < points.size(); i++ ) {
            PPath pPath = (PPath)points.get( i );
            addScreenChild( pPath );
        }
        updateLine();
    }

    private PPath createPoint( int x1, int y1, Color paint ) {
        PPath point1 = new PPath( new Ellipse2D.Double( x1, y1, 10, 10 ) );

        point1.setPaint( paint );
        point1.addInputEventListener( new CursorHandler() );
        point1.addInputEventListener( new PDragEventHandler() {
            protected void drag( PInputEvent event ) {
                super.drag( event );    //To change body of overridden methods use File | Settings | File Templates.
                updateLine();
            }
        } );
        return point1;
    }

    private void updateLine() {
        PPath point1 = (PPath)points.get( 0 );
        PPath point2 = (PPath)points.get( 1 );
        path.setPathTo( new Line2D.Double( point1.getFullBounds().getCenter2D(), point2.getFullBounds().getCenter2D() ) );
    }

}
