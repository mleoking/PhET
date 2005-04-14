/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
public class AtomGraphic extends CompositePhetGraphic implements Atom.ChangeListener, SimpleObserver {

    private static String s_imageName = LaserConfig.ATOM_IMAGE_FILE;

    private Atom atom;
    private Color energyRepColor;
    private PhetShapeGraphic energyGraphic;
    private Ellipse2D energyRep;
    private PhetImageGraphic imageGraphic;
    private double energyRepRad;
    private double groundStateRingThickness;

    public AtomGraphic( Component component, Atom atom ) {
        super( component );
        this.atom = atom;
        atom.addObserver( this );
        atom.addChangeListener( this );

        BufferedImage image = null;
        try {
            image = ImageLoader.loadBufferedImage( s_imageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        double scale = ( 2 * atom.getRadius() ) / image.getHeight();
        AffineTransform atx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage bi = atxOp.filter( image, null );

        imageGraphic = new PhetImageGraphic( component, bi );
        imageGraphic.setRegistrationPoint( imageGraphic.getHeight() / 2, imageGraphic.getHeight() / 2 );
        addGraphic( imageGraphic, 2 );

        energyGraphic = new PhetShapeGraphic( component, energyRep, energyRepColor );
        energyRepRad = ( imageGraphic.getImage().getWidth() / 2 ) + groundStateRingThickness;
        energyRep = new Ellipse2D.Double( 0, 0, energyRepRad * 2, energyRepRad * 2 );
        addGraphic( energyGraphic, 1 );
        determineEnergyRadiusAndColor();
        update( atom.getCurrState() );
    }

    /**
     * Determines the radius and color of the ring that represents the energy state of the atom
     */
    private void determineEnergyRadiusAndColor() {

        AtomicState state = atom.getCurrState();

        // Determine the color and thickness of the colored ring that represents the energy
        groundStateRingThickness = 5;
        // used to scale the thickness of the ring so it changes size a reasonable amount through the visible range
        double ringThicknessExponent = 0.15;
        double energyRatio = state.getEnergyLevel() / AtomicState.minEnergy;

        energyRepRad = Math.pow( energyRatio, ringThicknessExponent )
                       * ( imageGraphic.getImage().getWidth() / 2 ) + groundStateRingThickness;
        energyRep = new Ellipse2D.Double( 0, 0, energyRepRad * 2, energyRepRad * 2 );
        if( state.getWavelength() == Photon.GRAY ) {
            energyRepColor = Color.darkGray;
        }
        else {
            energyRepColor = VisibleColor.wavelengthToColor( state.getWavelength() );
            if( energyRepColor.equals( VisibleColor.INVISIBLE ) ) {
                energyRepColor = Color.darkGray;
            }
        }
        energyGraphic.setShape( energyRep );
        energyGraphic.setColor( energyRepColor );
        energyGraphic.setRegistrationPoint( (int)energyRepRad, (int)energyRepRad);
    }

    /**
     * @return
     */
    protected Rectangle determineBounds() {
        return energyRep.getBounds();
    }

    /**
     * Sets the location of the graphic
     *
     * @param state
     */
    public void update( AtomicState state ) {

        // The location of the graphic is offset by the radius of the energy representation
        setLocation( (int)( atom.getPosition().getX() ),
                     (int)( atom.getPosition().getY() ) );

        // Set the location of the image graphic
//        imageGraphic.setLocation( (int)( energyRepRad - imageGraphic.getHeight() / 2 ),
//                                  (int)( energyRepRad - imageGraphic.getHeight() / 2 ) );
//
//        // The location of the graphic is offset by the radius of the energy representation
//        setLocation( (int)( atom.getPosition().getX() - energyRepRad ),
//                     (int)( atom.getPosition().getY() - energyRepRad ) );
//
//        // Set the location of the image graphic
//        imageGraphic.setLocation( (int)( energyRepRad - imageGraphic.getHeight() / 2 ),
//                                  (int)( energyRepRad - imageGraphic.getHeight() / 2 ) );
        setBoundsDirty();
        repaint();
    }

    public void update() {
        update( atom.getCurrState() );
    }

    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );
        super.paint( g2 );

        // Debug: draws a dot at the center of the atom
        g2.setColor( Color.RED );
        g2.drawArc( (int)atom.getPosition().getX() - 2, (int)atom.getPosition().getY() - 2, 4, 4, 0, 360 );

        restoreGraphicsState();
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void stateChanged( Atom.ChangeEvent event ) {
        determineEnergyRadiusAndColor();
        update( event.getCurrState() );
    }

}

