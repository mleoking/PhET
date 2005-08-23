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

import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.dischargelamps.model.ElectronSink;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.*;

/**
 * PhotoelectricTarget
 * <p/>
 * The plate in the photoelectric model that is bombarded with light
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricTarget extends ElectronSource {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    static public final double ELECTRON_MASS = 9.11E-31;
    static private final double SPEED_SCALE_FACTOR = 5E-16;
    static private final double MINIMUM_SPEED = 0.1;

    static public final Object ZINC = new String( "Zinc" );
    static public final Object COPPER = new String( "Copper" );
    static public final Object SODIUM = new String( "Sodium" );
    static public final Object PLATINUM = new String( "Platinum" );
    static public final Object MAGNESIUM = new String( "???" );

    static public final HashMap WORK_FUNCTIONS = new HashMap();

    static {
        WORK_FUNCTIONS.put( ZINC, new Double( 4.3 ) );
        WORK_FUNCTIONS.put( COPPER, new Double( 4.7 ) );
        WORK_FUNCTIONS.put( SODIUM, new Double( 2.3 ) );
        WORK_FUNCTIONS.put( MAGNESIUM, new Double( 3.7 ) );
        WORK_FUNCTIONS.put( PLATINUM, new Double( 6.3 ) );
    }

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private Random random = new Random();
    // The line segment defined by the target
    private Line2D line;
    // The work function for the target material
    private double workFunction;
    private Object targetMaterial;
    // The strategy that determines the speed of emitted electrons
    private InitialElectronSpeedStrategy initialElectronSpeedStrategy = new
            InitialElectronSpeedStrategy.Uniform( SPEED_SCALE_FACTOR );

    /**
     * @param model
     * @param p1
     * @param p2
     */
    public PhotoelectricTarget( BaseModel model, Point2D p1, Point2D p2 ) {
        super( model, p1, p2 );
        line = new Line2D.Double( p1, p2 );
    }

    /**
     * @param p1
     * @param p2
     */
    public void setEndpoints( Point2D p1, Point2D p2 ) {
        line = new Line2D.Double( p1, p2 );
        super.setEndpoints( new Point2D[]{p1, p2} );
    }

    /**
     * Produces an electron of appropriate energy if the specified photon has enough energy.
     *
     * @param photon
     */
    public void handlePhotonCollision( Photon photon ) {

        double de = photon.getEnergy() - workFunction;
        if( de > 0 ) {

            // Determine where the electron will be emitted from
            // The location of the electron is coincident with where the photon hit the plate
            Point2D p = MathUtil.getLineSegmentsIntersection( line.getP1(), line.getP2(),
                                                              photon.getPosition(), photon.getPositionPrev() );
            Electron electron = new Electron( p.getX() + 1, p.getY() );
            electron.setPosition( p.getX() + 1, p.getY() );

            // Determine the speed of the new electron
            Vector2D velocity = determineNewElectronVelocity( de );
            electron.setVelocity( velocity );

            // Tell all the listeners
            getElectronProductionListenerProxy().electronProduced( new ElectronProductionEvent( this, electron ) );
        }
    }

    /**
     * Used if electrons can be emitted through a range of
     *
     * @param energy
     * @return
     */
    private Vector2D determineNewElectronVelocity( double energy ) {

        double speed = initialElectronSpeedStrategy.determineNewElectronSpeed( energy );
        double dispersionAngle = 0;
        double angle = random.nextDouble() * dispersionAngle - dispersionAngle / 2;
        double vx = speed * Math.cos( angle );
        double vy = speed * Math.sin( angle );
        return new Vector2D.Double( vx, vy );
    }

    /**
     * Tells if the target has been hit by a specified photon in the last time step
     *
     * @param photon
     * @return
     */
    public boolean isHitByPhoton( Photon photon ) {
        boolean result = line.intersectsLine( photon.getPosition().getX(), photon.getPosition().getY(),
                                              photon.getPositionPrev().getX(), photon.getPositionPrev().getY() );
        return result;
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------
    public void setWorkFunction( double workFunction ) {
        this.workFunction = workFunction;
    }

    public void setWorkFunction( Object workFunction ) {
        if( !( workFunction instanceof Double ) ) {
            throw new RuntimeException( "Invalid parameter type" );
        }
        setWorkFunction( ( (Double)workFunction ).doubleValue() );
    }

    public void setMaterial( Object material ) {
        this.targetMaterial = material;
        if( !WORK_FUNCTIONS.keySet().contains( material ) ) {
            throw new RuntimeException( "Invalid parameter" );
        }
        setWorkFunction( WORK_FUNCTIONS.get( material ) );
        materialChangeListenerProxy.materialChanged( new MaterialChangeEvent( this ) );
    }

    public Object getMaterial() {
        return targetMaterial;
    }

    public void setUniformInitialElectronSpeedStrategy() {
        this.initialElectronSpeedStrategy = new InitialElectronSpeedStrategy.Uniform( SPEED_SCALE_FACTOR );
    }

    public void setRandomizedInitialElectronSpeedStrategy() {
        this.initialElectronSpeedStrategy = new InitialElectronSpeedStrategy.Randomized( SPEED_SCALE_FACTOR,
                                                                                         MINIMUM_SPEED );
    }

    //----------------------------------------------------------------
    // Event and listener definitions
    //----------------------------------------------------------------
    public class MaterialChangeEvent extends EventObject {
        public MaterialChangeEvent( Object source ) {
            super( source );
        }

        public PhotoelectricTarget getPhotoelectricTarget() {
            return (PhotoelectricTarget)getSource();
        }

        public Object getMaterial() {
            return getPhotoelectricTarget().getMaterial();
        }
    }

    public interface MaterialChangeListener extends EventListener {
        void materialChanged( MaterialChangeEvent event );
    }

    private EventChannel materialChangeEventChannel = new EventChannel( MaterialChangeListener.class );
    private MaterialChangeListener materialChangeListenerProxy =
            (MaterialChangeListener)materialChangeEventChannel.getListenerProxy();

    public void addMaterialChangeListener( MaterialChangeListener listener ) {
        materialChangeEventChannel.addListener( listener );
    }

    public void removeMaterialChangeListener( MaterialChangeListener listener ) {
        materialChangeEventChannel.removeListener( listener );
    }
}
