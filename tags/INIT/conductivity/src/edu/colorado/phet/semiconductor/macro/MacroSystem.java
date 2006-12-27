// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.semiconductor.macro.bands.ConductorBandSet;
import edu.colorado.phet.semiconductor.macro.bands.DefaultBandSet;
import edu.colorado.phet.semiconductor.macro.bands.InsulatorBandSet;
import edu.colorado.phet.semiconductor.macro.bands.PhotoconductorBandSet;
import edu.colorado.phet.semiconductor.macro.battery.Battery;
import edu.colorado.phet.semiconductor.macro.battery.BatteryListener;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuit;
import edu.colorado.phet.semiconductor.macro.particles.WireParticle;

import java.util.ArrayList;

public class MacroSystem
        implements ModelElement {

    public DefaultBandSet getBandSet() {
        return bandSet;
    }

    public MacroSystem( double d, double d1, double d2 ) {
        particles = new ArrayList();
        minVolts = d;
        maxVolts = d1;
        circuit = new MacroCircuit();
        Battery battery = circuit.getBattery();
        battery.addBatteryListener( new BatteryListener() {

            public void voltageChanged( Battery battery1 ) {
                doVoltageChanged();
            }

        } );
        conductor = new ConductorBandSet( this, d2 );
        insulator = new InsulatorBandSet( this, d2 );
        photoconductor = new PhotoconductorBandSet( this, d2 );
        setBandSet( conductor );
    }

    public MacroCircuit getCircuit() {
        return circuit;
    }

    public void doVoltageChanged() {
        double d = circuit.getBattery().getVoltage();
        double d1 = -0.0060000000000000001D;
        double d2 = 0.0D;
        double d3 = maxVolts - minVolts;
        double d4 = d1 - d2;
        double d5 = d4 / d3;
        double d6 = d2;
        recommendedSpeed = d * d5 + d6;
        double d7 = bandSet.voltageChanged( d, recommendedSpeed );
        for( int i = 0; i < particles.size(); i++ ) {
            WireParticle wireparticle = (WireParticle)particles.get( i );
            wireparticle.setSpeed( d7 );
        }

    }

    public void stepInTime( double d ) {
        bandSet.stepInTime( d );
    }

    public void setBandSet( DefaultBandSet defaultbandset ) {
        bandSet = defaultbandset;
    }

    public void photonHit() {
        photoconductor.photonHit();
    }

    public ConductorBandSet getConductor() {
        return conductor;
    }

    public InsulatorBandSet getInsulator() {
        return insulator;
    }

    public PhotoconductorBandSet getPhotoconductor() {
        return photoconductor;
    }

    public void photonGotAbsorbed() {
        double d = circuit.getBattery().getVoltage();
        double d1 = -0.0060000000000000001D;
        double d2 = 0.0D;
        double d3 = maxVolts - minVolts;
        double d4 = d1 - d2;
        double d5 = d4 / d3;
        double d6 = d2;
        recommendedSpeed = d * d5 + d6;
        double d7 = bandSet.desiredSpeedToActualSpeed( recommendedSpeed );
        for( int i = 0; i < particles.size(); i++ ) {
            WireParticle wireparticle = (WireParticle)particles.get( i );
            wireparticle.setSpeed( d7 );
        }

    }

    private MacroCircuit circuit;
    ArrayList particles;
    private double minVolts;
    private double maxVolts;
    DefaultBandSet bandSet;
    private double recommendedSpeed;
    private ConductorBandSet conductor;
    private InsulatorBandSet insulator;
    private PhotoconductorBandSet photoconductor;
}
