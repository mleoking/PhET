/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * FileChooserFactory is a factory for creating file choosers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FileChooserFactory {

    /**
     * Creates a JAR file chooser.
     */
    public static JFileChooser createJarFileChooser() {
        return new FilteredFileChooser( ".jar", "JAR files (.jar)" );
    }
    
    /**
     * Creates a Properties file chooser.
     */
    public static JFileChooser createPropertiesFileChooser() {
        return new FilteredFileChooser( ".properties", "Java string files (.properties)" );
    }
    
    /**
     * Creates an XML file chooser.
     */
    public static JFileChooser createXMLFileChooser() {
        return new FilteredFileChooser( ".xml", "Flash string files (.xml)" );
    }

    /*
     * File chooser that filters on one type of file.
     */
    private static class FilteredFileChooser extends JFileChooser {

        protected FilteredFileChooser( String suffix, String description ) {
            setAcceptAllFileFilterUsed( false );
            FileFilter fileFilter = new SingleFileFilter( suffix, description );
            addChoosableFileFilter( fileFilter );
            setFileFilter( fileFilter );
        }
    }
    
    /*
     * Generic filter for one type of file.
     */
    private static class SingleFileFilter extends FileFilter {

        private final String suffix;
        private final String description;

        public SingleFileFilter( String suffix, String description ) {
            this.suffix = suffix;
            this.description = description;
        }

        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith( suffix );
        }

        public String getDescription() {
            return description;
        }
    }
}
