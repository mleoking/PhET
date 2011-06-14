// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.serialization;

import edu.colorado.phet.common.phetcommon.servicemanager.InputStreamFileContents;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.util.persistence.Point2DPersistenceDelegate;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Sam Reid
 * Jun 3, 2007, 3:37:37 PM
 */
public class EnergySkateParkIO implements IProguardKeepClass {

    public static void save( EnergySkateParkModule module ) throws UnavailableServiceException, IOException {
        Component component = module.getEnergySkateParkSimulationPanel();
        String xml = toXMLString( module );
        EnergySkateParkLogging.println( "xml = " + xml );
        InputStream stream = new ByteArrayInputStream( xml.getBytes() );
        FileContents data = new InputStreamFileContents( "esp_output", stream );

        FileSaveService fos = PhetServiceManager.getFileSaveService( component );
        FileContents out = fos.saveAsFileDialog( null, null, data );
        EnergySkateParkLogging.println( "Saved file." );
    }

    private static String toXMLString( EnergySkateParkModule module ) {
        ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder( out );
        e.setPersistenceDelegate( Point2D.Double.class, new Point2DPersistenceDelegate() );
        e.writeObject( new EnergySkateParkModuleBean( module ) );
        e.writeObject( new JButton( "My Button" ) );
        e.close();

        return out.toString();
    }

    public static void open( EnergySkateParkModule module ) throws UnavailableServiceException, IOException {
        FileOpenService fos = PhetServiceManager.getFileOpenService( module.getEnergySkateParkSimulationPanel() );
        FileContents open = fos.openFileDialog( null, null );
        if( open == null ) {
            return;
        }

        XMLDecoder xmlDecoder = new XMLDecoder( open.getInputStream() );
        Object obj = xmlDecoder.readObject();
        if( obj instanceof EnergySkateParkModuleBean ) {
            EnergySkateParkModuleBean energySkateParkModelBean = (EnergySkateParkModuleBean)obj;
            energySkateParkModelBean.apply( module );
        }
    }

    public static void open( String filename, EnergySkateParkModule module ) {
        EnergySkateParkLogging.println( "filename = " + filename );
        XMLDecoder xmlDecoder = new XMLDecoder( Thread.currentThread().getContextClassLoader().getResourceAsStream( filename ) );
        Object obj = xmlDecoder.readObject();
        if( obj instanceof EnergySkateParkModuleBean ) {
            EnergySkateParkModuleBean energySkateParkModelBean = (EnergySkateParkModuleBean)obj;
            energySkateParkModelBean.apply( module );
        }
    }

}
