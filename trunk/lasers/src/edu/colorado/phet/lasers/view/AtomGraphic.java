/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;

public class AtomGraphic extends CompositePhetGraphic implements Atom.ChangeListener, SimpleObserver {

    private static String s_imageName = LaserConfig.ATOM_IMAGE_FILE;

    private Atom atom;
    private Color energyRepColor;
    private Ellipse2D energyRep;
    private PhetImageGraphic imageGraphic;

    public AtomGraphic( Component component, Atom atom ) {
        super( component );
//        super( component, s_imageName );
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
//        imageGraphic = new PhetImageGraphic( component, image );
        addGraphic( imageGraphic);
        update( atom.getCurrState() );
    }

    public void stateChanged( Atom.ChangeEvent event ) {
        update( event.getCurrState() );
    }

    protected Rectangle determineBounds() {
        return energyRep.getBounds();
    }

    /**
     * Determines the radius and color of the ring surrounding the atom that represents its energy state.
     *
     * @param state
     */
    public void update( AtomicState state ) {
        double groundStateRingThickness = 5;
        // used to scale the thickness of the ring so it changes size a reasonable amount through the visible range
        double ringThicknessExponent = 0.15;
        double energyRatio = state.getEnergyLevel() / GroundState.instance().getEnergyLevel();

        double energyRepRad = Math.pow( energyRatio, ringThicknessExponent )
                              * ( imageGraphic.getImage().getWidth() / 2 ) + groundStateRingThickness;
        energyRep = new Ellipse2D.Double( atom.getPosition().getX() - energyRepRad, atom.getPosition().getY() - energyRepRad,
                                          energyRepRad * 2, energyRepRad * 2 );
        if( state.getWavelength() == Photon.GRAY ) {
            energyRepColor = Color.darkGray;
        }
        else {
            energyRepColor = VisibleColor.wavelengthToColor( state.getWavelength() );
        }
        setLocation( (int)( atom.getPosition().getX() - imageGraphic.getImage().getWidth() / 2 ),
                     (int)( atom.getPosition().getY() - imageGraphic.getImage().getHeight() / 2 ) );
        setBoundsDirty();
        repaint();
    }

    public void update() {
        update( atom.getCurrState() );
    }

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( energyRepColor );
        g.fill( energyRep );
        restoreGraphicsState();

        super.paint( g );

        // Debug: draws a dot at the center of the atom
        //        g.setColor( Color.RED );
        //        g.drawArc( (int)atom.getPosition().getX()-2, (int)atom.getPosition().getY()-2, 4, 4, 0, 360 );
    }
}

