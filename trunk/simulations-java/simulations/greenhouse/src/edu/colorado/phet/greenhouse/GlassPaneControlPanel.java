// Copyright 2002-2011, University of Colorado

/**
 * Class: GreenhouseControlPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.greenhouse.util.MessageFormatter;


public class GlassPaneControlPanel extends JPanel implements Resettable {

    private static Color panelBackground = GreenhouseConfig.PANEL_BACKGROUND_COLOR;
    private GlassPaneModule module;
    private JSpinner glassPaneSpinner;
    private JCheckBox allPhotonsCB;
    private JCheckBox thermometerCB;

    public GlassPaneControlPanel( final GlassPaneModule module ) {

        this.module = module;

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
        glassPaneSpinner = new JSpinner( glassPaneSpinnerModel );
        glassPaneSpinner.getEditor().setBackground( Color.white );
        glassPaneSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JSpinner spinner = (JSpinner) e.getSource();
                int i = ( (Integer) spinner.getValue() ).intValue();
                module.numGlassPanesEnabled( i );
            }
        } );

        JFormattedTextField tf = ( (JSpinner.DefaultEditor) glassPaneSpinner.getEditor() ).getTextField();
        tf.setEditable( false );
        tf.setBackground( Color.white );
        glassPanePanel.add( glassPaneSpinner );
        glassPanePanel.add( new JLabel( MessageFormatter.format( GreenhouseResources.getString( "GlassPaneControlPanel.GlassPaneLabel" ) ) ) );

        // Show/hide thermometer
        thermometerCB = new JCheckBox( GreenhouseResources.getString( "GlassPaneControlPanel.ThermometerCheckbox" ) );
        thermometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.thermometerEnabled( thermometerCB.isSelected() );
            }
        } );

        // Ratio of photons to see
        allPhotonsCB = new JCheckBox( GreenhouseResources.getString( "GlassPaneControlPanel.ViewPhotonsCheckbox" ) );
        allPhotonsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( allPhotonsCB.isSelected() ) {
                    module.setVisiblePhotonRatio( 1.0 );
                }
                else {
                    module.setVisiblePhotonRatio( 0.1 );
                }
            }
        } );

        // Set the default conditions
        setDefaultConditions();

        // Reset button
        JButton resetBtn = new ResetAllButton( this, PhetApplication.getInstance().getPhetFrame() );

        //
        // Lay out the controls
        //

        // Wrap the control panel in another panel that has a default layout manager, so the controls will show up
        // at the top of the overall control panel
        JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );

        panel.add( new GreenhouseLegend(), gbc );

        // Options Panel
        JPanel optionsPanel = new PhetTitledPanel( GreenhouseResources.getString( "GreenhouseControlPanel.Options" ) );
        optionsPanel.setLayout( new GridBagLayout() );
        GridBagConstraints optsGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 25, 0, 25 ), 0, 0 );
        optionsPanel.add( glassPanePanel, optsGbc );
        optionsPanel.add( thermometerCB, optsGbc );
        optionsPanel.add( allPhotonsCB, optsGbc );


        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add( optionsPanel, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets( 15, 15, 0, 15 );
        panel.add( resetBtn, gbc );
        this.add( panel );
        setBackground( this );
    }

    private void setDefaultConditions() {
        glassPaneSpinner.setValue( new Integer( 0 ) );
        thermometerCB.setSelected( true );
        module.thermometerEnabled( thermometerCB.isSelected() );
        allPhotonsCB.setSelected( false );
        module.setVisiblePhotonRatio( 0.1 );
    }

    public void reset() {
        module.reset();
        setDefaultConditions();
    }

    private void setBackground( Container container ) {
        container.setBackground( panelBackground );
        Component[] components = container.getComponents();
        for ( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            if ( component instanceof JLabel
                 || component instanceof JCheckBox ) {
//                component.setForeground( panelForeground );
            }
            if ( !( component instanceof JButton ) ) {
//                component.setBackground( panelBackground );
            }
            if ( component.getForeground().equals( Color.black )
                 && !( component instanceof JButton ) ) {
//                component.setForeground( panelForeground );
            }
            if ( component instanceof Container
                 && !( component instanceof JButton )
                 && !( component instanceof JSpinner ) ) {
                setBackground( (Container) component );
            }
        }
    }
}
