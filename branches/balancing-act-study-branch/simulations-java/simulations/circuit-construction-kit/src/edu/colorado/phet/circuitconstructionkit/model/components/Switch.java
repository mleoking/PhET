// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.vector.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 1:03:09 AM
 */
public class Switch extends CircuitComponent {
    boolean closed;
    public static final double OPEN_RESISTANCE = 1E11;
    public static final double DEFAULT_HANDLE_ANGLE_OPEN = 5;
    public static final double HANDLE_ANGLE_CLOSED = Math.PI;
    private double handleAngle = DEFAULT_HANDLE_ANGLE_OPEN;
    private boolean sendMessage = false;

    public Switch( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        super.setResistance( OPEN_RESISTANCE );
        setKirkhoffEnabled( true );
        setUserComponentID( CCKSimSharing.UserComponents.circuitSwitch );
        sendMessage = true;
    }

    public Switch( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, boolean closed, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
        setKirkhoffEnabled( false );
        this.closed = !closed;//to guarantee a change in setClosed.
        setClosed( closed );
        sendMessage = true;
        setKirkhoffEnabled( true );
        setHandleAngle( closed ? HANDLE_ANGLE_CLOSED : DEFAULT_HANDLE_ANGLE_OPEN );
        setUserComponentID( CCKSimSharing.UserComponents.circuitSwitch );
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed( boolean closed ) {
        if ( closed != this.closed ) {
            this.closed = closed;
            //Send open/close message, but not during startup
            if ( sendMessage ) {
                SimSharingManager.sendUserMessage( getUserComponentID(), UserComponentTypes.sprite, closed ? CCKSimSharing.UserActions.switchClosed : CCKSimSharing.UserActions.switchOpened );
            }
            if ( closed ) {
                super.setResistance( CCKModel.MIN_RESISTANCE ); //a resistance change fires a kirkhoff update.
            }
            else {
                super.setResistance( OPEN_RESISTANCE );
            }
        }
    }

    public void setHandleAngle( double angle ) {
        if ( angle == HANDLE_ANGLE_CLOSED ) {
            setClosed( true );
        }
        else {
            setClosed( false );
        }
        this.handleAngle = angle;
        notifyObservers();
    }

    public double getHandleAngle() {
        return handleAngle;
    }
}
