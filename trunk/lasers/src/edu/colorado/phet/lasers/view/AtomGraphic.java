/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class AtomGraphic extends PhetImageGraphic implements Atom.StateChangeListener, SimpleObserver {

    static String s_imageName = LaserConfig.ATOM_IMAGE_FILE;

    private Atom atom;
    private Color energyRepColor;
    private Ellipse2D energyRep;
    private AtomicState atomicState;
    // Gives a 4 pixel ring for the ground state.
    private double energyScaleFactor = 4 / GroundState.instance().getEnergyLevel();

    public AtomGraphic( Component component, Atom atom ) {
        super( component, s_imageName );
        this.atom = atom;
        atom.addObserver( this );
        atom.addListener( this );
        update( atom.getState() );
    }

    public void stateChangeOccurred( Atom.StateChangeEvent event ) {
        update( event.getState() );
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
        if( true ) {
//        if( atomicState != state ) {
            atomicState = state;
            double energyRatio = state.getEnergyLevel() / GroundState.instance().getEnergyLevel();

            double energyRepRad = Math.pow( energyRatio, ringThicknessExponent )
                                  * ( getImage().getWidth() / 2 ) + groundStateRingThickness;
            energyRep = new Ellipse2D.Double( atom.getPosition().getX() - energyRepRad, atom.getPosition().getY() - energyRepRad,
                                              energyRepRad * 2, energyRepRad * 2 );
            if( state.getWavelength() == Photon.GRAY ) {
                energyRepColor = Color.darkGray;
            }
            else {
                energyRepColor = VisibleColor.wavelengthToColor( state.getWavelength() );
            }
            setPosition( (int)( atom.getPosition().getX() - getImage().getWidth() / 2 ),
                         (int)( atom.getPosition().getY() - getImage().getHeight() / 2 ) );
            setBoundsDirty();
            repaint();
        }
    }

    public void update() {
        update( atom.getState() );
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

