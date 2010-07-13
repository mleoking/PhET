/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.TUStrings;
import edu.colorado.phet.translationutility.simulations.ISimulation;
import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;
import edu.colorado.phet.translationutility.userinterface.FindDialog.FindListener;
import edu.colorado.phet.translationutility.userinterface.ToolBar.ToolBarListener;
import edu.colorado.phet.translationutility.util.ExceptionHandler;

/**
 * MainFrame is the application's main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MainFrame extends JFrame implements ToolBarListener, FindListener {
    
    private final ISimulation _simulation;
    private final Locale _targetLocale;
    private final String _submitDirName;
    
    private final TranslationPanel _translationPanel;
    private File _saveLoadDirectory;
    private FindDialog _findDialog;
    private String _previousFindText;

    private static final Logger LOGGER = Logger.getLogger( MainFrame.class.getCanonicalName() );
    
    /**
     * Constructor.
     * 
     * @param simulation
     * @param sourceLocale
     * @param targetLocale
     * @param jarDirName directory where the sim JAR file resides
     */
    public MainFrame( ISimulation simulation, Locale sourceLocale, Locale targetLocale, String jarDirName ) {
        super( TUResources.getTitle() );
        
        this._simulation = simulation;
        this._targetLocale = targetLocale;
        _submitDirName = jarDirName;  // save submitted files to the same dir as the sim JAR
        
        _saveLoadDirectory = new File( jarDirName ); // start save/load file chooser in same dir as the sim JAR
        _findDialog = null;
        _previousFindText = null;
        
        setJMenuBar( new MenuBar( this ) );
        
        // Tool Bar
        ToolBar toolBar = new ToolBar();
        toolBar.addToolBarListener( this );
        
        // Translation Panel
        Properties sourceProperties = null;
        Properties targetProperties = null;
        try {
            sourceProperties = _simulation.getStrings( sourceLocale );
            targetProperties = _simulation.getStrings( targetLocale );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        assert( sourceProperties != null );
        if ( targetProperties == null ) {
            targetProperties = new Properties();
        }
        _translationPanel = new TranslationPanel( this, _simulation.getProjectName(), sourceLocale, sourceProperties, targetLocale, targetProperties );
        final JScrollPane scrollPane = new JScrollPane( _translationPanel );
        fixScrollPane();

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
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        //WORKAROUND: decrease the height to account for Windows task bar
        if ( PhetUtilities.isWindows() ) {
            int overlap = getBounds().height - screenSize.height - TUConstants.WINDOWS_TASK_BAR_HEIGHT;
            if ( overlap > 0 ) {
                setBounds( getBounds().x, getBounds().y, getBounds().width, getBounds().height - overlap );
            }
        }
        
        //WORKAROUND: increase the width so we don't get a horizontal scrollbar
        setBounds( getBounds().x, getBounds().y, getBounds().width + 30, getBounds().height );
        
        // make sure we didn't exceed the screen width
        if ( getBounds().getWidth() > screenSize.getWidth() ) {
            setBounds( getBounds().x, getBounds().y, screenSize.width, getBounds().height );
        }
        
        // center on the screen
        SwingUtils.centerWindowOnScreen( this );
    }
    
    /*
     *  WORKAROUND for #1795.
     *  Uses a listener to make the top of the translation panel is visible in the scrollpane.
     *  Then deletes the listener.
     */
    private void fixScrollPane() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent e ) {
                if ( "focusOwner".equals( e.getPropertyName() ) ) {
                    Component focusedComponent = getFocusOwner();
                    if ( focusedComponent != null ) {
                        _translationPanel.scrollRectToVisible( focusedComponent.getBounds() );
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener( this );
                    }
                }
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener( listener );
    }
    
    public boolean hasUnsavedChanges() {
        return _translationPanel.hasUnsavedChanges();
    }
    
    public void markAllSaved() {
        _translationPanel.markAllSaved();
    }

    //----------------------------------------------------------------------------
    // ToolBarListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when the ToolBar's Test button is pressed.
     * Add the current translations to a temporary JAR file, then runs that JAR file.
     */
    public void handleTest() {
        
        // if there are validation errors, warn the user, and confirm that they want to continue with Test
        if ( _translationPanel.validateTargets() == false ) {
            String message = HTMLUtils.toHTMLString( TUStrings.ERROR_VALIDATION + "<br><br>" + TUStrings.CONFIRM_TEST );
            int response = JOptionPane.showConfirmDialog( this, message, TUStrings.ERROR_TITLE, JOptionPane.YES_NO_OPTION );
            if ( response != JOptionPane.OK_OPTION ) {
                return;
            }
        }
        
        Properties properties = _translationPanel.getTargetProperties();
        try {
            _simulation.testStrings( properties, _targetLocale );
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
        
        // if there are validation errors, warn the user, and confirm that they want to continue with Save
        if ( _translationPanel.validateTargets() == false ) {
            String message = HTMLUtils.toHTMLString( TUStrings.ERROR_VALIDATION + "<br><br>" + TUStrings.CONFIRM_SAVE );
            int response = JOptionPane.showConfirmDialog( this, message, TUStrings.ERROR_TITLE, JOptionPane.YES_NO_OPTION );
            if ( response != JOptionPane.OK_OPTION ) {
                return;
            }
        }
        
        File defaultFile = new File( _saveLoadDirectory, _simulation.getStringFileName( _targetLocale ) );
        JFileChooser chooser = _simulation.getStringFileChooser();
        chooser.setSelectedFile( defaultFile );
        int option = chooser.showSaveDialog( this );
        _saveLoadDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            File outFile = chooser.getSelectedFile();
            if ( outFile.getName().endsWith( _simulation.getStringFileSuffix() ) ) {
                // file suffix is appropriate, proceed with Save operation
                Properties properties = _translationPanel.getTargetProperties();
                if ( outFile.exists() ) {
                    // confirm that it's OK to overwrite a file that already exists
                    Object[] args = { outFile.getAbsolutePath() };
                    String message = MessageFormat.format( TUStrings.CONFIRM_OVERWRITE_MESSAGE, args );
                    int selection = JOptionPane.showConfirmDialog( this, message, TUStrings.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION );
                    if ( selection != JOptionPane.YES_OPTION ) {
                        return;
                    }
                }
                try {
                    _simulation.saveStrings( properties, outFile );
                    markAllSaved();
                }
                catch ( SimulationException e ) {
                    ExceptionHandler.handleNonFatalException( e );
                }
            }
            else {
                // file suffix is inappropriate, tell the user and try again
                LOGGER.fine( "Save attempted with bogus filename: " + outFile.getAbsolutePath() );
                Object[] args = { _simulation.getStringFileSuffix() };
                String message = MessageFormat.format( TUStrings.ERROR_SAVE_SUFFIX, args );
                JOptionPane.showMessageDialog( this, message, TUStrings.ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
                handleSave();
            }
        }
    }

    /**
     * Called when the ToolBar's Load button is pressed.
     * Opens an "open" file chooser that allows the user to choose a properties file.
     * The contents of that properties file are loaded into the target text fields.
     */
    public void handleLoad() {
        
        // check for unsaved changes
        if ( hasUnsavedChanges() ) {
            String message = HTMLUtils.toHTMLString( TUStrings.UNSAVED_CHANGES_MESSAGE + "<br><br>" + TUStrings.CONFIRM_LOAD );
            int response = JOptionPane.showConfirmDialog( this, message, TUStrings.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION );
            if ( response != JOptionPane.YES_OPTION ) {
                return;
            }
        }
        
        // load
        JFileChooser chooser = _simulation.getStringFileChooser();
        chooser.setCurrentDirectory( _saveLoadDirectory );
        int option = chooser.showOpenDialog( this );
        _saveLoadDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            File inFile = chooser.getSelectedFile();
            Properties properties = new Properties();
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
        
        // if there are validation errors, warn the user, and prevent them from sending to PhET
        if ( _translationPanel.validateTargets() == false ) {
            String message = HTMLUtils.toHTMLString( TUStrings.ERROR_VALIDATION + "<br><br>" + TUStrings.CANNOT_SEND_ERRORS_MESSAGE );
            JOptionPane.showMessageDialog( this, message, "Error", JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        Properties properties = _translationPanel.getTargetProperties();
        
        // export properties to a file
        File outFile = new File( _submitDirName, _simulation.getStringFileName( _targetLocale ) );
        if ( outFile.exists() ) {
            Object[] args = { outFile.getAbsolutePath() };
            String message = MessageFormat.format( TUStrings.CONFIRM_OVERWRITE_MESSAGE, args );
            int selection = JOptionPane.showConfirmDialog( this, message, TUStrings.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION );
            if ( selection != JOptionPane.YES_OPTION ) {
                return;
            }
        }
        LOGGER.fine( "submit is saving to " + outFile.getAbsolutePath() );
        
        try {
            _simulation.saveStrings( properties, outFile );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleNonFatalException( e );
        }
        
        // Use a JEditorPane so that it's possible to copy-paste the filename and email address.
        JEditorPane submitText = new JEditorPane();
        submitText.setEditorKit( new HTMLEditorKit() );
        String html = MessageFormat.format( TUStrings.SUBMIT_MESSAGE, outFile.getAbsolutePath(), TUConstants.PHETHELP_EMAIL );
        submitText.setText( html );
        submitText.setEditable( false );
        submitText.setBackground( new JLabel().getBackground() );
        submitText.setFont( new JLabel().getFont() );
        
        JOptionPane.showMessageDialog( this, submitText, TUStrings.SUBMIT_TITLE, JOptionPane.INFORMATION_MESSAGE );
        markAllSaved();
    }
    
    /**
     * Called when the ToolBar's Find button is pressed. 
     * Opens a Find dialog.
     */
    public void handleFind() {
        if ( _findDialog == null ) {
            _findDialog = new FindDialog( this, _previousFindText, PhetFont.getPreferredFont( _targetLocale ) );
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
        String message = MessageFormat.format( TUStrings.HELP_MESSAGE, TUConstants.PHETHELP_EMAIL, TUResources.getVersionMajorMinorDev() );
        JOptionPane.showMessageDialog( this, message, TUStrings.HELP_TITLE, JOptionPane.INFORMATION_MESSAGE );
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
