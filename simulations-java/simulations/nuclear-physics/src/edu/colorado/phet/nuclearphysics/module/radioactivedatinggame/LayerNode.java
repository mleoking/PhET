package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 8, 2009
 * Time: 11:22:48 AM
 */
public class LayerNode extends PNode {
    public LayerNode( Shape topLine, Shape bottomLine, Color color ) {
//        Rectangle2D.Double aDouble = new Rectangle2D.Double( 0, 0, 400, 100 );

        Point2D topLeft = new Point2D.Double( topLine.getBounds2D().getX(), topLine.getBounds2D().getY() );
        Point2D bottomRight = new Point2D.Double( bottomLine.getBounds2D().getMaxX(), bottomLine.getBounds2D().getMaxY() );

        DoubleGeneralPath faceShape = new DoubleGeneralPath( topLeft );
        faceShape.append( topLine, true );
        faceShape.lineTo( bottomRight );

        faceShape.append( new ReversePathIterator( bottomLine.getPathIterator( new AffineTransform() ) ), true );
        faceShape.lineTo( topLeft );
        GeneralPath path1 = faceShape.getGeneralPath();
        path1.closePath();
        PhetPPath path = new PhetPPath( path1, color, new BasicStroke( 2 ), Color.black );
        addChild( path );

//        double dx = 50;
//        double dy = -50;
//        Point2D startPoint = new Point2D.Double( path.getFullBounds().getMaxX(), path.getFullBounds().getY() );
//        DoubleGeneralPath edgePath = new DoubleGeneralPath( startPoint );
//        edgePath.lineToRelative( dx, dy );
//        edgePath.lineToRelative( 0, path.getFullBounds().getHeight() );
//        edgePath.lineToRelative( -dx, -dy );
//        edgePath.lineTo( startPoint );
//        PhetPPath edge = new PhetPPath( edgePath.getGeneralPath(), color.darker(), new BasicStroke( 2 ), Color.black );
//        addChild( edge );

    }

    static Random random = new Random();

    public static GeneralPath newGeneralPath( double x1, double y1, double x2, double y2 ) {
        DoubleGeneralPath path = new DoubleGeneralPath( x1, y1 );
        path.curveTo( ( x1 + x2 ) / 3 + random.nextDouble() * 10, ( y1 + y2 ) / 2 + random.nextDouble() * 30,
                      2 * ( x1 + x2 ) / 3 + random.nextDouble() * 10, ( y1 + y2 ) / 2 + random.nextDouble() * 30,
                      x2, y2 );
//        path.lineTo( ( x2 + x1 ) / 2, y1 + 5 );
//        path.lineTo( x2, y2 );
        return path.getGeneralPath();
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PhetPCanvas contentPane = new PhetPCanvas();

        ArrayList<GeneralPath> lines = new ArrayList<GeneralPath>();
        lines.add( newGeneralPath( 0, 0, 400, 0 ) );
        lines.add( newGeneralPath( 0, 0, 400, 0 ) );
        lines.add( newGeneralPath( 0, 0, 400, 0 ) );
        lines.add( newGeneralPath( 0, 0, 400, 0 ) );
        lines.add( newGeneralPath( 0, 0, 400, 0 ) );

        int sep = 0;
//        addLayer( contentPane, new LayerNode( lines.get( 0 ), lines.get( 1 ), Color.yellow, 100 ), 0 );
//        addLayer( contentPane, new LayerNode( lines.get( 1 ), lines.get( 2 ), Color.green, 100 ), 100 + sep );
//        addLayer( contentPane, new LayerNode( lines.get( 2 ), lines.get( 3 ), Color.blue, 100 ), 200 + sep * 2 );
//        addLayer( contentPane, new LayerNode( lines.get( 3 ), lines.get( 4 ), Color.red, 100 ), 300 + sep * 3 );

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

    private static void addLayer( PhetPCanvas contentPane, LayerNode greenLayer, int y ) {
        greenLayer.setOffset( 0 + 50, y + 50 );
        contentPane.addScreenChild( greenLayer );
    }
}
