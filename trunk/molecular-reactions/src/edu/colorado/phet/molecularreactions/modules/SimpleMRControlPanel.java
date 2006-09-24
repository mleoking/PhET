/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.molecularreactions.controller.ResetAllAction;
import edu.colorado.phet.molecularreactions.view.Legend;
import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * TestControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class TestControlPanel extends ControlPanel {
public class SimpleMRControlPanel extends JPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;

    public SimpleMRControlPanel( MRModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1,1,1,1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0,0,0,0),0,0 );
    }
}
