/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
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
    private AbstractGunGraphic gunGraphic;
    private ArrayList momentumChangeListeners = new ArrayList();
    private double intensityScale = 1.0;

    public GunParticle( final AbstractGunGraphic gunGraphic, String label, String imageLocation ) {
        super( label, imageLocation );
        this.gunGraphic = gunGraphic;
    }

    public abstract void activate( AbstractGunGraphic abstractGunGraphic );

    public abstract void deactivate( AbstractGunGraphic abstractGunGraphic );

    public AbstractGunGraphic getGunGraphic() {
        return gunGraphic;
    }

    public abstract double getStartPy();

    public WaveSetup getInitialWavefunction( Wavefunction currentWave ) {
        double x = getDiscreteModel().getGridWidth() * 0.5;
        double y = getStartY();
        double px = 0;
        double py = getStartPy();
        Point phaseLockPoint = new Point( (int)x, (int)( y - 5 ) );

        double dxLattice = getStartDxLattice();
        GaussianWave2D wave2DSetup = new GaussianWave2D( new Point( (int)x, (int)y ),
                                                         new Vector2D.Double( px, py ), dxLattice, getHBar() );
        double desiredPhase = currentWave.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();
        Wavefunction copy = currentWave.createEmptyWavefunction();
        wave2DSetup.initialize( copy );

        double uneditedPhase = copy.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();
        double deltaPhase = desiredPhase - uneditedPhase;

        wave2DSetup.setPhase( deltaPhase );
        wave2DSetup.setScale( getIntensityScale() );

        return wave2DSetup;
    }

    protected abstract double getHBar();

    private double getIntensityScale() {
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

    SchrodingerModule getSchrodingerModule() {
        return gunGraphic.getSchrodingerModule();
    }

    protected DiscreteModel getDiscreteModel() {
        return getSchrodingerModule().getDiscreteModel();
    }

    protected double getStartDxLattice() {
//        return 1.0;
        return 0.06 * getDiscreteModel().getGridWidth();
//        return 0.06 * getDiscreteModel().getGridWidth();
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

    class ChangeHandler implements ChangeListener {
        private AbstractGunGraphic.MomentumChangeListener momentumChangeListener;

        public ChangeHandler( AbstractGunGraphic.MomentumChangeListener momentumChangeListener ) {
            this.momentumChangeListener = momentumChangeListener;
        }

        public void stateChanged( ChangeEvent e ) {
            notifyMomentumChanged();
        }
    }

    public void addMomentumChangeListerner( AbstractGunGraphic.MomentumChangeListener momentumChangeListener ) {
        momentumChangeListeners.add( momentumChangeListener );
        ChangeHandler changeHandler = new ChangeHandler( momentumChangeListener );
        changeHandlers.add( changeHandler );
        hookupListener( changeHandler );
    }

    public void removeMomentumChangeListener( AbstractGunGraphic.MomentumChangeListener listener ) {
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
            AbstractGunGraphic.MomentumChangeListener momentumChangeListener = (AbstractGunGraphic.MomentumChangeListener)momentumChangeListeners.get( i );
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
