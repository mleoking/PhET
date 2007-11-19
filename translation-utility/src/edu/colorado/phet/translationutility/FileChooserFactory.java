/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class FileChooserFactory {

    private static final String JAR_FILE_FILTER_NAME = TUResources.getString( "fileFilter.jar" );
    private static final String PROPERTIES_FILE_FILTER_NAME = TUResources.getString( "fileFilter.properties" );
    
    public static JarFileChooser createJarFileChooser( File currentDirectory ) {
        return new JarFileChooser( currentDirectory );
    }
    
    public static PropertiesFileChooser createPropertiesFileChooser( File currentDirectory ) {
        return new PropertiesFileChooser( currentDirectory );
    }
    
    private static class JarFileFilter extends FileFilter {
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith( ".jar" );
        }
        public String getDescription() {
            return JAR_FILE_FILTER_NAME;
        }
    }

    public static class JarFileChooser extends FilteredFileChooser {
        private JarFileChooser( File currentDirectory ) {
            super( currentDirectory, new JarFileFilter() );
        }
    }
    
    private static class PropertiesFileFilter extends FileFilter {
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith( ".properties" );
        }
        public String getDescription() {
            return PROPERTIES_FILE_FILTER_NAME;
        }
    }

    public static class PropertiesFileChooser extends FilteredFileChooser {
        private PropertiesFileChooser( File currentDirectory ) {
            super( currentDirectory, new PropertiesFileFilter() );
        }
    }
    
    private static class FilteredFileChooser extends JFileChooser {
        protected FilteredFileChooser( File currentDirectory, FileFilter fileFilter ) {
            super( currentDirectory );
            setAcceptAllFileFilterUsed( false );
            addChoosableFileFilter( fileFilter );
            setFileFilter( fileFilter );
        }
    }
}
