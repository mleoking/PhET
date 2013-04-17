// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.*;

import edu.colorado.phet.solublesalts.SolubleSaltResources;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.salt.*;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

/**
 * RealSaltsControlPanel
 *
 * @author Ron LeMaster
 */
public class RealSaltsControlPanel extends SolubleSaltsControlPanel {

    static protected HashMap saltMap;

    static {
        saltMap = new HashMap();
        saltMap.put( SolubleSaltResources.getString( "Salt.silverBromide" ), new SilverBromide() );
        saltMap.put( SolubleSaltResources.getString( "Salt.copperIodide" ), new CuprousIodide() );
        saltMap.put( SolubleSaltResources.getString( "Salt.thalliumSulfide" ), new ThallousSulfide() );
        saltMap.put( SolubleSaltResources.getString( "Salt.silverArsenate" ), new SilverArsenate() );
        saltMap.put( SolubleSaltResources.getString( "Salt.strontiumPhosphate" ), new StrontiumPhosphate() );
        saltMap.put( SolubleSaltResources.getString( "Salt.mercuryBromide" ), new MercuryBromide() );
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
                Salt saltClass = (Salt) saltMap.get( comboBox.getSelectedItem() );
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

        Salt saltClass = (Salt) saltMap.get( comboBox.getSelectedItem() );
        model.setCurrentSalt( saltClass );
        revalidate();

        return panel;
    }
}
