package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.bands.*;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.Entrance;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ModelCriteria;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.TypeCriteria;
import edu.colorado.phet.semiconductor.macro.energy.transitions.ExitLeft;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;
import edu.colorado.phet.semiconductor.phetcommon.model.ModelElement;

/**
 * User: Sam Reid
 * Date: May 2, 2004
 * Time: 9:21:49 AM
 */
public class PNHandler implements ModelElement, ModelCriteria {
    private EnergySection energySection;
    private DefaultStateDiagram pnBackBias;
    private ModelElement pnForwardBias;
    private ModelElement pnForwardClear;
    private PNForwardClear pnClearAll;

    public PNHandler( EnergySection energySection ) {
        this.energySection = energySection;
        pnBackBias = new DefaultStateDiagram( energySection );

        ExciteForConduction ext = pnBackBias.exciteN( 2, 1 );
        pnBackBias.exitRight( ext.getRightCell() );
        pnBackBias.addDepleteRight( 2 );
        pnBackBias.addFillLeft( 1 );
        pnBackBias.move( ext.getLeftCell(), ext.getRightCell(), energySection.getSpeed() );
        pnBackBias.addModelElement( new DepleteRight( 1, 2, energySection ) );
        pnBackBias.move( energySection.getLeftNeighbor( ext.getLeftCell() ), ext.getLeftCell(), energySection.getSpeed() );

        pnForwardBias = getPNForward();
        pnForwardClear = new PNForwardClear( energySection, pnForwardBias );
        DefaultStateDiagram doNext = new DefaultStateDiagram( energySection );
        EnergyCell cell = energySection.bandSetAt( 0 ).bandAt( 1 ).energyLevelAt( DopantType.P.getNumFilledLevels() ).cellAt( 0 );
        Entrance ent = doNext.enter( cell );
        doNext.moveRight( ent.getCell() );

        pnClearAll = new PNForwardClear( energySection, doNext );
        pnClearAll.setTopLevel( DopantType.P.getNumFilledLevels() + 1 );
        Entrance e2 = pnClearAll.enter( energySection.getLowerNeighbor( cell ) );
        Move mov = pnClearAll.move( e2.getCell(), energySection.getRightNeighbor( e2.getCell() ), energySection.getSpeed() );
        pnClearAll.unexcite( mov.getDst().getEnergyLevel() );

        EnergyCell topN = energySection.bandSetAt( 0 ).bandAt( 2 ).energyLevelAt( DopantType.N.getNumFilledLevels() ).cellAt( 1 );
//        System.out.println( "topN = " + topN );
        Move lastMove = pnClearAll.propagateRight( topN );
        pnClearAll.exitRight( lastMove.getDst() );
//        System.out.println( "mov = " + mov );

    }

    private ModelElement getPNForward() {
        DefaultStateDiagram pnForward = new DefaultStateDiagram( energySection );
        ExciteForConduction exciteN = pnForward.exciteN( 2, 1 );
        ExciteForConduction exciteP = pnForward.exciteP( 1, 0 );

        pnForward.enter( exciteN.getRightCell() );
        pnForward.moveLeft( exciteN.getRightCell() );

        Move move = pnForward.moveLeft( exciteN.getLeftCell() );
        pnForward.move( move.getDst(), exciteP.getRightCell(), energySection.getFallSpeed() );
        pnForward.move( exciteP.getRightCell(), exciteP.getLeftCell(), energySection.getSpeed() );
        pnForward.exitLeft( exciteP.getLeftCell() );
        return pnForward;
    }

    public void stepInTime( double dt ) {
        if( energySection.getVoltage() > 0 ) {
            pnBackBias.stepInTime( dt );
        }
        else if( energySection.getVoltage() < -.4 ) {
            pnForwardClear.stepInTime( dt );
        }
        else {
            pnClearAll.stepInTime( dt );
        }
    }

    public boolean isApplicable( EnergySection energySection ) {
        return new TypeCriteria( DopantType.P, DopantType.N ).isApplicable( energySection );
    }
}

class PNForwardClear extends DefaultStateDiagram {
    private ModelElement pnForward;
    private boolean carryOn;
    private int topLevel;

    public PNForwardClear( EnergySection energySection, ModelElement doNext ) {
        this( energySection, doNext, true );
    }

    public PNForwardClear( EnergySection energySection, ModelElement doNext, boolean carryOn ) {
        super( energySection );
        this.pnForward = doNext;
        this.carryOn = carryOn;
        topLevel = DopantType.P.getNumFilledLevels();
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        boolean finished = doClear( dt );
        if( finished && carryOn ) {
            pnForward.stepInTime( dt );
        }
    }

    public void setTopLevel( int topLevel ) {
        this.topLevel = topLevel;
    }

    public boolean doClear( double dt ) {
        SemiconductorBandSet bs0 = getEnergySection().bandSetAt( 0 );
        SemiconductorBandSet bs1 = getEnergySection().bandSetAt( 1 );
        Band p = bs0.bandAt( DopantType.P.getDopingBand() );
        Band n = bs1.bandAt( DopantType.N.getDopingBand() );
        boolean clear = true;
        for( int i = 0; i < DopantType.N.getNumFilledLevels() - 1; i++ ) {
            EnergyLevel eel = n.energyLevelAt( i );
            if( !getEnergySection().isOwned( eel ) ) {
                Entrance e = new Entrance( getEnergySection(), eel.cellAt( 1 ) );
                e.stepInTime( dt );
                Move mo = new Move( e.getCell(), getEnergySection().getLeftNeighbor( e.getCell() ), getSpeed() );
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
            if( pl != null ) {
                ExitLeft el = new ExitLeft();
                el.apply( pl, getEnergySection() );
                break;
            }
            if( pr != null ) {
                Move m = new Move( r, l, getSpeed() );
                m.apply( pr, getEnergySection() );
                break;
            }
        }

        return clear;
    }

}
