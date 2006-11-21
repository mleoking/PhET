/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.Gun;
import edu.colorado.phet.hydrogenatom.model.RutherfordScattering;
import edu.colorado.phet.hydrogenatom.module.HAModule;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieBrightnessMagnitudeNode;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieBrightnessNode;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieRadialDistanceNode;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperControlsDialog extends JDialog implements ColorChooserFactory.Listener {
    
    private static class ColorChip extends JLabel {
        public ColorChip() {
            super();
        }
    }
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int MIN_PARTICLES_IN_BOX = 1;
    private static final int MAX_PARTICLES_IN_BOX = 100;
    
    private static final int COLOR_CHIP_WIDTH = 15;
    private static final int COLOR_CHIP_HEIGHT = 15;
    private static final Stroke COLOR_CHIP_STROKE = new BasicStroke( 1f );
    private static final Color COLOR_CHIP_BORDER_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAModule _module;
    
    private JSpinner _maxParticlesSpinner;
    private JSpinner _absorptionClosenessSpinner;
    private JCheckBox _rutherfordScatteringOutputCheckBox;

    private JCheckBox _bohrAbsorptionCheckBox;
    private JCheckBox _bohrEmissionCheckBox;
    private JCheckBox _bohrStimulatedEmissionCheckBox;
    private JSpinner _bohrMinStateTimeSpinner;

    private ColorChip _deBroglieBrightnessMagnitudePlusColor;
    private ColorChip _deBroglieBrightnessMagnitudeZeroColor;
    private ColorChip _deBroglieBrightnessPlusColor;
    private ColorChip _deBroglieBrightnessMinusColor;
    private ColorChip _deBroglieBrightnessZeroColor;
    private JSpinner _deBroglieRadialAmplitudeSpinner;
    
    private JDialog _colorChooserDialog;
    private ColorChip _editColorChip;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperControlsDialog( Frame owner, HAModule module ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        
        _module = module;
        
        JPanel inputPanel = createInputPanel();
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel() {

        // Photon & alpha particle production
        HorizontalLayoutPanel maxParticlesPanel = new HorizontalLayoutPanel();
        {
            JLabel label = new JLabel( "Max particles in box:" );
            
            int maxParticles = _module.getGun().getMaxParticles();
            SpinnerModel model = new SpinnerNumberModel( maxParticles, MIN_PARTICLES_IN_BOX, MAX_PARTICLES_IN_BOX, 1 /* stepSize */);
            _maxParticlesSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _maxParticlesSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            maxParticlesPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            maxParticlesPanel.add( label );
            maxParticlesPanel.add( _maxParticlesSpinner );
        }
        
        // How close photon must be to electron for it to be absorbed
        HorizontalLayoutPanel absorptionClosenessPanel = new HorizontalLayoutPanel();
        {
            JLabel label = new JLabel( "Photon absorbed when this close:" );
            
            int closeness = AbstractHydrogenAtom.ABSORPTION_CLOSENESS;
            SpinnerModel model = new SpinnerNumberModel( closeness, closeness, closeness * 4, 1 /* stepSize */);
            _absorptionClosenessSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _absorptionClosenessSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            absorptionClosenessPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            absorptionClosenessPanel.add( label );
            absorptionClosenessPanel.add( _absorptionClosenessSpinner );
        }
        
        // Enables debug output from Rutherford Scattering algorithm
        _rutherfordScatteringOutputCheckBox = new JCheckBox( "Rutherford Scattering debug output" );
        _rutherfordScatteringOutputCheckBox.setSelected( RutherfordScattering.DEBUG_OUTPUT_ENABLED );
        
        // Bohr absorption/emission enables
        _bohrAbsorptionCheckBox = new JCheckBox( "absorption enabled", BohrModel.DEBUG_ASBORPTION_ENABLED );
        _bohrEmissionCheckBox = new JCheckBox( "emission enabled", BohrModel.DEBUG_EMISSION_ENABLED );
        _bohrStimulatedEmissionCheckBox = new JCheckBox( "stimulated emission enabled", BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED );
        
        // Bohr min time that electron stays in a state
        HorizontalLayoutPanel minStateTimePanel = new HorizontalLayoutPanel();
        {
            JLabel label = new JLabel( "<html>Min time that electron must spend<br>in a state before it can emit a photon:</html>" );
            JLabel units = new JLabel( "dt" );
            
            int minStateTime = (int) BohrModel.MIN_TIME_IN_STATE;
            SpinnerModel model = new SpinnerNumberModel( minStateTime, 1, 300, 1 /* stepSize */);
            _bohrMinStateTimeSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _bohrMinStateTimeSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            minStateTimePanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            minStateTimePanel.add( label );
            minStateTimePanel.add( _bohrMinStateTimeSpinner );
            minStateTimePanel.add( units );
        }
        
        // deBroglie "brightness magnitude" colors
        HorizontalLayoutPanel deBroglieBrightnessMagnitudeColorsPanel = new HorizontalLayoutPanel();
        {
            JLabel titleLabel = new JLabel( "Brightness magnitude colors:" );
            JLabel plusLabel = new JLabel( "1=" );
            JLabel zeroLabel = new JLabel( "0=" );
            
            _deBroglieBrightnessMagnitudePlusColor = new ColorChip();
            setColor( _deBroglieBrightnessMagnitudePlusColor, DeBroglieBrightnessMagnitudeNode.MAX_COLOR );
            _deBroglieBrightnessMagnitudeZeroColor = new ColorChip();
            setColor( _deBroglieBrightnessMagnitudeZeroColor, DeBroglieBrightnessMagnitudeNode.MIN_COLOR );
            
            deBroglieBrightnessMagnitudeColorsPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            deBroglieBrightnessMagnitudeColorsPanel.add( titleLabel );
            deBroglieBrightnessMagnitudeColorsPanel.add( plusLabel );
            deBroglieBrightnessMagnitudeColorsPanel.add( _deBroglieBrightnessMagnitudePlusColor );
            deBroglieBrightnessMagnitudeColorsPanel.add( zeroLabel );
            deBroglieBrightnessMagnitudeColorsPanel.add( _deBroglieBrightnessMagnitudeZeroColor );
        }
        
        // deBroglie "brightness" colors
        HorizontalLayoutPanel deBroglieBrightnessColorsPanel = new HorizontalLayoutPanel();
        {
            JLabel titleLabel = new JLabel( "Brightness colors:" );
            JLabel plusLabel = new JLabel( "+1=" );
            JLabel zeroLabel = new JLabel( "0=" );
            JLabel minusLabel = new JLabel( "-1=" );
            
            _deBroglieBrightnessPlusColor = new ColorChip();
            setColor( _deBroglieBrightnessPlusColor, DeBroglieBrightnessNode.PLUS_COLOR );
            _deBroglieBrightnessZeroColor = new ColorChip();
            setColor( _deBroglieBrightnessZeroColor, DeBroglieBrightnessNode.ZERO_COLOR );
            _deBroglieBrightnessMinusColor = new ColorChip();
            setColor( _deBroglieBrightnessMinusColor, DeBroglieBrightnessNode.MINUS_COLOR );
            
            deBroglieBrightnessColorsPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            deBroglieBrightnessColorsPanel.add( titleLabel );
            deBroglieBrightnessColorsPanel.add( plusLabel );
            deBroglieBrightnessColorsPanel.add( _deBroglieBrightnessPlusColor );
            deBroglieBrightnessColorsPanel.add( zeroLabel );
            deBroglieBrightnessColorsPanel.add( _deBroglieBrightnessZeroColor );
            deBroglieBrightnessColorsPanel.add( minusLabel );
            deBroglieBrightnessColorsPanel.add( _deBroglieBrightnessMinusColor );
        }
        
        // deBroglie radial amplitude control
        HorizontalLayoutPanel deBroglieRadialAmplitudePanel = new HorizontalLayoutPanel();
        {
            JLabel label = new JLabel( "Max radial amplitude:" );
            JLabel units = new JLabel( "% of orbit radius" );
            
            int value = (int)( DeBroglieRadialDistanceNode.RADIAL_OFFSET_FACTOR * 100 );
            int min = 5;
            int max = 50;
            SpinnerModel model = new SpinnerNumberModel( value, min, max, 1 /* stepSize */);
            _deBroglieRadialAmplitudeSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _deBroglieRadialAmplitudeSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            deBroglieRadialAmplitudePanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            deBroglieRadialAmplitudePanel.add( label );
            deBroglieRadialAmplitudePanel.add( _deBroglieRadialAmplitudeSpinner );
            deBroglieRadialAmplitudePanel.add( units );
        }
        
        // Event handling
        EventListener listener = new EventListener();
        _maxParticlesSpinner.addChangeListener( listener );
        _absorptionClosenessSpinner.addChangeListener( listener );
        _rutherfordScatteringOutputCheckBox.addChangeListener( listener );
        _bohrAbsorptionCheckBox.addChangeListener( listener );
        _bohrEmissionCheckBox.addChangeListener( listener );
        _bohrStimulatedEmissionCheckBox.addChangeListener( listener );
        _bohrMinStateTimeSpinner.addChangeListener( listener );
        _deBroglieBrightnessMagnitudePlusColor.addMouseListener( listener );
        _deBroglieBrightnessMagnitudeZeroColor.addMouseListener( listener );
        _deBroglieBrightnessPlusColor.addMouseListener( listener );
        _deBroglieBrightnessZeroColor.addMouseListener( listener );
        _deBroglieBrightnessMinusColor.addMouseListener( listener );
        _deBroglieRadialAmplitudeSpinner.addChangeListener( listener );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        {
            layout.addComponent( new JLabel( "Global controls:" ), row++, 0 );
            layout.addComponent( maxParticlesPanel, row++, 0 );
            layout.addComponent( absorptionClosenessPanel, row++, 0 );
            layout.addComponent( _rutherfordScatteringOutputCheckBox, row++, 0 );
            layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
            
            layout.addComponent( new JLabel( "Bohr/deBroglie/Schrodinger controls:" ), row++, 0 );
            layout.addComponent( _bohrAbsorptionCheckBox, row++, 0 );
            layout.addComponent( _bohrEmissionCheckBox, row++, 0 );
            layout.addComponent( _bohrStimulatedEmissionCheckBox, row++, 0 );
            layout.addComponent( minStateTimePanel, row++, 0 );
            layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
            
            layout.addComponent( new JLabel( "deBroglie controls:" ), row++, 0 );
            layout.addComponent( deBroglieBrightnessMagnitudeColorsPanel, row++, 0 );
            layout.addComponent( deBroglieBrightnessColorsPanel, row++, 0 );
            layout.addComponent( deBroglieRadialAmplitudePanel, row++, 0 );
            layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        return panel;
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Sets the color of a color chip.
     * 
     * @param colorBar
     * @param color
     */
    private void setColor( ColorChip colorChip, Color color ) {
        Rectangle r = new Rectangle( 0, 0, COLOR_CHIP_WIDTH, COLOR_CHIP_HEIGHT );
        BufferedImage image = new BufferedImage( r.width, r.height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( color );
        g2.fill( r );
        g2.setStroke( COLOR_CHIP_STROKE );
        g2.setColor( COLOR_CHIP_BORDER_COLOR );
        g2.draw( r );
        colorChip.setIcon( new ImageIcon( image ) );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends MouseAdapter implements ChangeListener {
        
        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            if ( source == _maxParticlesSpinner ) {
                handleMaxParticlesSpinner();
            }
            else if ( source == _absorptionClosenessSpinner ) {
                handleAbsorptionClosenessSpinner();
            }
            else if ( source == _rutherfordScatteringOutputCheckBox ) {
                handleRutherfordScatteringOutputCheckBox();
            }
            else if ( source == _bohrAbsorptionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _bohrEmissionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _bohrStimulatedEmissionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _bohrMinStateTimeSpinner ) {
                handleMinStateTime();
            }
            else if ( source == _deBroglieRadialAmplitudeSpinner ) {
                handleDeBroglieRadialAmplitudeSpinner();
            }
            else {
                throw new UnsupportedOperationException( "unsupported ChangeEvent source: " + source );
            }
        }
        
        public void mouseClicked( MouseEvent event ) {
            Object source = event.getSource();
            if ( source instanceof ColorChip ) {
                editColor( (ColorChip) source );
            }
            else {
                throw new UnsupportedOperationException( "unsupported MouseEvent source: " + source );
            }
        }
    }
    
    private void handleMaxParticlesSpinner() {
        Gun gun = _module.getGun();
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _maxParticlesSpinner.getModel();
        int maxParticles = spinnerModel.getNumber().intValue();
        gun.setMaxParticles( maxParticles );
    }
    
    private void handleAbsorptionClosenessSpinner() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _absorptionClosenessSpinner.getModel();
        int closeness = spinnerModel.getNumber().intValue();
        AbstractHydrogenAtom.ABSORPTION_CLOSENESS = closeness;
    }
    
    private void handleRutherfordScatteringOutputCheckBox() {
        RutherfordScattering.DEBUG_OUTPUT_ENABLED = _rutherfordScatteringOutputCheckBox.isSelected();
    }
    
    private void handleBohrAbsorptionEmission() {
        BohrModel.DEBUG_ASBORPTION_ENABLED = _bohrAbsorptionCheckBox.isSelected();
        BohrModel.DEBUG_EMISSION_ENABLED = _bohrEmissionCheckBox.isSelected();
        BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED = _bohrStimulatedEmissionCheckBox.isSelected();
    }
    
    private void handleMinStateTime() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _bohrMinStateTimeSpinner.getModel();
        int minStateTime = spinnerModel.getNumber().intValue();
        BohrModel.MIN_TIME_IN_STATE = minStateTime;
    }
    
    private void handleDeBroglieRadialAmplitudeSpinner() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _deBroglieRadialAmplitudeSpinner.getModel();
        double value = spinnerModel.getNumber().intValue() / 100.0;
        DeBroglieRadialDistanceNode.RADIAL_OFFSET_FACTOR = value;
    }
    
    private void editColor( ColorChip colorChip ) {
        
        _editColorChip = colorChip;

        String titlePrefix = null;
        Color initialColor = null;
        
        if ( colorChip == _deBroglieBrightnessPlusColor ) {
            initialColor = DeBroglieBrightnessNode.PLUS_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessZeroColor ) {
            initialColor = DeBroglieBrightnessNode.ZERO_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessMinusColor ) {
            initialColor = DeBroglieBrightnessNode.MINUS_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessMagnitudePlusColor ) {
            initialColor = DeBroglieBrightnessMagnitudeNode.PLUS_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessMagnitudeZeroColor ) {
            initialColor = DeBroglieBrightnessMagnitudeNode.ZERO_COLOR;
        }
        else {
            throw new UnsupportedOperationException( "unsupported ColorChip" );
        }
        
        closeColorChooser();
        String title = "Color Chooser";
        Component parent = PhetApplication.instance().getPhetFrame();
        _colorChooserDialog = ColorChooserFactory.createDialog( title, parent, initialColor, this );
        _colorChooserDialog.show();
        
    }
    
    /*
     * Closes the color chooser dialog.
     */
    private void closeColorChooser() {
        if ( _colorChooserDialog != null ) {
            _colorChooserDialog.dispose();
        }
    }
    
    //----------------------------------------------------------------------------
    // ColorChooserFactory.Listener implementation
    //----------------------------------------------------------------------------
    
    public void colorChanged( Color color ) {
        handleColorChange( color );
    }

    public void ok( Color color ) {
        handleColorChange( color );
    }

    public void cancelled( Color originalColor ) {
        handleColorChange( originalColor );
    }
    
    private void handleColorChange( Color color ) {

        // Set the color chip's color...
        setColor( _editColorChip, color );
        
        if ( _editColorChip == _deBroglieBrightnessPlusColor ) {
            DeBroglieBrightnessNode.PLUS_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessZeroColor ) {
            DeBroglieBrightnessNode.ZERO_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessMinusColor ) {
            DeBroglieBrightnessNode.MINUS_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessMagnitudePlusColor ) {
            DeBroglieBrightnessMagnitudeNode.MAX_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessMagnitudeZeroColor ) {
            DeBroglieBrightnessMagnitudeNode.MIN_COLOR = color;
        }
        else {
            throw new UnsupportedOperationException( "unsupported ColorChip" );
        }
    }
}
