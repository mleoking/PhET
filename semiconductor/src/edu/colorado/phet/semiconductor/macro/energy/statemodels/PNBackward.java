/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateModel;
import edu.colorado.phet.semiconductor.macro.energy.StateTransitionList;
import edu.colorado.phet.semiconductor.macro.energy.bands.Band;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyLevel;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Exit;
import edu.colorado.phet.semiconductor.macro.energy.transitions.IfTop;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;

/**
 * User: Sam Reid
 * Date: Mar 27, 2004
 * Time: 7:19:20 PM
 * Copyright (c) Mar 27, 2004 by Sam Reid
 */
public class PNBackward implements StateModel {
    StateTransitionList transitionList = new StateTransitionList();
    EnergySection energySection;

    public PNBackward( EnergySection energySection ) {
        this.energySection = energySection;

        Band valenceBand = energySection.bandSetAt( 0 ).getValenceBand();
        for( int i = 0; i < valenceBand.numEnergyLevels(); i++ ) {
            EnergyLevel level = valenceBand.energyLevelAt( i );
            Move m = new Move( level.cellAt( 0 ), level.cellAt( 1 ), energySection.getSpeed() );
            transitionList.addTransition( m );
        }

        Band upright = energySection.bandSetAt( 1 ).getConductionBand();
        for( int i = 0; i < upright.numEnergyLevels(); i++ ) {
            EnergyLevel lvl = upright.energyLevelAt( i );
            EnergyCell cell = lvl.cellAt( 1 );
            Exit exit = new Exit( cell, cell.getPosition().getAddedInstance( .4, 0 ) );
            IfTop it = new IfTop( exit );
            transitionList.addTransition( it );

            EnergyCell leftCell = lvl.cellAt( 0 );
            Move move = new Move( leftCell, cell, energySection.getSpeed() );
            IfTop topmove = new IfTop( move );
            transitionList.addTransition( topmove );
        }
        Band upleft = energySection.bandSetAt( 0 ).getConductionBand();
        for( int i = 0; i < upleft.numEnergyLevels(); i++ ) {
            EnergyLevel level = upleft.energyLevelAt( i );
            EnergyCell rightCell = level.cellAt( 1 );
            EnergyCell rightNeighbor = energySection.getNeighbor( rightCell, 0, 1 );
            Move move = new Move( rightCell, rightNeighbor, energySection.getSpeed() );
            transitionList.addTransition( move );
        }

    }

    //step in time.
    public void updateStates() {
        for( int i = 0; i < energySection.numParticles(); i++ ) {
            BandParticle bp = energySection.particleAt( i );
            transitionList.apply( bp, energySection );
        }
        enterNewElectrons();
    }

    public void enterNewElectrons() {
        EntryPoint[] sources = getEntryPoints();
        for( int i = 0; i < sources.length; i++ ) {
            EntryPoint source = sources[i];
            enter( source );
        }
    }

    public void enter( EntryPoint source ) {
        BandParticle bp = energySection.getBandParticle( source.getCell() );
        if( bp == null ) {
            //free to enter.
            bp = new BandParticle( source.getSource() );
            bp.setEnergyCell( source.getCell() );
            bp.setExcited( true );
            bp.setState( new MoveToCell( bp, source.getCell(), energySection.getSpeed() ) );
            energySection.addParticle( bp );
        }
    }

    public EntryPoint[] getEntryPoints() {
        //Enter at the valence band's highest free level, could wait until other level is "finished", ie
        //when particles stop moving.
        Band valenceBand = energySection.bandSetAt( 0 ).getValenceBand();
        double x = energySection.bandSetAt( 0 ).getX();
        for( int i = 0; i < valenceBand.numEnergyLevels(); i++ ) {
            EnergyLevel level = valenceBand.energyLevelAt( i );
            int num = energySection.getNumParticlesAtCells( level );
            EnergyCell cell = level.cellAt( 0 );
            if( num < 2 || level.getDistanceFromTopLevelInBand() == 0 ) {
                return new EntryPoint[]{new EntryPoint( x, cell.getEnergy(), cell )};
            }
        }

        return new EntryPoint[0];
    }

    public static boolean isLowerPresent( EnergySection energySection, EnergyCell lower ) {
        if( lower == null ) {
            return true;
        }
        BandParticle bp = energySection.getBandParticle( lower );
        if( bp == null ) {
            return true;
        }
        if( bp.isLocatedAtCell() ) {
            return true;
        }
        return false;
    }

}
