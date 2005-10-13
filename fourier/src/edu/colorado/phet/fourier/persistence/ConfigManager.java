/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.persistence;

import java.awt.Color;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.jnlp.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierApplication;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.Version;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.view.HarmonicColors;


/**
 * ConfigManager manages the application's configuration.
 * It saves/loads configurations to/from files as XML-encoded objects.
 * It handles the user interface for selecting the file to save/load.
 * It works differently if the application was started with Web Start.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConfigManager {

    private FourierApplication _app; // the application whose configuration we are managing
    private String _directoryName; // the most recent directory visited in a file chooser
    private boolean _useJNLP; // whether to use JNLP services
    
    /**
     * Sole constructor.
     * 
     * @param app
     */
    public ConfigManager( FourierApplication app ) {
        _app = app;
        _useJNLP = wasWebStarted();
    }
    
    /**
     * Saves the application state to a file as an XML-encoded FourierConfig object.
     */
    public void save() {
        
        // Save the application's configuration
        FourierConfig config = new FourierConfig();
        {
            // Modules
            Module[] modules = _app.getModuleManager().getModules();
            for ( int i = 0; i < modules.length; i++ ) {
                if ( modules[i] instanceof FourierModule ) {
                    ( (FourierModule) modules[i] ).save( config );
                }
            }

            // Globals
            {
                // Version & build info
                config.getGlobalConfig().setVersionNumber( Version.NUMBER );
                config.getGlobalConfig().setBuildNumber( Version.BUILD );

                // Harmonic colors
                Color[] harmonicColors = new Color[HarmonicColors.getInstance().getNumberOfColors()];
                for ( int i = 0; i < harmonicColors.length; i++ ) {
                    harmonicColors[i] = HarmonicColors.getInstance().getColor( i );
                }
                config.getGlobalConfig().setHarmonicColors( harmonicColors );
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
            JFrame frame = _app.getPhetFrame();
            String title = SimStrings.get( "Save.error.title" );
            String format = SimStrings.get( "Save.error.message" );
            Object[] args = { e.getMessage() };
            String message = MessageFormat.format( format, args );
            JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
            e.printStackTrace();
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
            JFrame frame = _app.getPhetFrame();
            String title = SimStrings.get( "Load.error.title" );
            String format = SimStrings.get( "Load.error.message" );
            Object[] args = { e.getMessage() };
            String message = MessageFormat.format( format, args );
            JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
            e.printStackTrace();
        }
        if ( object == null ) {
            return;
        }
        
        // Check the object's type
        FourierConfig config = null;
        if ( object instanceof FourierConfig ) {
            config = (FourierConfig) object;
        }
        else {
            JFrame frame = _app.getPhetFrame();
            String title = SimStrings.get( "Load.error.title" );
            String format = SimStrings.get( "Load.error.message" );
            Object[] args = { SimStrings.get( "Load.error.notConfig" ) };
            String message = MessageFormat.format( format, args );
            JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        // Configure the application
        JFrame frame = _app.getPhetFrame();
        frame.setCursor( FourierConstants.WAIT_CURSOR );
        {
            // Modules
            Module[] modules = _app.getModuleManager().getModules();
            for ( int i = 0; i < modules.length; i++ ) {
                if ( modules[i] instanceof FourierModule ) {
                    ( (FourierModule) modules[i] ).load( config );
                }
            }

            // Globals
            Color[] harmonicColors = config.getGlobalConfig().getHarmonicColors();
            for ( int i = 0; i < harmonicColors.length; i++ ) {
                HarmonicColors.getInstance().setColor( i, harmonicColors[i] );
            }
        }
        frame.setCursor( FourierConstants.DEFAULT_CURSOR );
    }
    
    /*
     * Implementation of "Save" for non-Web Start clients, uses JFileChooser and java.io.
     */
    public void saveLocal( Object object ) throws Exception {
       
        JFrame frame = _app.getPhetFrame();
        
        // Choose the file to save.
        JFileChooser fileChooser = new JFileChooser( _directoryName );
        fileChooser.setDialogTitle( SimStrings.get( "Save.title" ) );
        int rval = fileChooser.showSaveDialog( frame );
        _directoryName = fileChooser.getCurrentDirectory().getAbsolutePath();
        File selectedFile = fileChooser.getSelectedFile();
        if ( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
            return;
        }

        _directoryName = selectedFile.getParentFile().getAbsolutePath();

        // If the file exists, confirm overwrite.
        if ( selectedFile.exists() ) {
            String title = SimStrings.get( "Save.confirm.title" );
            String message = SimStrings.get( "Save.confirm.message" );
            int reply = JOptionPane.showConfirmDialog( frame, message, title, JOptionPane.YES_NO_OPTION );
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
            public void exceptionThrown( Exception e ) {
                JFrame frame = PhetApplication.instance().getPhetFrame();
                String title = SimStrings.get( "Save.error.title" );
                String format = SimStrings.get( "Save.error.encode" );
                Object[] args = { e.getMessage() };
                String message = MessageFormat.format( format, args );
                JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
                e.printStackTrace();
            }      
        } );
        encoder.writeObject( object );
        encoder.close();
    }
    
    /*
     * Implementation of "Load" for non-Web Start clients, uses JFileChooser and java.io.
     */
    public Object loadLocal() throws Exception {
        JFrame frame = _app.getPhetFrame();
        
        // Choose the file to load.
        JFileChooser fileChooser = new JFileChooser( _directoryName );
        fileChooser.setDialogTitle( SimStrings.get( "Load.title" ) );
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
            public void exceptionThrown( Exception e ) {
                JFrame frame = PhetApplication.instance().getPhetFrame();
                String title = SimStrings.get( "Load.error.title" );
                String format = SimStrings.get( "Load.error.decode" );
                Object[] args = { e.getMessage() };
                String message = MessageFormat.format( format, args );
                JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
                e.printStackTrace();
            }      
        } );
        object = decoder.readObject();
        decoder.close();

        return object;
    }
    
    /*
     * Implementation of "Save" for Web Start clients, uses JNLP services.
     */
    private void saveJNLP( Object object ) throws Exception {
        
        final JFrame frame = PhetApplication.instance().getPhetFrame();
        
        // XML encode into a byte output stream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder( baos );
        encoder.setExceptionListener( new ExceptionListener() {
            public void exceptionThrown( Exception e ) {
                String title = SimStrings.get( "Save.error.title" );
                String format = SimStrings.get( "Save.error.encode" );
                Object[] args = { e.getMessage() };
                String message = MessageFormat.format( format, args );
                JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
                e.printStackTrace();
            }      
        } );
        encoder.writeObject( object );
        encoder.close();
        
        // Convert to a byte input stream.
        ByteArrayInputStream inputStream = new ByteArrayInputStream( baos.toByteArray() );
        
        // Get the JNLP service for saving files.
        FileSaveService fss = (FileSaveService) ServiceManager.lookup( "javax.jnlp.FileSaveService" );
        if ( fss == null ) {
            throw new UnavailableServiceException();
        }

        // Save the configuration to a file.
        FileContents fc = fss.saveFileDialog( null, null, inputStream, _directoryName );
    }
    
    /*
     * Implementation of "Load" for Web Start clients, uses JNLP services.
     */
    private Object loadJNLP() throws Exception {
        
        JFrame frame = _app.getPhetFrame();
        
        // Get the JNLP service for opening files.
        FileOpenService fos = (FileOpenService) ServiceManager.lookup( "javax.jnlp.FileOpenService" );
        if ( fos == null ) {
            throw new UnavailableServiceException();
        }
        
        // Read the configuration from a file.
        FileContents fc = fos.openFileDialog( _directoryName, null );
        if ( fc == null ) {
            return null;
        }
        _directoryName = fc.getName();            

        // Convert the FileContents to an input stream.
        InputStream inputStream = fc.getInputStream();
        
        // XML-decode the input stream.
        Object object = null;
        XMLDecoder decoder = new XMLDecoder( inputStream );
        decoder.setExceptionListener( new ExceptionListener() {

            public void exceptionThrown( Exception e ) {
                JFrame frame = PhetApplication.instance().getPhetFrame();
                String title = SimStrings.get( "Load.error.title" );
                String format = SimStrings.get( "Load.error.decode" );
                Object[] args = { e.getMessage() };
                String message = MessageFormat.format( format, args );
                JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
                e.printStackTrace();
            }
        } );
        object = decoder.readObject();
        decoder.close();
        
        return object;
    }
    
    /**
     * Determines if you application was started using Java Web Start.
     * 
     * @return true or false
     */
    public static boolean wasWebStarted() {
        return ( System.getProperty( "javawebstart.version" ) != null );
    }
}
