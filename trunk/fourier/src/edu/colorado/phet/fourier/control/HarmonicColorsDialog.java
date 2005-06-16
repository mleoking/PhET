/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.util.FourierUtils;


/**
 * HarmonicColorsDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicColorsDialog extends JDialog implements ActionListener, ColorChooserFactory.Listener {

    private static final int COLOR_BAR_WIDTH = 100;
    private static final int COLOR_BAR_HEIGHT = 20;
    private static final Stroke COLOR_BAR_STROKE = new BasicStroke( 1f );
    private static final Color COLOR_BAR_BORDER_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetApplication _app;
    private JButton _okButton, _cancelButton;
    private Color[] _restoreColors;
    private JLabel[] _colorBars;
    private JButton[] _editButtons;
    private int _editIndex;
    
    /**
     * Sole constructor.
     * 
     * @param app the application
     */
    public HarmonicColorsDialog( PhetApplication app ) {
        super( app.getPhetFrame() );
        _app = app;
        super.setTitle( SimStrings.get( "HarmonicColorsDialog.title" ) );
        super.setModal( false );
        super.setResizable( false );
        
        createUI( app.getPhetFrame() );
    }
    
    /**
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( inputPanel, BorderLayout.CENTER );
        panel.add( actionsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( panel );
        this.pack();
        this.setResizable( false );
        this.setLocationRelativeTo( parent );
    }
    
    /**
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        String editString = SimStrings.get( "HarmonicColorsDialog.edit" );
        Stroke colorBarStroke = new BasicStroke( 1f );
        
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( inputPanelLayout );
        int row = 0;
        
        int numberOfHarmonics = FourierUtils.getNumberOfHarmonicColors();
        _restoreColors = new Color[ numberOfHarmonics ];
        _colorBars = new JLabel[ numberOfHarmonics ];
        _editButtons = new JButton[ numberOfHarmonics ];
        
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            
            JPanel colorBarPanel = new JPanel();
            
            JLabel numberLabel = new JLabel( String.valueOf( i+1 ) );
            
            Color harmonicColor = FourierUtils.getHarmonicColor( i );
            _restoreColors[i] = harmonicColor;
            
            JLabel colorBar = new JLabel();
            _colorBars[i] = colorBar;
            setColor( colorBar, harmonicColor );
            colorBar.addMouseListener( new MouseInputAdapter() {
                public void mouseClicked( MouseEvent e ) {
                    for ( int i = 0; i < _colorBars.length; i++ ) {
                        if ( e.getSource() == _colorBars[i] ) {
                            editColor( i );
                        }
                    }
                }
            });
            
            JButton editButton = new JButton( editString );
            _editButtons[i] = editButton;
            editButton.addActionListener( this );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( colorBarPanel );
            colorBarPanel.setLayout( layout );
            int column = 0;
            layout.addAnchoredComponent( numberLabel, 0, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( colorBar, 0, column++, GridBagConstraints.WEST );
            layout.addAnchoredComponent( editButton, 0, column++, GridBagConstraints.WEST );
            
            inputPanelLayout.addAnchoredComponent( colorBarPanel, row++, 0, GridBagConstraints.EAST );
        }
        
        return inputPanel;
    }
    
    /** 
     * Creates the dialog's actions panel, consisting of OK and Cancel buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _okButton = new JButton( SimStrings.get( "HarmonicColorsDialog.ok" ) );
        _okButton.addActionListener( this );

        _cancelButton = new JButton( SimStrings.get( "HarmonicColorsDialog.cancel" ) );
        _cancelButton.addActionListener( this );

        JPanel innerPanel = new JPanel( new GridLayout( 1, 2, 10, 0 ) );
        innerPanel.add( _okButton );
        innerPanel.add( _cancelButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( innerPanel );

        return actionPanel;
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == _okButton ) {
            dispose();
        }
        else if ( e.getSource() == _cancelButton ) {
            restoreColors();
            dispose();
        }
        else { /* Edit button */
            for ( int i = 0; i < _editButtons.length; i++ ) {
                if ( e.getSource() == _editButtons[i] ) {
                    editColor( i );
                }
            }
        }
    }

    private void editColor( int index ) {
        _editIndex = index;
        String title = "Harmonic {0} Color"; ///XXX SimString and MessageFormat
        Component parent = _app.getPhetFrame();
        Color initialColor = FourierUtils.getHarmonicColor( index );
        JDialog dialog = ColorChooserFactory.createDialog( title, parent, initialColor, this );
        dialog.show();
    }
    
    /**
     * Sets all of the harmonic colors.
     */
    private void restoreColors() {
        int numberOfHarmonics = FourierUtils.getNumberOfHarmonicColors();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            FourierUtils.setHarmonicColor( i, _restoreColors[i] );
            //XXX tell the app to update all modules
        }
    }
    
    private void setColor( JLabel colorBar, Color color ) {
        Rectangle r = new Rectangle( 0, 0, COLOR_BAR_WIDTH, COLOR_BAR_HEIGHT );
        BufferedImage image = new BufferedImage( r.width, r.height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( color );
        g2.fill( r );
        g2.setStroke( COLOR_BAR_STROKE );
        g2.setColor( COLOR_BAR_BORDER_COLOR );
        g2.draw( r );
        colorBar.setIcon( new ImageIcon( image ) );
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
     * 
     */
    private void handleColorChange( Color color ) {
        setColor( _colorBars[_editIndex], color );
        FourierUtils.setHarmonicColor( _editIndex, color );
        //XXX tell the app to update all modules
        _app.getPhetFrame().repaint();
    }
}
