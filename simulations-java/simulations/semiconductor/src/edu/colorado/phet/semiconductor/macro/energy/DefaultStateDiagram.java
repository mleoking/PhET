package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyLevel;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.Entrance;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;
import edu.colorado.phet.semiconductor.macro.energy.states.Speed;
import edu.colorado.phet.semiconductor.macro.energy.transitions.ExitLeftFrom;
import edu.colorado.phet.semiconductor.macro.energy.transitions.ExitRightFrom;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;
import edu.colorado.phet.semiconductor.phetcommon.model.CompositeModelElement;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:29:58 AM
 */
public class DefaultStateDiagram extends CompositeModelElement {
    private EnergySection energySection;
    private StateTransitionList list;
    private ParticleActionApplicator applicator;

    public DefaultStateDiagram( EnergySection energySection ) {
        this.energySection = energySection;
        list = new StateTransitionList( energySection );
        addModelElement( list );

        applicator = new ParticleActionApplicator( energySection );
        addModelElement( applicator );
    }

    public EnergySection getEnergySection() {
        return energySection;
    }

    public ExciteForConduction exciteP( int band, int bandSet ) {
        return excite( band, bandSet, DopantType.P.getNumFilledLevels() - 1 );
    }

    public ExciteForConduction exciteN( int band, int bandSet ) {
        return excite( band, bandSet, DopantType.N.getNumFilledLevels() - 1 );
    }

    protected ExciteForConduction createExcite( int band, int bandSet, int srcLevel ) {
        ExciteForConduction excite = new ExciteForConduction( energySection, band, bandSet, srcLevel );
        return excite;
    }

    public ExciteForConduction excite( int band, int bandSet, int srcLevel ) {
        ExciteForConduction excite = createExcite( band, bandSet, srcLevel );
        addModelElement( excite );
        return excite;
    }

    public Move move( EnergyCell src, EnergyCell dst, Speed speed ) {
        Move move = new Move( src, dst, speed );
        list.addTransition( move );
        return move;
    }

    public Move moveRight( EnergyCell src ) {
        return move( src, energySection.getRightNeighbor( src ), energySection.getSpeed() );
    }

    public void exitRight( EnergyCell from ) {
        ExitRightFrom erf = new ExitRightFrom( from );
        list.addTransition( erf );
    }

    public void exitLeft( EnergyCell from ) {
        ExitLeftFrom erf = new ExitLeftFrom( from );
        list.addTransition( erf );
    }

    public Entrance enter( EnergyCell cell ) {
        Entrance enter = new Entrance( energySection, cell );
        addModelElement( enter );
        return enter;
    }

    public void fall( EnergyCell src ) {
        move( src, energySection.getLowerNeighbor( src ), energySection.getFallSpeed() );
    }

    public Speed getSpeed() {
        return getEnergySection().getSpeed();
    }

    public void unexcite( EnergyCell cell ) {
        Unexcite u = new Unexcite( cell );
        addAction( u );
    }

    public void addAction( ParticleAction action ) {
        applicator.addParticleAction( action );
    }

    public Move propagateRight( EnergyCell src ) {
        EnergyCell from = src;
        EnergyCell to = energySection.getRightNeighbor( from );
        Move mo = null;
        while( to != null ) {
            mo = move( from, to, getSpeed() );
            from = to;
            to = energySection.getRightNeighbor( to );
        }
        return mo;
    }

    public Move propagateLeft( EnergyCell src ) {
        EnergyCell from = src;
        EnergyCell to = energySection.getLeftNeighbor( from );
        Move mo = null;
        while( to != null ) {
            mo = move( from, to, getSpeed() );
            from = to;
            to = energySection.getLeftNeighbor( to );
        }
        return mo;
    }

    public void addDepleteRight( int band ) {
        DepleteRight d = new DepleteRight( energySection.numBandSets() - 1, band, energySection );
        addModelElement( d );
    }

    public void addFillLeft( int band ) {
        FillLeft f = new FillLeft( 0, band, energySection );
        addModelElement( f );
    }

    public void addFillRight( int band ) {
        FillRight f = new FillRight( energySection.numBandSets() - 1, band, energySection );
        addModelElement( f );
    }

    public void unexcite( EnergyLevel energyLevel ) {
        unexcite( energyLevel.cellAt( 0 ) );
        unexcite( energyLevel.cellAt( 1 ) );
    }

    public Move moveLeft( EnergyCell cell ) {
        return move( cell, energySection.getLeftNeighbor( cell ), energySection.getSpeed() );
    }
}
