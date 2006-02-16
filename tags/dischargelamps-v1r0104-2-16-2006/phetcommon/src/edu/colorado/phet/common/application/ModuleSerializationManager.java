/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.util.persistence.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.beans.*;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class handles serialization of sets of modules.
 *
 * @author Ron Lemaster, Sam Reid
 */
public class ModuleSerializationManager {
    public static boolean USE_GZIP_STREAMS = true;
    private ArrayList transientPropertySourceClasses = new ArrayList();

    public void addTransientPropertySource( Class sourceClass ) {
        if( !transientPropertySourceClasses.contains( sourceClass ) ) {
            transientPropertySourceClasses.add( sourceClass );
        }
    }

    /**
     * Saves the state of the active module.
     *
     * @param fileName
     */
    public void saveState( PhetApplication phetApplication, String fileName ) {
        Module[]modules = phetApplication.getModules();
        for( int i = 0; i < modules.length; i++ ) {
            Module module = modules[i];
            Class[] transientPropertySources = module.getTransientPropertySources();
            for( int j = 0; j < transientPropertySources.length; j++ ) {
                Class transientPropertySource = transientPropertySources[j];
                addTransientPropertySource( transientPropertySource );
            }
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog( phetApplication.getPhetFrame() );
        File file = fileChooser.getSelectedFile();

        if( file != null ) {
            XMLEncoder encoder = null;
            try {
                // Prevent the component for a graphic from being persisted for now. This keeps
                // ApparatusPanel from being persisted, for now.
                for( int k = 0; k < transientPropertySourceClasses.size(); k++ ) {
                    Class aClass = (Class)transientPropertySourceClasses.get( k );
                    BeanInfo info = Introspector.getBeanInfo( aClass );
                    PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
                    for( int i = 0; i < propertyDescriptors.length; i++ ) {
                        PropertyDescriptor pd = propertyDescriptors[i];
                        if( pd.getName().equals( "image" ) ) {
                            pd.setValue( "transient", Boolean.TRUE );
                        }
                    }
                }


                OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( file ) );
                if( USE_GZIP_STREAMS ) {
                    outputStream = new GZIPOutputStream( outputStream );
                }
                encoder = new XMLEncoder( outputStream );

                encoder.setPersistenceDelegate( AffineTransform.class, new AffineTransformPersistenceDelegate() );
                encoder.setPersistenceDelegate( BasicStroke.class, new BasicStrokePersistenceDelegate() );
                encoder.setPersistenceDelegate( Ellipse2D.Double.class, new Ellipse2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Ellipse2D.Float.class, new Ellipse2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( GeneralPath.class, new GeneralPathPersistenceDelegate() );
                encoder.setPersistenceDelegate( GradientPaint.class, new GradientPaintPersistenceDelegate() );
                encoder.setPersistenceDelegate( Point2D.Double.class, new Point2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Point2D.Float.class, new Point2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Rectangle2D.Double.class, new Rectangle2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Rectangle2D.Float.class, new Rectangle2DPersistenceDelegate() );
//                encoder.setPersistenceDelegate( FontMetrics.class, new FontMetricsPersistenceDelegate() );
            }
            catch( Exception ex ) {
                ex.printStackTrace();
            }
            Module module = phetApplication.getActiveModule();
            ModuleStateDescriptor sd = module.getModuleStateDescriptor();
            encoder.writeObject( sd );
            encoder.close();
        }
    }

    /**
     * Sets the active module to the one specified in the named file, and sets the state of the
     * module to that specified in the file.
     *
     * @param fileName
     */
    public void restoreState( PhetApplication phetApplication, String fileName ) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog( phetApplication.getPhetFrame() );
        File file = fileChooser.getSelectedFile();

        if( file != null ) {

            XMLDecoder decoder = null;
            try {
                InputStream inputStream = new BufferedInputStream( new FileInputStream( file ) );
                if( USE_GZIP_STREAMS ) {
                    inputStream = new GZIPInputStream( inputStream );
                }
                decoder = new XMLDecoder( inputStream );
            }
            catch( FileNotFoundException e ) {
                e.printStackTrace();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            // Read in the ModuleStateDescriptor
            ModuleStateDescriptor sd = (ModuleStateDescriptor)decoder.readObject();
            decoder.setExceptionListener( new ExceptionListener() {
                public void exceptionThrown( Exception exception ) {
                    exception.printStackTrace();
                }
            } );

            // Find the module that is of the same class as the one that we're
            // restoring. Set it to be the active module, and tell it to
            // restore itself from the saved state
            Module[]modules = phetApplication.getModules();
            for( int i = 0; i < modules.length; i++ ) {
                Module module = modules[i];
                if( module.getClass().getName().equals( sd.getModuleClassName() ) ) {
                    sd.setModuleState( module );
                    phetApplication.setActiveModule( module );
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Save/restore methods
    //
    public void saveStateToConsole( PhetApplication phetApplication ) {
        Module[]modules = phetApplication.getModules();
        for( int i = 0; i < modules.length; i++ ) {
            XMLEncoder encoder = new XMLEncoder( System.out );
            encoder.writeObject( modules[i] );
            encoder.close();
        }
    }
}