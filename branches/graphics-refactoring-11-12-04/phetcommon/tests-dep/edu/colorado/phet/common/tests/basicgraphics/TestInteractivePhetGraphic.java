/**
 * Class: TestCompositeGraphic
 * Package: edu.colorado.phet.common.tests.basicgraphics
 * Author: Another Guy
 * Date: Nov 12, 2004
 */
package edu.colorado.phet.common.tests.basicgraphics;

import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class TestInteractivePhetGraphic {
    static int x = 300;
    static int y = 300;

    public static final void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel() {
            public void repaint( int x, int y, int width, int height ) {
                paintImmediately( x, y, width, height );
            }
        };
        final CompositePhetGraphic compositePhetGraphic = new CompositePhetGraphic( ap );
        final PhetShapeGraphic circleGraphic = new PhetShapeGraphic( ap, new Ellipse2D.Double( 50, 50, 75, 300 ), Color.blue );
        PhetShapeGraphic squareGraphic = new PhetShapeGraphic( ap, new Rectangle2D.Double( 400, 400, 50, 50 ), Color.red );
        compositePhetGraphic.addGraphic( circleGraphic );
        compositePhetGraphic.addGraphic( squareGraphic );

        ap.addGraphic( compositePhetGraphic );
        ap.addGraphicsSetup( new BasicGraphicsSetup() );

        JFrame frame = new JFrame();
        frame.setContentPane( ap );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        frame.setVisible( true );

        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
        RepaintDebugGraphic repaintDebugGraphic = new RepaintDebugGraphic( ap, clock );
        repaintDebugGraphic.setActive( true );
        clock.start();

        compositePhetGraphic.setCursorHand();
        compositePhetGraphic.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                compositePhetGraphic.setLocation( compositePhetGraphic.getLocation().x + (int)dx, compositePhetGraphic.getLocation().y + (int)dy );
            }
        } );

        JPopupMenu jpm = new JPopupMenu();
        jpm.add( new JMenuItem( "Delete" ) );
        jpm.add( new JMenuItem( "Rotate" ) );
        jpm.addSeparator();
        jpm.add( new JMenuItem( "Change Color" ) );

        compositePhetGraphic.setPopupMenu( jpm );

        final PhetShadowTextGraphic textGraphic = new PhetShadowTextGraphic( ap, "Drag Me", new Font( "Lucida Sans", Font.PLAIN, 42 ), x, y, Color.red, 1, 1, Color.black );
        textGraphic.setCursorHand();
        textGraphic.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                x += dx;
                y += dy;
                textGraphic.setPosition( x, y );
            }
        } );

        ap.addGraphic( textGraphic );
    }
}
