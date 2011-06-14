// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Tests PLayoutUtils.alignInside.
 * <p/>
 * This is not an automated test.
 * To confirm that alignInside is working properly, change the alignment args.
 * To confirm that origin offset is accounted for, change the (x,y) coordinates of the test Rectangle2Ds.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestAlignInside extends JFrame {

    public TestAlignInside() {
        super( TestAlignInside.class.getName() );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );

        PPath pathNode1 = new PPath( new Rectangle2D.Double( -200, -400, 400, 200 ) );
        canvas.addWorldChild( pathNode1 );

        PPath pathNode2 = new PPath( new Rectangle2D.Double( 100, 200, 500, 300 ) );
        pathNode2.setStrokePaint( Color.RED );
        canvas.addWorldChild( pathNode2 );

        pathNode2.setOffset( 100, 100 );
        PNodeLayoutUtils.alignInside( pathNode1, pathNode2, SwingConstants.BOTTOM, SwingConstants.CENTER );

        setContentPane( canvas );
        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestAlignInside();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
