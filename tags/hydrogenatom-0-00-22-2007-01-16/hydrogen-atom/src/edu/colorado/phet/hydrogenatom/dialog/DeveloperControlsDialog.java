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
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.GunWavelengthControl;
import edu.colorado.phet.hydrogenatom.control.SpinnerControl;
import edu.colorado.phet.hydrogenatom.energydiagrams.AbstractEnergyDiagram;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.Gun;
import edu.colorado.phet.hydrogenatom.model.RutherfordScattering;
import edu.colorado.phet.hydrogenatom.module.HAModule;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieBrightnessMagnitudeNode;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieBrightnessNode;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieRadialDistanceNode;
import edu.colorado.phet.hydrogenatom.view.atom.SchrodingerNode;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperControlsDialog extends JDialog implements ColorChooserFactory.Listener {
    
    // Color chips (marker class)
    private static class ColorChip extends JLabel {
        public ColorChip() {
            super();
        }
    }
    
    // Title labels
    private static class TitleLabel extends JLabel {
        public TitleLabel( String title ) {
            super( title );
            setForeground( TITLE_COLOR );
            setFont( TITLE_FONT );
        }
    }
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Insets DEFAULT_INSETS = new Insets( 3, 3, 3, 3 );
    
    private static final int MIN_PARTICLES_IN_BOX = 1;
    private static final int MAX_PARTICLES_IN_BOX = 100;
    
    private static final int COLOR_CHIP_WIDTH = 15;
    private static final int COLOR_CHIP_HEIGHT = 15;
    private static final Stroke COLOR_CHIP_STROKE = new BasicStroke( 1f );
    private static final Color COLOR_CHIP_BORDER_COLOR = Color.BLACK;
    
    private static final Color TITLE_COLOR = Color.RED;
    private static final Font TITLE_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAModule _module;
    
    private SpinnerControl _maxParticlesSpinner;
    private SpinnerControl _absorptionClosenessSpinner;
    private JCheckBox _rutherfordScatteringOutputCheckBox;
    private SpinnerControl _wavelengthHiliteThreshold;

    private JCheckBox _absorptionCheckBox;
    private JCheckBox _spontanuousEmissionCheckBox;
    private JCheckBox _stimulatedEmissionCheckBox;
    private SpinnerControl _minStateTimeSpinner;

    private ColorChip _deBroglieBrightnessMagnitudePlusChip;
    private ColorChip _deBroglieBrightnessMagnitudeZeroChip;
    private ColorChip _deBroglieBrightnessPlusChip;
    private ColorChip _deBroglieBrightnessMinusChip;
    private ColorChip _deBroglieBrightnessZeroChip;
    private SpinnerControl _deBroglieRadialAmplitudeSpinner;
    
    private SpinnerControl _schrodingerProtonThresholdSpinner;
    
    private SpinnerControl _squiggleLifetimeSpinner;
    
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
        {
            int value = _module.getGun().getMaxParticles();
            int min = MIN_PARTICLES_IN_BOX;
            int max = MAX_PARTICLES_IN_BOX;
            int stepSize = 1;
            int columns = 3;
            String label = "Max particles in box:";
            String units = "";
            _maxParticlesSpinner = new SpinnerControl( value, min, max, stepSize, columns, label, units );
            _maxParticlesSpinner.setEditable( false );
        }
        
        // How close photon must be to electron for it to be absorbed
        {
            int value = AbstractHydrogenAtom.COLLISION_CLOSENESS;
            int min = value;
            int max = value * 4;
            int stepSize = 1;
            int columns = 3;
            String label = "Photon absorbed when this close:";
            String units = "";
            _absorptionClosenessSpinner = new SpinnerControl( value, min, max, stepSize, columns, label, units );
            _absorptionClosenessSpinner.setEditable( false );
        }
        
        // Enables debug output from Rutherford Scattering algorithm
        _rutherfordScatteringOutputCheckBox = new JCheckBox( "Rutherford Scattering debug output", RutherfordScattering.DEBUG_OUTPUT_ENABLED );
        
        // Hiliting of transition wavelengths on wavelength control
        {
            int value = GunWavelengthControl.HILITE_THRESHOLD;
            int min = 1;
            int max = 20;
            int stepSize = 1;
            int columns = 3;
            String label = "<html>Wavelength slider hilites when knob is<br>this close to a transition wavelength:</html>";
            String units = "ns";
            _wavelengthHiliteThreshold = new SpinnerControl( value, min, max, stepSize, columns, label, units );
            _wavelengthHiliteThreshold.setEditable( false );
        }
        
        // Enable absorption/emission
        _absorptionCheckBox = new JCheckBox( "absorption enabled", BohrModel.DEBUG_ABSORPTION_ENABLED );
        _spontanuousEmissionCheckBox = new JCheckBox( "spontaneous emission enabled", BohrModel.DEBUG_SPONTANEOUS_EMISSION_ENABLED );
        _stimulatedEmissionCheckBox = new JCheckBox( "stimulated emission enabled", BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED );
        
        // Min time that electron stays in a state
        {
            int value = (int) BohrModel.MIN_TIME_IN_STATE;
            int min = 1;
            int max = 300;
            int stepSize = 1;
            int columns = 4;
            String label = "<html>Min time that electron must spend<br>in a state before it can emit a photon:</html>";
            String units = "dt";
            _minStateTimeSpinner = new SpinnerControl( value, min, max, stepSize, columns, label, units );
            _minStateTimeSpinner.setEditable( false );
        }
        
        // deBroglie "brightness magnitude" colors
        HorizontalLayoutPanel deBroglieBrightnessMagnitudeColorsPanel = new HorizontalLayoutPanel();
        {
            JLabel titleLabel = new JLabel( "Brightness magnitude colors:" );
            JLabel plusLabel = new JLabel( "1=" );
            JLabel zeroLabel = new JLabel( "0=" );
            
            _deBroglieBrightnessMagnitudePlusChip = new ColorChip();
            setColor( _deBroglieBrightnessMagnitudePlusChip, DeBroglieBrightnessMagnitudeNode.MAX_COLOR );
            _deBroglieBrightnessMagnitudeZeroChip = new ColorChip();
            setColor( _deBroglieBrightnessMagnitudeZeroChip, DeBroglieBrightnessMagnitudeNode.MIN_COLOR );
            
            deBroglieBrightnessMagnitudeColorsPanel.setInsets( DEFAULT_INSETS );
            deBroglieBrightnessMagnitudeColorsPanel.add( titleLabel );
            deBroglieBrightnessMagnitudeColorsPanel.add( plusLabel );
            deBroglieBrightnessMagnitudeColorsPanel.add( _deBroglieBrightnessMagnitudePlusChip );
            deBroglieBrightnessMagnitudeColorsPanel.add( zeroLabel );
            deBroglieBrightnessMagnitudeColorsPanel.add( _deBroglieBrightnessMagnitudeZeroChip );
        }
        
        // deBroglie "brightness" colors
        HorizontalLayoutPanel deBroglieBrightnessColorsPanel = new HorizontalLayoutPanel();
        {
            JLabel titleLabel = new JLabel( "Brightness colors:" );
            JLabel plusLabel = new JLabel( "+1=" );
            JLabel zeroLabel = new JLabel( "0=" );
            JLabel minusLabel = new JLabel( "-1=" );
            
            _deBroglieBrightnessPlusChip = new ColorChip();
            setColor( _deBroglieBrightnessPlusChip, DeBroglieBrightnessNode.PLUS_COLOR );
            _deBroglieBrightnessZeroChip = new ColorChip();
            setColor( _deBroglieBrightnessZeroChip, DeBroglieBrightnessNode.ZERO_COLOR );
            _deBroglieBrightnessMinusChip = new ColorChip();
            setColor( _deBroglieBrightnessMinusChip, DeBroglieBrightnessNode.MINUS_COLOR );
            
            deBroglieBrightnessColorsPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            deBroglieBrightnessColorsPanel.add( titleLabel );
            deBroglieBrightnessColorsPanel.add( plusLabel );
            deBroglieBrightnessColorsPanel.add( _deBroglieBrightnessPlusChip );
            deBroglieBrightnessColorsPanel.add( zeroLabel );
            deBroglieBrightnessColorsPanel.add( _deBroglieBrightnessZeroChip );
            deBroglieBrightnessColorsPanel.add( minusLabel );
            deBroglieBrightnessColorsPanel.add( _deBroglieBrightnessMinusChip );
        }
        
        // deBroglie radial amplitude control
        HorizontalLayoutPanel deBroglieRadialAmplitudePanel = new HorizontalLayoutPanel();
        {
            int value = (int)( DeBroglieRadialDistanceNode.RADIAL_OFFSET_FACTOR * 100 );
            int min = 5;
            int max = 50;
            int stepSize = 1;
            int columns = 3;
            String label = "Max radial amplitude:";
            String units = "% of orbit radius";
            _deBroglieRadialAmplitudeSpinner = new SpinnerControl( value, min, max, stepSize, columns, label, units );
            _deBroglieRadialAmplitudeSpinner.setEditable( false );
        }
        
        // brightness threshold for showing Schrodinger proton
        {
            double value = SchrodingerNode.PROTON_VISIBILITY_THRESHOLD;
            double min = 0;
            double max = 1;
            double stepSize = 0.05;
            int columns = 3;
            String label = "<html>Proton is visible when<br>brightness at (0,0) is less than:</html>";
            String units = "";
            _schrodingerProtonThresholdSpinner = new SpinnerControl( value, min, max, stepSize, columns, label, units );
            _schrodingerProtonThresholdSpinner.setEditable( false );
        }
        
        // Time that squiggles are visible in energy diagrams
        {
            double value = AbstractEnergyDiagram.SQUIGGLE_LIFETIME / 1000;
            double min = 0.1;
            double max = 5;
            double stepSize = 0.1;
            int columns = 3;
            String label = "Squiggles disappear after";
            String units = "sec";
            _squiggleLifetimeSpinner = new SpinnerControl( value, min, max, stepSize, columns, label, units );
            _squiggleLifetimeSpinner.setEditable( false );
        }
        
        // Event handling
        EventListener listener = new EventListener();
        _maxParticlesSpinner.getSpinner().addChangeListener( listener );
        _absorptionClosenessSpinner.getSpinner().addChangeListener( listener );
        _rutherfordScatteringOutputCheckBox.addChangeListener( listener );
        _wavelengthHiliteThreshold.getSpinner().addChangeListener( listener );
        _absorptionCheckBox.addChangeListener( listener );
        _spontanuousEmissionCheckBox.addChangeListener( listener );
        _stimulatedEmissionCheckBox.addChangeListener( listener );
        _minStateTimeSpinner.getSpinner().addChangeListener( listener );
        _deBroglieBrightnessMagnitudePlusChip.addMouseListener( listener );
        _deBroglieBrightnessMagnitudeZeroChip.addMouseListener( listener );
        _deBroglieBrightnessPlusChip.addMouseListener( listener );
        _deBroglieBrightnessZeroChip.addMouseListener( listener );
        _deBroglieBrightnessMinusChip.addMouseListener( listener );
        _deBroglieRadialAmplitudeSpinner.getSpinner().addChangeListener( listener );
        _schrodingerProtonThresholdSpinner.getSpinner().addChangeListener( listener );
        _squiggleLifetimeSpinner.getSpinner().addChangeListener( listener );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        {
            layout.addComponent( new TitleLabel( "Global:" ), row++, 0 );
            layout.addComponent( _maxParticlesSpinner, row++, 0 );
            layout.addComponent( _absorptionClosenessSpinner, row++, 0 );
            layout.addComponent( _rutherfordScatteringOutputCheckBox, row++, 0 );
            layout.addComponent( _wavelengthHiliteThreshold, row++, 0 );
            layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
            
            layout.addComponent( new TitleLabel( "Bohr/deBroglie/Schr\u00f6dinger:" ), row++, 0 );
            layout.addComponent( _absorptionCheckBox, row++, 0 );
            layout.addComponent( _spontanuousEmissionCheckBox, row++, 0 );
            layout.addComponent( _stimulatedEmissionCheckBox, row++, 0 );
            layout.addComponent( _minStateTimeSpinner, row++, 0 );
            layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
            
            layout.addComponent( new TitleLabel( "deBroglie:" ), row++, 0 );
            layout.addComponent( deBroglieBrightnessMagnitudeColorsPanel, row++, 0 );
            layout.addComponent( deBroglieBrightnessColorsPanel, row++, 0 );
            layout.addComponent( deBroglieRadialAmplitudePanel, row++, 0 );
            layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
            
            layout.addComponent( new TitleLabel( "Schr\u00f6dinger:" ), row++, 0 );
            layout.addComponent( _schrodingerProtonThresholdSpinner, row++, 0 );
            layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
            
            layout.addComponent( new TitleLabel( "Energy diagrams:" ), row++, 0 );
            layout.addComponent( _squiggleLifetimeSpinner, row++, 0 );
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
            if ( source == _maxParticlesSpinner.getSpinner() ) {
                handleMaxParticlesSpinner();
            }
            else if ( source == _absorptionClosenessSpinner.getSpinner() ) {
                handleAbsorptionClosenessSpinner();
            }
            else if ( source == _rutherfordScatteringOutputCheckBox ) {
                handleRutherfordScatteringOutputCheckBox();
            }
            else if ( source == _wavelengthHiliteThreshold.getSpinner() ) {
                handleWavelengthHiliteThresholdSpinner();
            }
            else if ( source == _absorptionCheckBox ) {
                handleAbsorptionEmissionCheckBoxes();
            }
            else if ( source == _spontanuousEmissionCheckBox ) {
                handleAbsorptionEmissionCheckBoxes();
            }
            else if ( source == _stimulatedEmissionCheckBox ) {
                handleAbsorptionEmissionCheckBoxes();
            }
            else if ( source == _minStateTimeSpinner.getSpinner() ) {
                handleMinStateTime();
            }
            else if ( source == _deBroglieRadialAmplitudeSpinner.getSpinner() ) {
                handleDeBroglieRadialAmplitudeSpinner();
            }
            else if ( source == _schrodingerProtonThresholdSpinner.getSpinner() ) {
                handleSchrodingerThresholdSpinner();
            }
            else if ( source == _squiggleLifetimeSpinner.getSpinner() ) {
                handleSquiggleLifetimeSpinner();
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
        int value = _maxParticlesSpinner.getIntValue();
        Gun gun = _module.getGun();
        gun.setMaxParticles( value );
    }
    
    private void handleAbsorptionClosenessSpinner() {
        AbstractHydrogenAtom.COLLISION_CLOSENESS = _absorptionClosenessSpinner.getIntValue();
    }
    
    private void handleRutherfordScatteringOutputCheckBox() {
        RutherfordScattering.DEBUG_OUTPUT_ENABLED = _rutherfordScatteringOutputCheckBox.isSelected();
    }
    
    private void handleWavelengthHiliteThresholdSpinner() {
        GunWavelengthControl.HILITE_THRESHOLD = _wavelengthHiliteThreshold.getIntValue();
    }
    
    private void handleAbsorptionEmissionCheckBoxes() {
        BohrModel.DEBUG_ABSORPTION_ENABLED = _absorptionCheckBox.isSelected();
        BohrModel.DEBUG_SPONTANEOUS_EMISSION_ENABLED = _spontanuousEmissionCheckBox.isSelected();
        BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED = _stimulatedEmissionCheckBox.isSelected();
    }
    
    private void handleMinStateTime() {
        BohrModel.MIN_TIME_IN_STATE = _minStateTimeSpinner.getIntValue();
    }
    
    private void handleDeBroglieRadialAmplitudeSpinner() {
        DeBroglieRadialDistanceNode.RADIAL_OFFSET_FACTOR = _deBroglieRadialAmplitudeSpinner.getIntValue() / 100.0;
    }
    
    private void handleSchrodingerThresholdSpinner() {
        SchrodingerNode.PROTON_VISIBILITY_THRESHOLD = _schrodingerProtonThresholdSpinner.getDoubleValue();
    }
    
    private void handleSquiggleLifetimeSpinner() {
        AbstractEnergyDiagram.SQUIGGLE_LIFETIME = _squiggleLifetimeSpinner.getDoubleValue() * 1000;
    }
    
    //----------------------------------------------------------------------------
    // Color editing
    //----------------------------------------------------------------------------
    
    private void editColor( ColorChip colorChip ) {
        
        _editColorChip = colorChip;

        Color initialColor = null;
        
        if ( colorChip == _deBroglieBrightnessPlusChip ) {
            initialColor = DeBroglieBrightnessNode.PLUS_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessZeroChip ) {
            initialColor = DeBroglieBrightnessNode.ZERO_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessMinusChip ) {
            initialColor = DeBroglieBrightnessNode.MINUS_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessMagnitudePlusChip ) {
            initialColor = DeBroglieBrightnessMagnitudeNode.PLUS_COLOR;
        }
        else if ( colorChip == _deBroglieBrightnessMagnitudeZeroChip ) {
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
        
        if ( _editColorChip == _deBroglieBrightnessPlusChip ) {
            DeBroglieBrightnessNode.PLUS_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessZeroChip ) {
            DeBroglieBrightnessNode.ZERO_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessMinusChip ) {
            DeBroglieBrightnessNode.MINUS_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessMagnitudePlusChip ) {
            DeBroglieBrightnessMagnitudeNode.MAX_COLOR = color;
        }
        else if ( _editColorChip == _deBroglieBrightnessMagnitudeZeroChip ) {
            DeBroglieBrightnessMagnitudeNode.MIN_COLOR = color;
        }
        else {
            throw new UnsupportedOperationException( "unsupported ColorChip" );
        }
    }
}
