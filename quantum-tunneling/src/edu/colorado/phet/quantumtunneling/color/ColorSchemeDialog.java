/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.color;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.module.QTModule;


/**
 * ColorSchemeDialog is the dialog used to edit the color scheme.
 * <p>
 * NOTE: If you add a new property to the color scheme, you'll need to 
 * add support for that property in these methods:
 * <ul>
 * <li>createInputPanel
 * <li>editColor
 * <li>handleColorChange
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorSchemeDialog extends JDialog implements ColorChooserFactory.Listener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int COLOR_CHIP_WIDTH = 30;
    private static final int COLOR_CHIP_HEIGHT = 30;
    private static final Stroke COLOR_CHIP_STROKE = new BasicStroke( 1f );
    private static final Color COLOR_CHIP_BORDER_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Frame _parent;
    private QTModule _module;
    private QTColorScheme _scheme;
    private QTColorScheme _restoreScheme;
    private JLabel _currentChip;
    private JLabel _chartChip, _ticksChip, _gridlinesChip, _annotationChip, _regionMarkersChip;
    private JLabel _totalEnergyChip, _potentialEnergyChip;
    private JLabel _realChip, _imaginaryChip, _magnitudeChip, _probabilityDensityChip;
    private JButton _okButton, _cancelButton;
    private JDialog _colorChooserDialog;
    
    /**
     * Sole constructor.
     * 
     * @param app the application
     */
    public ColorSchemeDialog( QTModule module, QTColorScheme scheme ) {
        super( PhetApplication.instance().getPhetFrame() );
        super.setTitle( SimStrings.get( "title.colorScheme" ) );
        super.setModal( false );
        super.setResizable( false );
        _parent = PhetApplication.instance().getPhetFrame();
        _module = module;
        _scheme = scheme;
        _restoreScheme = new QTColorScheme( scheme );
        createUI();
        setLocationRelativeTo( _parent );
    }
    
    /*
     * Creates the user interface for the dialog.
     */
    private void createUI() {
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( inputPanel, BorderLayout.CENTER );
        JPanel panel2 = new JPanel( new BorderLayout() );
        panel2.add( new JSeparator(), BorderLayout.NORTH );
        panel2.add( actionsPanel, BorderLayout.SOUTH );
        panel.add( panel2, BorderLayout.SOUTH );

        this.getContentPane().add( panel );
        this.pack();
    }
    
    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        MouseInputListener listener = new MouseInputAdapter() {
            public void mouseClicked( MouseEvent event ) {
                if ( event.getSource() instanceof JLabel ) {
                    editColor( (JLabel) event.getSource() );
                }
            }
        };
        
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( inputPanelLayout );
        int row = 0;
        
        // Chart
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.chart" ) );
            _chartChip = new JLabel();
            setColor( _chartChip, _scheme.getChartColor() );
            _chartChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _chartChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Ticks
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.ticks" ) );
            _ticksChip = new JLabel();
            setColor( _ticksChip, _scheme.getTickColor() );
            _ticksChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _ticksChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Gridlines
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.gridlines" ) );
            _gridlinesChip = new JLabel();
            setColor( _gridlinesChip, _scheme.getGridlineColor() );
            _gridlinesChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _gridlinesChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Annotations
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.annotations" ) );
            _annotationChip = new JLabel();
            setColor( _annotationChip, _scheme.getAnnotationColor() );
            _annotationChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _annotationChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Region Markers
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.regionMarkers" ) );
            _regionMarkersChip = new JLabel();
            setColor( _regionMarkersChip, _scheme.getRegionMarkerColor() );
            _regionMarkersChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _regionMarkersChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Vertical space
        inputPanelLayout.addComponent( createVerticalStrut( 6 ), row, 0 );
        row++;
        
        // Total Energy
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.totalEnergy" ) );
            _totalEnergyChip = new JLabel();
            setColor( _totalEnergyChip, _scheme.getTotalEnergyColor() );
            _totalEnergyChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _totalEnergyChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Potential Energy
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.potentialEnergy" ) );
            _potentialEnergyChip = new JLabel();
            setColor( _potentialEnergyChip, _scheme.getPotentialEnergyColor() );
            _potentialEnergyChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _potentialEnergyChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Vertical space
        inputPanelLayout.addComponent( createVerticalStrut( 6 ), row, 0 );
        row++;
        
        // Real
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.real" ) );
            _realChip = new JLabel();
            setColor( _realChip, _scheme.getRealColor() );
            _realChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _realChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Imaginary
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.imaginary" ) );
            _imaginaryChip = new JLabel();
            setColor( _imaginaryChip, _scheme.getImaginaryColor() );
            _imaginaryChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _imaginaryChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Magnitude
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.magnitude" ) );
            _magnitudeChip = new JLabel();
            setColor( _magnitudeChip, _scheme.getMagnitudeColor() );
            _magnitudeChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _magnitudeChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Probability Density
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.probabilityDensity" ) );
            _probabilityDensityChip = new JLabel();
            setColor( _probabilityDensityChip, _scheme.getProbabilityDensityColor() );
            _probabilityDensityChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _probabilityDensityChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        return inputPanel;
    }
    
    /*
     * Creates the dialog's actions panel, consisting of OK and Cancel buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _okButton = new JButton( SimStrings.get( "choice.ok" ) );
        _okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        });

        _cancelButton = new JButton( SimStrings.get( "choice.cancel" ) );
        _cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                restoreColors();
                dispose();
            }
        });

        JPanel innerPanel = new JPanel( new GridLayout( 1, 2, 10, 0 ) );
        innerPanel.add( _okButton );
        innerPanel.add( _cancelButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( innerPanel );

        return actionPanel;
    }

    /*
     * Creates a vertical strut that is a JComponent.
     * 
     * @param height
     * @return
     */
    private JComponent createVerticalStrut( int height ) {
        JPanel panel = new JPanel();
        panel.add( Box.createVerticalStrut( height ) );
        return panel;
    }
    
    /*
     * Edits one of the colors.
     * 
     * @param order
     */
    private void editColor( JLabel colorChip ) {
        
        _currentChip = colorChip;

        String titlePrefix = null;
        Color initialColor = null;
        
        if ( _currentChip == _chartChip ) {
            titlePrefix = SimStrings.get( "label.color.chart" );
            initialColor = _scheme.getChartColor();
        }
        else if ( _currentChip == _ticksChip ) {
            titlePrefix = SimStrings.get( "label.color.ticks" );
            initialColor = _scheme.getTickColor();
        }
        else if ( _currentChip == _gridlinesChip ) {
            titlePrefix = SimStrings.get( "label.color.gridlines" );
            initialColor = _scheme.getGridlineColor();
        }
        else if ( _currentChip == _annotationChip ) {
            titlePrefix = SimStrings.get( "label.color.annotations" );
            initialColor = _scheme.getAnnotationColor();
        }
        else if ( _currentChip == _regionMarkersChip ) {
            titlePrefix = SimStrings.get( "label.color.regionMarkers" );
            initialColor = _scheme.getRegionMarkerColor();
        }
        else if ( _currentChip == _totalEnergyChip ) {
            titlePrefix = SimStrings.get( "label.color.totalEnergy" );
            initialColor = _scheme.getTotalEnergyColor();
        }
        else if ( _currentChip == _potentialEnergyChip ) {
            titlePrefix = SimStrings.get( "label.color.potentialEnergy" );
            initialColor = _scheme.getPotentialEnergyColor();
        }
        else if ( _currentChip == _realChip ) {
            titlePrefix = SimStrings.get( "label.color.real" );
            initialColor = _scheme.getRealColor();
        }
        else if ( _currentChip == _imaginaryChip ) {
            titlePrefix = SimStrings.get( "label.color.imaginary" );
            initialColor = _scheme.getImaginaryColor();
        }
        else if ( _currentChip == _magnitudeChip ) {
            titlePrefix = SimStrings.get( "label.color.magnitude" );
            initialColor = _scheme.getMagnitudeColor();
        }
        else if ( _currentChip == _probabilityDensityChip ) {
            titlePrefix = SimStrings.get( "label.color.probabilityDensity" );
            initialColor = _scheme.getProbabilityDensityColor();
        }
        else {
            throw new IllegalStateException( "unsupported color scheme property" );
        }
        
        String title = titlePrefix + " " + SimStrings.get( "title.chooseColor" );
        
        closeColorChooser();
        _colorChooserDialog = ColorChooserFactory.createDialog( title, _parent, initialColor, this );
        _colorChooserDialog.show();
    }
    
    /*
     * Resets the color scheme.
     */
    private void restoreColors() {
        _module.setColorScheme( _restoreScheme );
        _scheme = new QTColorScheme( _restoreScheme );
    }
    
    /*
     * Closes the color chooser dialog.
     */
    private void closeColorChooser() {
        if ( _colorChooserDialog != null ) {
            _colorChooserDialog.dispose();
        }
    }
    
    /*
     * Sets the color of a color bar.
     * 
     * @param colorBar
     * @param color
     */
    private void setColor( JLabel colorBar, Color color ) {
        Rectangle r = new Rectangle( 0, 0, COLOR_CHIP_WIDTH, COLOR_CHIP_HEIGHT );
        BufferedImage image = new BufferedImage( r.width, r.height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( color );
        g2.fill( r );
        g2.setStroke( COLOR_CHIP_STROKE );
        g2.setColor( COLOR_CHIP_BORDER_COLOR );
        g2.draw( r );
        colorBar.setIcon( new ImageIcon( image ) );
    }

    //----------------------------------------------------------------------------
    // JDialog overrides
    //----------------------------------------------------------------------------
    
    /**
     * When this dialog is disposed, also dispose of any dialogs that it has created.
     */
    public void dispose() {
        closeColorChooser();
        super.dispose();
    }
    
    //----------------------------------------------------------------------------
    // ColorChooserFactory.Listener implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.faraday.control.ColorChooserFactory.Listener#colorChanged(java.awt.Color)
     */
    public void colorChanged( Color color ) {
        handleColorChange( color ); 
    }

    /*
     * @see edu.colorado.phet.faraday.control.ColorChooserFactory.Listener#ok(java.awt.Color)
     */
    public void ok( Color color ) {
        handleColorChange( color );  
    }

    /*
     * @see edu.colorado.phet.faraday.control.ColorChooserFactory.Listener#cancelled(java.awt.Color)
     */
    public void cancelled( Color originalColor ) {
        handleColorChange( originalColor );
    }
    
    /*
     * Handles a color change.
     * 
     * @param color
     */
    private void handleColorChange( Color color ) {

        // Set the color chip's color...
        setColor( _currentChip, color );
        
        // Set the color property in the color scheme...
        if ( _currentChip == _chartChip ) {
            _scheme.setChartColor( color );
        }
        else if ( _currentChip == _ticksChip ) {
            _scheme.setTickColor( color );
        }
        else if ( _currentChip == _gridlinesChip ) {
            _scheme.setGridlineColor( color );
        }
        else if ( _currentChip == _annotationChip ) {
            _scheme.setAnnotationColor( color );
        }
        else if ( _currentChip == _regionMarkersChip ) {
            _scheme.setRegionMarkerColor( color );
        }
        else if ( _currentChip == _totalEnergyChip ) {
            _scheme.setTotalEnergyColor( color );
        }
        else if ( _currentChip == _potentialEnergyChip ) {
            _scheme.setPotentialEnergyColor( color );
        }
        else if ( _currentChip == _realChip ) {
            _scheme.setRealColor( color );
        }
        else if ( _currentChip == _imaginaryChip ) {
            _scheme.setImaginaryColor( color );
        }
        else if ( _currentChip == _magnitudeChip ) {
            _scheme.setMagnitudeColor( color );
        }
        else if ( _currentChip == _probabilityDensityChip ) {
            _scheme.setProbabilityDensityColor( color );
        }
        else {
            throw new IllegalStateException( "unsupported color scheme property" );
        }

        // Tell the module that the color scheme has changed...
        _module.setColorScheme( _scheme );
    }
}
