/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A graphic that shows a standing wave whose amplitude is proportional to the number of photons
 * that are traveling more-or-less horizontally. It has a separate standing wave for inside the
 * cavity and a travelling wave for outside. It doesn't really work as a CompositePhetGraphic, however,
 * because it doesn't get added to the ApparatusPanel itself. Its two components do. This is necessary
 * because they need to be on different levels.
 */
public class LaserWaveGraphic implements LaserModel.LaserListener {
    // This factor controls the visual amplitude of the waves inside and outside of the cavity
    public static double scaleFactor = 5;
    public static double cyclesInCavity = 10;

    private Point2D internalWaveOrigin;
    private Point2D externalWaveOrigin;
    // The waves that are shown when the thing is lasing
    private StandingWaveGraphic internalStandingWaveGraphic;
    private TravelingWaveGraphic externalTravelingWaveGraphic;
    // The waves that are shown when the thins is not lasing
    private int numNonLasingExternalWaveGraphics = 5;
    private WaveGraphic[] nonLasingExternalWaveGraphics = new WaveGraphic[numNonLasingExternalWaveGraphics];
    // Angle that is considered horizontal, for purposes of lasing
    private double angleWindow = LaserConfig.PHOTON_CHEAT_ANGLE;
    private Stroke stroke = new BasicStroke( 2f );
    private PartialMirror mirror;
    private int numLasingPhotons;
    private ApparatusPanel apparatusPanel;

    public LaserWaveGraphic( ApparatusPanel apparatusPanel, ResonatingCavity cavity,
                             PartialMirror mirror, LaserModel model, AtomicState atomicState ) {

        this.apparatusPanel = apparatusPanel;

        // Have the model tell us when the number of lasing photons changes
        model.addLaserListener( this );

        this.mirror = mirror;

        // Create the lasing wave graphics
        internalWaveOrigin = new Point2D.Double( cavity.getMinX(), cavity.getMinY() + cavity.getHeight() / 2 );
        internalStandingWaveGraphic = new StandingWaveGraphic( apparatusPanel, internalWaveOrigin, cavity.getWidth(),
                                                               cavity.getWidth() / cyclesInCavity, 100,
                                                               getNumLasingPhotons(), atomicState, model );
        externalWaveOrigin = new Point2D.Double( cavity.getMinX() + cavity.getWidth(),
                                                 cavity.getMinY() + cavity.getHeight() / 2 );
        externalTravelingWaveGraphic = new TravelingWaveGraphic( apparatusPanel, externalWaveOrigin, 400,
                                                                 cavity.getWidth() / cyclesInCavity, 100,
                                                                 getNumLasingPhotons(), atomicState, model );

        // Create the non-lasing wave graphics
        double dTheta = 20;
        int j = numNonLasingExternalWaveGraphics / 2;
        Point2D nonLasingWaveOrigin = new Point2D.Double( cavity.getMinX() + cavity.getWidth() + LaserConfig.MIRROR_THICKNESS,
                                                          cavity.getMinY() + cavity.getHeight() / 2 );
        for( int i = 0; i < numNonLasingExternalWaveGraphics; i++ ) {
            double theta = ( i - j ) * dTheta;
            WaveGraphic waveGraphic = new NonLasingWaveGraphic( apparatusPanel, nonLasingWaveOrigin, cavity.getWidth(),
                                                                cavity.getWidth() / cyclesInCavity, 100,
                                                                getNumLasingPhotons(), atomicState, model,
                                                                Math.toRadians( theta ) );
            nonLasingExternalWaveGraphics[i] = waveGraphic;
        }

        apparatusPanel.addGraphic( internalStandingWaveGraphic, LaserConfig.LEFT_MIRROR_LAYER - 1 );
        apparatusPanel.addGraphic( externalTravelingWaveGraphic, LaserConfig.RIGHT_MIRROR_LAYER - 1 );
        for( int i = 0; i < nonLasingExternalWaveGraphics.length; i++ ) {
            apparatusPanel.addGraphic( nonLasingExternalWaveGraphics[i], LaserConfig.RIGHT_MIRROR_LAYER - 1 );
        }

    }

    public void setVisible( boolean isVisible ) {
        internalStandingWaveGraphic.setVisible( isVisible );
        externalTravelingWaveGraphic.setVisible( isVisible );
        for( int i = 0; i < nonLasingExternalWaveGraphics.length; i++ ) {
            nonLasingExternalWaveGraphics[i].setVisible( isVisible );
        }
    }


    public WaveGraphic[] getNonLasingExternalWaveGraphics() {
        return nonLasingExternalWaveGraphics;
    }

    private int getNumLasingPhotons() {
        return numLasingPhotons;
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
        return externalTravelingWaveGraphic;
    }

    private void update() {
        internalStandingWaveGraphic.setAmplitude( getInternalAmplitude() );
        externalTravelingWaveGraphic.setAmplitude( getExternalAmplitude() );
        for( int i = 0; i < nonLasingExternalWaveGraphics.length; i++ ) {
            WaveGraphic waveGraphic = nonLasingExternalWaveGraphics[i];
            int amp = getNumLasingPhotons() > LaserConfig.LASING_THRESHOLD ? 0 : ( getNumLasingPhotons() / 5 );
            waveGraphic.setAmplitude( amp );
        }
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
