/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * Developer controls for capacitor.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlPanel extends PhetTitledPanel {
    
    private final CLModel model;

    public DeveloperControlPanel( final CLModel model ) {
        super( "Developer" );
        setTitleColor( Color.RED );
        
        this.model = model;
        this.model.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {
            //XXX override whatever is needed
        });
        
        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        //XXX add components here
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
