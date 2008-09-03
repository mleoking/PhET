/*  */
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.bands.*;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.Entrance;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ModelCriteria;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.TypeCriteria;
import edu.colorado.phet.semiconductor.macro.energy.transitions.ExitRight;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;
import edu.colorado.phet.semiconductor.phetcommon.model.ModelElement;

/**
 * User: Sam Reid
 * Date: May 2, 2004
 * Time: 9:21:49 AM
 */
public class NNPHandler implements ModelElement, ModelCriteria {
    private EnergySection energySection;
    private DefaultStateDiagram npBackBias;
    private ModelElement npForwardBias;
    private ModelElement npForwardClear;
    private NPForwardClear npClearAll;

    public NNPHandler( EnergySection energySection ) {
        this.energySection = energySection;
        npBackBias = new DefaultStateDiagram( energySection );

        ExciteForConduction ext = npBackBias.exciteN( 2, 0 );
        npBackBias.exitLeft( ext.getLeftCell() );
        npBackBias.addDepleteRight( 2 );
        npBackBias.addFillRight( 1 );
        npBackBias.move( ext.getRightCell(), ext.getLeftCell(), energySection.getSpeed() );
        npBackBias.addModelElement( new DepleteLeft( 0, 2, energySection ) );
        npBackBias.move( energySection.getRightNeighbor( ext.getRightCell() ), ext.getRightCell(), energySection.getSpeed() );

        npForwardBias = getNPForward();
        npForwardClear = new NNPForwardClear( energySection, npForwardBias );
        DefaultStateDiagram doNext = new DefaultStateDiagram( energySection );
        EnergyCell cell = energySection.bandSetAt( 2 ).bandAt( 1 ).energyLevelAt( DopantType.P.getNumFilledLevels() ).cellAt( 1 );
        Entrance ent = doNext.enter( cell );
        doNext.moveLeft( ent.getCell() );

        npClearAll = new NPForwardClear( energySection, doNext );
        npClearAll.setTopLevel( DopantType.P.getNumFilledLevels() + 1 );
        Entrance e2 = npClearAll.enter( energySection.getLowerNeighbor( cell ) );
        Move mov = npClearAll.move( e2.getCell(), energySection.getLeftNeighbor( e2.getCell() ), energySection.getSpeed() );
        npClearAll.unexcite( mov.getDst().getEnergyLevel() );

        EnergyCell topN = energySection.bandSetAt( 2 ).bandAt( 2 ).energyLevelAt( DopantType.N.getNumFilledLevels() ).cellAt( 0 );
//        System.out.println( "topN = " + topN );
        Move lastMove = npClearAll.propagateLeft( topN );
        npClearAll.exitLeft( lastMove.getDst() );
//        System.out.println( "mov = " + mov );

    }

    private ModelElement getNPForward() {
        DefaultStateDiagram pnForward = new DefaultStateDiagram( energySection );
        ExciteForConduction exciteN = pnForward.exciteN( 2, 0 );
        ExciteForConduction exciteP = pnForward.exciteP( 1, 2 );

        pnForward.enter( exciteN.getLeftCell() );
        pnForward.moveRight( exciteN.getLeftCell() );

        Move move = pnForward.moveRight( exciteN.getRightCell() );
        pnForward.move( move.getDst(), exciteP.getLeftCell(), energySection.getFallSpeed() );
        pnForward.move( exciteP.getLeftCell(), exciteP.getRightCell(), energySection.getSpeed() );
        pnForward.exitRight( exciteP.getRightCell() );
        return pnForward;
    }

    public void stepInTime( double dt ) {
        if( energySection.getVoltage() < 0 ) {
            npBackBias.stepInTime( dt );
        }
        else if( energySection.getVoltage() > .4 ) {
            npForwardClear.stepInTime( dt );
        }
        else {
            npClearAll.stepInTime( dt );
        }
    }

    public boolean isApplicable( EnergySection energySection ) {
        return new TypeCriteria( DopantType.N, DopantType.N, DopantType.P ).isApplicable( energySection );
    }

    class NNPForwardClear extends DefaultStateDiagram {
        private ModelElement npForward;
        private int topLevel = DopantType.P.getNumFilledLevels();

        public NNPForwardClear( EnergySection energySection, ModelElement npForward ) {
            super( energySection );
            this.npForward = npForward;
        }

        public void stepInTime( double dt ) {
            super.stepInTime( dt );
            boolean finished = doClear( dt );
            if( finished ) {
                npForward.stepInTime( dt );
            }
        }

        public void setTopLevel( int topLevel ) {
            this.topLevel = topLevel;
        }

        private boolean doClear( double dt ) {
            SemiconductorBandSet bs0 = getEnergySection().bandSetAt( 0 );
            SemiconductorBandSet bs1 = getEnergySection().bandSetAt( 1 );
            Band p = bs1.bandAt( DopantType.P.getDopingBand() );
            Band n = bs0.bandAt( DopantType.N.getDopingBand() );
            boolean clear = true;
            for( int i = 0; i < DopantType.N.getNumFilledLevels() - 1; i++ ) {
                EnergyLevel eel = n.energyLevelAt( i );
                if( !getEnergySection().isOwned( eel ) ) {
                    Entrance e = new Entrance( getEnergySection(), eel.cellAt( 0 ) );
                    e.stepInTime( dt );
                    Move mo = new Move( e.getCell(), getEnergySection().getRightNeighbor( e.getCell() ), getSpeed() );
                    BandParticle bp = getEnergySection().getBandParticle( e.getCell() );
                    if( bp != null && bp.isLocatedAtCell() ) {
                        mo.apply( bp, getEnergySection() );
                    }
                    clear = false;
                    break;
                }
                else {
                    BandParticle bp = getEnergySection().getBandParticle( eel.cellAt( 0 ) );
                    BandParticle bp1 = getEnergySection().getBandParticle( eel.cellAt( 1 ) );
                    new Unexcite( eel.cellAt( 0 ) ).apply( bp );
                    new Unexcite( eel.cellAt( 1 ) ).apply( bp1 );
                }
            }

            //add a loop for the p band
            for( int i = p.numEnergyLevels() - 1; i >= topLevel; i-- ) {
                EnergyLevel eel = p.energyLevelAt( i );
                EnergyCell l = eel.cellAt( 0 );
                EnergyCell r = eel.cellAt( 1 );
                BandParticle pl = getEnergySection().getBandParticle( l );
                BandParticle pr = getEnergySection().getBandParticle( r );
                if( pl != null && pr != null && i != DopantType.P.getNumFilledLevels() ) {
                    clear = false;
                }
                if( pr != null ) {
                    ExitRight er = new ExitRight();
                    er.apply( pr, getEnergySection() );
                    break;
                }
                if( pl != null ) {
                    Move m = new Move( l, r, getSpeed() );
                    m.apply( pl, getEnergySection() );
                    break;
                }
            }

            return clear;
        }
    }

}
