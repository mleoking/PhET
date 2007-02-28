/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.color;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.boundstates.BSAbstractApplication;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSColorSchemeDialog is the dialog used to edit the color scheme.
 * <p>
 * NOTE: If you add a new property to the color scheme, you'll need to 
 * add support for that property in these methods:
 * <ul>
 * <li>createInputPanel
 * <li>editColor
 * <li>handleColorChange
 * <li>restoreColors
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSColorSchemeDialog extends JDialog implements ColorChooserFactory.Listener {

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
    private BSAbstractApplication _app;
    private BSColorScheme _scheme;
    private BSColorScheme _restoreScheme;
    
    private JLabel _currentChip;
    private JLabel _chartChip, _ticksChip, _gridlinesChip;
    private JLabel _eigenstateNormalChip, _eigenstateHiliteChip, _eigenstateSelectionChip, _potentialEnergyChip;
    private JLabel _realChip, _imaginaryChip, _magnitudeChip;
    private JLabel _magnifyingGlassBezelChip, _magnifyingGlassHandleChip;
    private JLabel _dragHandleChip, _dragHandleHiliteChip, _dragHandleValueChip, _dragHandleMarkersChip;
    
    private JButton _okButton, _cancelButton;
    private JDialog _colorChooserDialog;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param app the application
     */
    public BSColorSchemeDialog( BSAbstractApplication app, BSColorScheme scheme ) {
        super( PhetApplication.instance().getPhetFrame() );
        super.setTitle( SimStrings.get( "title.colorScheme" ) );
        super.setModal( false );
        super.setResizable( false );
        _parent = PhetApplication.instance().getPhetFrame();
        _app = app;
        _scheme = scheme;
        _restoreScheme = new BSColorScheme( scheme );
        createUI();
        setLocationRelativeTo( _parent );
    }
   
    //----------------------------------------------------------------------------
    // User interface construction
    //----------------------------------------------------------------------------
    
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
        
        // Font used for titles
        Font titleFont = null;
        {
            JLabel label = new JLabel();
            Font defaultFont = label.getFont();
            titleFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() );   
        }
        
        // Chart section
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.chartSection" ) );
            label.setFont( titleFont );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.WEST );
            row++;
        }
        
        // Chart background
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.chartBackground" ) );
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
        
        // Horizontal separator
        inputPanelLayout.addFilledComponent( new JSeparator(), row, 0, 2, 1, GridBagConstraints.HORIZONTAL );
        row++;
        
        // Energy Plots section
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.energySection" ) );
            label.setFont( titleFont );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.WEST );
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
        
        // Eigenstate normal
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.eigenstateNormal" ) );
            _eigenstateNormalChip = new JLabel();
            setColor( _eigenstateNormalChip, _scheme.getEigenstateNormalColor() );
            _eigenstateNormalChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _eigenstateNormalChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Eigenstate hilite
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.eigenstateHilite" ) );
            _eigenstateHiliteChip = new JLabel();
            setColor( _eigenstateHiliteChip, _scheme.getEigenstateHiliteColor() );
            _eigenstateHiliteChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _eigenstateHiliteChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Eigenstate selection
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.eigenstateSelection" ) );
            _eigenstateSelectionChip = new JLabel();
            setColor( _eigenstateSelectionChip, _scheme.getEigenstateSelectionColor() );
            _eigenstateSelectionChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _eigenstateSelectionChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Horizontal separator
        inputPanelLayout.addFilledComponent( new JSeparator(), row, 0, 2, 1, GridBagConstraints.HORIZONTAL );
        row++;
        
        // Wave Function Plots section
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.waveFunctionSection" ) );
            label.setFont( titleFont );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.WEST );
            row++;
        }
        
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
        
        // Horizontal separator
        inputPanelLayout.addFilledComponent( new JSeparator(), row, 0, 2, 1, GridBagConstraints.HORIZONTAL );
        row++;
        
        // Magnifying Glass section
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.magnifyingGlassSection" ) );
            label.setFont( titleFont );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.WEST );
            row++;
        }
        
        // Magnifying Glass bezel
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.magnifyingGlassBezel" ) );
            _magnifyingGlassBezelChip = new JLabel();
            setColor( _magnifyingGlassBezelChip, _scheme.getMagnifyingGlassBezelColor() );
            _magnifyingGlassBezelChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _magnifyingGlassBezelChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Magnifying Glass handle
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.magnifyingGlassHandle" ) );
            _magnifyingGlassHandleChip = new JLabel();
            setColor( _magnifyingGlassHandleChip, _scheme.getMagnifyingGlassHandleColor() );
            _magnifyingGlassHandleChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _magnifyingGlassHandleChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Horizontal separator
        inputPanelLayout.addFilledComponent( new JSeparator(), row, 0, 2, 1, GridBagConstraints.HORIZONTAL );
        row++;
        
        // Drag Handles section
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.dragHandleSection" ) );
            label.setFont( titleFont );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.WEST );
            row++;
        }
        
        // Drag handle "normal" color
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.dragHandle" ) );
            _dragHandleChip = new JLabel();
            setColor( _dragHandleChip, _scheme.getDragHandleColor() );
            _dragHandleChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _dragHandleChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Drag handle "hilite" color
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.dragHandleHilite" ) );
            _dragHandleHiliteChip = new JLabel();
            setColor( _dragHandleHiliteChip, _scheme.getDragHandleHiliteColor() );
            _dragHandleHiliteChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _dragHandleHiliteChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Drag handle "value" color
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.dragHandleValue" ) );
            _dragHandleValueChip = new JLabel();
            setColor( _dragHandleValueChip, _scheme.getDragHandleValueColor() );
            _dragHandleValueChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _dragHandleValueChip, row, 1, GridBagConstraints.WEST );
            row++;
        }
        
        // Drag handle "markers" color
        {
            JLabel label = new JLabel( SimStrings.get( "label.color.dragHandleMarkers" ) );
            _dragHandleMarkersChip = new JLabel();
            setColor( _dragHandleMarkersChip, _scheme.getDragHandleMarkersColor() );
            _dragHandleMarkersChip.addMouseListener( listener );
            inputPanelLayout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addAnchoredComponent( _dragHandleMarkersChip, row, 1, GridBagConstraints.WEST );
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
    
    //----------------------------------------------------------------------------
    // Color editing
    //----------------------------------------------------------------------------
    
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
            titlePrefix = SimStrings.get( "label.color.chartBackground" );
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
        else if ( _currentChip == _eigenstateNormalChip ) {
            titlePrefix = SimStrings.get( "label.color.eigenstateNormal" );
            initialColor = _scheme.getEigenstateNormalColor();
        }
        else if ( _currentChip == _eigenstateHiliteChip ) {
            titlePrefix = SimStrings.get( "label.color.eigenstateHilite" );
            initialColor = _scheme.getEigenstateHiliteColor();
        }
        else if ( _currentChip == _eigenstateSelectionChip ) {
            titlePrefix = SimStrings.get( "label.color.eigenstateSelection" );
            initialColor = _scheme.getEigenstateSelectionColor();
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
        else if ( _currentChip == _magnifyingGlassBezelChip ) {
            titlePrefix = SimStrings.get( "label.color.magnifyingGlassBezel" );
            initialColor = _scheme.getMagnifyingGlassBezelColor();
        }
        else if ( _currentChip == _magnifyingGlassHandleChip ) {
            titlePrefix = SimStrings.get( "label.color.magnifyingGlassHandle" );
            initialColor = _scheme.getMagnifyingGlassHandleColor();
        }
        else if ( _currentChip == _dragHandleChip ) {
            titlePrefix = SimStrings.get( "label.color.dragHandle" );
            initialColor = _scheme.getDragHandleColor(); 
        }
        else if ( _currentChip == _dragHandleHiliteChip ) {
            titlePrefix = SimStrings.get( "label.color.dragHandleHilite" );
            initialColor = _scheme.getDragHandleHiliteColor(); 
        }
        else if ( _currentChip == _dragHandleValueChip ) {
            titlePrefix = SimStrings.get( "label.color.dragHandleValue" );
            initialColor = _scheme.getDragHandleValueColor(); 
        }
        else if ( _currentChip == _dragHandleMarkersChip ) {
            titlePrefix = SimStrings.get( "label.color.dragHandleMarkers" );
            initialColor = _scheme.getDragHandleMarkersColor(); 
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
        _app.setColorScheme( _restoreScheme );
        _scheme.copy( _restoreScheme );
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
        else if ( _currentChip == _eigenstateNormalChip ) {
            _scheme.setEigenstateNormalColor( color );
        }
        else if ( _currentChip == _eigenstateHiliteChip ) {
            _scheme.setEigenstateHiliteColor( color );
        }
        else if ( _currentChip == _eigenstateSelectionChip ) {
            _scheme.setEigenstateSelectionColor( color );
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
        else if ( _currentChip == _magnifyingGlassBezelChip ) {
            _scheme.setMagnifyingGlassBezelColor( color );
        }
        else if ( _currentChip == _magnifyingGlassHandleChip ) {
            _scheme.setMagnifyingGlassHandleColor( color );
        }
        else if ( _currentChip == _dragHandleChip ) {
            _scheme.setDragHandleColor( color );
        }
        else if ( _currentChip == _dragHandleHiliteChip ) {
            _scheme.setDragHandleHiliteColor( color );
        }
        else if ( _currentChip == _dragHandleValueChip ) {
            _scheme.setDragHandleValueColor( color );
        }
        else if ( _currentChip == _dragHandleMarkersChip ) {
            _scheme.setDragHandleMarkersColor( color );
        }
        else {
            throw new IllegalStateException( "unsupported color scheme property" );
        }

        // Tell the application that the color scheme has changed...
        _app.setColorScheme( _scheme );
    }
}
