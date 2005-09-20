/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.control;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.PhotonSource;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.Battery;

/**
 * SpectrumSliderKnob is the knob on a SpectrumSlider.
 * The origin is at the knob's tip.
 * The default orientation is with the tip of the arrow pointing straight up.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SpectrumSliderKnob extends CompositePhetGraphic {
//public class SpectrumSliderKnob extends PhetShapeGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Location of the knob's tip.
    private Point _location;
    // Size of the knob
    private Dimension _size;
    // Rotation angle
    private double _angle;
    // ShapeGraphic for the knob
    private PhetShapeGraphic knobShape;
//    private Readout readout;
    private SpectrumSlider parentSlider;
    private WavelengthReadout wavelengthReadout;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Creates a white knob with a black border, located at (0,0).
     *
     * @param component the parent Component
     * @param size      dimensions in pixels
     * @param angle     rotation angle, in radians
     */
    public SpectrumSliderKnob( Component component, Dimension size, double angle, SpectrumSlider parentSlider ) {

        super( component );
//        super( component, null, null );
        this.parentSlider = parentSlider;

        _location = new Point( 0, 0 );
        _size = new Dimension( size );
        _angle = angle;

        //  Request antialiasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.setRenderingHints( hints );

        // Make the shape for the knob
        knobShape = new PhetShapeGraphic( component, null, null );
        knobShape.setPaint( Color.WHITE );
        knobShape.setStroke( new BasicStroke( 1f ) );
        knobShape.setBorderColor( Color.BLACK );
        addGraphic( knobShape );

        // Make the text readout
//        readout = new Readout( component );
//        addGraphic( readout );

        wavelengthReadout = new WavelengthReadout( component );
        addGraphic( wavelengthReadout );

        updateShape();
    }

//    private class Readout extends CompositePhetGraphic {
//        private Rectangle box;
//        private WavelengthReadout wavelengthReadout;
//
//        public Readout( Component component ) {
//            super( component );
//            box = new Rectangle( 0, 0, 10,10);
//            PhetShapeGraphic background = new PhetShapeGraphic( component,
//                                                                box,
//                                                                Color.white,
//                                                                new BasicStroke( 1 ),
//                                                                Color.black );
//            addGraphic( background );
//
//            wavelengthReadout = new WavelengthReadout( component );
//            addGraphic( wavelengthReadout );
//        }
//
//        public void setWidth( int width ) {
//            wavelengthReadout.setWidth( width );
////            box.setBounds( 0, 0, width-6, (int)(knobShape.getBounds().getHeight() * 0.67 ) - 6 );
////            setRegistrationPoint( (int)box.getWidth() / 2, -(int)(knobShape.getBounds().getHeight() * 0.33 ));
//
//            setBoundsDirty();
//            repaint();
//        }
//    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Implements the method in the ISliderKnob interface
     *
     * @return
     */
    public PhetGraphic getPhetGraphic() {
        return this;
    }

    /**
     * Sets the knob's location.
     *
     * @param location the location
     */
    public void setLocation( Point location ) {

        super.translate( -_location.x, -_location.y );
        _location = new Point( location );
        super.translate( location.x, location.y );
        updateShape();
    }

    /**
     * Convenience method for setting the knob's location.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setLocation( int x, int y ) {

        this.setLocation( new Point( x, y ) );
    }

    /**
     * Gets the knob's location.\
     *
     * @return the location
     */
    public Point getLocation() {

        return new Point( _location );
    }

    /**
     * Sets the knob's size.
     *
     * @param size the size
     */
    public void setSize( Dimension size ) {

        _size = new Dimension( size );
        updateShape();
    }

    /**
     * Gets the knob's size.
     *
     * @return the size
     */
    public Dimension getSize() {

        return new Dimension( _size );
    }

    /**
     * Sets the angle of rotation.
     * Rotation is performed about the tip of the knob.
     * At zero degrees, the tip is pointing straight up.
     *
     * @param angle the angle, in radians
     */
    public void setAngle( double angle ) {

        _angle = angle;
        updateShape();
    }

    /**
     * Gets the angle of rotation.
     *
     * @return the angle, in degrees
     */
    public double getAngle() {

        return _angle;
    }

    //----------------------------------------------------------------------------
    // Shape initialization
    //----------------------------------------------------------------------------

    /*
     * Updates the knob's shape, based on its size and angle.
     */
    private void updateShape() {

        GeneralPath path = new GeneralPath();

        // counterclockwise, starting at the tip
        path.moveTo( 0, 0 );
        path.lineTo( -0.5f * _size.width, 0.3f * _size.height );
        path.lineTo( -0.5f * _size.width, 1f * _size.height );
        path.lineTo( 0.5f * _size.width, 1f * _size.height );
        path.lineTo( 0.5f * _size.width, 0.3f * _size.height );
        path.closePath();
        Shape shape = path;

        // Rotate and translate.
//        AffineTransform netTx = getNetTransform();
//        AffineTransform transform = new AffineTransform();
//        transform.translate( _location.x, _location.y );
//        transform.rotate( _angle );
//        shape = transform.createTransformedShape( shape );

        knobShape.setShape( shape );
        wavelengthReadout.setWidth( knobShape.getWidth() );
    }

    public void setBorderColor( Color color ) {
        knobShape.setBorderColor( color );
    }

    public void setPaint( Color color ) {
        knobShape.setPaint( color );

    }

    public void setWavelength( double wavelength ) {
        wavelengthReadout.setValue( wavelength );
    }



    public class WavelengthReadout extends CompositePhetGraphic {
        private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
        private Color VALUE_COLOR = Color.BLACK;

        private PhetTextGraphic valueText;
        private PhetShapeGraphic background;
        private double wavelength;
        private Rectangle2D backgroundRect;

        public WavelengthReadout( Component component ) {
            super( component );

            backgroundRect = new Rectangle2D.Double( 0, 0, 40, 20 );
            background = new PhetShapeGraphic( component, backgroundRect, Color.white, new BasicStroke( 1 ), Color.black );
            addGraphic( background );

            valueText = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( valueText );

            update( 123 );
        }

        private void update( double wavelength ) {
            this.wavelength = wavelength;
            DecimalFormat voltageFormat = new DecimalFormat( "000" );
            Object[] args = {voltageFormat.format( Math.abs( wavelength ))};
            String text = MessageFormat.format( "nm", args );
//            valueText.setText( text );
            valueText.setText( voltageFormat.format( wavelength ) + "nm");

            // Move the wavelength label to the positive end of the battery
            valueText.setLocation( (int)background.getBounds().getWidth(), (int)background.getBounds().getHeight() );

            // Right justify in the bckground rectangle
            valueText.setRegistrationPoint( valueText.getWidth(), 6 );
//            valueText.setRegistrationPoint( -valueText.getWidth(), VALUE_FONT.getSize() - 30 );

        }

        public void setWidth( int width ) {
            int inset = 10;
            backgroundRect.setRect( 0, 0, width-inset, (int)(knobShape.getBounds().getHeight() * 0.67 ) - inset / 2 );
            setRegistrationPoint( (int)backgroundRect.getWidth() / 2, -(int)(knobShape.getBounds().getHeight() * 0.33) );
            update( wavelength );
            setBoundsDirty();
            repaint();
        }

        void setValue( double wavelength ) {
            update(wavelength );
        }
    }

}