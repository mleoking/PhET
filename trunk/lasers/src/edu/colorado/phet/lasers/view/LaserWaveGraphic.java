/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A graphic that shows a standing wave whose amplitude is proportional to the number of photons
 * that are traveling more-or-less horizontally.
 */
public class LaserWaveGraphic extends CompositePhetGraphic implements /*Photon.LeftSystemEventListener,
                                                                      PhotonEmittedListener,*/
                                                                      LaserModel.LaserListener,
                                                                      StandingWaveGraphic.Listener {
    // This factor controls the visual amplitude of the waves inside and outside of the cavity
    public static double scaleFactor = 5;
    public static double cyclesInCavity = 10;

    private Point2D internalWaveOrigin;
    private Point2D externalWaveOrigin;
    private StandingWaveGraphic internalStandingWaveGraphic;
    private TravelingWaveGraphic externalStandingWaveGraphic;
    // Angle that is considered horizontal, for purposes of lasing
    private double angleWindow = LaserConfig.PHOTON_CHEAT_ANGLE;
//    private HashSet lasingPhotons = new HashSet();
    private Stroke stroke = new BasicStroke( 2f );
    private PartialMirror mirror;
    private Rectangle bounds;
    private AtomicState atomicState;
    private int numLasingPhotons;

    public LaserWaveGraphic( Component component, ResonatingCavity cavity,
                             PartialMirror mirror, LaserModel model, AtomicState atomicState ) {
        super( component );

        // Register with the Photon class so we will get notified when photons are created
//        Photon.addClassListener( this );

        model.addLaserListener( this );

        this.atomicState = atomicState;
        this.mirror = mirror;
        internalWaveOrigin = new Point2D.Double( cavity.getMinX(), cavity.getMinY() + cavity.getHeight() / 2 );
        internalStandingWaveGraphic = new StandingWaveGraphic( component, internalWaveOrigin, cavity.getWidth(),
                                                               cavity.getWidth() / cyclesInCavity, 100,
                                                               getNumLasingPhotons(), atomicState, model );
        internalStandingWaveGraphic.addListener( this );
        externalWaveOrigin = new Point2D.Double( cavity.getMinX() + cavity.getWidth(),
                                                 cavity.getMinY() + cavity.getHeight() / 2 );
        externalStandingWaveGraphic = new TravelingWaveGraphic( component, externalWaveOrigin, 400,
                                                                cavity.getWidth() / cyclesInCavity, 100,
                                                                getNumLasingPhotons(), atomicState, model );
        externalStandingWaveGraphic.addListener( this );
    }

    public void waveChanged( StandingWaveGraphic.ChangeEvent event ) {
        setBoundsDirty();
        repaint();
    }

    private int getNumLasingPhotons() {
        return numLasingPhotons;
    }

    protected Rectangle determineBounds() {
        Rectangle isb = internalStandingWaveGraphic.determineBounds();
        Rectangle esb = externalStandingWaveGraphic.determineBounds();
        bounds = esb.union( isb );
        return bounds;
    }

    private double getExternalAmplitude() {
        double n = getInternalAmplitude();
        if( mirror != null ) {
            return n * Math.sqrt( 1 - mirror.getReflectivity() );
        }
        else {
            return 0;
        }
    }

    private double getInternalAmplitude() {
        double n = scaleFactor * Math.sqrt( getNumLasingPhotons() < LaserConfig.LASING_THRESHOLD ? 0 : (double)getNumLasingPhotons() );
        return n;
    }

    public StandingWaveGraphic getInternalStandingWave() {
        return internalStandingWaveGraphic;
    }

    public TravelingWaveGraphic getExternalStandingWave() {
        return externalStandingWaveGraphic;
    }

    private void update() {
        internalStandingWaveGraphic.setAmplitude( getInternalAmplitude() );
        externalStandingWaveGraphic.setAmplitude( getExternalAmplitude() );
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    public void lasingPopulationChanged( LaserModel.LaserEvent event ) {
        int newNum = event.getLasingPopulation();
        if( newNum != numLasingPhotons ) {
            numLasingPhotons = newNum;
            update();
        }
    }
}
