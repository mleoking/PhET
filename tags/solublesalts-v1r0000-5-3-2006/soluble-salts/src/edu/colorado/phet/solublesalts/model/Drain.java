/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Drain
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Drain extends Spigot implements Vessel.ChangeListener {
    private Vessel vessel;

    public static class Orientation {
    }

    public static Orientation HORIZONTAL = new Orientation();
    public static Orientation VERTICAL = new Orientation();

    private Line2D opening;
    // Distance, in ion radii, between an ion and the drain opening that will cause the ion to be
    // captured by the drain when it's open
    private int ionCaptureDistance = 2;
//    private int ionCaptureDistance = 8;
    private Vector2D.Double inputToOutputOffset = new Vector2D.Double( -95, 50 );

    public Drain( SolubleSaltsModel model,
                  Point2D location,
                  double openingHeight,
                  Orientation orientation,
                  double wallThickness ) {
        super( model );
        setPosition( location );
        this.vessel = model.getVessel();
        vessel.addChangeListener( this );


        if( orientation == HORIZONTAL ) {
            opening = new Line2D.Double( location.getX() + wallThickness,
                                         location.getY() - openingHeight / 2,
                                         location.getX() + wallThickness,
                                         location.getY() + openingHeight / 2 );
        }
        else if( orientation == VERTICAL ) {
            opening = new Line2D.Double( location.getX() - openingHeight / 2,
                                         location.getY(),
                                         location.getX() + openingHeight / 2,
                                         location.getY() );
        }
        else {
            throw new RuntimeException( "Invalid orientation" );
        }
    }

    /**
     * Capture ions that get in the drain opening
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        if( getFlow() != 0 ) {
            Vessel vessel = getModel().getVessel();
            double area = vessel.getWidth();
            double volume = vessel.getWaterLevel() - getFlow() / area;
            vessel.setWaterLevel( volume );

            ArrayList capturedIons = new ArrayList();
            List ions = getModel().getIons();
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );
                if( !ion.isBound() && opening.ptSegDist( ion.getPosition() ) < ion.getRadius() * ionCaptureDistance ) {
                    capturedIons.add( ion );
                }
            }
            for( int i = 0; i < capturedIons.size(); i++ ) {
                Ion ion = (Ion)capturedIons.get( i );
                ion.setPosition( new Point2D.Double( getPosition().getX() + inputToOutputOffset.getX(),
                                                     getPosition().getY() + inputToOutputOffset.getY()) );
                ion.setVelocity( new Vector2D.Double( ion.getVelocity().getMagnitude(), 0 ).rotate( Math.PI / 2 ));
            }
        }
    }

    public void setFlow( double flow ) {
        if( vessel.getWaterLevel() > 0 ) {
            super.setFlow( flow );
        }
        else {
            super.setFlow( 0 );
        }
    }

    public Line2D getOpening() {
        return opening;
    }

    //----------------------------------------------------------------
    // Vessel.ChangeListener implementation
    //----------------------------------------------------------------
    boolean noWater = false;
    double savedFlow;

    public void stateChanged( Vessel.ChangeEvent event ) {
        Vessel vessel = event.getVessel();
        if( vessel.getWaterLevel() <= 0 ) {
            setFlow( 0 );
        }
    }
}
