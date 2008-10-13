/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumwaveinterference.persistence;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.quantumwaveinterference.QWIResources;

import javax.jnlp.*;
import javax.swing.*;
import java.awt.*;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.text.MessageFormat;

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
public class PersistenceManager implements IProguardKeepClass {

    //    private PhetApplication _app; // the application whose configuration we are managing
    private String _directoryName; // the most recent directory visited in a file chooser
    private boolean _useJNLP; // whether to use JNLP services
    private JComponent parent;

    /**
     * Sole constructor.
     */
    public PersistenceManager( JComponent parent ) {
        this.parent = parent;
//        _app = app;
        _useJNLP = wasWebStarted();
    }

    /**
     * Saves the application state to a file as an XML-encoded FourierConfig object.
     */
    public void save( Serializable saveData ) {

        // Save the configuration object to a file.
        try {
            if( _useJNLP ) {
                saveJNLP( saveData );
            }
            else {
                saveLocal( saveData );
            }
        }
        catch( Exception e ) {
            showError( SimStrings.getInstance().getString( "Save.error.message" ), e );
        }
    }


    /**
     * Loads the application state from a file as an XML-encoded FourierConfig object.
     */
    public Object load() throws IOException, UnavailableServiceException {

        // Load a configuration object.
        if( _useJNLP ) {
            return loadJNLP();
        }
        else {
            return loadLocal();
        }

//        // Verify the object's type
//        if( !( object instanceof QTConfig ) ) {
//            showError( SimStrings.get( "Load.error.message" ), SimStrings.get( "Load.error.contents" ) );
//            return;
//        }

//        // Configure the application
//        QTConfig config = (QTConfig)object;
//        JFrame frame = _app.getPhetFrame();
//        frame.setCursor( QTConstants.WAIT_CURSOR );
//        {
//            // Global
//            _app.load( config );
//
//            // Modules
//            Module[] modules = _app.getModules();
//            for( int i = 0; i < modules.length; i++ ) {
//                if( modules[i] instanceof AbstractModule ) {
//                    ( (AbstractModule)modules[i] ).load( config );
//                }
//            }
//        }
//        frame.setCursor( QTConstants.DEFAULT_CURSOR );
    }

    /*
     * Implementation of "Save" for non-Web Start clients, uses JFileChooser and java.io.
     */
    public void saveLocal( Object object ) throws Exception {

        Window frame = getFrame();

        // Choose the file to save.
        JFileChooser fileChooser = new JFileChooser( _directoryName );
        fileChooser.setDialogTitle( QWIResources.getString( "save" ) );
        int rval = fileChooser.showSaveDialog( frame );
        _directoryName = fileChooser.getCurrentDirectory().getAbsolutePath();
        File selectedFile = fileChooser.getSelectedFile();
        if( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
            return;
        }

        _directoryName = selectedFile.getParentFile().getAbsolutePath();

        // If the file exists, confirm overwrite.
        if( selectedFile.exists() ) {
            String title = QWIResources.getString( "confirm" );
            String message = QWIResources.getString( "confirm.save" );
            int reply = JOptionPane.showConfirmDialog( frame, message, title, JOptionPane.YES_NO_OPTION );
            if( reply != JOptionPane.YES_OPTION ) {
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
                if( errors == 0 ) {
                    showError( SimStrings.getInstance().getString( "Save.error.encode" ), e );
                    errors++;
                }
            }
        } );
        encoder.writeObject( object );
        encoder.close();
    }

    private Window getFrame() {
        return SwingUtilities.getWindowAncestor( parent );
    }

    /*
     * Implementation of "Load" for non-Web Start clients, uses JFileChooser and java.io.
     */
    public Object loadLocal() throws FileNotFoundException {
        Window frame = getFrame();

        // Choose the file to load.
        JFileChooser fileChooser = new JFileChooser( _directoryName );
        fileChooser.setDialogTitle( "Load File" );
        int rval = fileChooser.showOpenDialog( frame );
        _directoryName = fileChooser.getCurrentDirectory().getAbsolutePath();
        File selectedFile = fileChooser.getSelectedFile();
        if( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
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
                if( errors == 0 ) {
                    showError( SimStrings.getInstance().getString( "Load.error.decode" ), e );
                    errors++;
                }
            }
        } );
        object = decoder.readObject();
        decoder.close();
//        if( object == null ) {
//            throw new Exception( SimStrings.get( "Load.error.contents" ) );
//        }

        return object;
    }

    /*
     * Implementation of "Save" for Web Start clients, uses JNLP services.
     */
    private void saveJNLP( Serializable object ) throws Exception {

        final JFrame frame = PhetApplication.instance().getPhetFrame();

        // XML encode into a byte output stream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder( baos );
        encoder.setExceptionListener( new ExceptionListener() {
            private int errors = 0;

            // Report the first recoverable exception.
            public void exceptionThrown( Exception e ) {
                if( errors == 0 ) {
                    showError( SimStrings.getInstance().getString( "Save.error.encode" ), e );
                    errors++;
                }
            }
        } );
        encoder.writeObject( object );
        encoder.close();
        if( object == null ) {
            throw new Exception( SimStrings.getInstance().getString( "XML encoding failed" ) );
        }

        // Convert to a byte input stream.
        ByteArrayInputStream inputStream = new ByteArrayInputStream( baos.toByteArray() );

        // Get the JNLP service for saving files.
        FileSaveService fss = (FileSaveService)ServiceManager.lookup( "javax.jnlp.FileSaveService" );
        if( fss == null ) {
            throw new UnavailableServiceException( "JNLP FileSaveService is unavailable" );
        }

        // Save the configuration to a file.
        FileContents fc = fss.saveFileDialog( null, null, inputStream, _directoryName );
        if( fc != null ) {
            _directoryName = getDirectoryName( fc.getName() );
        }
    }

    /*
     * Implementation of "Load" for Web Start clients, uses JNLP services.
     */
    private Object loadJNLP() throws UnavailableServiceException, IOException {

        Window frame = getFrame();

        // Get the JNLP service for opening files.
        FileOpenService fos = (FileOpenService)ServiceManager.lookup( "javax.jnlp.FileOpenService" );
        if( fos == null ) {
            throw new UnavailableServiceException( "JNLP FileOpenService is unavailable" );
        }

        // Read the configuration from a file.
        FileContents fc = fos.openFileDialog( _directoryName, null );
        if( fc == null ) {
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
                if( errors == 0 ) {
                    showError( SimStrings.getInstance().getString( "Load.error.decode" ), e );
                    errors++;
                }
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

    /**
     * Shows the error message associated with an exception in an
     * error dialog, and prints a stack trace to the console.
     *
     * @param format
     * @param e
     */
    public void showError( String format, Exception e ) {
        showError( format, e.getMessage() );
        e.printStackTrace();
    }

    /**
     * Shows the error message in an error dialog.
     *
     * @param format
     */
    public void showError( String format, String errorMessage ) {
        Window frame = getFrame();
        String title = QWIResources.getString( "error" );
        Object[] args = {errorMessage};
        String message = MessageFormat.format( format, args );
        JOptionPane.showMessageDialog( frame, message, title, JOptionPane.ERROR_MESSAGE );
    }

    /**
     * Gets the directory name portion of a filename.
     *
     * @param filename
     * @return directory name
     */
    public static String getDirectoryName( String filename ) {
        String directoryName = null;
        int index = filename.lastIndexOf( File.pathSeparatorChar );
        if( index != -1 ) {
            directoryName = filename.substring( index );
        }
        return directoryName;
    }

    public static void main( String[] args ) throws Exception {
        PersistenceManager persistenceManager = new PersistenceManager( new JButton() );
//        persistenceManager.save( new String( "hello" ) );
        Object o = persistenceManager.load();
        System.out.println( "o = " + o );
    }
}
