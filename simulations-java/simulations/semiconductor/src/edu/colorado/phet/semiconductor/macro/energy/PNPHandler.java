package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.bands.*;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ModelCriteria;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.TypeCriteria;
import edu.colorado.phet.semiconductor.macro.energy.transitions.ExitLeft;
import edu.colorado.phet.semiconductor.macro.energy.transitions.ExitRight;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;


/**
 * User: Sam Reid
 * Date: May 2, 2004
 * Time: 11:02:50 AM
 */
public class PNPHandler implements ModelCriteria, ModelElement {
    private EnergySection energySection;
    private PNPRightClear rightClear;
    private PNPLeftClear leftClear;

    public PNPHandler( EnergySection energySection ) {
        this.energySection = energySection;
        {
            //voltage pushes right.
            double minVolts = .1;
            DefaultStateDiagram pnpRight = new DefaultStateDiagram( energySection );
            ExciteForConduction nexcite = pnpRight.exciteN( 2, 1 );
            Move m = pnpRight.moveRight( nexcite.getRightCell() );
            pnpRight.moveRight( nexcite.getLeftCell() );

            ExciteForConduction rex = pnpRight.exciteP( 1, 2 );
            pnpRight.move( m.getDst(), rex.getLeftCell(), energySection.getFallSpeed() );

            pnpRight.moveRight( rex.getLeftCell() );
            pnpRight.exitRight( rex.getRightCell() );

            pnpRight.addFillLeft( 1 );
            DepleteRight dr = new DepleteRight( 1, 2, energySection );
            pnpRight.addModelElement( dr );

            Band band = energySection.bandSetAt( 2 ).getConductionBand();
            for ( int i = 0; i < DopantType.N.getNumFilledLevels(); i++ ) {
                pnpRight.move( band.energyLevelAt( i ).cellAt( 0 ), rex.getLeftCell(), energySection.getFallSpeed() );
            }
            //rightClear pnpRight
            rightClear = new PNPRightClear( energySection, pnpRight );
        }
        {
            //voltage pushes left
            double minVolts = .1;
            DefaultStateDiagram pnpLeft = new DefaultStateDiagram( energySection );
            ExciteForConduction nexcite = pnpLeft.exciteN( 2, 1 );
            Move m = pnpLeft.moveLeft( nexcite.getLeftCell() );
            pnpLeft.moveLeft( nexcite.getRightCell() );

            ExciteForConduction lex = pnpLeft.exciteP( 1, 0 );
            pnpLeft.move( m.getDst(), lex.getRightCell(), energySection.getFallSpeed() );

            pnpLeft.moveLeft( lex.getRightCell() );
            pnpLeft.exitLeft( lex.getLeftCell() );

            pnpLeft.addFillRight( 1 );
            DepleteLeft dl = new DepleteLeft( 1, 2, energySection );
            pnpLeft.addModelElement( dl );

            Band band = energySection.bandSetAt( 0 ).getConductionBand();
            for ( int i = 0; i < DopantType.N.getNumFilledLevels(); i++ ) {
                pnpLeft.move( band.energyLevelAt( i ).cellAt( 1 ), lex.getRightCell(), energySection.getFallSpeed() );
            }
            //clear pnpLeft
            leftClear = new PNPLeftClear( energySection, pnpLeft );
        }
    }

    public boolean isApplicable( EnergySection energySection ) {
        return new TypeCriteria( DopantType.P, DopantType.N, DopantType.P ).isApplicable( energySection );
    }

    public void stepInTime( double dt ) {
        if ( energySection.getVoltage() > .4 ) {
            rightClear.stepInTime( dt );
        }
        else if ( energySection.getVoltage() < -.4 ) {
            leftClear.stepInTime( dt );
        }
        else {
            //do nothing.
        }
    }
}

class PNPLeftClear extends DefaultStateDiagram {
    private ModelElement npForward;

    public PNPLeftClear( EnergySection energySection, ModelElement npForward ) {
        super( energySection );
        this.npForward = npForward;
    }

    public void stepInTime( double dt ) {
        boolean finished = doClear( dt );
        if ( finished ) {
            npForward.stepInTime( dt );
        }
    }

    private boolean doClear( double dt ) {
        SemiconductorBandSet bs2 = getEnergySection().bandSetAt( 0 );
        Band p = bs2.bandAt( DopantType.P.getDopingBand() );
        boolean clear = true;
        for ( int i = p.numEnergyLevels() - 1; i >= DopantType.P.getNumFilledLevels(); i-- ) {
            EnergyLevel eel = p.energyLevelAt( i );
            EnergyCell l = eel.cellAt( 0 );
            EnergyCell r = eel.cellAt( 1 );
            BandParticle pl = getEnergySection().getBandParticle( l );
            BandParticle pr = getEnergySection().getBandParticle( r );
            if ( pl != null || pr != null ) {
                clear = false;
            }
            if ( pl != null ) {
                ExitLeft exitLeft = new ExitLeft();
                exitLeft.apply( pl, getEnergySection() );
                break;
            }
            if ( pr != null ) {
                Move m = new Move( r, l, getSpeed() );
                m.apply( pr, getEnergySection() );
                break;
            }
        }
        return clear;
    }
}

class PNPRightClear extends DefaultStateDiagram {
    private ModelElement npForward;

    public PNPRightClear( EnergySection energySection, ModelElement npForward ) {
        super( energySection );
        this.npForward = npForward;
    }

    public void stepInTime( double dt ) {
        boolean finished = doClear( dt );
        if ( finished ) {
            npForward.stepInTime( dt );
        }
    }

    private boolean doClear( double dt ) {
        SemiconductorBandSet bs2 = getEnergySection().bandSetAt( 2 );
        Band p = bs2.bandAt( DopantType.P.getDopingBand() );
        boolean clear = true;
        for ( int i = p.numEnergyLevels() - 1; i >= DopantType.P.getNumFilledLevels() + 1; i-- ) {
            EnergyLevel eel = p.energyLevelAt( i );
            EnergyCell l = eel.cellAt( 0 );
            EnergyCell r = eel.cellAt( 1 );
            BandParticle pl = getEnergySection().getBandParticle( l );
            BandParticle pr = getEnergySection().getBandParticle( r );
            if ( pl != null || pr != null ) {
                clear = false;
            }
            if ( pr != null ) {
                ExitRight er = new ExitRight();
                er.apply( pr, getEnergySection() );
                break;
            }
            if ( pl != null ) {
                Move m = new Move( l, r, getSpeed() );
                m.apply( pl, getEnergySection() );
                break;
            }
        }

        return clear;
    }
}