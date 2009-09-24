/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.TUStrings;
import edu.colorado.phet.translationutility.simulations.ISimulation;
import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;
import edu.colorado.phet.translationutility.userinterface.FindDialog.FindListener;
import edu.colorado.phet.translationutility.userinterface.ToolBar.ToolBarListener;
import edu.colorado.phet.translationutility.util.ExceptionHandler;
import edu.colorado.phet.translationutility.util.TULogger;

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
    private final Locale _targetLocale;
    private final String _submitDirName;
    
    private final TranslationPanel _translationPanel;
    private File _saveLoadDirectory;
    private FindDialog _findDialog;
    private String _previousFindText;
    
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
        
        _simulation = simulation;
        _targetLocale = targetLocale;
        _submitDirName = jarDirName;  // save submitted files to the same dir as the sim JAR
        
        _saveLoadDirectory = new File( jarDirName ); // start save/load file chooser in same dir as the sim JAR
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
        JScrollPane scrollPane = new JScrollPane( _translationPanel );
        
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
        
        // if there are validation errors, warn the user, and confirm that they want to continue with Test
        if ( _translationPanel.validateTargets() == false ) {
            String message = TUStrings.VALIDATION_ERROR_MESSAGE + "\n" + "Do you want to continue with this test?";
            int response = JOptionPane.showConfirmDialog( this, message, "Error", JOptionPane.YES_NO_OPTION );
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
            String message = TUStrings.VALIDATION_ERROR_MESSAGE + "\n" + "Do you want to continue with this Save?";
            int response = JOptionPane.showConfirmDialog( this, message, "Error", JOptionPane.YES_NO_OPTION );
            if ( response != JOptionPane.OK_OPTION ) {
                return;
            }
        }
        
        File defaultFile = new File( _saveLoadDirectory, _simulation.getStringFileName( _targetLocale ) );
        JFileChooser chooser = new JFileChooser();
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
            else {
                // file suffix is inappropriate, tell the user and try again
                TULogger.log( "Save attempted with bogus filename: " + outFile.getAbsolutePath() );
                String message = "<html>You are saving a string file.<br>The file name must end with " + _simulation.getStringFileSuffix() + "</html>";
                JOptionPane.showMessageDialog( this, message, "Error", JOptionPane.ERROR_MESSAGE );
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
        JFileChooser chooser = new JFileChooser( _saveLoadDirectory );
        int option = chooser.showOpenDialog( this );
        _saveLoadDirectory = chooser.getCurrentDirectory();
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
        
        // if there are validation errors, warn the user, and prevent them from sending to PhET
        if ( _translationPanel.validateTargets() == false ) {
            String message = TUStrings.VALIDATION_ERROR_MESSAGE + "\n" + "You cannot send to PhET until errors are corrected.";
            JOptionPane.showMessageDialog( this, message, "Error", JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        Properties properties = _translationPanel.getTargetProperties();
        
        // export properties to a file
        File outFile = new File( _submitDirName, _simulation.getStringFileName( _targetLocale ) );
        if ( outFile.exists() ) {
            Object[] args = { outFile.getAbsolutePath() };
            String message = MessageFormat.format( CONFIRM_OVERWRITE_MESSAGE, args );
            int selection = JOptionPane.showConfirmDialog( this, message, CONFIRM_OVERWRITE_TITLE, JOptionPane.YES_NO_OPTION );
            if ( selection != JOptionPane.YES_OPTION ) {
                return;
            }
        }
        TULogger.log( "MainFrame: submit is saving to " + outFile.getAbsolutePath() );
        
        try {
            _simulation.saveStrings( properties, outFile );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleNonFatalException( e );
        }
        
        // Use a JEditorPane so that it's possible to copy-paste the filename and email address.
        JEditorPane submitText = new JEditorPane();
        submitText.setEditorKit( new HTMLEditorKit() );
        Object[] args = { outFile.getAbsolutePath() };
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
