/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.simulations.ISimulation;
import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;
import edu.colorado.phet.translationutility.userinterface.FindDialog.FindListener;
import edu.colorado.phet.translationutility.userinterface.ToolBar.ToolBarListener;
import edu.colorado.phet.translationutility.util.ExceptionHandler;
import edu.colorado.phet.translationutility.util.FontFactory;

/**
 * MainFrame is the application's main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MainFrame extends JFrame implements ToolBarListener, FindListener {
    
    private static final String CONFIRM_OVERWRITE_TITLE = TUResources.getString( "title.confirmOverwrite" );
    private static final String CONFIRM_OVERWRITE_MESSAGE = TUResources.getString( "message.confirmOverwrite" );
    private static final String SUBMIT_MESSAGE = TUResources.getString( "message.submit" );
    private static final String SUBMIT_TITLE = TUResources.getString( "title.submitDialog" );
    private static final String HELP_TITLE = TUResources.getString( "title.help" );
    private static final String HELP_MESSAGE = TUResources.getString( "help.translation" );
    
    private final ISimulation _simulation;
    private final String _targetLanguageCode;
    private final String _saveDirName;
    
    private final TranslationPanel _translationPanel;
    private File _currentDirectory;
    private FindDialog _findDialog;
    private String _previousFindText;
    
    /**
     * Constructor.
     * 
     * @param simulation
     * @param sourceLanguageCode
     * @param targetLanguageCode
     * @param saveDirName
     */
    public MainFrame( ISimulation simulation, String sourceLanguageCode, String targetLanguageCode, String saveDirName ) {
        super( TUResources.getTitle() );
        
        _simulation = simulation;
        _targetLanguageCode = targetLanguageCode;
        _saveDirName = saveDirName;
        
        _currentDirectory = null;
        _findDialog = null;
        _previousFindText = null;
        
        setJMenuBar( new MenuBar() );
        
        // Tool Bar
        ToolBar toolBar = new ToolBar();
        toolBar.addToolBarListener( this );
        
        // Translation Panel
        Properties sourceProperties = null;
        Properties targetProperties = null;
        try {
            sourceProperties = _simulation.getStrings( sourceLanguageCode );
            targetProperties = _simulation.getStrings( targetLanguageCode );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        if ( targetProperties == null ) {
            targetProperties = new Properties();
        }
        _translationPanel = new TranslationPanel( _simulation.getProjectName(), sourceLanguageCode, sourceProperties, targetLanguageCode, targetProperties );
        JScrollPane scrollPane = new JScrollPane( _translationPanel );
        
        // make Component with focus visible in the scroll pane
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent e ) {
                if ( "focusOwner".equals( e.getPropertyName() ) ) {
                    Component focusedComponent = getFocusOwner();
                    if ( focusedComponent != null ) {
                        _translationPanel.scrollRectToVisible( focusedComponent.getBounds( null ) );
                    }
                }
            }
        } );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 0, 10, 10, 10 ) );
        panel.setLayout( new BorderLayout() );
        panel.add( toolBar, BorderLayout.NORTH );
        panel.add( scrollPane, BorderLayout.CENTER );
        getContentPane().add( panel );
        pack();

        fixFrameBounds();
    }
    
    /*
     * Fixes various problems with the bounds of the main frame.
     */
    private void fixFrameBounds() {
        
        //WORKAROUND: decrease the height to account for Windows task bar
        if ( PhetUtilities.isWindows() ) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int taskBarHeight = 200;
            int overlap = (int) ( getBounds().getHeight() - ( screenSize.getHeight() - taskBarHeight ) );
            if ( overlap > 0 ) {
                setBounds( (int) getBounds().getX(), (int) getBounds().getY(), 
                        (int) getBounds().getWidth(), (int) getBounds().getHeight() - overlap );
            }
        }
        
        //WORKAROUND: increase the width so we don't get a horizontal scrollbar
        setBounds( (int) getBounds().getX(), (int) getBounds().getY(),
                (int) getBounds().getWidth() + 30, (int) getBounds().getHeight() );
        
        // center on the screen
        SwingUtils.centerWindowOnScreen( this );
    }

    //----------------------------------------------------------------------------
    // ToolBarListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when the ToolBar's Test button is pressed.
     * Add the current translations to a temporary JAR file, then runs that JAR file.
     */
    public void handleTest() {
        Properties properties = _translationPanel.getTargetProperties();
        try {
            _simulation.testStrings( properties, _targetLanguageCode );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleNonFatalException( e );
        }
    }

    /**
     * Called when the ToolBar's Save button is pressed.
     * Opens a "save" file chooser that allows the user to save the current translations to a properties file.
     */
    public void handleSave() {
        JFileChooser chooser = new JFileChooser( _currentDirectory );
        int option = chooser.showSaveDialog( this );
        _currentDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            Properties properties = _translationPanel.getTargetProperties();
            File outFile = chooser.getSelectedFile();
            if ( outFile.exists() ) {
                Object[] args = { outFile.getAbsolutePath() };
                String message = MessageFormat.format( CONFIRM_OVERWRITE_MESSAGE, args );
                int selection = JOptionPane.showConfirmDialog( this, message, CONFIRM_OVERWRITE_TITLE, JOptionPane.YES_NO_OPTION );
                if ( selection != JOptionPane.YES_OPTION ) {
                    return;
                }
            }
            try {
                _simulation.saveStrings( properties, outFile );
            }
            catch ( SimulationException e ) {
                ExceptionHandler.handleNonFatalException( e );
            }
        }
    }

    /**
     * Called when the ToolBar's Load button is pressed.
     * Opens an "open" file chooser that allows the user to choose a properties file.
     * The contents of that properties file are loaded into the target text fields.
     */
    public void handleLoad() {
        JFileChooser chooser = new JFileChooser( _currentDirectory );
        int option = chooser.showOpenDialog( this );
        _currentDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            File inFile = chooser.getSelectedFile();
            Properties properties = null;
            try {
                properties = _simulation.loadStrings( inFile );
            }
            catch ( SimulationException e ) {
                ExceptionHandler.handleNonFatalException( e );
            }
            _translationPanel.setTargetProperties( properties );
        }
    }

    /**
     * Called when the ToolBar's Submit button is pressed.
     * Saves the currrent translations to a properties file, then notifies the user of what to do.
     */
    public void handleSubmit() {
        
        Properties properties = _translationPanel.getTargetProperties();
        
        // export properties to a file
        String baseName = _simulation.getSubmitBasename( _targetLanguageCode );
        String fileName = _saveDirName + File.separatorChar + baseName;
        File outFile = new File( fileName );
        if ( outFile.exists() ) {
            Object[] args = { fileName };
            String message = MessageFormat.format( CONFIRM_OVERWRITE_MESSAGE, args );
            int selection = JOptionPane.showConfirmDialog( this, message, CONFIRM_OVERWRITE_TITLE, JOptionPane.YES_NO_OPTION );
            if ( selection != JOptionPane.YES_OPTION ) {
                return;
            }
        }
        
        try {
            _simulation.saveStrings( properties, outFile );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleNonFatalException( e );
        }
        
        // Use a JEditorPane so that it's possible to copy-paste the filename and email address.
        JEditorPane submitText = new JEditorPane();
        submitText.setEditorKit( new HTMLEditorKit() );
        Object[] args = { fileName };
        String html = MessageFormat.format( SUBMIT_MESSAGE, args );
        submitText.setText( html );
        submitText.setEditable( false );
        submitText.setBackground( new JLabel().getBackground() );
        submitText.setFont( new JLabel().getFont() );
        
        JOptionPane.showMessageDialog( this, submitText, SUBMIT_TITLE, JOptionPane.INFORMATION_MESSAGE );
    }
    
    /**
     * Called when the ToolBar's Find button is pressed. 
     * Opens a Find dialog.
     */
    public void handleFind() {
        if ( _findDialog == null ) {
            _findDialog = new FindDialog( this, _previousFindText, FontFactory.createFont( _targetLanguageCode ) );
            _findDialog.addFindListener( this );
            _findDialog.addWindowListener( new WindowAdapter() {

                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _findDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    _previousFindText = _findDialog.getText();
                    _findDialog = null;
                }
            } );
            SwingUtils.centerDialogInParent( _findDialog );
            _findDialog.setVisible( true );
        }
    }

    /**
     * Called when the ToolBar's Help button is pressed.
     * Opens a dialog that contains help information.
     */
    public void handleHelp() {
        JOptionPane.showMessageDialog( this, HELP_MESSAGE, HELP_TITLE, JOptionPane.INFORMATION_MESSAGE );
    }
    
    //----------------------------------------------------------------------------
    // FindListener implementation
    //----------------------------------------------------------------------------

    /**
     * Called with the Find dialog's Next button is pressed.
     * 
     * @param text
     */
    public void findNext( String text ) {
        _translationPanel.findNext( text );
    }

    /**
     * Called when the Find dialog's Previous button is pressed.
     * 
     * @param text
     */
    public void findPrevious( String text ) {
        _translationPanel.findPrevious( text );
    }
}
