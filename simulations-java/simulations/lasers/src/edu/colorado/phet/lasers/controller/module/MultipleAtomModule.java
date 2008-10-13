/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller.module;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.quantum.QuantumConfig;
import edu.colorado.phet.common.quantum.model.*;
import edu.colorado.phet.lasers.LasersApplication;
import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.controller.BeamControl;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.UniversalLaserControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.LaserAtom;
import edu.colorado.phet.lasers.model.atom.ThreeLevelElementProperties;
import edu.colorado.phet.lasers.model.atom.TwoLevelElementProperties;
import edu.colorado.phet.lasers.view.LampGraphic;

/**
 * MultipleAtomModule
 * <p/>
 * Module that has full laser capabilities
 */
public class MultipleAtomModule extends BaseLaserModule {

    private double s_maxSpeed = 0.1;
    private ArrayList atoms;
    private UniversalLaserControlPanel laserControlPanel;
    private BeamControl pumpBeamControl;
    private static final boolean SHOW_LEGEND = false;

    public MultipleAtomModule( PhetFrame frame, IClock clock ) {
        super( frame, LasersResources.getString( "ModuleTitle.MultipleAtomModule" ), clock, Photon.DEFAULT_SPEED * LasersApplication.MULTI_ATOM_MODULE_SPEED );

        // Set the size of the cavity
        Tube cavity = getCavity();
        Rectangle2D cavityBounds = cavity.getBounds();

        // Set up the beams
        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() );
        Beam seedBeam = ( (LaserModel) getModel() ).getSeedBeam();
        seedBeam.setPosition( beamOrigin );
        seedBeam.setBeamWidth( s_boxHeight );
        seedBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        seedBeam.setPhotonsPerSecond( 1 );

        // Pumping beam
        Beam pumpingBeam = ( (LaserModel) getModel() ).getPumpingBeam();
        Point2D pumpingBeamOrigin = new Point2D.Double( getCavity().getBounds().getX() + getCavity().getBounds().getWidth() / 2,
                                                        getCavity().getBounds().getY() - 100 );
        pumpingBeam.setPosition( pumpingBeamOrigin );
        pumpingBeam.setBeamWidth( cavityBounds.getWidth() );
        pumpingBeam.setFanout( LaserConfig.PUMPING_BEAM_FANOUT );

        // Set the max pumping rate
        pumpingBeam.setMaxPhotonsPerSecond( LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE );
        // Start with the beam turned all the way down
        pumpingBeam.setPhotonsPerSecond( 0 );

        // Only the pump beam is enabled
        seedBeam.setEnabled( false );
        pumpingBeam.setEnabled( true );

        // Set up the graphics
        BufferedImage gunBI = LasersResources.getImage( LaserConfig.RAY_GUN_IMAGE_FILE );

        // Pumping beam lamps. Note that the images start out horizontal, and then are rotated. This accounts for
        // some funny looking code
        int numLamps = 8;
        double yOffset = 10;
        // The lamps should span the cavity
        double pumpScaleX = ( cavity.getMinY() - 100 - yOffset ) / gunBI.getWidth();
        double pumpScaleY = ( pumpingBeam.getBeamWidth() / numLamps ) / gunBI.getHeight();
        AffineTransformOp atxOp2 = new AffineTransformOp( AffineTransform.getScaleInstance( pumpScaleX, pumpScaleY ), AffineTransformOp.TYPE_BILINEAR );
        BufferedImage pumpBeamImage = atxOp2.filter( gunBI, null );
        for ( int i = 0; i < numLamps; i++ ) {
            AffineTransform tx = new AffineTransform();
            tx.translate( cavity.getMinX() + pumpBeamImage.getHeight() * ( i + 1 ), yOffset );
            tx.rotate( Math.PI / 2 );
            BufferedImage img = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( pumpBeamImage, null );
            LampGraphic pumpLampGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), img, tx );
            setPumpLampGraphic( pumpLampGraphic );
            addGraphic( pumpLampGraphic, LaserConfig.PHOTON_LAYER + 1 );
        }

        // Add the beam control
        PhetImageGraphic wireGraphic = new PhetImageGraphic( getApparatusPanel(), LasersResources.getImage( LaserConfig.WIRE_IMAGE ) );
        AffineTransform atx = AffineTransform.getScaleInstance( 4, 1 );
        AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        wireGraphic.setImage( atxOp.filter( wireGraphic.getImage(), null ) );
        wireGraphic.setLocation( 180, 50 );
        getApparatusPanel().addGraphic( wireGraphic );
        Point pumpControlLocation = new Point( (int) ( cavity.getBounds().getMaxX() ) + 140, 10 );
        pumpBeamControl = new BeamControl( getApparatusPanel(),
                                           this,
                                           pumpControlLocation,
                                           pumpingBeam,
                                           QuantumConfig.MIN_WAVELENGTH,
                                           QuantumConfig.MAX_WAVELENGTH,
                                           LasersResources.getImage( LaserConfig.BEAM_CONTROL_IMAGE ) );
        getApparatusPanel().addGraphic( pumpBeamControl );

        // Add some atoms
        addAtoms( cavityBounds );

        // Set initial conditions
        setThreeEnergyLevels( true );
        setEnergyLevelsAveragingPeriod( LaserConfig.ENERGY_LEVEL_MONITOR_AVERAGING_PERIOD );

        // Set the control panel
        laserControlPanel = new UniversalLaserControlPanel( this, SHOW_LEGEND );
        setControlPanel( laserControlPanel );
        laserControlPanel.setUpperTransitionView( BaseLaserModule.PHOTON_CURTAIN );
    }

    public void reset() {
        super.reset();
        deactivate();
        setThreeEnergyLevels( true );
        setEnergyLevelsAveragingPeriod( LaserConfig.ENERGY_LEVEL_MONITOR_AVERAGING_PERIOD );
        laserControlPanel = new UniversalLaserControlPanel( this, SHOW_LEGEND );
        setControlPanel( laserControlPanel );
        laserControlPanel.setUpperTransitionView( BaseLaserModule.PHOTON_CURTAIN );
        laserControlPanel.setThreeEnergyLevels( true );
        setMirrorsEnabled( false );
        setDisplayHighLevelEmissions( false );
        activate();

        // Reset the energy levels. We only need to get the states from one atom, since all atoms share the
        // same state objects
        ThreeLevelElementProperties props = new ThreeLevelElementProperties();
        Atom atom = (Atom) atoms.get( 0 );
        AtomicState[] states = atom.getStates();
        for ( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            // If we do this before we call activate(), we only have to call it once, but it doesn't look good. (The
            // slider comes up twice in different places). By doing it this way, it looks better
            state.setEnergyLevel( props.getStates()[i].getEnergyLevel() );
            state.setEnergyLevel( props.getStates()[i].getEnergyLevel() );
        }
    }

    private void addAtoms( Rectangle2D cavityBounds ) {
        Atom atom = null;
        atoms = new ArrayList();
        int numAtoms = 30;
        for ( int i = 0; i < numAtoms; i++ ) {
            ElementProperties properties;
            if ( getThreeEnergyLevels() ) {
                properties = new ThreeLevelElementProperties();
            }
            else {
                properties = new TwoLevelElementProperties();
            }
//            atom = new PropertiesBasedAtom( getLaserModel(), properties );
            atom = new LaserAtom( getLaserModel(), properties );
//            atom = new Atom( getLaserModel(), numEnergyLevels );
            boolean placed = false;

            // Place atoms so they don't overlap
            int tries = 0;
            do {
                placed = true;
                atom.setPosition( ( cavityBounds.getX() + ( Math.random() ) * ( cavityBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                                  ( cavityBounds.getY() + ( Math.random() ) * ( cavityBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
                atom.setVelocity( (float) ( Math.random() - 0.5 ) * s_maxSpeed,
                                  (float) ( Math.random() - 0.5 ) * s_maxSpeed );
                for ( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom) atoms.get( j );
                    double d = atom.getPosition().distance( atom2.getPosition() );
                }
                if ( tries > 1000 ) {
                    System.out.println( "Unable to place all atoms" );
                    break;
                }
            } while ( !placed );
            atoms.add( atom );
            addAtom( atom );
        }
    }

    /**
     * Extends the base class behavior. After letting the base class behavior
     * happen, the pumping beam is enabled, since this is the only beam shown
     * on the multiple atom panel.
     *
     * @param threeEnergyLevels
     */
    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        super.setThreeEnergyLevels( threeEnergyLevels );
        getLaserModel().getPumpingBeam().setEnabled( true );
    }
}
