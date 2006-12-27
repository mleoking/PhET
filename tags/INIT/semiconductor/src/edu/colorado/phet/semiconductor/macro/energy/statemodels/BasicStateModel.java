/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateModel;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.StateTransitionList;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Exit;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Fall;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 27, 2004
 * Time: 3:04:44 PM
 * Copyright (c) Mar 27, 2004 by Sam Reid
 */
public abstract class BasicStateModel implements StateModel {
    protected EnergySection energySection;
    private EnergyCell entryPoint;
    private EnergyCell exitCell;
    protected StateTransitionList transitionList = new StateTransitionList();
    ArrayList entryPoints = new ArrayList();
    EnergyCell current;

    public BasicStateModel( EnergySection energySection, EnergyCell entryPoint, EnergyCell exitCell ) {
        this.energySection = energySection;
        this.entryPoint = entryPoint;
        this.exitCell = exitCell;
    }

    //step in time.
    public void updateStates() {
        for( int i = 0; i < energySection.numParticles(); i++ ) {
            BandParticle bp = energySection.particleAt( i );
            transitionList.apply( bp, energySection );
        }
        enterNewElectrons();
    }

    public EnergySection getEnergySection() {
        return energySection;
    }

    public EnergyCell getEntryPoint() {
        return entryPoint;
    }

    public EnergyCell getExitCell() {
        return exitCell;
    }

    public void enterNewElectrons() {
        EntryPoint[] sources = getEntryPoints();
        for( int i = 0; i < sources.length; i++ ) {
            EntryPoint source = sources[i];
            enter( source );
        }
    }

    public void addTransition( StateTransition t ) {
        transitionList.addTransition( t );
    }

    public void enter( EntryPoint source ) {
        BandParticle bp = energySection.getBandParticle( source.getCell() );
        if( bp == null ) {
            //free to enter.
            bp = new BandParticle( source.getSource() );
            bp.setExcited( true );
            bp.setState( new MoveToCell( bp, source.getCell(), energySection.getSpeed() ) );
            energySection.addParticle( bp );
        }
    }

    public void addEntryPoint( EntryPoint ep ) {
        entryPoints.add( ep );
    }

    public EntryPoint[] getEntryPoints() {
        return (EntryPoint[])entryPoints.toArray( new EntryPoint[0] );
    }

    public void exitTo( PhetVector pos ) {
        Exit exit = new Exit( current, pos );
        transitionList.addTransition( exit );
    }

    public void moveTo( EnergyCell at ) {
        Move move = new Move( current, at, energySection.getSpeed() );
        this.current = at;
        transitionList.addTransition( move );
    }

    public void fallTo( EnergyCell at ) {
        Move move = new Move( current, at, energySection.getFallSpeed() );
        this.current = at;
        transitionList.addTransition( move );
    }

    public void enterTo( double x, EnergyCell at ) {
        current = at;
        EntryPoint ep = new EntryPoint( x, at.getEnergy(), at );
        addEntryPoint( ep );
    }

    public void fall() {
        Fall fall = new Fall( current );
        transitionList.addTransition( fall );
    }

}
