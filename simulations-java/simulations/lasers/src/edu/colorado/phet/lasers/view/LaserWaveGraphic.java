/**
 * Class: BlueBeamGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 28, 2004
 */
package edu.colorado.phet.lasers.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.Tube;
import edu.colorado.phet.lasers.controller.LasersConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

/**
 * A graphic that shows a standing wave whose amplitude is proportional to the number of photons
 * that are traveling more-or-less horizontally. It has a separate standing wave for inside the
 * cavity and a travelling wave for outside. It doesn't really work as a CompositePhetGraphic, however,
 * because it doesn't get added to the ApparatusPanel itself. Its two components do. This is necessary
 * because they need to be on different levels.
 */
public class LaserWaveGraphic implements LaserModel.ChangeListener {
    // This factor controls the visual amplitude of the waves inside and outside of the cavity
    public static double scaleFactor = 10;
    //    public static double scaleFactor = 5;
    public static double cyclesInCavity = 10;

    private Point2D internalWaveOrigin;
    private Point2D externalWaveOrigin;
    // The waves that are shown when the thing is lasing
    private StandingWaveGraphic internalStandingWaveGraphic;
    private TravelingWaveGraphic externalTravelingWaveGraphic;
    // The waves that are shown when the thins is not lasing
    private int numNonLasingExternalWaveGraphics = 5;
    private WaveGraphic[] nonLasingExternalWaveGraphics = new WaveGraphic[numNonLasingExternalWaveGraphics];
    private Stroke stroke = new BasicStroke( 2f );
    private PartialMirror mirror;
    private int numLasingPhotons;

    /**
     * Constructor
     *
     * @param apparatusPanel
     * @param cavity
     * @param mirror
     * @param module
     * @param atomicStates
     */
    public LaserWaveGraphic( ApparatusPanel apparatusPanel, Tube cavity,
                             PartialMirror mirror, BaseLaserModule module, final AtomicState[] atomicStates ) {

        LaserModel model = module.getLaserModel();

        // Have the model tell us when the number of lasing photons changes
        model.addLaserListener( this );

        this.mirror = mirror;

        // Create the lasing wave graphics
        Color color = Color.white;
        internalWaveOrigin = new Point2D.Double( cavity.getMinX(), cavity.getMinY() + cavity.getHeight() / 2 );
        internalStandingWaveGraphic = new StandingWaveGraphic( apparatusPanel, internalWaveOrigin, cavity.getWidth(),
                                                               cavity.getWidth() / cyclesInCavity, 100,
                                                               getNumLasingPhotons(), color, model );
        externalWaveOrigin = new Point2D.Double( cavity.getMinX() + cavity.getWidth(),
                                                 cavity.getMinY() + cavity.getHeight() / 2 );
        externalTravelingWaveGraphic = new TravelingWaveGraphic( apparatusPanel, externalWaveOrigin, 400,
                                                                 cavity.getWidth() / cyclesInCavity, 100,
                                                                 getNumLasingPhotons(), color, model );

        // Create the non-lasing wave graphics
        double dTheta = 20;
        double dy = cavity.getHeight() / numNonLasingExternalWaveGraphics;
        int j = numNonLasingExternalWaveGraphics / 2;
        for ( int i = 0; i < numNonLasingExternalWaveGraphics; i++ ) {
            double theta = ( i - j ) * dTheta;
            double yOffset = ( i - j ) * dy;
            Point2D nonLasingWaveOrigin = new Point2D.Double( cavity.getMinX() + cavity.getWidth(),
                                                              cavity.getMinY() + ( cavity.getHeight() / 2 ) + yOffset );
            WaveGraphic waveGraphic = new NonLasingWaveGraphic( apparatusPanel, nonLasingWaveOrigin, cavity.getWidth(),
                                                                cavity.getWidth() / cyclesInCavity, 100,
                                                                getNumLasingPhotons(), color, module,
                                                                Math.toRadians( theta ) );
            nonLasingExternalWaveGraphics[i] = waveGraphic;
        }

        apparatusPanel.addGraphic( internalStandingWaveGraphic, LasersConfig.LEFT_MIRROR_LAYER - 1 );
        apparatusPanel.addGraphic( externalTravelingWaveGraphic, LasersConfig.RIGHT_MIRROR_LAYER - 1 );
        for ( int i = 0; i < nonLasingExternalWaveGraphics.length; i++ ) {
            apparatusPanel.addGraphic( nonLasingExternalWaveGraphics[i], LasersConfig.RIGHT_MIRROR_LAYER - 1 );
        }

        atomicStates[1].addListener( new AtomicState.Listener() {
            public void energyLevelChanged( AtomicState.Event event ) {
                determineColor( atomicStates );
            }

            public void meanLifetimechanged( AtomicState.Event event ) {

            }
        } );
        atomicStates[0].addListener( new AtomicState.Listener() {
            public void energyLevelChanged( AtomicState.Event event ) {
                determineColor( atomicStates );
            }

            public void meanLifetimechanged( AtomicState.Event event ) {

            }
        } );
        determineColor( atomicStates );
    }

    /**
     * Determines the color to be used for the wave graphics. This is the wavelength
     * associated with the energy differential between the ground state and the next state up
     *
     * @param atomicStates
     */
    private void determineColor( AtomicState[] atomicStates ) {
        double wavelength = PhysicsUtil.energyToWavelength( atomicStates[1].getEnergyLevel() - atomicStates[0].getEnergyLevel() );
        Color color = VisibleColor.wavelengthToColor( wavelength );
        if ( color.getAlpha() == 0 ) {
            color = Color.gray;
        }
        externalTravelingWaveGraphic.setColor( color );
        internalStandingWaveGraphic.setColor( color );
        for ( int i = 0; i < nonLasingExternalWaveGraphics.length; i++ ) {
            WaveGraphic nonLasingExternalWaveGraphic = nonLasingExternalWaveGraphics[i];
            nonLasingExternalWaveGraphic.setColor( color );
        }
    }

    public void setVisible( boolean isVisible ) {
        internalStandingWaveGraphic.setVisible( isVisible );
        externalTravelingWaveGraphic.setVisible( isVisible );
        for ( int i = 0; i < nonLasingExternalWaveGraphics.length; i++ ) {
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
        if ( mirror != null ) {
            return n * Math.sqrt( 1 - mirror.getReflectivity() );
        }
        else {
            return 0;
        }
    }

    private double getInternalAmplitude() {
        double n = 4 * Math.sqrt( Math.max( 0, getNumLasingPhotons() - LasersConfig.LASING_THRESHOLD ) );
//        double n = scaleFactor * Math.sqrt( getNumLasingPhotons() < LaserConfig.LASING_THRESHOLD ? 0 : (double)getNumLasingPhotons() );

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

        // Update the non-lasing wave graphics. Reduce the amplitude by a large factor
        for ( int i = 0; i < nonLasingExternalWaveGraphics.length; i++ ) {
            WaveGraphic waveGraphic = nonLasingExternalWaveGraphics[i];
            int amp = getNumLasingPhotons() > LasersConfig.LASING_THRESHOLD ? 0 : ( getNumLasingPhotons() / 6 );
            waveGraphic.setAmplitude( amp );
        }
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    public void lasingPopulationChanged( LaserModel.ChangeEvent event ) {
        int newNum = event.getLasingPopulation();
        if ( newNum != numLasingPhotons ) {
            numLasingPhotons = newNum;
            update();
        }
    }

    public void atomicStatesChanged( LaserModel.ChangeEvent event ) {
        AtomicState[] atomicStates = new AtomicState[]{event.getLaserModel().getGroundState(),
                event.getLaserModel().getMiddleEnergyState()};
        determineColor( atomicStates );
        update();
    }
}
