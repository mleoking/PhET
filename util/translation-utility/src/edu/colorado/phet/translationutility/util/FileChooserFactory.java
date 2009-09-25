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
     * Create a JAR file chooser (for files with a .jar suffix).
     * 
     * @param currentDirectory
     * @return JarFileChooser
     */
    public static JarFileChooser createJarFileChooser( File currentDirectory ) {
        return new JarFileChooser( currentDirectory );
    }
    
    private static class JarFileFilter extends FileFilter {
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith( ".jar" );
        }
        public String getDescription() {
            return "JAR files";
        }
    }

    public static class JarFileChooser extends FilteredFileChooser {
        private JarFileChooser( File currentDirectory ) {
            super( currentDirectory, new JarFileFilter() );
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
