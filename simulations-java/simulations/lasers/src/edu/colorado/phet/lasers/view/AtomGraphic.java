/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.lasers.LasersConfig;
import edu.colorado.phet.lasers.LasersResources;

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
    private static EnergyRepColorStrategy energyRepColorStrategy = new GrayScaleStrategy();
    //    private static EnergyRepColorStrategy energyRepColorStrategy = new VisibleColorStrategy();
    private static ArrayList changeListenerList = new ArrayList();

    protected static EnergyRepColorStrategy getEnergyRepColorStrategy() {
        return energyRepColorStrategy;
    }

    public static void setEnergyRepColorStrategy( EnergyRepColorStrategy strategy ) {
        energyRepColorStrategy = strategy;
        for ( int i = 0; i < changeListenerList.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener) changeListenerList.get( i );
//            changeListener.stateChanged( new ChangeEvent( AtomGraphic.class ) );
        }
//        changeListenerProxy.stateChanged( new ChangeEvent( AtomGraphic.class ) );
    }

    //
    public static void addChangeListener( ChangeListener listener ) {
        changeListenerList.add( listener );
    }

    public static void removeChangeListener( ChangeListener listener ) {
        changeListenerList.remove( listener );
    }

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

        BufferedImage image = LasersResources.getImage( LasersConfig.ATOM_IMAGE_FILE );
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
        setLocation( (int) ( atom.getPosition().getX() ),
                     (int) ( atom.getPosition().getY() ) );
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
        groundStateRingThickness = 2;
        double f = 0.3;
        // used to scale the thickness of the ring so it changes size a reasonable amount through the visible range
        double ringThicknessExponent = 0.15;
        double energyDif = state.getEnergyLevel() - atom.getGroundState().getEnergyLevel();
        energyRepRad = ( energyDif * f ) + groundStateRingThickness + baseImageRad;

        double de1 = atom.getHighestEnergyState().getEnergyLevel() - atom.getGroundState().getEnergyLevel();
        double de2 = state.getEnergyLevel() - atom.getGroundState().getEnergyLevel();
        double maxRingThickness = 6;
        energyRepRad = maxRingThickness * de2 / de1 + groundStateRingThickness + baseImageRad;
        energyRep = new Ellipse2D.Double( -energyRepRad, -energyRepRad, energyRepRad * 2, energyRepRad * 2 );
        energyGraphic.setShape( energyRep );
        energyGraphic.setColor( energyRepColorStrategy.getColor( atom ) );
        energyGraphic.setColor( Color.green );

        // Set the radius of the atom to be the same as the energy rep.
        atom.setRadius( energyRep.getWidth() / 2 );
    }

    /**
     * @param g2
     */
    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        super.paint( g2 );

        // Debug: draws a dot at the center of the atom
        if ( DEBUG ) {
            g2.setColor( Color.GREEN );
            g2.fillArc( (int) getLocation().getX() - 2,
                        (int) getLocation().getY() - 2, 4, 4, 0, 360 );
            g2.setColor( Color.RED );
            g2.drawArc( (int) atom.getPosition().getX() - 2, (int) atom.getPosition().getY() - 2, 4, 4, 0, 360 );
        }

        restoreGraphicsState();
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    /**
     * Determines if the atom can be moved with the mouse, and the bounds within
     * which it can be moved
     *
     * @param isMouseable
     * @param bounds
     */
    public void setIsMouseable( boolean isMouseable, final Rectangle2D bounds ) {
        setIgnoreMouse( !isMouseable );
        if ( isMouseable ) {
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

    protected PhetShapeGraphic getEnergyGraphic() {
        return energyGraphic;
    }

    protected Atom getAtom() {
        return atom;
    }

    protected PhetImageGraphic getImageGraphic() {
        return imageGraphic;
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


    /**
     * Picks a Color to represent the energy level of an atom
     */
    public static interface EnergyRepColorStrategy {
        Color getColor( Atom atom );
    }

    /**
     * Picks an RGB color that renders the color corresponding to the energy level of the atom
     */
    static public class VisibleColorStrategy implements EnergyRepColorStrategy {

        public Color getColor( Atom atom ) {
            double de = atom.getCurrState().getEnergyLevel() - atom.getGroundState().getEnergyLevel();
            double wavelength = PhysicsUtil.energyToWavelength( de );
            Color c = VisibleColor.wavelengthToColor( wavelength );
            Color returnColor = new Color( c.getRed(), c.getGreen(), c.getBlue(), 255 );
            return returnColor;
//            return VisibleColor.wavelengthToColor( wavelength );
        }
    }

    /**
     * Picks a shade of gray for the energy rep color.
     */
    static public class GrayScaleStrategy implements EnergyRepColorStrategy {
        private Color[] grayScale = new Color[240];

        public GrayScaleStrategy() {
            for ( int i = 0; i < grayScale.length; i++ ) {
                grayScale[i] = new Color( i, i, i );
            }
        }

        public Color getColor( Atom atom ) {
            int idx = (int) ( grayScale.length * ( ( atom.getCurrState().getEnergyLevel() - atom.getGroundState().getEnergyLevel() ) /
                                                   ( atom.getHighestEnergyState().getEnergyLevel() - atom.getGroundState().getEnergyLevel() ) ) );
            idx = Math.min( Math.max( 0, idx ), grayScale.length - 1 );
            return grayScale[idx];
        }
    }
}

