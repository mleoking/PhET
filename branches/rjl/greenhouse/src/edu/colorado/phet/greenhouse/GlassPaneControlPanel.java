/**
 * Class: GreenhouseControlPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.MessageFormatter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GlassPaneControlPanel extends JPanel {

    public GlassPaneControlPanel( final GlassPaneModule module ) {

        //
        // Create the controls
        //

        // Add/remove clouds
        JPanel glassPanePanel = new JPanel();
        int min = 0;
        int max = module.getMaxGlassPanes();
        int step = 1;
        int initValue = 0;
        SpinnerModel glassPaneSpinnerModel = new SpinnerNumberModel( initValue, min, max, step );
        final JSpinner glassPaneSpinner = new JSpinner( glassPaneSpinnerModel );
        glassPaneSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JSpinner spinner = (JSpinner)e.getSource();
                int i = ( (Integer)spinner.getValue() ).intValue();
                module.numGlassPanesEnabled( i );
            }
        } );
        glassPanePanel.add( glassPaneSpinner );
        glassPanePanel.add( new JLabel( MessageFormatter.format( "Number of\nGlass Panes" )));

        // Show/hide thermometer
        final JCheckBox thermometerCB = new JCheckBox( "Thermometer" );
        thermometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.thermometerEnabled( thermometerCB.isSelected() );
            }
        } );
        thermometerCB.setSelected( module.isThermometerEnabled() );

        // Ratio of photons to see
        final JCheckBox allPhotonsCB = new JCheckBox( "View all photons" );
        allPhotonsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( allPhotonsCB.isSelected() ) {
                    module.setVisiblePhotonRatio( 1.0 );
                }
                else {
                    module.setVisiblePhotonRatio( 0.1 );
                }
            }
        } );
        allPhotonsCB.setSelected( true );
        module.setVisiblePhotonRatio( 1.0 );

        //
        // Lay out the controls
        //
        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new GreenhouseLegend(), 0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, glassPanePanel, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this, thermometerCB, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this, allPhotonsCB, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
        }
        catch( AWTException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
}
