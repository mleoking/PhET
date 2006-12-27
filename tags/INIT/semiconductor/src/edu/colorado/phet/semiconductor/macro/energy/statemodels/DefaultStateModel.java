/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateModel;
import edu.colorado.phet.semiconductor.macro.energy.StateTransitionList;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandSet;
import edu.colorado.phet.semiconductor.macro.energy.bands.SemiconductorBandSet;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energy.transitions.*;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 8:50:15 AM
 * Copyright (c) Mar 15, 2004 by Sam Reid
 */
public class DefaultStateModel implements StateModel {
    EnergySection energySection;
    StateTransitionList transitionList = new StateTransitionList();

    public DefaultStateModel( EnergySection energySection ) {
        this.energySection = energySection;

        BandSet typical = energySection.bandSetAt( 0 );
        int band1Height = typical.bandAt( 1 ).numEnergyLevels();

        FallExcited fe1 = new FallExcited( 2, 1, 1, band1Height - 2 );
        FallExcited fe2 = new FallExcited( 2, 2, 1, band1Height - 2 );
        FallExcited fe3 = new FallExcited( 2, 3, 1, band1Height - 1 );
        FallExcited fe4 = new FallExcited( 2, 3, 1, band1Height - 2 );

        FallExcited fe5 = new FallExcited( 2, 3, 1, band1Height - 3 );
        FallExcited fe6 = new FallExcited( 2, 3, 1, band1Height - 4 );

        transitionList.addTransition( fe1 );
        transitionList.addTransition( fe2 );
        transitionList.addTransition( fe3 );
        transitionList.addTransition( fe4 );
        transitionList.addTransition( fe5 );
        transitionList.addTransition( fe6 );

        VoltageExcite e1 = new VoltageExcite( 1, typical.bandAt( 1 ).numEnergyLevels() - 1 );
        VoltageExcite e2 = new VoltageExcite( 1, typical.bandAt( 1 ).numEnergyLevels() - 2 );
        VoltageExcite e3 = new VoltageExcite( 1, typical.bandAt( 1 ).numEnergyLevels() - 3 );
        VoltageExcite e4 = new VoltageExcite( 1, typical.bandAt( 1 ).numEnergyLevels() - 4 );

        transitionList.addTransition( e1 );
        transitionList.addTransition( e2 );
        transitionList.addTransition( e3 );
        transitionList.addTransition( e4 );

        VoltageExcite e5 = new VoltageExcite( 2, 0 );
        VoltageExcite e6 = new VoltageExcite( 2, 1 );
        VoltageExcite e7 = new VoltageExcite( 2, 2 );

        transitionList.addTransition( e5 );
        transitionList.addTransition( e6 );
        transitionList.addTransition( e7 );

        VoltageFall f1 = new VoltageFall( 2, 3 );
        VoltageFall f2 = new VoltageFall( 2, 2 );
        VoltageFall f3 = new VoltageFall( 2, 1 );
        VoltageFall f4 = new VoltageFall( 1, band1Height - 1 );
        VoltageFall f5 = new VoltageFall( 1, band1Height - 2 );
        VoltageFall f6 = new VoltageFall( 1, band1Height - 3 );
        transitionList.addTransition( f1 );
        transitionList.addTransition( f2 );
        transitionList.addTransition( f3 );
        transitionList.addTransition( f4 );
        transitionList.addTransition( f5 );
        transitionList.addTransition( f6 );

        MoveRight mr = new MoveRight();
        transitionList.addTransition( mr );
        ExitRight er = new ExitRight();
        transitionList.addTransition( er );

        MoveLeft ml = new MoveLeft();
        transitionList.addTransition( ml );
        ExitLeft el = new ExitLeft();
        transitionList.addTransition( el );
    }

    //step in time.
    public void updateStates() {
        for( int i = 0; i < energySection.numParticles(); i++ ) {
            BandParticle bp = energySection.particleAt( i );
            transitionList.apply( bp, energySection );
        }
        enterNewElectrons();
    }

    private void enterNewElectrons() {
        EntryPoint[] sources = getEntryPoints();
        for( int i = 0; i < sources.length; i++ ) {
            EntryPoint source = sources[i];
            enter( source );
        }
    }

    private void enter( EntryPoint source ) {
        BandParticle bp = energySection.getBandParticle( source.getCell() );

        if( bp == null ) {
            //free to enter.
            bp = new BandParticle( source.getSource() );
            bp.setExcited( true );
            bp.setState( new MoveToCell( bp, source.getCell(), energySection.getSpeed() ) );
            energySection.addParticle( bp );
        }
    }

    public EntryPoint[] getEntryPoints() {

        if( energySection.getBattery().getVoltage() < 0 ) {
            return getRightSources();
        }
        else if( energySection.getBattery().getVoltage() > 0 ) {
            return getLeftSources();
        }
        else {
            return new EntryPoint[0];
        }
    }

    private EntryPoint[] getLeftSources() {
        ArrayList all = new ArrayList();
        //move right come from left
        SemiconductorBandSet leftBand = energySection.bandSetAt( 0 );
        DopantType type = leftBand.getDopantType();
        double y0 = 0;
        double y1 = 0;
        double x = leftBand.getX();
        if( type == DopantType.N ) {
            //TODO fix entry points.
//            EnergyCell cell = leftBand.activeLevelAt( 4 ).cellAt( 0 );
//            EnergyCell cell2 = leftBand.activeLevelAt( 5 ).cellAt( 0 );
//            y0 = cell.getEnergy();
//            y1 = cell2.getEnergy();
//
//            all.add( new EntryPoint( x, y0, cell, leftBand, 4, 0 ) );
//            all.add( new EntryPoint( x, y1, cell2, leftBand, 5, 0 ) );
        }
        else if( type == DopantType.P || type == null ) {
//            EnergyCell cell = leftBand.activeLevelAt( 0 ).cellAt( 0 );
//            EnergyCell cell2 = leftBand.activeLevelAt( 1 ).cellAt( 0 );
//            y0 = cell.getEnergy();
//            y1 = cell2.getEnergy();
//            all.add( new EntryPoint( x, y0, cell, leftBand, 0, 0 ) );
//            all.add( new EntryPoint( x, y1, cell2, leftBand, 1, 0 ) );
        }
        return (EntryPoint[])all.toArray( new EntryPoint[0] );
    }

    private EntryPoint[] getRightSources() {
        ArrayList all = new ArrayList();
        //move left, come in at the right.
        SemiconductorBandSet rightBand = energySection.getRightBand();
        DopantType type = rightBand.getDopantType();
        double y0 = 0;
        double y1 = 0;
        double x = rightBand.getX() + rightBand.getWidth();
        if( type == DopantType.N ) {
//            EnergyCell cell = rightBand.activeLevelAt( 4 ).cellAt( 1 );
//            EnergyCell cell2 = rightBand.activeLevelAt( 5 ).cellAt( 1 );
//            y0 = cell.getEnergy();
//            y1 = cell2.getEnergy();
//            all.add( new EntryPoint( x, y0, cell, rightBand, 4, 1 ) );
//            all.add( new EntryPoint( x, y1, cell2, rightBand, 5, 1 ) );
        }
        else if( type == DopantType.P || type == null ) {
//            EnergyCell cell = rightBand.activeLevelAt( 0 ).cellAt( 1 );
//            EnergyCell cell2 = rightBand.activeLevelAt( 1 ).cellAt( 1 );
//            y0 = cell.getEnergy();
//            y1 = cell2.getEnergy();
//            all.add( new EntryPoint( x, y0, cell, rightBand, 0, 1 ) );
//            all.add( new EntryPoint( x, y1, cell2, rightBand, 1, 1 ) );
        }

        return (EntryPoint[])all.toArray( new EntryPoint[0] );
    }
}
