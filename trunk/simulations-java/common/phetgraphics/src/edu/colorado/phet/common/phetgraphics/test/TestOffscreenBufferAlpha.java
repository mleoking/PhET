package edu.colorado.phet.common.phetgraphics.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.application.DeprecatedPhetApplicationLauncher;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;

/**
 * TestOffscreenBufferAlpha tests support for alpha blending in the
 * offscreen buffer of ApparatusPanel2.
 * <p/>
 * If this buffer is a BufferedImage of TYPE_INT_RGB, then alpha blending
 * does not occur on Macintosh. Using TYPE_INT_RGBA results in a performance
 * hit (on all platforms?)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestOffscreenBufferAlpha {

    private static final boolean USE_OFFSCREEN_BUFFER = true;

    public static void main( String args[] ) throws IOException {
        TestOffscreenBufferAlpha test = new TestOffscreenBufferAlpha( args );
    }

    public TestOffscreenBufferAlpha( String[] args ) throws IOException {

        // Set up the application descriptor.
        String title = "TestOffscreenBufferAlpha";
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 300, 300 );
        IClock clock = new SwingClock( 40, 1 );
        PhetGraphicsModule module = new TestModule( clock );

        // Create and start the application.
        DeprecatedPhetApplicationLauncher app = new DeprecatedPhetApplicationLauncher( args, "title", "desc", "version" );
        app.addModule( module );
        app.startApplication();
    }

    private class TestModule extends PhetGraphicsModule {
        public TestModule( IClock clock ) {
            super( "Test Module", clock );

            // Model
            BaseModel model = new BaseModel();
            setModel( model );

            // Apparatus Panel
            ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
            apparatusPanel.setBackground( Color.WHITE );
            apparatusPanel.setUseOffscreenBuffer( USE_OFFSCREEN_BUFFER );
            setApparatusPanel( apparatusPanel );

            // Rectangle
            PhetShapeGraphic rectangleGraphic = new PhetShapeGraphic( apparatusPanel );
            rectangleGraphic.setShape( new Rectangle( 0, 0, 50, 50 ) );
            rectangleGraphic.setPaint( Color.YELLOW );
            rectangleGraphic.setLocation( 100, 100 );
            apparatusPanel.addGraphic( rectangleGraphic, 1 );

            // Circle
            final PhetShapeGraphic circleGraphic = new PhetShapeGraphic( apparatusPanel );
            circleGraphic.setShape( new Ellipse2D.Double( -35, -35, 70, 70 ) );
            circleGraphic.setPaint( new Color( 255, 0, 0, 100 ) ); // white with alpha
            circleGraphic.setLocation( 100, 100 );
            apparatusPanel.addGraphic( circleGraphic, 2 );

            // Instructions
            Font font = new Font( null, Font.PLAIN, 14 );
            String message = "The red circle should be transparent.";
            PhetTextGraphic textGraphic = new PhetTextGraphic( apparatusPanel, font, message, Color.BLACK );
            textGraphic.setLocation( 10, 30 );
            apparatusPanel.addGraphic( textGraphic, 3 );
        }
    }
}
