/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.*;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.model.Voltmeter;

/**
 * VoltmeterGraphic is the graphic representation of a voltmeter.
 * The meter's needle moves on a relative scale.
 * Registration point is at bottom-center of the meter body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterGraphic extends CompositePhetGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Pivot point, the point about which the needle pivots.
    private static final Point PIVOT_POINT = new Point( 85, 82 );
    
    // Needle
    private static final Color NEEDLE_COLOR = Color.BLUE;
    private static final int NEEDLE_LENGTH = 66;
    private static final Dimension NEEDLE_HEAD_SIZE = new Dimension( 12, 15 );
    private static final int NEEDLE_TAIL_WIDTH = 3;

    // Screw that holds the needle in place.
    private static final Color SCREW_COLOR = Color.BLUE;
    private static final int SCREW_DIAMETER = 9;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Voltmeter _voltmeterModel;
    private PhetShapeGraphic _needle;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * The registration point is at the bottom center of the meter's body.
     * 
     * @param component the parent Component
     * @param voltmeterModel
     */
    public VoltmeterGraphic( Component component, Voltmeter voltmeterModel ) {
        super( component );
        assert( component != null );
        assert( voltmeterModel != null );

        _voltmeterModel = voltmeterModel;
        _voltmeterModel.addObserver( this );
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Background, contains all of the static graphic components.
        {
            BackgroundGraphic background = new BackgroundGraphic( component );
            addGraphic( background );
        }
        
        // Needle
        {
            Point2D tail = new Point2D.Double( 0, 0 );
            Point2D tip = new Point2D.Double( 0, -NEEDLE_LENGTH );
            Arrow arrow = new Arrow( tail, tip, NEEDLE_HEAD_SIZE.height, NEEDLE_HEAD_SIZE.width, NEEDLE_TAIL_WIDTH );
            _needle = new PhetShapeGraphic( component );
            addGraphic( _needle );
            _needle.setShape( arrow.getShape() );
            _needle.setPaint( NEEDLE_COLOR );
            _needle.setLocation( PIVOT_POINT );
        }
        
        // Screw that holds the needle in place.
        {
            PhetShapeGraphic screw = new PhetShapeGraphic( component );
            addGraphic( screw );
            screw.setShape( new Ellipse2D.Double( 0, 0, SCREW_DIAMETER, SCREW_DIAMETER ) );
            screw.setPaint( SCREW_COLOR );
            screw.centerRegistrationPoint();
            screw.setLocation( PIVOT_POINT.x - 1, PIVOT_POINT.y - 1 );
        }
        
        // Registration point at bottom center.
        int rx = getWidth() / 2;
        int ry = getHeight();
        setRegistrationPoint( rx, ry );

        update();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _voltmeterModel.removeObserver( this );
        _voltmeterModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        setVisible( _voltmeterModel.isEnabled() );
        if ( isVisible() ) {     
            double angle = _voltmeterModel.getNeedleAngle();
            _needle.clearTransform();
            _needle.rotate( angle );
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * BackgroundGraphic creates a background image from a bunch of static 
     * graphic components.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class BackgroundGraphic extends PhetImageGraphic {
        
        // Layers
        private static final double RESISTOR_LAYER = 1;
        private static final double PROBE_LAYER = 2;
        private static final double BODY_LAYER = 3;
        private static final double TITLE_LAYER = 4;
        private static final double GUAGE_LAYER = 5;
        
        // Guage
        private static final int GUAGE_RADIUS = NEEDLE_LENGTH;
        private static final Color GUAGE_COLOR = Color.BLACK;
        private static final Stroke GUAGE_STROKE = new BasicStroke( 1f );
        
        // Title
        private static final Font TITLE_FONT = new PhetFont( 14 );
        private static final Color TITLE_COLOR = Color.WHITE;
        
        // Tick marks
        private static final double MINOR_TICK_SPACING = 180.0 / 40; // degrees
        private static final int MINOR_TICKS_PER_MAJOR_TICK = 4;
        private static final int MAJOR_TICK_LENGTH = 8;
        private static final int MINOR_TICK_LENGTH = 4;
        private static final Color MAJOR_TICK_COLOR = Color.BLACK;
        private static final Color MINOR_TICK_COLOR = Color.BLACK;
        private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
        private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
            
        /**
         * Sole constructor.
         * 
         * @param component
         */
        public BackgroundGraphic( Component component ) {
            super( component );
            
            // This will be flattened after we've added graphics to it.
            GraphicLayerSet graphicLayerSet = new GraphicLayerSet( component );
            RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            graphicLayerSet.setRenderingHints( hints );    
            
            // Meter body
            BufferedImage voltMeterImage = FaradayResources.getImage( FaradayConstants.VOLTMETER_IMAGE );
            PhetImageGraphic body = new PhetImageGraphic( component, voltMeterImage );
            graphicLayerSet.addGraphic( body, BODY_LAYER );
            
            // Title label
            {
                PhetTextGraphic title = new PhetTextGraphic( component, TITLE_FONT, FaradayStrings.TITLE_VOLTMETER, TITLE_COLOR );
                title.centerRegistrationPoint();
                title.setLocation( body.getWidth() / 2, body.getHeight() + 3 );
                graphicLayerSet.addGraphic( title, TITLE_LAYER );
            }
            
            // Meter guage, a 180-degree chorded arc.
            {
                PhetShapeGraphic guage = new PhetShapeGraphic( component );
                double diameter = 2 * GUAGE_RADIUS;
                guage.setShape( new Arc2D.Double( 0, 0, diameter, diameter, 0, 180, Arc2D.CHORD ) );
                guage.setBorderColor( GUAGE_COLOR );
                guage.setStroke( GUAGE_STROKE );
                guage.setRegistrationPoint( (int)(diameter / 2 ), (int)(diameter / 2 ) );
                guage.setLocation( PIVOT_POINT );
                graphicLayerSet.addGraphic( guage, GUAGE_LAYER  );
            }
            
            // Vertical line at zero-point of guage.
            {
                PhetShapeGraphic line = new PhetShapeGraphic( component );
                line.setShape( new Line2D.Double( 0, 0, 0, -GUAGE_RADIUS ) );
                line.setBorderColor( GUAGE_COLOR );
                line.setStroke( GUAGE_STROKE );
                line.setLocation( PIVOT_POINT );
                graphicLayerSet.addGraphic( line, GUAGE_LAYER  );
            }
            
            // Major and minor tick marks around the outside of the guage.
            {
                double angle = MINOR_TICK_SPACING;
                double tickCount = 1;
                while ( angle < 90 ) {
                    
                    // Major or minor tick mark?
                    int length = MINOR_TICK_LENGTH;
                    Color color = MINOR_TICK_COLOR;
                    Stroke stroke = MINOR_TICK_STROKE;
                    if ( tickCount % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                        length = MAJOR_TICK_LENGTH;
                        color = MAJOR_TICK_COLOR;
                        stroke = MAJOR_TICK_STROKE;
                    }
                    
                    // Positive tick mark
                    PhetShapeGraphic positiveTick = new PhetShapeGraphic( component );
                    positiveTick.setShape( new Line2D.Double( 0, 1, 0, length ) );
                    positiveTick.setBorderColor( color );
                    positiveTick.setStroke( stroke );
                    positiveTick.setLocation( PIVOT_POINT );
                    positiveTick.translate( 0, -GUAGE_RADIUS );
                    positiveTick.rotate( Math.toRadians( angle ) );
                    graphicLayerSet.addGraphic( positiveTick, GUAGE_LAYER );
                    
                    // Negative tick mark
                    PhetShapeGraphic negativeTick = new PhetShapeGraphic( component );
                    negativeTick.setShape( new Line2D.Double( 0, 1, 0, length ) );
                    negativeTick.setBorderColor( color );
                    negativeTick.setStroke( stroke );
                    negativeTick.setLocation( PIVOT_POINT );
                    negativeTick.translate( 0, -GUAGE_RADIUS );
                    negativeTick.rotate( Math.toRadians( -angle ) );
                    graphicLayerSet.addGraphic( negativeTick, GUAGE_LAYER );
                    
                    angle += MINOR_TICK_SPACING;
                    tickCount++;
                }
            }

            // Resistor
            BufferedImage resistorImage = FaradayResources.getImage( FaradayConstants.RESISTOR_IMAGE );
            PhetImageGraphic resistor = new PhetImageGraphic( component, resistorImage );
            resistor.centerRegistrationPoint();
            resistor.setLocation( body.getWidth() / 2, body.getHeight() + 40 );
            graphicLayerSet.addGraphic( resistor, RESISTOR_LAYER );
            
            // Probes
            {
                int xOffset = 53;
                int yOffset = body.getHeight() - 52;
                
                BufferedImage leftProbeImage = FaradayResources.getImage( FaradayConstants.VOLTMETER_PROBE_WHITE_IMAGE );
                PhetImageGraphic leftProbe = new PhetImageGraphic( component, leftProbeImage );
                leftProbe.setLocation( xOffset, yOffset );
                graphicLayerSet.addGraphic( leftProbe, PROBE_LAYER );
                
                BufferedImage rightProbeImage = FaradayResources.getImage( FaradayConstants.VOLTMETER_PROBE_BLACK_IMAGE );
                PhetImageGraphic rightProbe = new PhetImageGraphic( component, rightProbeImage );
                rightProbe.setLocation( xOffset + resistor.getWidth() + 2, leftProbe.getY() );
                graphicLayerSet.addGraphic( rightProbe, PROBE_LAYER );
            }
            
            // Flatten the graphic layer set.
            {
                Dimension size = graphicLayerSet.getSize();
                BufferedImage bufferedImage = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
                Graphics2D g2 = bufferedImage.createGraphics();
                graphicLayerSet.paint( g2 );
                setImage( bufferedImage );
            }
        }
    } // class Background
}