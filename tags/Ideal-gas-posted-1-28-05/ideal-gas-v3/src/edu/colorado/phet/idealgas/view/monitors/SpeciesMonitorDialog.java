/**
 * Class: SpeciesMonitorDialog
 * Class: edu.colorado.phet.idealgas.view.monitors
 * User: Ron LeMaster
 * Date: Sep 29, 2004
 * Time: 6:54:22 PM
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import java.awt.*;

public class SpeciesMonitorDialog extends JDialog {
    public SpeciesMonitorDialog( PhetFrame phetFrame, IdealGasModel idealGasModel ) {
        super( phetFrame, "Species Statistics", false );
        this.setUndecorated( true );
        this.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );

        JPanel speciesPanel = new JPanel( new GridLayout( 2, 1 ) );
        GasSpeciesMonitorPanel heavySpeciesPanel = new GasSpeciesMonitorPanel( HeavySpecies.class,
                                                                               SimStrings.get( "IdealGasMonitorPanel.Heavy_species" ),
                                                                               idealGasModel );
        speciesPanel.add( heavySpeciesPanel );
        GasSpeciesMonitorPanel lightSpeciesPanel = new GasSpeciesMonitorPanel( LightSpecies.class,
                                                                               SimStrings.get( "IdealGasMonitorPanel.Light_species" ),
                                                                               idealGasModel );
        speciesPanel.add( lightSpeciesPanel );

        this.getContentPane().add( speciesPanel );
        pack();
    }


}
