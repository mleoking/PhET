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
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.view.HarmonicColors;


/**
 * HarmonicColorsDialog is the dialog for changing the colors
 * used to draw harmonic waveforms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicColorsDialog extends JDialog implements ColorChooserFactory.Listener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final int COLOR_BAR_WIDTH = 200;
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
    private int _editIndex;

    /**
     * Sole constructor.
     *
     * @param app the application
     */
    public HarmonicColorsDialog( PhetApplication app ) {
        super( app.getPhetFrame() );
        _app = app;
        super.setTitle( FourierResources.getString( "HarmonicColorsDialog.title" ) );
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
        this.setLocationRelativeTo( parent );
    }

    /**
     * Creates the dialog's input panel.
     *
     * @return the input panel
     */
    private JPanel createInputPanel() {

        String editString = FourierResources.getString( "HarmonicColorsDialog.edit" );
        Stroke colorBarStroke = new BasicStroke( 1f );

        JPanel inputPanel = new JPanel();
        EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( inputPanelLayout );
        int row = 0;

        int numberOfHarmonics = HarmonicColors.getInstance().getNumberOfColors();
        _restoreColors = new Color[ numberOfHarmonics ];
        _colorBars = new JLabel[ numberOfHarmonics ];

        for ( int i = 0; i < numberOfHarmonics; i++ ) {

            JPanel colorBarPanel = new JPanel();

            JLabel numberLabel = new JLabel( String.valueOf( i+1 ) );

            Color harmonicColor = HarmonicColors.getInstance().getColor( i );
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

            EasyGridBagLayout layout = new EasyGridBagLayout( colorBarPanel );
            colorBarPanel.setLayout( layout );
            int column = 0;
            layout.addAnchoredComponent( numberLabel, 0, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( colorBar, 0, column++, GridBagConstraints.WEST );

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

        _okButton = new JButton( FourierResources.getString( "HarmonicColorsDialog.ok" ) );
        _okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        });

        _cancelButton = new JButton( FourierResources.getString( "HarmonicColorsDialog.cancel" ) );
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

    /**
     * Edits the color of one harmonic.
     *
     * @param order
     */
    private void editColor( int order ) {
        _editIndex = order;
        Object[] args = { new Integer( order + 1 ) };
        String format = FourierResources.getString( "HarmonicColors.colorChooser.title" );
        String title = MessageFormat.format( format, args );
        Component parent = _app.getPhetFrame();
        Color initialColor = HarmonicColors.getInstance().getColor( order );
        JDialog dialog = ColorChooserFactory.createDialog( title, parent, initialColor, this );
        dialog.show();
    }

    /**
     * Sets all of the harmonic colors.
     */
    private void restoreColors() {
        int numberOfHarmonics = HarmonicColors.getInstance().getNumberOfColors();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            if ( ! HarmonicColors.getInstance().getColor( i ).equals( _restoreColors[i] ) ) {
                HarmonicColors.getInstance().setColor( i, _restoreColors[i] );
            }
        }
    }

    /**
     * Sets the color of a color bar.
     *
     * @param colorBar
     * @param color
     */
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
        setColor( _colorBars[ _editIndex ], color );
        HarmonicColors.getInstance().setColor( _editIndex, color );
    }
}
