/**
 * Class: BaseLaserModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.BeamControl;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.MultipleAtomControlPanel;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.LampGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class MultipleAtomModule extends BaseLaserModule {

    private double s_maxSpeed = .1;
    private ArrayList atoms;

    /**
     *
     */
    public MultipleAtomModule( PhetFrame frame, AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.MultipleAtomModule" ), frame, clock );

        // Set the control panel
        setControlPanel( new MultipleAtomControlPanel( this ) );

        ResonatingCavity cavity = getCavity();
        Rectangle2D cavityBounds = cavity.getBounds();
        cavity.setBounds( cavityBounds.getMinX(), cavityBounds.getMinY(), cavityBounds.getMinX() + cavityBounds.getWidth(), cavityBounds.getMinY() + ( cavityBounds.getHeight() * 1.5 ) );
        cavityBounds = cavity.getBounds();
        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() );
        CollimatedBeam seedBeam = ( (LaserModel)getModel() ).getSeedBeam();

        Rectangle2D.Double seedBeamBounds = new Rectangle2D.Double( beamOrigin.getX(), beamOrigin.getY(),
                                                                    s_boxWidth + s_laserOffsetX * 2, s_boxHeight );
        seedBeam.setBounds( seedBeamBounds );
        seedBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        seedBeam.setEnabled( true );
        seedBeam.setPhotonsPerSecond( 1 );

        CollimatedBeam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Point2D pumpingBeamOrigin = new Point2D.Double( cavityBounds.getMinX() + cavityBounds.getWidth() / 2 - Photon.s_radius / 2,
                                                        s_origin.getY() - 140 );
        Rectangle2D.Double pumpingBeamBounds = new Rectangle2D.Double( pumpingBeamOrigin.getX(), pumpingBeamOrigin.getY(),
                                                                       cavityBounds.getWidth(), s_boxHeight + s_laserOffsetX * 2 );
        pumpingBeam.setBounds( pumpingBeamBounds );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );
        pumpingBeam.setEnabled( true );

        // Add the ray gun for firing photons
        try {
            Rectangle2D allocatedBounds = new Rectangle2D.Double( (int)seedBeamBounds.getX() - 100,
                                                                  (int)( seedBeamBounds.getY() ),
                                                                  100, (int)seedBeamBounds.getHeight() );
            BufferedImage gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
            double scaleX = allocatedBounds.getWidth() / gunBI.getWidth();
            double scaleY = allocatedBounds.getHeight() / gunBI.getHeight();

            AffineTransformOp atxOp1 = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage beamImage = atxOp1.filter( gunBI, null );
            AffineTransform atx = new AffineTransform();
            atx.translate( allocatedBounds.getX(), allocatedBounds.getY() );

            // Add the intensity control
            JPanel sbmPanel = new JPanel();
            BeamControl sbm = new BeamControl( seedBeam );
            Dimension sbmDim = sbm.getPreferredSize();
            sbmPanel.setBounds( (int)allocatedBounds.getX(), (int)( allocatedBounds.getY() + allocatedBounds.getHeight() ),
                                (int)sbmDim.getWidth() + 10, (int)sbmDim.getHeight() + 10 );
            sbm.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            sbmPanel.add( sbm );
            //            sbmPanel.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            sbmPanel.setOpaque( false );
            //            getApparatusPanel().add( sbmPanel );

            // Pumping beam lamp
            double pumpScaleX = scaleX;
            double pumpScaleY = s_boxWidth / gunBI.getHeight();
            AffineTransformOp atxOp2 = new AffineTransformOp( AffineTransform.getScaleInstance( pumpScaleX, pumpScaleY ), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage pumpBeamImage = atxOp2.filter( gunBI, null );
            AffineTransform pumpingBeamTx = new AffineTransform();
            pumpingBeamTx.translate( getLaserOrigin().getX() + pumpBeamImage.getHeight() + s_boxWidth / 2 - pumpBeamImage.getHeight() / 2, 10 );
            pumpingBeamTx.rotate( Math.PI / 2 );
            BufferedImage pumpingBeamLamp = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( pumpBeamImage, null );
            PhetImageGraphic pumpingLampGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), pumpingBeamLamp, pumpingBeamTx );
            addGraphic( pumpingLampGraphic, LaserConfig.PHOTON_LAYER + 1 );

            // Add the beam control
            JPanel pbmPanel = new JPanel();
            BeamControl pbm = new BeamControl( pumpingBeam, 0, LaserConfig.MAXIMUM_PUMPING_PHOTON_RATE );
            Dimension pbmDim = pbm.getPreferredSize();
            pbmPanel.setBounds( (int)( pumpingBeamTx.getTranslateX() - ( pumpingLampGraphic.getHeight() * pumpScaleY ) - pbmDim.getWidth() ), 10,
                                (int)pbmDim.getWidth() + 10, (int)pbmDim.getHeight() + 10 );
            pbmPanel.add( pbm );
            //            pbmPanell.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pbm.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pbmPanel.setOpaque( false );
            getApparatusPanel().add( pbmPanel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Only the pumping beam is enabled for this module
        pumpingBeam.setEnabled( true );
        getEnergyLevelsMonitorPanel().setNumLevels( 3 );
        getLaserModel().getPumpingBeam().setEnabled( true );
        getModel().removeModelElement( seedBeam );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setSeedPhotonRate( 1 );
        config.setMiddleEnergySpontaneousEmissionTime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );
        config.setPumpingPhotonRate( 0 );
        config.setReflectivity( 0.7 );
        config.configureSystem( getLaserModel() );

    }

    /**
     *
     */
    public void activate( PhetApplication app ) {
        super.activate( app );

        Rectangle2D cavityBounds = getCavity().getBounds();

        Atom atom = null;
        atoms = new ArrayList();
        //        int numAtoms = 1;
        int numAtoms = 20;
        //        int numAtoms = 20;
        for( int i = 0; i < numAtoms; i++ ) {
            atom = new Atom( getModel() );
            boolean placed = false;

            // Place atoms so they don't overlap
            do {
                placed = true;
                atom.setPosition( ( cavityBounds.getX() + ( Math.random() ) * ( cavityBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
                                  //                atom.setPosition( ( cavityBounds.getX() + ( Math.random() ) * ( cavityBounds.getWidth() - atom.getRadius() * 2 ) + atom.getRadius() ),
                                  ( cavityBounds.getY() + ( Math.random() ) * ( cavityBounds.getHeight() - atom.getRadius() * 2 ) ) + atom.getRadius() );
                for( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom)atoms.get( j );
                    double d = atom.getPosition().distance( atom2.getPosition() );
                    if( d <= atom.getRadius() + atom2.getRadius() ) {
                        placed = false;
                        break;
                    }
                }
            } while( !placed );
            atoms.add( atom );
            addAtom( atom );
        }

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setSeedPhotonRate( 0f );
        config.setMiddleEnergySpontaneousEmissionTime( 2000f );
        config.setPumpingPhotonRate( 0 );
        //        config.setPumpingPhotonRate( 100f );
        config.setHighEnergySpontaneousEmissionTime( 2000f );
        config.setReflectivity( 0.7f );
        config.configureSystem( (LaserModel)getModel() );
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            getLaserModel().removeModelElement( atom );
            atom.removeFromSystem();
        }
        atoms.clear();
    }
}
