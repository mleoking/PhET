/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Rectangle2D;

/**
 * SampleScanner
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleScanner extends Particle {
    private SampleTarget sampleTarget= new SampleTarget();
    private double speed = 0.5;
    private double rowSpacing = 20;
    private int rowNumber = 0;
    private Rectangle2D scanningArea;

    public SampleScanner( Sample sample ) {
        scanningArea = sample.getBounds();
        setPosition( scanningArea.getX(), scanningArea.getY() );
        sampleTarget.setLocation( getPosition());

        setVelocity( speed, 0 );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if( getPosition().getX() > scanningArea.getMaxX() ) {
            rowNumber++;
            if( getPosition().getY() > scanningArea.getMaxY() ) {
                rowNumber = 0;
            }
            setPosition( scanningArea.getX(), scanningArea.getY() + rowNumber * rowSpacing );
        }
        sampleTarget.setLocation( getPosition() );
    }

    public SampleTarget getSampleTarget() {
        return sampleTarget;
    }
}
