package edu.colorado.phet.buildanatom.modules.game.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Represents one row from the table defined in the design doc (see pools for level 1-3)
 *
 * @author Sam Reid
 */
public class AtomValue extends Atom {
    private Property<Integer> numProtonsProperty=new Property<Integer>( 0);
    private Property<Integer> numElectronsProperty=new Property<Integer>( 0);
    private Property<Integer> numNeutronsProperty=new Property<Integer>( 0);

    public AtomValue( int protons, int neutrons, int electrons ) {
        super( new Point2D.Double( 0, 0 ) );
        numProtonsProperty.addObserver( new SimpleObserver() {
            public void update() {
                setNumProtons( numProtonsProperty.getValue() );
            }
        } );
        numElectronsProperty.addObserver( new SimpleObserver() {
            public void update() {
                setNumElectrons( numElectronsProperty.getValue() );
            }
        } );
        numNeutronsProperty().addObserver( new SimpleObserver() {
            public void update() {
                setNumNeutrons( numNeutronsProperty.getValue() );
            }
        } );
        addObserver( new SimpleObserver() {
            public void update() {
                numProtonsProperty.setValue( getNumProtons() );
                numElectronsProperty.setValue( getNumElectrons() );
                numNeutronsProperty.setValue( getNumNeutrons() );
            }
        } );
        for ( int i = 0; i < protons; i++ ) {
            addProton( new Proton( ConstantDtClock.TEST ) );
        }
        for ( int i = 0; i < neutrons; i++ ) {
            addNeutron( new Neutron( ConstantDtClock.TEST ) );
        }
        for ( int i = 0; i < electrons; i++ ) {
            addElectron( new Electron( ConstantDtClock.TEST ) );
        }
    }

    public void setNumProtons(int numProtons){
        while ( numProtons > getNumProtons()){
            addProton(new Proton( ConstantDtClock.TEST ));
        }
        while ( numProtons < getNumProtons()){
            removeProton();
        }
    }
    public void setNumNeutrons(int numNeutrons){
        while ( numNeutrons > getNumNeutrons()){
            addNeutron(new Neutron( ConstantDtClock.TEST ));
        }
        while ( numNeutrons < getNumNeutrons()){
            removeNeutron();
        }
    }
    public void setNumElectrons(int numElectrons){
        while ( numElectrons > getNumElectrons()){
            addElectron(new Electron( ConstantDtClock.TEST ));
        }
        while ( numElectrons < getNumElectrons()){
            removeElectron();
        }
    }

    public boolean guessEquals( AtomValue atom ) {
        return atom.getNumProtons()==getNumProtons() && atom.getNumNeutrons()==getNumNeutrons() && atom.getNumElectrons() == getNumElectrons();
    }

    public void setState( AtomValue atom ) {
        setNumElectrons( atom.getNumElectrons() );
        setNumProtons( atom.getNumProtons() );
        setNumNeutrons( atom.getNumNeutrons() );
    }

    public Property<Integer> numProtonsProperty() {
        return numProtonsProperty;
    }

    public Property<Integer> numNeutronsProperty() {
        return numNeutronsProperty;
    }

    public Property<Integer> numElectronsProperty() {
        return numElectronsProperty;
    }
}
