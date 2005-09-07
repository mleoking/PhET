/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
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
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Represents an atom with an image of a sphere, surrounded by a "halo" that represents its energy state. The halo
 * changes radius and color depending on the energy. The color is that of the wavelength of light with the same energy,
 * if the wavelength is visible. If the wavelength is in the IR, the color is gray.
 */
public class AtomGraphic extends CompositePhetGraphic implements Atom.ChangeListener, SimpleObserver {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static final boolean DEBUG = false;
    private static String s_imageName = LaserConfig.ATOM_IMAGE_FILE;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private Atom atom;
    private Color energyRepColor;
    private PhetShapeGraphic energyGraphic;
    private Ellipse2D energyRep;
    private PhetImageGraphic imageGraphic;
    private double energyRepRad;
    private double groundStateRingThickness;
    private double baseImageRad;

    /**
     * @param component
     * @param atom
     */
    public AtomGraphic( Component component, Atom atom ) {
        super( component );
        this.atom = atom;
        this.setIgnoreMouse( true );
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
        baseImageRad = bi.getWidth() / 2;

        imageGraphic = new PhetImageGraphic( component, bi );
        imageGraphic.setRegistrationPoint( imageGraphic.getHeight() / 2, imageGraphic.getHeight() / 2 );
        addGraphic( imageGraphic, 2 );

        energyGraphic = new PhetShapeGraphic( component, energyRep, energyRepColor, new BasicStroke( 1 ), Color.black );
        addGraphic( energyGraphic, 1 );
        update();
    }

    /**
     * Sets the location of the graphic, and determines the color and radius of the halo
     */
    public void update() {
        determineEnergyRadiusAndColor();
        setLocation( (int)( atom.getPosition().getX() ),
                     (int)( atom.getPosition().getY() ) );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------

    /**
     * Determines the radius and color of the ring that represents the energy state of the atom
     */
    protected void determineEnergyRadiusAndColor() {
        AtomicState state = atom.getCurrState();

        // Determine the color and thickness of the colored ring that represents the energy
        groundStateRingThickness = 3;
        double f = 0.3;
        // used to scale the thickness of the ring so it changes size a reasonable amount through the visible range
        double ringThicknessExponent = 0.15;
        double energyDif = state.getEnergyLevel() - atom.getGroundState().getEnergyLevel();
//        double energyRatio = state.getEnergyLevel() / atom.getGroundState().getEnergyLevel();
//        double energyRatio = state.getEnergyLevel() / AtomicState.minEnergy;
//        double e0 = ( baseImageRad * groundState.getEnergyLevel() ) / ( baseImageRad + groundStateRingThickness );
//        double energyRatio = ( state.getEnergyLevel() - e0 ) / ( groundState.getEnergyLevel() - e0 );
        energyRepRad = ( energyDif * f ) + groundStateRingThickness + baseImageRad;

//        energyRepRad = Math.pow( energyRatio, ringThicknessExponent )
//                       * ( imageGraphic.getImage().getWidth() / 2 ) + groundStateRingThickness;
//

        energyRep = new Ellipse2D.Double( -energyRepRad, -energyRepRad, energyRepRad * 2, energyRepRad * 2 );
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
    }

    /**
     * @param g2
     */
    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        super.paint( g2 );

        // Debug: draws a dot at the center of the atom
        if( DEBUG ) {
            g2.setColor( Color.GREEN );
            g2.fillArc( (int)getLocation().getX() - 2,
                        (int)getLocation().getY() - 2, 4, 4, 0, 360 );
            g2.setColor( Color.RED );
            g2.drawArc( (int)atom.getPosition().getX() - 2, (int)atom.getPosition().getY() - 2, 4, 4, 0, 360 );
        }

        restoreGraphicsState();
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    /**
     * Determines if the atom can be moved with the mouse
     *
     * @param isMouseable
     */
    public void setIsMouseable( boolean isMouseable, final Rectangle2D bounds ) {
        setIgnoreMouse( !isMouseable );
        if( isMouseable ) {
            this.addTranslationListener( new TranslationListener() {

                /**
                 * Graphic can be moved anywhere within the specified bounds
                 *
                 * @param translationEvent
                 */
                public void translationOccurred( TranslationEvent translationEvent ) {
                    double dx = translationEvent.getDx();
                    double dy = translationEvent.getDy();
                    double xCurr = getLocation().getX();
                    double yCurr = getLocation().getY();
                    double xNew = Math.max( Math.min( bounds.getMaxX(), xCurr + dx ), bounds.getMinX() );
                    double yNew = Math.max( Math.min( bounds.getMaxY(), yCurr + dy ), bounds.getMinY() );
                    atom.setPosition( xNew, yNew );
                }
            } );
        }
    }

    /**
     * @return
     */
    protected PhetShapeGraphic getEnergyGraphic() {
        return energyGraphic;
    }

    /**
     * @return
     */
    protected Atom getAtom() {
        return atom;
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void stateChanged( Atom.ChangeEvent event ) {
        update();
    }

    public void positionChanged( Atom.ChangeEvent event ) {
        update();
    }
}

