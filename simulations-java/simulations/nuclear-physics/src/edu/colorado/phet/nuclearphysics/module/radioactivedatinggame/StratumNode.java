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
public class StratumNode extends PNode {
    public StratumNode( Shape topLine, Shape bottomLine, Color color ) {

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
    }

    static Random random = new Random();

    public static GeneralPath newGeneralPath( double x1, double y1, double x2, double y2 ) {
        DoubleGeneralPath path = new DoubleGeneralPath( x1, y1 );
        path.curveTo( ( x1 + x2 ) / 3 + random.nextDouble() * 10, ( y1 + y2 ) / 2 + random.nextDouble() * 30,
                      2 * ( x1 + x2 ) / 3 + random.nextDouble() * 10, ( y1 + y2 ) / 2 + random.nextDouble() * 30,
                      x2, y2 );
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

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }
}
