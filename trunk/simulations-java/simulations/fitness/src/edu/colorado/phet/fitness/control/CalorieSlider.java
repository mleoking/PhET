package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Created by: Sam
 * Apr 17, 2008 at 12:14:23 PM
 */
public class CalorieSlider extends PNode {
    public static double WIDTH = 200;
    public static double HEIGHT = 30;
    private PhetPPath arrowNode;
    private PText titleNode;
    private PhetPPath borderNode;

    public CalorieSlider( String title, Color color, boolean arrowDown ) {
        titleNode = new PText( title );
        addChild( titleNode );
        borderNode = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ), new BasicStroke( 1.5f ), Color.black );
        addChild( borderNode );
        arrowNode = new PhetPPath( new Arrow( new Point2D.Double( 0, arrowDown ? 0 : HEIGHT ), new Point2D.Double( 0, arrowDown ? HEIGHT : 0 ), 12, 12, 7 ).getShape(), color );

        arrowNode.addInputEventListener( new CursorHandler() );
        arrowNode.addInputEventListener( new PDragEventHandler() {
            protected void drag( PInputEvent event ) {
//                    super.drag( event );
                PDimension d = event.getDeltaRelativeTo( getDraggedNode() );
                getDraggedNode().localToParent( d );
                getDraggedNode().offset( d.getWidth(), 0 );
                if ( getDraggedNode().getOffset().getX() < 0 ) {
                    getDraggedNode().setOffset( 0, getDraggedNode().getOffset().getY() );
                }
                else if ( getDraggedNode().getOffset().getX() > WIDTH ) {
                    getDraggedNode().setOffset( WIDTH, getDraggedNode().getOffset().getY() );
                }
            }
        } );
//            titleNode.setOffset( 0, -titleNode.getHeight() );
        borderNode.setOffset( 0, titleNode.getHeight() );
        arrowNode.setOffset( 0, titleNode.getHeight() );

        PText low = new PText( "None" );
        addChild( low );
        low.setOffset( -low.getFullBounds().getWidth(), borderNode.getFullBounds().getCenterY() - low.getFullBounds().getHeight() / 2 );
        PText lots = new PText( "Lots" );
        addChild( lots );
        lots.setOffset( borderNode.getFullBounds().getWidth(), borderNode.getFullBounds().getCenterY() - low.getFullBounds().getHeight() / 2 );

        addChild( arrowNode );
    }

    public void setArrowLocation( double x ) {
        System.out.println( "pre x=" + arrowNode.getOffset().getX() );
//        arrowNode.setOffset( arrowNode.getOffset().getX()+1, arrowNode.getOffset().getY() );
        arrowNode.setOffset( x, arrowNode.getOffset().getY() );
        System.out.println( "post x=" + arrowNode.getOffset().getX() );
    }

    public void addRegion( int x0, int x1, Color color ) {
        PhetPPath path = new PhetPPath( new Rectangle2D.Double( x0, 0, x1, HEIGHT ), color );
        path.setOffset( 0, titleNode.getHeight() );
        addChild( path );
        borderNode.moveToFront();
        arrowNode.moveToFront();
    }
}
