package edu.colorado.phet.movingman.motion.movingman;

import edu.colorado.phet.common.motion.model.ITemporalVariable;

/**
 * Created by: Sam
 * Dec 5, 2007 at 10:14:34 AM
 */
public interface IMovingManModel {
    void setPositionDriven();

    void setPosition( double x );

    ITemporalVariable getXVariable();

    double getPosition();

    ITemporalVariable getVVariable();

    void addListener( MovingManMotionModel.Listener listener );

    boolean isBoundaryOpen();

    void unpause();
}
