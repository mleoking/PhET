/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.bands.Band;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyLevel;
import edu.colorado.phet.semiconductor.macro.energy.bands.SemiconductorBandSet;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Exit;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Fall;
import edu.colorado.phet.semiconductor.macro.energy.transitions.IfTop;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 8:50:15 AM
 * Copyright (c) Mar 15, 2004 by Sam Reid
 */
public class PNForward extends ForwardBiasDiode {

    public PNForward( EnergySection energySection, EnergyCell entryPoint, EnergyCell exitCell ) {
        super( energySection, entryPoint, exitCell, -1 );

        Band pband = energySection.bandSetAt( 0 ).getValenceBand();
        int startLevel = exitCell.getBand().indexOf( exitCell.getEnergyLevel() );
        for( int i = startLevel; i < pband.numEnergyLevels(); i++ ) {
//            System.out.println( "i = " + i );
            EnergyLevel lvl = pband.energyLevelAt( i );
            EnergyCell right = lvl.cellAt( 1 );
            EnergyCell left = lvl.cellAt( 0 );
            Move m = new Move( right, left, energySection.getSpeed() );
            IfTop mt = new IfTop( m );
            super.addTransition( mt );
            Exit exit = new Exit( left, left.getPosition().getAddedInstance( -.4, 0 ) );
            IfTop et = new IfTop( exit );
            super.addTransition( et );

            super.addTransition( new Fall( right ) );
            super.addTransition( new Fall( left ) );

        }
        Band pconBand = energySection.bandSetAt( 0 ).getConductionBand();
        for( int i = 0; i < pconBand.numEnergyLevels(); i++ ) {
            EnergyLevel lvl = pconBand.energyLevelAt( i );
            EnergyCell a = lvl.cellAt( 0 );
            super.addTransition( new Fall( a ) );
            super.addTransition( new Fall( lvl.cellAt( 1 ) ) );
        }
        Band nconBand = energySection.bandSetAt( 1 ).getConductionBand();
        for( int i = 0; i < nconBand.numEnergyLevels(); i++ ) {
            EnergyLevel lvl = nconBand.energyLevelAt( i );
            Move mo = new Move( lvl.cellAt( 1 ), lvl.cellAt( 0 ), energySection.getSpeed() );
            super.addTransition( mo );
        }
    }

    public EntryPoint[] getEntryPoints() {
        //Enter at the valence band's highest free level, could wait until other level is "finished", ie
        //when particles stop moving.
        Band valenceBand = energySection.bandSetAt( 1 ).getConductionBand();
        double x = energySection.bandSetAt( 1 ).getX() + energySection.bandSetAt( 1 ).getWidth();
        for( int i = 0; i < valenceBand.numEnergyLevels() && i < SemiconductorBandSet.NUM_DOPING_LEVELS; i++ ) {
            EnergyLevel level = valenceBand.energyLevelAt( i );
            EnergyCell cell = level.cellAt( 1 );

            int num = energySection.getNumParticlesAtCells( level );
            if( num < 2 ) {
                return new EntryPoint[]{new EntryPoint( x, cell.getEnergy(), cell )};
            }

        }
        EnergyLevel level = valenceBand.energyLevelAt( SemiconductorBandSet.NUM_DOPING_LEVELS - 1 );
        EnergyCell cell = level.cellAt( 1 );
        return new EntryPoint[]{new EntryPoint( x, cell.getEnergy(), cell )};
    }
}