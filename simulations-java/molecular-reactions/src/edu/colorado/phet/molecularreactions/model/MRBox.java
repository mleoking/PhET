/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;

import java.awt.geom.Point2D;

/**
 * MRBox
 * <p/>
 * This is an extension of the common class Box2D. It extends that class
 * it has a temperature, and when molecules hit the box, they then have the
 * same temperature as the box.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRBox extends Box2D implements PublishingModel.ModelListener {
    private double temperature;
    private MRModel model;

    public MRBox( MRModel model ) {
        super();
        init( model );
    }

    public MRBox( Point2D corner1, Point2D corner2, MRModel model ) {
        super( corner1, corner2 );
        init( model );
    }

    public MRBox( Point2D corner1, Point2D corner2, double width0, MRModel model ) {
        super( corner1, corner2, width0 );
        init( model );
    }

    private void init( MRModel model ) {
        model.addListener( this );
    }

    public void setTemperature( double temperature ) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of PublishingModel.ModelListener
    //--------------------------------------------------------------------------------------------------


    public void modelElementAdded( ModelElement element ) {
        //noop
    }

    public void modelElementRemoved( ModelElement element ) {
        //noop
    }

    public void endOfTimeStep( PublishingModel model, ClockEvent event ) {
        setTemperature( ( (MRModel)model ).getTemperature() );
    }
}
