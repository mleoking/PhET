/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateModel;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.SemiconductorBandSet;
import edu.colorado.phet.semiconductor.macro.energy.states.ExitLeftState;
import edu.colorado.phet.semiconductor.macro.energy.states.ExitRightState;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energy.states.Waiting;

import java.util.Random;

/**
 * User: Sam Reid
 * Date: Apr 13, 2004
 * Time: 11:23:16 PM
 * Copyright (c) Apr 13, 2004 by Sam Reid
 */
public class CompleteStateModel implements StateModel {
    EnergySection energySection;

    public CompleteStateModel( EnergySection energySection ) {
        this.energySection = energySection;
    }

    public void updateStates() {
        for( int i = 0; i < energySection.numParticles(); i++ ) {
            BandParticle bp = energySection.particleAt( i );
            //is the particle already doing something?
            BandParticleState state = bp.getState();
            if( state == null || state instanceof Waiting ) {
                apply( bp );
            }
        }
        if( rand.nextDouble() < .90 ) {
            enterNewElectrons();
        }
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
            bp.setExcited( true );
            bp.setState( new MoveToCell( bp, source.getCell(), energySection.getSpeed() ) );
            energySection.addParticle( bp );

        }
    }

    private void apply( BandParticle bp ) {
        if( bp.isExcited() ) {
            if( rand.nextDouble() < .6 ) {
                applyExcited( bp );
            }
        }
        else {
            if( rand.nextDouble() < .04 ) {
                applyUnexcited( bp );
            }
        }
    }

    private boolean testFallBand( BandParticle bp ) {
        EnergyCell cell = bp.getEnergyCell();
        int col = cell.getColumn();
        EnergyCell to = getHighestFreeCell( col, 1, cell.getEnergyLevelAbsoluteIndex() - 1 );
        while( energySection.isClaimed( to ) ) {
            to = energySection.getUpperNeighbor( to );
            if( to.getEnergyLevelAbsoluteIndex() >= cell.getEnergyLevelAbsoluteIndex() ) {
                return false;
            }
            if( to.getBand() == cell.getBand() ) {
                return false;
            }
        }
        if( to != null && !energySection.isClaimed( to ) && to.getBand() != cell.getBand() ) {
            bp.setState( new MoveToCell( bp, to, energySection.getFallSpeed() ) );
            return true;
        }
        return false;
    }

    private void applyUnexcited( BandParticle bp ) {
        //calculate the force on the particle
//        double force = energySection.getElectricForce( bp );
        double force = energySection.getVoltage();
//        System.out.println( "force = " + force );
        if( Math.abs( force ) > 0 ) {
            EnergyCell cell = energySection.getUpperNeighbor( bp.getEnergyCell() );
            if( cell.getEnergyLevelBandIndex() <= -1 ) { //should we keep a base of e-?
                return;
            }
            if( cell.getBand() == bp.getEnergyCell().getBand() ) {
                if( cell != null && !energySection.isClaimed( cell ) && System.currentTimeMillis() - cell.getLastVisitTime() > 1000 ) {
                    bp.setState( new MoveToCell( bp, cell, energySection.getSpeed() ) );
                }
            }
        }
    }

    static double exitDX = .2;

    PhetVector getExitRightLoc( int row ) {
        EnergyCell c = energySection.energyCellAt( row, 0 );
        return new PhetVector( energySection.getRightBand().getX() + energySection.getRightBand().getWidth() + exitDX, c.getEnergy() );
    }

    PhetVector getExitLeftLoc( int row ) {
        EnergyCell c = energySection.energyCellAt( row, 0 );
        return new PhetVector( energySection.getLeftBand().getX() - exitDX, c.getEnergy() );
    }

    Random rand = new Random( 0 );

    private void applyExcited( BandParticle bp ) {
        //calculate the force on the particle
        //double force = energySection.getElectricForce( bp );
        EnergyCell currentCell=bp.getEnergyCell();
        int column=currentCell.getColumn();
        int left=column-1;
        int right=column+1;
        int leftCharge=energySection.getColumnCharge( left );
        int rightCharge=energySection.getColumnCharge( right );
        int myCharge=energySection.getColumnCharge( column );
        int desireToMoveLeft=myCharge-leftCharge;
        int desireToMoveRight=myCharge-rightCharge;

//        int force=leftCharge-rightCharge;
        bp.setMessage(""+leftCharge+", "+rightCharge+", dl="+desireToMoveLeft+", dr="+desireToMoveRight);
        if( rand.nextDouble() < .9 ) {
            boolean fell = testFallBand( bp );
            if( fell ) {
                return;
            }
        }
//        if (myCharge<leftCharge&&myCharge<rightCharge)
//        {
//            return;
//        }
//        System.out.println( "force = " + force );
        int force=desireToMoveRight-desireToMoveLeft;
        if( force>0&&desireToMoveRight>1) {
            EnergyCell cell = energySection.getRightNeighbor( bp.getEnergyCell() );
//            if( cell == null && energySection.getVoltage() > 0 ) {
            if( cell == null ) {
                bp.setState( new ExitRightState( getExitRightLoc( bp.getEnergyCell().getEnergyLevelAbsoluteIndex() ), energySection.getSpeed(), energySection ) );
            }
            if( cell != null && !energySection.isClaimed( cell ) ) {
                bp.setState( new MoveToCell( bp, cell, energySection.getSpeed() ) );
            }
        }
        else if( force<0 &&desireToMoveLeft>1) {
            EnergyCell cell = energySection.getLeftNeighbor( bp.getEnergyCell() );
//            if( cell == null && energySection.getVoltage() < 0 ) {
            if( cell == null ) {
                bp.setState( new ExitLeftState( getExitLeftLoc( bp.getEnergyCell().getEnergyLevelAbsoluteIndex() ), energySection.getSpeed(), energySection ) );
            }
            if( cell != null && !energySection.isClaimed( cell ) ) {
                bp.setState( new MoveToCell( bp, cell, energySection.getSpeed() ) );
            }
        }
    }

    public EntryPoint[] getEntryPoints() {
        if( energySection.getVoltage() > 0 ) {
            int max = getMax( energySection.getLeftBand() );
            EnergyCell cell = getHighestFreeCell( 0, 0, max );
            if( cell == null ) {
                return new EntryPoint[0];
            }
            else {
                return new EntryPoint[]{new EntryPoint( energySection.getLeftBand().getX() - .2, cell.getEnergy(), cell )};
            }
        }
        else if( energySection.getVoltage() < 0 ) {
            int max = getMax( energySection.getRightBand() );
            EnergyCell cell = getHighestFreeCell( energySection.numBandSets() * 2 - 1, 0, max );
            if( cell == null ) {
                return new EntryPoint[0];
            }
            else {
                return new EntryPoint[]{new EntryPoint( energySection.getRightBand().getX() + energySection.getRightBand().getWidth() + .2, cell.getEnergy(), cell )};
            }
        }
        else {
            return new EntryPoint[0];
        }
    }

    private int getMax( SemiconductorBandSet band ) {
        if( band.getDopantType() == DopantType.P ) {
            return 19;
        }
        else if( band.getDopantType() == DopantType.N ) {
            return 29;
        }
        else if( band.getDopantType() == null ) {
            return 19;
        }
        return band.numEnergyLevels();
    }

    private EnergyCell getHighestFreeCell( int column, int min, int max ) {
        for( int row = min; row < energySection.numRows() && row <= max; row++ ) {
            EnergyCell cell = energySection.energyCellAt( row, column );
            if( energySection.isClaimed( cell ) ) {
                BandParticle bp = energySection.getBandParticle( cell );
                if( !bp.isLocatedAtCell() ) {
                    return cell;
                }
            }
            if( !energySection.isClaimed( cell ) ) {
                return cell;
            }
            if( row == max ) {
                return cell;
            }
        }
        return null;
    }
}
