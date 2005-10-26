/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.photon;

import edu.colorado.phet.lasers.model.atom.Atom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * LaserPhoton
 * <p/>
 * Extends photon in that it knows how to produce photons due to stimulated
 * emission.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaserPhoton extends Photon {
    private static Random random = new Random( System.currentTimeMillis() );

    // The bounds within which a stimulated photon must be created. This keeps them inside the
    // laser cavity
    static private Rectangle2D stimulationBounds;

    static private HashMap photonToStimRecordMap = new HashMap();

    public static void setStimulationBounds( Rectangle2D stimulationBounds ) {
        LaserPhoton.stimulationBounds = stimulationBounds;
    }

    static public Photon createStimulated( Photon stimulatingPhoton, Point2D location, Atom atom ) {
        StimRecord stimRecord = (StimRecord)photonToStimRecordMap.get( stimulatingPhoton );
        if( stimRecord == null ) {
            stimRecord = new StimRecord();
            photonToStimRecordMap.put( stimulatingPhoton, stimRecord );
        }
        stimRecord.numStimulatedPhotons++;
        Photon newPhoton = create( stimulatingPhoton.getWavelength(), location,
                                   stimulatingPhoton.getVelocity() );


        int idx = stimRecord.addChildPhoton( newPhoton );
        int yOffset = ( 1 + idx / 2 ) * 4;
//        int yOffset = stimRecord.numStimulatedPhotons * 4;
        int sign = idx % 2 == 0 ? 1 : -1;
//        int sign = random.nextBoolean() ? 1 : -1;
        double dy = yOffset * sign * ( stimulatingPhoton.getVelocity().getX() / stimulatingPhoton.getVelocity().getMagnitude() );
        double dx = yOffset * -sign * ( stimulatingPhoton.getVelocity().getY() / stimulatingPhoton.getVelocity().getMagnitude() );
        double newY = stimulatingPhoton.getPosition().getY() + dy;
        double newX = stimulatingPhoton.getPosition().getX() + dx;

        // Keep the photon inside the cavity.
        // todo: if we get the photon graphic positioned better, this may change.
        double minY = stimulationBounds.getMinY() + Photon.RADIUS;
        double maxY = stimulationBounds.getMaxY();
        if( newY < minY || newY > maxY ) {
            newY = atom.getPosition().getY();
            newX = atom.getPosition().getX() - 10;
            stimRecord.numStimulatedPhotons = 1;
        }
        newPhoton.setPosition( newX, newY );

        newPhoton.addLeftSystemListener( new ChildPhotonTracker( stimRecord ) );
        return newPhoton;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private static class StimRecord {
        int numStimulatedPhotons;
        List childPhotons = new ArrayList();

        /**
         * Adds a photon to the list of children, and returns that photon's index in the
         * list. The index can be used by the client to place the photon in the right spot
         * relative to the stimulating photon
         *
         * @param photon
         * @return
         */
        int addChildPhoton( Photon photon ) {
            int idx = -1;
            // Search through all the list of childPhoton slots to see if any are empty
            for( int i = 0; i < childPhotons.size() && idx < 0; i++ ) {
                Photon testPhoton = (Photon)childPhotons.get( i );
                if( testPhoton == null ) {
                    childPhotons.set( i, photon );
                }
            }
            // If we didn't find an empty slot, add the photon to the end of the list
            if( idx < 0 ) {
                childPhotons.add( photon );
            }
            idx = childPhotons.indexOf( photon );
            return idx;
        }

        void removeChildPhoton( Photon photon ) {
            int idx = childPhotons.indexOf( photon );
            childPhotons.set( idx, null );
        }
    }

    private static class ChildPhotonTracker implements Photon.LeftSystemEventListener {
        private StimRecord stimRecord;

        public ChildPhotonTracker( StimRecord stimRecord ) {
            this.stimRecord = stimRecord;
        }

        public void leftSystemEventOccurred( LeftSystemEvent event ) {
            stimRecord.removeChildPhoton( event.getPhoton() );
        }
    }
}
