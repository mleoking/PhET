/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.dischargelamps.model.ElectronSink;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.Electrode;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import java.util.List;
import java.util.ArrayList;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * PhotoelectricModel
 * <p/>
 * Builds on the DischargeLampModel.
 * <p/>
 * Uses a PhotoelectricTarget, which is an extension of Electrode
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModel extends DischargeLampModel {

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private List photons = new ArrayList();
    private PhotoelectricTarget target;

    // Beam specification
    private CollimatedBeam beam;
    private double defaultBeamWavelength = 400;
    private double beamMaxPhotonsPerSecond = 20;
    double beamHeight = 50;
    private double beamSourceToTargetDist = 300;
    private double beamAngle = Math.toRadians( 40 + 180 );
    private Point2D beamLocation = new Point2D.Double( DischargeLampsConfig.CATHODE_LOCATION.getX() + Math.cos( beamAngle + Math.PI ) * beamSourceToTargetDist,
                                                       DischargeLampsConfig.CATHODE_LOCATION.getY() - Math.sin( beamAngle + Math.PI ) * beamSourceToTargetDist );

    //----------------------------------------------------------------
    // Contructors and initialization
    //----------------------------------------------------------------

    public PhotoelectricModel() {

        // Photon beam

        beam = new CollimatedBeam( defaultBeamWavelength, beamLocation, beamHeight,
                                   100.0, new Vector2D.Double( Math.cos( beamAngle ), Math.sin( beamAngle ) ),
                                   beamMaxPhotonsPerSecond, 0 );
        addModelElement( beam );
    }


    /**
     * Tracks special classes of model elements
     *
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        if( modelElement instanceof Photon ) {
            photons.add( modelElement );
        }
        if( modelElement instanceof ElectronSource ) {
            Electrode electrode = (Electrode)modelElement;
            target = new PhotoelectricTarget( this, electrode.getEndpoints()[0], electrode.getEndpoints()[1] );
        }
        super.addModelElement( modelElement );
    }

    /**
     * Tracks photons
     *
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        if( modelElement instanceof Photon ) {
            photons.remove( modelElement );
        }
        super.removeModelElement( modelElement );
    }

    /**
     * Handles production of photons from the cathode
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        for( int i = 0; i < photons.size(); i++ ) {
            Photon photon = (Photon)photons.get( i );

            // If the photon is hitting the cathode, produce an electron, if appropriate,
            // and remove the photon from the model
            if( target.isHitByPhoton( photon ) ) {
                target.handlePhotonCollision( photon );
                removeModelElement( photon );
            }
        }
    }

    //----------------------------------------------------------------
    // Getters and setters 
    //----------------------------------------------------------------
    public PhotoelectricTarget getTarget() {
        return target;
    }

    public CollimatedBeam getBeam() {
        return beam;
    }
}
