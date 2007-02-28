/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.salt.*;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * RealSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RealSaltsControlPanel extends SolubleSaltsControlPanel {

    static protected HashMap saltMap;

    static {
        saltMap = new HashMap();
        saltMap.put( SimStrings.get("Salt.silverBromide"), new SilverBromide() );
        saltMap.put( SimStrings.get("Salt.copperIodide"), new CuprousIodide() );
        saltMap.put( SimStrings.get("Salt.thalliumSulfide"), new ThallousSulfide() );
        saltMap.put( SimStrings.get("Salt.silverArsenate"), new SilverArsenate() );
        saltMap.put( SimStrings.get( "Salt.strontiumPhosphate"), new StrontiumPhosphate() );
        saltMap.put( SimStrings.get("Salt.mercuryBromide"), new MercuryBromide() );
    }


    public RealSaltsControlPanel( final SolubleSaltsModule module ) {
        super( module );
    }

    /**
     *
     */
    protected JPanel makeSaltSelectionPanel( final SolubleSaltsModel model ) {

        final JComboBox comboBox = new JComboBox( saltMap.keySet().toArray() );
        comboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Salt saltClass = (Salt)saltMap.get( comboBox.getSelectedItem() );
                model.setCurrentSalt( saltClass );
                getModule().reset();
                revalidate();
            }
        } );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        panel.add( comboBox, gbc );

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        SaltSpinnerPanel saltSPinnerPanel = new SaltSpinnerPanel( model );
        panel.add( saltSPinnerPanel, gbc );

        Salt saltClass = (Salt)saltMap.get( comboBox.getSelectedItem() );
        model.setCurrentSalt( saltClass );
        revalidate();

        return panel;
    }
}
