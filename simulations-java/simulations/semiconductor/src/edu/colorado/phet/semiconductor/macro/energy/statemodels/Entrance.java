package edu.colorado.phet.semiconductor.macro.energy.statemodels;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;


/**
 * User: Sam Reid
 * Date: Apr 20, 2004
 * Time: 12:39:21 PM
 */
public class Entrance implements ModelElement {
    private EnergyCell cell;
    private EnergySection energySection;

    public Entrance( EnergySection energySection, EnergyCell cell ) {
        this.cell = cell;
        this.energySection = energySection;
    }

    public EnergyCell getCell() {
        return cell;
    }

    public void stepInTime( double dt ) {
        if ( cell.getIndex() == 0 ) {
            EntryPoint ep = new EntryPoint( cell, new Vector2D.Double( -1, 0 ) );
            enter( energySection, ep );
        }
        else {
            EntryPoint ep = new EntryPoint( cell, new Vector2D.Double( 1, 0 ) );
            enter( energySection, ep );
        }
    }

    public void enter( EnergySection energySection, EntryPoint source ) {
        BandParticle bp = energySection.getBandParticle( source.getCell() );
        if ( bp == null ) {
            //free to enter.
            bp = new BandParticle( source.getSource() );
            bp.setExcited( true );
            bp.setState( new MoveToCell( bp, source.getCell(), energySection.getSpeed() ) );
            energySection.addParticle( bp );
        }
    }
}
