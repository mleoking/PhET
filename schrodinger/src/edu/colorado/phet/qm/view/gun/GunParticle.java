/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.waves.GaussianWave2D;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:12 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public abstract class GunParticle extends ImageComboBox.Item {
    private AbstractGunNode gunNode;
    private ArrayList momentumChangeListeners = new ArrayList();
    private double intensityScale = 1.0;
    protected boolean active = false;

    public GunParticle( final AbstractGunNode gunNode, String label, String imageLocation ) {
        super( label, imageLocation );
        this.gunNode = gunNode;
    }

    public abstract void activate( AbstractGunNode abstractGunNode );

    public abstract void deactivate( AbstractGunNode abstractGunNode );

    public AbstractGunNode getGunGraphic() {
        return gunNode;
    }

    public abstract double getStartPy();

    public WaveSetup getInitialWavefunction( Wavefunction currentWave ) {
        double x = getDiscreteModel().getGridWidth() * 0.5;
        double y = getStartY();
        double px = 0;
        double py = getStartPy();
        Point phaseLockPoint = new Point( (int)x, (int)( y - 5 ) );
        if( phaseLockPoint.y >= currentWave.getHeight() ) {
            phaseLockPoint.y = currentWave.getHeight() - 1;
        }
        if( phaseLockPoint.y < 0 ) {
            phaseLockPoint.y = 0;
        }
        double dxLattice = getStartDxLattice();
        GaussianWave2D wave2DSetup = new GaussianWave2D( new Point( (int)x, (int)y ),
                                                         new Vector2D.Double( px, py ), dxLattice, getHBar() );
        double desiredPhase = currentWave.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();
        Wavefunction copy = currentWave.createEmptyWavefunction();
        wave2DSetup.initialize( copy );

        double uneditedPhase = copy.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();
        double deltaPhase = desiredPhase - uneditedPhase;

        wave2DSetup.setPhase( deltaPhase );
//        wave2DSetup.setScale( getIntensityScale() * getWaveAreaSizeScale() * getResolutionIntensityFudgeFactor() );//would have handled bleaching.
        wave2DSetup.setScale( getIntensityScale() * getWaveAreaSizeScale() );

        return wave2DSetup;
    }

    private double getResolutionIntensityFudgeFactor() {
        return gunNode.getSchrodingerModule().getResolution().getTimeFudgeFactorForParticles();
    }

    private double getWaveAreaSizeScale() {
        return getDiscreteModel().getGridHeight() / 45;
    }

    protected abstract double getHBar();

    public double getIntensityScale() {
        return intensityScale;
    }

    protected double getStartY() {
        return getDiscreteModel().getGridHeight() * 0.85;
    }

    protected void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void fireParticle() {
        WaveSetup initialWavefunction = getInitialWavefunction( getDiscreteModel().getWavefunction() );
        getSchrodingerModule().fireParticle( initialWavefunction );
    }

    QWIModule getSchrodingerModule() {
        return gunNode.getSchrodingerModule();
    }

    protected QWIModel getDiscreteModel() {
        return getSchrodingerModule().getQWIModel();
    }

    protected double getStartDxLattice() {
        return 0.06 * getDiscreteModel().getGridWidth();
//        return 0.06 * getDiscreteModel().getGridWidth()/3.0;
    }

    private ArrayList changeHandlers = new ArrayList();

    public Point getGunLocation() {
        return new Point( -10, 35 );
    }

    public Map getModelParameters() {
        HashMap map = new HashMap();
        map.put( "startDXLattice", "" + getStartDxLattice() );
        return map;
    }

    public abstract boolean isFiring();

    public boolean isActive() {
        return active;
    }

    public abstract double getMinimumProbabilityForDetection();

    protected class ChangeHandler implements ChangeListener {
        private AbstractGunNode.MomentumChangeListener momentumChangeListener;

        public ChangeHandler( AbstractGunNode.MomentumChangeListener momentumChangeListener ) {
            this.momentumChangeListener = momentumChangeListener;
        }

        public void stateChanged( ChangeEvent e ) {
            notifyMomentumChanged();
        }
    }

    public void addMomentumChangeListerner( AbstractGunNode.MomentumChangeListener momentumChangeListener ) {
        momentumChangeListeners.add( momentumChangeListener );
        ChangeHandler changeHandler = new ChangeHandler( momentumChangeListener );
        changeHandlers.add( changeHandler );
        hookupListener( changeHandler );
    }

    public void removeMomentumChangeListener( AbstractGunNode.MomentumChangeListener listener ) {
        momentumChangeListeners.remove( listener );

        ArrayList toRemove = new ArrayList();
        for( int i = 0; i < changeHandlers.size(); i++ ) {
            ChangeHandler changeHandler = (ChangeHandler)changeHandlers.get( i );
            if( changeHandler.momentumChangeListener == listener ) {
                toRemove.add( changeHandler );
            }
        }

        for( int i = 0; i < toRemove.size(); i++ ) {
            ChangeHandler changeHandler = (ChangeHandler)toRemove.get( i );
            changeHandlers.remove( changeHandler );
            detachListener( changeHandler );
        }
    }

    protected abstract void detachListener( ChangeHandler changeHandler );

    protected abstract void hookupListener( ChangeHandler changeHandler );

    protected void notifyMomentumChanged() {
        double momentum = getStartPy();
        for( int i = 0; i < momentumChangeListeners.size(); i++ ) {
            AbstractGunNode.MomentumChangeListener momentumChangeListener = (AbstractGunNode.MomentumChangeListener)momentumChangeListeners.get( i );
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
