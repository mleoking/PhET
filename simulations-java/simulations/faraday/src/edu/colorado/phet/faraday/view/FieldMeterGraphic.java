/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.model.FieldMeter;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * FieldMeterGraphic is a meter that measure a magnetic field.
 * It displays the magnitude (total and X/Y components) and direction of
 * the magnetic field at a point in 2D space. Values are displayed in
 * a coordinate system in which +X is to the right, +Y is up, and 
 * +direction is counterclockwise, and 0 degrees points down the
 * positive X axis.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FieldMeterGraphic extends CompositePhetGraphic
    implements SimpleObserver, ApparatusPanel2.ChangeListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Center of the crosshairs (MUST MATCH IMAGE FILE!)
    private static final Point CROSSHAIRS_LOCATION = new Point( 71, 27 );
    
    // Bottom center of the title (MUST MATCH IMAGE FILE!)
    private static final Point TITLE_LOCATION = new Point( 0, 84 );
    
    // Bottom right of the top-most field (MUST MATCH IMAGE FILE!)
    private static final Point FIELD_LOCATION = new Point( 35, 112 );
    
    // Horizontal spacing between fields (MUST MATCH IMAGE FILE!)
    private static final int FIELD_SPACING = 24;
    
    // Title color
    private static final Color TITLE_COLOR = Color.WHITE;
    
    // Field color
    private static final Color FIELD_COLOR = Color.WHITE;
    
    // Title font
    private static final Font TITLE_FONT = new PhetFont( 15 );
    
    // Field font
    private static final Font FIELD_FONT = new PhetFont( 15 );
    
    // Field format
    private static final String MAGNITUDE_FORMAT = "###0.00";
    private static final String ANGLE_FORMAT = "###0.00";
    
    // Offensive formatted values and their corrections. Dependent on field formats !!
    private static final String STRING_MAGNITUDE_NEGATIVE_ZERO = "-0.00";
    private static final String STRING_MAGNITUDE_POSITIVE_ZERO = "0.00";
    private static final String STRING_ANGLE_NEGATIVE_ZERO = "-0.00";
    private static final String STRING_ANGLE_POSITIVE_ZERO = "0.00";
    private static final String STRING_ANGLE_NEGATIVE_PI = "-180.00";
    private static final String STRING_ANGLE_POSITIVE_PI = "180.00";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FieldMeter _fieldMeterModel;
    private PhetTextGraphic _bText, _bxText, _byText, _angleText;
    private NumberFormat _magnitudeFormatter, _angleFormatter;
    private Vector2D _fieldVector; // reusable vector
    private FaradayMouseHandler _mouseHandler;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param fieldMeterModel
     */
    public FieldMeterGraphic( Component component, FieldMeter fieldMeterModel ) {
        super( component );
        assert( component != null );
        assert( fieldMeterModel != null );
        
        _fieldMeterModel = fieldMeterModel;
        fieldMeterModel.addObserver( this );
        
        _magnitudeFormatter = new DecimalFormat( MAGNITUDE_FORMAT );
        _angleFormatter = new DecimalFormat( ANGLE_FORMAT );
        
        _fieldVector = new Vector2D();
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Probe body, registration point at center of crosshairs.
        BufferedImage fieldMeterImage = FaradayResources.getImage( FaradayConstants.FIELD_METER_IMAGE );
        PhetImageGraphic body = new PhetImageGraphic( component, fieldMeterImage );
        body.setRegistrationPoint( CROSSHAIRS_LOCATION.x, CROSSHAIRS_LOCATION.y );
        addGraphic( body );
        
        // Title text, registration point at bottom center.
        PhetTextGraphic titleText = new PhetTextGraphic( component, TITLE_FONT, FaradayStrings.TITLE_FIELD_METER, TITLE_COLOR, 0, 0 );
        int width = titleText.getBounds().width;
        int height = titleText.getBounds().height;
        titleText.setRegistrationPoint( width /2, height ); // bottom center
        titleText.setLocation( TITLE_LOCATION.x, TITLE_LOCATION.y );
        addGraphic( titleText );
        
        // Fields, registration point is set in update method.
        {
            int x = FIELD_LOCATION.x;
            int y = FIELD_LOCATION.y;
            
            // B text
            _bText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _bText );

            // Bx text
            _bxText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _bxText );

            // By text
            _byText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _byText );

            // Angle text
            _angleText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _angleText );
        }
        
        // Setup interactivity.
        _mouseHandler = new FaradayMouseHandler( _fieldMeterModel, this );
        super.setCursorHand();
        super.addMouseInputListener( _mouseHandler );
        
        // Synchronize view with model.
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _fieldMeterModel.removeObserver( this );
        _fieldMeterModel = null;
    }
    
    //----------------------------------------------------------------------------
    // CompositePhetGraphic overrides
    //----------------------------------------------------------------------------
    
    /**
     * When the graphic is made visible, update it to match the model.
     * 
     * @param visible true or false
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        super.setVisible( _fieldMeterModel.isEnabled() );
        if ( isVisible() ) {
           
            // Set location.
            setLocation( (int) _fieldMeterModel.getX(), (int) _fieldMeterModel.getY() );
            
            // Get the field vector from the model.
            _fieldMeterModel.getStrength( _fieldVector /* output */ );
            
            // Get the components, adjust the coordinate system.
            double b = _fieldVector.getMagnitude();
            double bx = _fieldVector.getX();
            double by = -( _fieldVector.getY() ); // +Y is up
            double angle = -( _fieldVector.getAngle() );  // +angle is counterclockwise
            
            // Convert the angle to a value in the range -180...180 degrees.
            {
                // Normalize the angle to the range -360...360 degrees
                if ( Math.abs( angle ) >= ( 2 * Math.PI ) ) {
                    int sign = ( angle < 0 ) ? -1 : +1;
                    angle = sign * ( angle % ( 2 * Math.PI ) );
                }
                
                // Convert to an equivalent angle in the range -180...180 degrees.
                if ( angle < -Math.PI ) {
                    angle = angle + ( 2 * Math.PI );
                }
                else if ( angle > Math.PI ) {
                    angle = angle - ( 2 * Math.PI );
                }
            }
    
            // Format the values.
            String bString = _magnitudeFormatter.format( b );
            String bxString = _magnitudeFormatter.format( bx );
            String byString = _magnitudeFormatter.format( by );
            String angleString = _angleFormatter.format( Math.toDegrees( angle ) );
            
            /*
             * Correct some offensive looking values.
             * We need to perform this format-dependent check on the strings
             * because a value like -0.00005 will display as -0.00.
             */
            {
                // B
                if ( bString.equals( STRING_MAGNITUDE_NEGATIVE_ZERO ) ) {
                    bString = STRING_MAGNITUDE_POSITIVE_ZERO;
                }

                if ( bString.equals( STRING_MAGNITUDE_POSITIVE_ZERO ) ) {
                    // If B displays 0, all others should display zero.
                    bxString = STRING_MAGNITUDE_POSITIVE_ZERO;
                    byString = STRING_MAGNITUDE_POSITIVE_ZERO;
                    angleString = STRING_ANGLE_POSITIVE_ZERO;
                }
                else {
                    // Bx
                    if ( bxString.equals( STRING_MAGNITUDE_NEGATIVE_ZERO ) ) {
                        bxString = STRING_MAGNITUDE_POSITIVE_ZERO;
                    }

                    // By
                    if ( byString.equals( STRING_MAGNITUDE_NEGATIVE_ZERO ) ) {
                        byString = STRING_MAGNITUDE_POSITIVE_ZERO;
                    }

                    // Theta
                    if ( angleString.equals( STRING_ANGLE_NEGATIVE_ZERO ) ) {
                        angleString = STRING_ANGLE_POSITIVE_ZERO;
                    }
                    else if ( angleString.equals( STRING_ANGLE_NEGATIVE_PI ) ) {
                        angleString = STRING_ANGLE_POSITIVE_PI;
                    }
                }
            }
            
            // Set the text graphics.
            _bText.setText( bString );
            _bxText.setText( bxString );
            _byText.setText( byString );
            _angleText.setText( angleString );
            
            // Right justify the text graphics (registration point at bottom right)
            _bText.setRegistrationPoint( _bText.getWidth(), _bText.getHeight() );
            _bxText.setRegistrationPoint( _bxText.getWidth(), _bxText.getHeight() );
            _byText.setRegistrationPoint( _byText.getWidth(), _byText.getHeight() );
            _angleText.setRegistrationPoint( _angleText.getWidth(), _angleText.getHeight() );
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * Informs the mouse handler of changes to the apparatus panel size.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _mouseHandler.setDragBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
}