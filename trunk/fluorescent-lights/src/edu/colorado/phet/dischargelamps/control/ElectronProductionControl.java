/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.ElectronPulser;
import edu.colorado.phet.quantum.model.ElectronSource;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * ElectronProductionControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronProductionControl extends JPanel {
    private JRadioButton continuousModeRB;
    private JRadioButton singleShotModeRB;

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static class ProductionMode {
        private ProductionMode() {
        }
    }

    public static final ProductionMode CONTINUOUS = new ProductionMode();
    public static final ProductionMode SINGLE_SHOT = new ProductionMode();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private DischargeLampModel model;
    private double currentDisplayFactor = 1E-3;
    private DischargeLampModule module;
    private double maxCurrent;
    private double continuousCurrent;
    private CurrentSlider heaterControlSlider;
    private JComponent heaterControl;
    private JComponent modeSelectorControl;
    private JComponent fireElectronButton;

    public ElectronProductionControl( DischargeLampModule module, double maxCurrent ) {
        this.module = module;
        this.maxCurrent = maxCurrent;
        this.model = ( (DischargeLampModel)module.getModel() );
        fireElectronButton = createFireElectronBtn();
        heaterControl = createHeaterControl();
        modeSelectorControl = createModeSelectorControl();

        // Set the default current for when continuous mode is first selected. Note that this
        // must be done after the above initializations are performed
        this.continuousCurrent = maxCurrent * .2;

        layoutPanel();
    }

    /**
     *
     */
    public void setMaxCurrent( double maxCurrent ) {
        heaterControlSlider.setMaxCurrent( maxCurrent );
    }

    /**
     * Sets the production mode to single-shot or continuous
     *
     * @param mode
     */
    public void setProductionMode( ElectronProductionControl.ProductionMode mode ) {
        if( mode == CONTINUOUS ) {
            continuousModeRB.setSelected( true );
            heaterControl.setVisible( true );
            fireElectronButton.setVisible( false );
            model.setElectronProductionMode( ElectronSource.CONTINUOUS_MODE );
            model.setCurrent( continuousCurrent, currentDisplayFactor );
        }
        if( mode == SINGLE_SHOT ) {
            // Save the current current
            continuousCurrent = model.getCurrent() / currentDisplayFactor;
            singleShotModeRB.setSelected( true );
            heaterControl.setVisible( false );
            fireElectronButton.setVisible( true );
            model.setCurrent( 0 );
            model.setElectronProductionMode( ElectronSource.SINGLE_SHOT_MODE );
        }
        module.setProductionType( mode );
    }

    private JComponent createModeSelectorControl() {
        // Radio buttons to choose between single-shot and continuous modes
        continuousModeRB = new JRadioButton( new AbstractAction( SimStrings.get( "Controls.Continuous" ) ) {
            public void actionPerformed( ActionEvent e ) {
                setProductionMode( ElectronProductionControl.CONTINUOUS );
            }
        } );
        singleShotModeRB = new JRadioButton( new AbstractAction( SimStrings.get( "Controls.Single" ) ) {
            public void actionPerformed( ActionEvent e ) {
                setProductionMode( ElectronProductionControl.SINGLE_SHOT );
            }
        } );
        ButtonGroup electronProductionBtnGrp = new ButtonGroup();
        electronProductionBtnGrp.add( continuousModeRB );
        electronProductionBtnGrp.add( singleShotModeRB );
        singleShotModeRB.setSelected( true );

        JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints(
                GridBagConstraints.RELATIVE,
                0,
                1, 1, 0, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets( 0, 0, 0, 0 ), 0, 0 );
        panel.add( singleShotModeRB, gbc );
        panel.add( continuousModeRB, gbc );

        singleShotModeRB.setSelected( true );
        setProductionMode( ElectronProductionControl.SINGLE_SHOT );

        return panel;
    }

    private JComponent createFireElectronBtn() {
        final JButton singleShotBtn = new JButton( SimStrings.get( "Controls.FireElectron" ) );
        singleShotBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.addModelElement( new ElectronPulser( model, maxCurrent / 2 * currentDisplayFactor ) );
            }
        } );
        return singleShotBtn;
    }

    private JComponent createHeaterControl() {
        heaterControlSlider = new CurrentSlider( maxCurrent );
        heaterControlSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setCurrent( heaterControlSlider.getValue(), currentDisplayFactor );
            }
        } );

        final JTextField readout = new JTextField();
        readout.setHorizontalAlignment( JTextField.RIGHT );
        final DecimalFormat format = new DecimalFormat( "##0%" );
        heaterControlSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double displayNumber = heaterControlSlider.getPctMax();
                readout.setText( format.format( displayNumber ) );
            }
        } );
        double displayNumber = heaterControlSlider.getPctMax();
        readout.setText( format.format( displayNumber ) );

        model.addChangeListener( new DischargeLampModel.ChangeListenerAdapter() {
            public void currentChanged( DischargeLampModel.ChangeEvent event ) {
                heaterControlSlider.setValue( (int)( model.getCurrent() / currentDisplayFactor ) );
                double displayNumber = heaterControlSlider.getPctMax();
                readout.setText( format.format( displayNumber ) );
            }
        } );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        panel.add( Box.createHorizontalStrut( 175 ), gbc );
        panel.add( heaterControlSlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( Box.createHorizontalStrut( 40 ), gbc );
        panel.add( readout, gbc );
        return panel;
    }

    /**
     * Lay out the components in the panel
     */
    private void layoutPanel() {
        // Put a simple border around the panel
        setBorder( new LineBorder( Color.black, 1 ) );

        // Lay out the components
        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets( 3, 0, 3, 0 ), 0, 0 );

        JLabel title = new JLabel( SimStrings.get( "Controls.ElectronProduction" ) );
        Font defaultFont = title.getFont();
        Font newFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() );
        title.setFont( newFont );
        gbc.gridwidth = 2;
        add( title, gbc );

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets( 0, 20, 0, 0 );
        add( modeSelectorControl, gbc );
        gbc.gridx++;
        add( Box.createHorizontalStrut( 250 ), gbc );
        add( Box.createVerticalStrut( 30 ), gbc );
        add( fireElectronButton, gbc );
        add( heaterControl, gbc );
    }
}