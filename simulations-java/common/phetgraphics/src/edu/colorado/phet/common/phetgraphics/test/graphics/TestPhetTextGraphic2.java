package edu.colorado.phet.common.phetgraphics.test.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.DeprecatedPhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic2;

/**
 * TestPhetTextGraphic2 test the bounds, justifications, and registration point
 * features of PhetTextGraphic22.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPhetTextGraphic2 {

    public static void main( String args[] ) throws IOException {
        TestPhetTextGraphic2 test = new TestPhetTextGraphic2( args );
    }

    public TestPhetTextGraphic2( String[] args ) throws IOException {

        // Clock
        double timeStep = 1;
        double frameRate = 25; // fps
        int waitTime = (int) ( 1000 / frameRate ); // milliseconds
        boolean isFixed = true;
        IClock clock = new SwingClock( waitTime, timeStep );

        String title = "TestPhetTextGraphic2";
        String description = "";
        String version = "0.0";
        boolean useClockControlPanel = false;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

        DeprecatedPhetApplicationLauncher app = new DeprecatedPhetApplicationLauncher( args,
                                                                       title, description, version, frameSetup );

        PhetGraphicsModule module = new TestModule( clock );

        app.setModules( new PhetGraphicsModule[]{module} );

        app.startApplication();
    }

    private class TestModule extends PhetGraphicsModule {

        public TestModule( IClock clock ) {
            super( "TestModule", clock );

            int textLayer = 1;
            int debugLayer = 2;

            // Model
            BaseModel model = new BaseModel();
            setModel( model );

            // Apparatus Panel
            ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
            apparatusPanel.setBackground( Color.BLACK );
            setApparatusPanel( apparatusPanel );

            // Bounds debugger
            BoundsDebugger boundsDebugger = new BoundsDebugger( apparatusPanel );
            apparatusPanel.addGraphic( boundsDebugger, debugLayer );

            // Title
            PhetTextGraphic2 title = new PhetTextGraphic2( apparatusPanel );
            title.setFont( new PhetFont( Font.PLAIN, 24 ) );
            title.setColor( Color.YELLOW );
            title.setText( "PhetTextGraphic2: test of bounds, justifications, and registration points" );
            title.setLocation( 50, 50 );
            apparatusPanel.addGraphic( title, textLayer );

            // A bunch of PhetTextGraphic2s

            Font font = new PhetFont( Font.PLAIN, 18 );
            Color color = Color.LIGHT_GRAY;

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = NORTH_WEST", color );
                t.setLocation( 50, 150 );
                t.setJustification( PhetTextGraphic2.NORTH_WEST );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = NORTH", color );
                t.setLocation( 500, 150 );
                t.setJustification( PhetTextGraphic2.NORTH );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = NORTH_EAST", color );
                t.setLocation( 900, 150 );
                t.setJustification( PhetTextGraphic2.NORTH_EAST );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = WEST", color );
                t.setLocation( 50, 300 );
                t.setJustification( PhetTextGraphic2.WEST );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = CENTER", color );
                t.setLocation( 500, 300 );
                t.setJustification( PhetTextGraphic2.CENTER );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = EAST", color );
                t.setLocation( 900, 300 );
                t.setJustification( PhetTextGraphic2.EAST );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = SOUTH_WEST", color );
                t.setLocation( 50, 450 );
                t.setJustification( PhetTextGraphic2.SOUTH_WEST );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = SOUTH", color );
                t.setLocation( 500, 450 );
                t.setJustification( PhetTextGraphic2.SOUTH );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = SOUTH_EAST", color );
                t.setLocation( 900, 450 );
                t.setJustification( PhetTextGraphic2.SOUTH_EAST );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "justification = NONE", color );
                t.setLocation( 50, 600 );
                t.setJustification( PhetTextGraphic2.NONE );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }

            {
                PhetTextGraphic2 t = new PhetTextGraphic2( apparatusPanel, font, "custom registration point = bottom center", color );
                t.setLocation( 600, 600 );
                t.setRegistrationPoint( t.getWidth() / 2, t.getHeight() );
                apparatusPanel.addGraphic( t, textLayer );
                boundsDebugger.add( t );
            }
        }
    }

    /**
     * BoundsDebugger displays the bounds and locations of a set of PhetGraphics.
     * It is intended for use in debugging phetcommon and client applications.
     */
    public class BoundsDebugger extends PhetGraphic {

        //----------------------------------------------------------------------------
        // Instance data
        //----------------------------------------------------------------------------

        final Color _boundsColor = Color.GREEN;
        final Stroke _boundsStroke = new BasicStroke( 1f );
        final Color _locationColor = Color.RED;
        final Stroke _locationStroke = new BasicStroke( 1f );
        final Dimension _locationSize = new Dimension( 10, 10 );
        private ArrayList _graphics = new ArrayList();

        //----------------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------------

        public BoundsDebugger( Component component ) {
            super( component );
            setIgnoreMouse( true );
        }

        public void add( PhetGraphic graphic ) {
            _graphics.add( graphic );
        }

        public void remove( PhetGraphic graphic ) {
            _graphics.remove( graphic );
        }

        protected Rectangle determineBounds() {
            Rectangle bounds = null;
            for ( int i = 0; i < _graphics.size(); i++ ) {
                PhetGraphic graphic = (PhetGraphic) _graphics.get( i );
                if ( bounds == null ) {
                    bounds = new Rectangle( graphic.getBounds() );
                }
                else {
                    bounds.union( graphic.getBounds() );
                }
            }
            return bounds;
        }

        public void paint( Graphics2D g2 ) {
            if ( isVisible() ) {
                saveGraphicsState( g2 );
                for ( int i = 0; i < _graphics.size(); i++ ) {
                    // Get the rendering details for the next graphic.
                    PhetGraphic graphic = (PhetGraphic) _graphics.get( i );
                    Rectangle bounds = graphic.getBounds();
                    Point location = graphic.getLocation();

                    // Outline the bounds.
                    g2.setStroke( _boundsStroke );
                    g2.setPaint( _boundsColor );
                    g2.draw( bounds );

                    // Convert the graphic's location to screen coordinates.
                    AffineTransform transform = getNetTransform();
                    Point2D transformedLocation = new Point2D.Double();
                    transform.transform( location, transformedLocation );
                    int x = (int) transformedLocation.getX();
                    int y = (int) transformedLocation.getY();

                    // Draw a cross, centered at the graphic's location.
                    g2.setStroke( _locationStroke );
                    g2.setPaint( _locationColor );
                    g2.drawLine( x, y - _locationSize.height / 2, x, y + _locationSize.height / 2 );
                    g2.drawLine( x - _locationSize.width / 2, y, x + _locationSize.width / 2, y );
                }
                restoreGraphicsState();
            }
        }
    }
}