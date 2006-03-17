/* Copyright 2004, Sam Reid */
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.Entrance;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ModelCriteria;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.TypeCriteria;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;

/**
 * User: Sam Reid
 * Date: May 2, 2004
 * Time: 9:21:49 AM
 * Copyright (c) May 2, 2004 by Sam Reid
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
        npForwardClear = new NPForwardClear( energySection, npForwardBias );
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
}
