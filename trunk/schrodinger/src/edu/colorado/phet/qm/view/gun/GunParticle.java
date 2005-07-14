/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:12 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public abstract class GunParticle extends ImageComboBox.Item {
    private AbstractGun gun;
    private ArrayList momentumChangeListeners = new ArrayList();
    private double intensityScale = 1.0;

    public GunParticle( final AbstractGun gun, String label, String imageLocation ) {
        super( label, imageLocation );
        this.gun = gun;

    }

    public abstract void setup( AbstractGun abstractGun );

    public abstract void deactivate( AbstractGun abstractGun );

    public AbstractGun getGunGraphic() {
        return gun;
    }

    public abstract double getStartPy();

    public WaveSetup getInitialWavefunction( Wavefunction currentWave ) {
        double x = getDiscreteModel().getGridWidth() * 0.5;
        double y = getStartY();
        double px = 0;
        double py = getStartPy();

//        Point phaseLockPoint = new Point( (int)x, (int)( y + 5 ) );
        Point phaseLockPoint = new Point( (int)x, (int)( y - 5 ) );

        double dxLattice = getStartDxLattice();
        GaussianWave waveSetup = new GaussianWave( new Point( (int)x, (int)y ),
                                                   new Vector2D.Double( px, py ), dxLattice );

        double desiredPhase = currentWave.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();

        Wavefunction copy = currentWave.createEmptyWavefunction();
        waveSetup.initialize( copy );

        double uneditedPhase = copy.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();
        double deltaPhase = desiredPhase - uneditedPhase;

        waveSetup.setPhase( deltaPhase );
        waveSetup.setScale( getIntensityScale() );

        return waveSetup;
    }

    private double getIntensityScale() {
        return intensityScale;
    }

    protected double getStartY() {
        double y = getDiscreteModel().getGridHeight() * 0.8;
//        double y = getDiscreteModel().getGridHeight() * 0.7;
        return y;
    }

    private WaveSetup getInitialWavefunctionVerifyCorrect( Wavefunction currentWave ) {
        double x = getDiscreteModel().getGridWidth() * 0.5;
        double y = getStartY();
        double px = 0;
        double py = getStartPy();
        System.out.println( "py = " + py );

        Point phaseLockPoint = new Point( (int)x, (int)( y - 5 ) );

        double dxLattice = getStartDxLattice();
        System.out.println( "dxLattice = " + dxLattice );
        GaussianWave waveSetup = new GaussianWave( new Point( (int)x, (int)y ),
                                                   new Vector2D.Double( px, py ), dxLattice );

        Complex centerValue = currentWave.valueAt( phaseLockPoint.x, phaseLockPoint.y );
        double desiredPhase = centerValue.getComplexPhase();

        System.out.println( "original Center= " + centerValue + ", desired phase=" + desiredPhase );

        Wavefunction copy = currentWave.createEmptyWavefunction();
        waveSetup.initialize( copy );

        Complex centerValueCopy = copy.valueAt( phaseLockPoint.x, phaseLockPoint.y );
        System.out.println( "unedited: =" + centerValueCopy + ", unedited phase=" + centerValueCopy.getComplexPhase() );

        double uneditedPhase = centerValueCopy.getComplexPhase();
        double deltaPhase = desiredPhase - uneditedPhase;

        System.out.println( "deltaPhase = " + deltaPhase );
        waveSetup.setPhase( deltaPhase );

        Wavefunction test = currentWave.createEmptyWavefunction();
        waveSetup.initialize( test );
        Complex testValue = test.valueAt( phaseLockPoint.x, phaseLockPoint.y );
        System.out.println( "created testValue = " + testValue + ", created phase=" + testValue.getComplexPhase() );

        return waveSetup;
    }

    protected void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void fireParticle() {
        WaveSetup initialWavefunction = getInitialWavefunction( getDiscreteModel().getWavefunction() );
        getSchrodingerModule().fireParticle( initialWavefunction );
    }

    private SchrodingerModule getSchrodingerModule() {
        return gun.getSchrodingerModule();
    }

    protected DiscreteModel getDiscreteModel() {
        return getSchrodingerModule().getDiscreteModel();
    }

    protected double getStartDxLattice() {
//            double dxLattice = 0.04 * getDiscreteModel().getGridWidth();
        double dxLattice = 0.06 * getDiscreteModel().getGridWidth();
        return dxLattice;
    }

    public void addMomentumChangeListerner( AbstractGun.MomentumChangeListener momentumChangeListener ) {
        momentumChangeListeners.add( momentumChangeListener );
        hookupListener( momentumChangeListener );
    }

    protected abstract void hookupListener( AbstractGun.MomentumChangeListener momentumChangeListener );

    protected void notifyMomentumChanged() {
        double momentum = getStartPy();
        for( int i = 0; i < momentumChangeListeners.size(); i++ ) {
            AbstractGun.MomentumChangeListener momentumChangeListener = (AbstractGun.MomentumChangeListener)momentumChangeListeners.get( i );
            momentumChangeListener.momentumChanged( momentum );
        }
    }


    public void autofire() {
        fireParticle();
    }

    public void setIntensityScale( double intensityScale ) {
        this.intensityScale = intensityScale;
    }
}
