/**
 * Class: MicrowaveModel
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwaves.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.microwaves.coreadditions.Vector2D;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;
import edu.colorado.phet.microwaves.model.waves.FiniteWaveMedium;
import edu.colorado.phet.microwaves.model.waves.WaveMedium;

public class MicrowaveModel extends BaseModel {

    private ArrayList polarBodies = new ArrayList();
    private FiniteWaveMedium waveMedium = new FiniteWaveMedium( new Point2D.Double( 100, 100 ), 500, 400 );
    private ArrayList microwaves = new ArrayList();
    private double frequency;
    private double amplitude;
    private Box2D oven;
    private Microwave mw;
    private final String name;

    public MicrowaveModel( String name ) {
        this.name = name;
        addModelElement( waveMedium );
        new WaterMoleculeWaterMoleculeCollisionExpert();
    }
    
    public void addMicrowave( Microwave microwave ) {
        waveMedium.addWavefront( microwave );
        microwaves.add( microwave );
        mw = microwave;
    }

    public void addPolarBody( PolarBody polarBody ) {
        polarBodies.add( polarBody );
        this.addModelElement( polarBody );
    }

    public void setOven( Box2D oven ) {
        if ( this.oven != null ) {
            removeModelElement( this.oven );
        }
        this.oven = oven;
        waveMedium.setBounds( new Point2D.Double( oven.getMinX(), oven.getMinY() ),
                              oven.getMaxX() - oven.getMinX(),
                              oven.getMaxY() - oven.getMinY() );
        addModelElement( oven );
    }

    public void update( ClockEvent event ) {

        for ( int i = 0; i < polarBodies.size(); i++ ) {
            PolarBody polarBody = (PolarBody) polarBodies.get( i );
            if ( (int) polarBody.getLocation().getX() >= 0 && (int) polarBody.getLocation().getX() < mw.getAmplitude().length ) {
                polarBody.respondToEmf( new Vector2D( 0, mw.getAmplitude()[(int) polarBody.getLocation().getX()] ), 0.1 );
            }
        }

        for ( int i = 0; i < polarBodies.size() - 1; i++ ) {
            for ( int j = i + 1; j < polarBodies.size(); j++ ) {
                WaterMolecule moleculeA = (WaterMolecule) polarBodies.get( i );
                WaterMolecule moleculeB = (WaterMolecule) polarBodies.get( j );

                // The two bodies were the last ones to contact each other, don't consider
                // that they might be colliding
                if ( !( moleculeA.getLastColidedBody() == moleculeB
                        && moleculeB.getLastColidedBody() == moleculeA ) ) {

                    if ( WaterMoleculeWaterMoleculeCollisionExpert.areInContact(
                            moleculeA, moleculeB ) ) {
                        moleculeA.setLastColidedBody( moleculeB );
                        moleculeB.setLastColidedBody( moleculeA );
                    }
                }
            }
        }

        super.update( event );

        if ( oven != null ) {
            for ( int i = 0; i < polarBodies.size(); i++ ) {
                WaterMolecule waterMolecule = (WaterMolecule) polarBodies.get( i );
                WaterMoleculeWallCollisionExpert.areInContact( (WaterMolecule) polarBodies.get( i ),
                                                               oven );
                oven.setLastColidedBody( waterMolecule );
                waterMolecule.setLastColidedBody( oven );
            }
        }
    }

    public double getFrequency() {
        return frequency;
    }

    public void setMicrowaveFrequency( double freq ) {
        this.frequency = freq;
        for ( int i = 0; i < microwaves.size(); i++ ) {
            Microwave microwave = (Microwave) microwaves.get( i );
            microwave.setFrequency( (float) freq );
        }
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setMicrowaveAmplitude( double amp ) {
        this.amplitude = amp;
        for ( int i = 0; i < microwaves.size(); i++ ) {
            Microwave microwave = (Microwave) microwaves.get( i );
            microwave.setMaxAmplitude( (float) amp );
        }
    }

    public final WaveMedium getWaveMedium() {
        return waveMedium;
    }

    public Box2D getOven() {
        return this.oven;
    }

    public void clear() {
        this.polarBodies.clear();
        this.waveMedium.clear();
        this.microwaves.clear();
        this.removeAllModelElements();
        this.addModelElement( waveMedium );
    }
}
