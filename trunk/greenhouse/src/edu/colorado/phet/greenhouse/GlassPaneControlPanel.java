/**
 * Class: GreenhouseControlPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.MessageFormatter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GlassPaneControlPanel extends JPanel {

    private static Color panelBackground = new Color( 110, 110, 110 );
    private static Color panelForeground = Color.white;
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
                JSpinner spinner = (JSpinner)e.getSource();
                int i = ( (Integer)spinner.getValue() ).intValue();
                module.numGlassPanesEnabled( i );
            }
        } );
        glassPanePanel.add( glassPaneSpinner );
        glassPanePanel.add( new JLabel( MessageFormatter.format( SimStrings.get( "GlassPaneControlPanel.GlassPaneLabel" ) ) ) );

        // Show/hide thermometer
        thermometerCB = new JCheckBox( SimStrings.get( "GlassPaneControlPanel.ThermometerCheckbox" ) );
        thermometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.thermometerEnabled( thermometerCB.isSelected() );
            }
        } );

        // Ratio of photons to see
        allPhotonsCB = new JCheckBox( SimStrings.get( "GlassPaneControlPanel.ViewPhotonsCheckbox" ) );
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

        // Set the default conditions
        setDefaultConditions();

        // Reset button
        JButton resetBtn = new JButton( SimStrings.get( "GreenhouseControlPanel.Reset" ) );
        resetBtn.setForeground( Color.black );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );

        //
        // Lay out the controls
        //

        // Wrap the control panel in another panel that has a default layout manager, so the controls will show up
        // at the top of the overall control panel
        JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( panel, new GreenhouseLegend(), 0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( panel, glassPanePanel, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( panel, thermometerCB, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( panel, allPhotonsCB, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( panel, resetBtn, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }

        setBackground( panel );
        this.add( panel );
        setBackground( this );
    }

    private void setDefaultConditions() {
        glassPaneSpinner.setValue( new Integer( 0 ) );
        thermometerCB.setSelected( true );
        module.thermometerEnabled( thermometerCB.isSelected() );
        allPhotonsCB.setSelected( true );
        module.setVisiblePhotonRatio( 1.0 );
    }

    private void reset() {
        module.reset();
        setDefaultConditions();
    }

    private void setBackground( Container container ) {
        container.setBackground( panelBackground );
        Component[] components = container.getComponents();
        for( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            if( component instanceof JLabel
                    || component instanceof JCheckBox ) {
                component.setForeground( panelForeground );
            }
            if( ! ( component instanceof JButton ) ) {
                component.setBackground( panelBackground );
            }
            if( component.getForeground().equals( Color.black )
                    && ! ( component instanceof JButton ) ) {
                component.setForeground( panelForeground );
            }
            if( component instanceof Container
                     && ! ( component instanceof JButton )
                     && ! ( component instanceof JSpinner ) ) {
                setBackground( (Container)component );
            }
        }
    }
}
