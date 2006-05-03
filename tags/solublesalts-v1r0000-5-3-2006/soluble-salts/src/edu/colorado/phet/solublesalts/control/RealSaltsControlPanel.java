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
        saltMap.put( "Silver Bromide", new SilverBromide() );
//        saltMap.put( "Sodium Chloride", new SodiumChloride() );
//        saltMap.put( "Lead Chloride", new LeadChloride() );
        saltMap.put( "Copper(I) Iodide", new CuprousIodide() );
//        saltMap.put( "Silver Iodide", new SilverIodide() );
        saltMap.put( "Thallium(I) Sulfide", new ThallousSulfide() );
//        saltMap.put( "Copper Hydroxide", new CopperHydroxide() );
        saltMap.put( "Silver Arsenate", new SilverArsenate() );
//        saltMap.put( "Chromium Hydroxide", new ChromiumHydroxide() );
        saltMap.put( "Strontium Phosphate", new StrontiumPhosphate() );
        saltMap.put( "Mercury(II) Bromide", new MercuryBromide() );
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
