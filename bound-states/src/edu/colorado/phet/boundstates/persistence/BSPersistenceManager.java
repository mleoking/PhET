/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.text.MessageFormat;

import javax.jnlp.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.colorado.phet.boundstates.BSAbstractApplication;
import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.boundstates.util.DialogUtils;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * ConfigManager manages the application's configuration.
 * It saves/loads configurations to/from files as XML-encoded objects.
 * It handles the user interface for selecting the file to save/load,
 * including any error dialogs.
 * It works differently if the application was started with Web Start.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSPersistenceManager {

    private BSAbstractApplication _app; // the application whose configuration we are managing
    private String _directoryName; // the most recent directory visited in a file chooser
    private boolean _useJNLP; // whether to use JNLP services
    
    /**
     * Sole constructor.
     * 
     * @param app
     */
    public BSPersistenceManager( BSAbstractApplication app ) {
        _app = app;
        _useJNLP = wasWebStarted();
    }
    
    /**
     * Saves the application state to a file as an XML-encoded FourierConfig object.
     */
    public void save() {
        
        // Save the application's configuration
        BSConfig config = new BSConfig();
        {
            // Globals 
            _app.save( config.getGlobalConfig() );
            
            // One Well module
            BSAbstractModule oneWellModule = _app.getOneWellModule();
            if ( oneWellModule != null ) {
                BSModuleConfig oneWellModuleConfig = config.getOneWellModuleConfig();
                oneWellModule.save( oneWellModuleConfig );
            }
            
            // Two Wells module
            BSAbstractModule twoWellsModule = _app.getTwoWellsModule();
            if ( twoWellsModule != null ) {
                BSModuleConfig twoWellsModuleConfig = config.getTwoWellsModuleConfig();
                twoWellsModule.save( twoWellsModuleConfig );
            }
            
            // Many Wells module
            BSAbstractModule manyWellsModule = _app.getManyWellsModule();
            if ( manyWellsModule != null ) {
                BSModuleConfig manyWellsModuleConfig = config.getManyWellsModuleConfig();
                manyWellsModule.save( manyWellsModuleConfig );
            }
        }
        
        // Save the configuration object to a file.
        try {
            if ( _useJNLP ) {
                saveJNLP( config );
            }
            else {
                saveLocal( config );
            }
        }
        catch ( Exception e ) {
            showError( SimStrings.get( "Save.error.message" ), e );
        }
    }
    
    /**
     * Loads the application state from a file as an XML-encoded FourierConfig object.
     */
    public void load() {
        
        // Load a configuration object.
        Object object = null;
        try {
            if ( _useJNLP ) {
                object = loadJNLP();
            }
            else {
                object = loadLocal();
            }
        }
        catch ( Exception e ) {
            showError( SimStrings.get( "Load.error.message" ), e );
        }
        if ( object == null ) {
            return;
        }
        
        // Verify the object's type
        if ( !( object instanceof BSConfig ) ) {
            showError( SimStrings.get( "Load.error.message" ), SimStrings.get( "Load.error.contents" ) );
            return;
        }
        
        // Configure the application
        BSConfig config = (BSConfig) object;
        try {
            // Global
            _app.load( config.getGlobalConfig() );
            
            // One Well module
            BSAbstractModule oneWellModule = _app.getOneWellModule();
            if ( oneWellModule != null ) {
                BSModuleConfig oneWellModuleConfig = config.getOneWellModuleConfig();
                oneWellModule.load( oneWellModuleConfig );
            }
            
            // Two Wells module
            BSAbstractModule twoWellsModule = _app.getTwoWellsModule();
            if ( twoWellsModule != null ) {
                BSModuleConfig twoWellsModuleConfig = config.getTwoWellsModuleConfig();
                twoWellsModule.load( twoWellsModuleConfig );
            }
            
            // Many Wells module
            BSAbstractModule manyWellsModule = _app.getManyWellsModule();
            if ( manyWellsModule != null ) {
                BSModuleConfig manyWellsModuleConfig = config.getManyWellsModuleConfig();
                manyWellsModule.load( manyWellsModuleConfig );
            }
        }
        catch ( Exception e ) {
            showError( SimStrings.get( "Load.error.message" ), e );
        }
    }
    
    /*
     * Implementation of "Save" for non-Web Start clients, uses JFileChooser and java.io.
     */
    private void saveLocal( Object object ) throws Exception {
       
        JFrame frame = _app.getPhetFrame();
        
        // Choose the file to save.
        JFileChooser fileChooser = new JFileChooser( _directoryName );
        fileChooser.setDialogTitle( SimStrings.get( "title.save" ) );
        int rval = fileChooser.showSaveDialog( frame );
        _directoryName = fileChooser.getCurrentDirectory().getAbsolutePath();
        File selectedFile = fileChooser.getSelectedFile();
        if ( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
            return;
        }

        _directoryName = selectedFile.getParentFile().getAbsolutePath();

        // If the file exists, confirm overwrite.
        if ( selectedFile.exists() ) {
            String message = SimStrings.get( "Save.confirm.message" );
            int reply = DialogUtils.showConfirmDialog( frame, message, JOptionPane.YES_NO_CANCEL_OPTION );
            if ( reply != JOptionPane.YES_OPTION ) {
                return;
            }
        }

        // XML encode directly to the file.
        String filename = selectedFile.getAbsolutePath();
        FileOutputStream fos = new FileOutputStream( filename );
        BufferedOutputStream bos = new BufferedOutputStream( fos );
        XMLEncoder encoder = new XMLEncoder( bos );
        encoder.setExceptionListener( new ExceptionListener() {
            private int errors = 0;
            // Report the first recoverable exception.
            public void exceptionThrown( Exception e ) {
                if ( errors == 0 ) {
                    showError( SimStrings.get( "Save.error.encode" ), e );
                    errors++;
                }
            }      
        } );
        encoder.writeObject( object );
        encoder.close();
    }
    
    /*
     * Implementation of "Load" for non-Web Start clients, uses JFileChooser and java.io.
     */
    private Object loadLocal() throws Exception {
        JFrame frame = _app.getPhetFrame();
        
        // Choose the file to load.
        JFileChooser fileChooser = new JFileChooser( _directoryName );
        fileChooser.setDialogTitle( SimStrings.get( "title.load" ) );
        int rval = fileChooser.showOpenDialog( frame );
        _directoryName = fileChooser.getCurrentDirectory().getAbsolutePath();
        File selectedFile = fileChooser.getSelectedFile();
        if ( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
            return null;
        }

        // XML decode directly from the file.
        Object object = null;
        String filename = selectedFile.getAbsolutePath();
        FileInputStream fis = new FileInputStream( filename );
        BufferedInputStream bis = new BufferedInputStream( fis );
        XMLDecoder decoder = new XMLDecoder( bis );
        decoder.setExceptionListener( new ExceptionListener() {
            private int errors = 0;
            // Report the first recoverable exception.
            public void exceptionThrown( Exception e ) {
                if ( errors == 0 ) {
                    showError( SimStrings.get( "Load.error.decode" ), e );
                    errors++;
                }
            }      
        } );
        object = decoder.readObject();
        decoder.close();
        if ( object == null ) {
            throw new Exception( SimStrings.get( "Load.error.contents" ) );
        }

        return object;
    }
    
    /*
     * Implementation of "Save" for Web Start clients, uses JNLP services.
     */
    private void saveJNLP( Object object ) throws Exception {
        
        // XML encode into a byte output stream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder( baos );
        encoder.setExceptionListener( new ExceptionListener() {
            private int errors = 0;
            // Report the first recoverable exception.
            public void exceptionThrown( Exception e ) {
                if ( errors == 0 ) {
                    showError( SimStrings.get( "Save.error.encode" ), e );
                    errors++;
                }
            }
        } );
        encoder.writeObject( object );
        encoder.close();
        if ( object == null ) {
            throw new Exception( SimStrings.get( "XML encoding failed" ) );
        }
        
        // Convert to a byte input stream.
        ByteArrayInputStream inputStream = new ByteArrayInputStream( baos.toByteArray() );
        
        // Get the JNLP service for saving files.
        FileSaveService fss = (FileSaveService) ServiceManager.lookup( "javax.jnlp.FileSaveService" );
        if ( fss == null ) {
            throw new UnavailableServiceException( "JNLP FileSaveService is unavailable" );
        }
        
        // Save the configuration to a file.
        FileContents fc = fss.saveFileDialog( null, null, inputStream, _directoryName );
        if ( fc != null ) {
            _directoryName = getDirectoryName( fc.getName() );
        }
    }
    
    /*
     * Implementation of "Load" for Web Start clients, uses JNLP services.
     */
    private Object loadJNLP() throws Exception {
        
        // Get the JNLP service for opening files.
        FileOpenService fos = (FileOpenService) ServiceManager.lookup( "javax.jnlp.FileOpenService" );
        if ( fos == null ) {
            throw new UnavailableServiceException( "JNLP FileOpenService is unavailable" );
        }
        
        // Read the configuration from a file.
        FileContents fc = fos.openFileDialog( _directoryName, null );
        if ( fc == null ) {
            return null;
        }
        _directoryName = getDirectoryName( fc.getName() );

        // Convert the FileContents to an input stream.
        InputStream inputStream = fc.getInputStream();
        
        // XML-decode the input stream.
        Object object = null;
        XMLDecoder decoder = new XMLDecoder( inputStream );
        decoder.setExceptionListener( new ExceptionListener() {
            private int errors = 0;
            // Report the first recoverable exception.
            public void exceptionThrown( Exception e ) {
                if ( errors == 0 ) {
                    showError( SimStrings.get( "Load.error.decode" ), e );
                    errors++;
                }
            }
        } );
        object = decoder.readObject();
        decoder.close();
        if ( object == null ) {
            throw new Exception( SimStrings.get( "Load.error.contents" ) );
        }
        
        return object;
    }
    
    /*
     * Determines if the simulation was started using Java Web Start.
     * 
     * @return true or false
     */
    private static boolean wasWebStarted() {
        return ( System.getProperty( "javawebstart.version" ) != null );
    }
    
    /*
     * Shows the error message associated with an exception in an
     * error dialog, and prints a stack trace to the console.
     * 
     * @param format
     * @param e
     */
    private void showError( String format, Exception e ) {
        showError( format, e.getMessage() );
        e.printStackTrace();
    }
    
    /*
     * Shows the error message in an error dialog.
     * 
     * @param format
     * @param e
     */
    private void showError( String format, String errorMessage ) {
        JFrame frame = _app.getPhetFrame();
        String title = SimStrings.get( "title.error" );
        Object[] args = { errorMessage };
        String message = MessageFormat.format( format, args );
        DialogUtils.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
    }
    
    /*
     * Gets the directory name portion of a filename.
     * 
     * @param filename
     * @return directory name
     */
    private static String getDirectoryName( String filename ) {
        String directoryName = null;
        int index = filename.lastIndexOf( File.pathSeparatorChar );
        if ( index != -1 ) {
            directoryName = filename.substring( index );
        }
        return directoryName;
    }
}
