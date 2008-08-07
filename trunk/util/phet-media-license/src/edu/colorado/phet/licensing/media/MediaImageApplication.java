package edu.colorado.phet.licensing.media;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.*;

import edu.colorado.phet.licensing.media.FileUtils;

/**
 * User: Sam Reid
 * Date: Aug 11, 2006
 * Time: 9:02:20 AM
 * Copyright (c) Aug 11, 2006 by Sam Reid
 */

public class MediaImageApplication {
    private JFrame frame;
    private JPanel controlPanel;
    private final JTextArea textArea = new JTextArea();
    private ImageEntry[] imageEntries;

    public MediaImageApplication() {
        frame = new JFrame( "PhET Multimedia Browser" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        controlPanel = new JPanel();
        controlPanel.add( new JButton( new AbstractAction( "Scan Sims" ) {
            public void actionPerformed( ActionEvent e ) {
                scanImages();
            }
        } ) );
        controlPanel.add( new JButton( new AbstractAction( "Load Annotations" ) {
            public void actionPerformed( ActionEvent e ) {
                try {
                    setImageEntries( ConvertAnnotatedRepository.loadAnnotatedEntries() );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } ) );
        controlPanel.add( new JButton( new AbstractAction( "Statistics" ) {
            public void actionPerformed( ActionEvent e ) {
                count();
            }
        } ) );
        controlPanel.add( new JButton( new AbstractAction( "Save Annotations" ) {
            public void actionPerformed( ActionEvent e ) {
                saveAnnotations();
            }
        } ) );
        controlPanel.add( new JButton( new AbstractAction( "Export to HTML" ) {
            public void actionPerformed( ActionEvent e ) {
                exportToHTML();
            }
        } ) );


        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( controlPanel, BorderLayout.CENTER );
        textArea.setAutoscrolls( true );
        textArea.setRows( 10 );
        mainPanel.add( new JScrollPane( textArea ), BorderLayout.SOUTH );
        frame.setContentPane( mainPanel );
        frame.setSize( 1000, 1000 );
    }

    private void exportToHTML() {
        try {
            new HTMLExport( true ).export( new File( "phet-media.html" ), imageEntries );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void saveAnnotations() {
        int count = 0;
        for ( int i = 0; i < imageEntries.length; i++ ) {
            ImageEntry imageEntry = imageEntries[i];
//            System.out.println( "imageEntry.getFile() = " + imageEntry.getFile() );
            if ( changed( imageEntry ) ) {
                boolean didchange = changed( imageEntry );//debugging
                System.out.println( "Saving annotation for: " + imageEntry.getImageName() );

                ConvertAnnotatedRepository.storeProperties( imageEntry, imageEntry.getImageName() );
                count++;
            }
        }
        System.out.println( "Saved: count = " + count );
    }

    private boolean changed( ImageEntry imageEntry ) {
        Properties memory = imageEntry.toProperties();
        Properties onDisk = new Properties();
        try {
            onDisk.load( new FileInputStream( ConvertAnnotatedRepository.getPropertiesFile( imageEntry.getImageName() ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return !memory.equals( onDisk );
    }

    //Searches through phet simulations for images not in the annotation repository
    private void scanImages() {
        File[] imageFiles = MediaFinder.getImageFiles();
        for ( int i = 0; i < imageFiles.length; i++ ) {
            System.out.println( "scanning i=" + i + "/" + imageFiles.length );
            File imageFile = imageFiles[i];
            File repositoryCopy = getRepositoryCopy( imageFile );
            if ( repositoryCopy == null ) {
                System.out.println( "Found an unannotated file: " + imageFile.getAbsolutePath() );
                addToRepository( imageFile );
            }
        }
    }

    private void addToRepository( File imageFile ) {
        File file = ConvertAnnotatedRepository.createNewRepositoryFile( imageFile );
        try {
            FileUtils.copy( imageFile, file );
            ImageEntry imageEntry = ImageEntry.createNewEntry( file.getName() );
            ConvertAnnotatedRepository.storeProperties( imageEntry, file.getName() );
            System.out.println( "added to repository: imageEntry = " + imageEntry.toString() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private File getRepositoryCopy( File imageFile ) {
        File[] repositoryFiles = new File( "annotated-data" ).listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return !pathname.getAbsolutePath().toLowerCase().endsWith( ".properties" ) &&
                       !pathname.getAbsolutePath().toLowerCase().endsWith( ".svn" );
            }
        } );
        for ( int i = 0; i < repositoryFiles.length; i++ ) {
            File repositoryFile = repositoryFiles[i];
            try {
                if ( FileUtils.contentEquals( imageFile, repositoryFile ) ) {
                    return repositoryFile;
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void appendLine( String line ) {
        textArea.append( line + System.getProperty( "line.separator" ) );
        textArea.setCaretPosition( textArea.getDocument().getLength() );
    }

    private void count() {
        int numDone = 0;
        int total = 0;
        int numNonPhet = 0;
        Hashtable sourceTable = new Hashtable();
        for ( int i = 0; i < imageEntries.length; i++ ) {
            ImageEntry imageEntry = imageEntries[i];
            total++;
            if ( imageEntry.isDone() ) {
                numDone++;
            }
            if ( imageEntry.isNonPhet() ) {
                numNonPhet++;
            }
            if ( imageEntry.getSource() != null && imageEntry.getSource().trim().length() != 0 ) {
                String source = imageEntry.getSource();
                if ( sourceTable.containsKey( source ) ) {
                    Integer value = (Integer) sourceTable.get( source );
                    sourceTable.put( source, new Integer( value.intValue() + 1 ) );
                }
                else {
                    sourceTable.put( source, new Integer( 1 ) );
                }
            }
        }
        DateFormat fulldate = DateFormat.getDateTimeInstance( DateFormat.LONG,
                                                              DateFormat.LONG );
        appendLine( "Count finished on " + fulldate.format( new Date() ) );
        appendLine( "Total number images: " + total );
        appendLine( "Number finished: " + numDone );
        appendLine( "Number Non-phet: " + numNonPhet );
        appendLine( "Sources:" );
        appendLine( sourceTable.toString() );
    }

    public static File getDataDirectory() {
        return new File( "./phet-mm-temp" );
    }

    public void setImageEntries( ImageEntry[] imageEntries ) {
        this.imageEntries = imageEntries;
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        MultimediaTable table = new MultimediaTable();
        for ( int i = 0; i < imageEntries.length; i++ ) {
            table.addEntry( imageEntries[i] );
        }
        JScrollPane comp = new JScrollPane( table );
        comp.setPreferredSize( new Dimension( 800, 700 ) );
        controlPanel.add( comp );
        controlPanel.invalidate();
        controlPanel.revalidate();
        controlPanel.doLayout();
        controlPanel.updateUI();
        controlPanel.paintImmediately( controlPanel.getBounds() );
    }

    public static String getPath( File parent, File child ) {
        String parentAbs = parent.getAbsolutePath();
        String childAbs = child.getAbsolutePath();
        return childAbs.substring( parentAbs.length() + 1 );
    }

    public static void main( String[] args ) {
        MediaImageApplication mediaImageApplication = new MediaImageApplication();
        mediaImageApplication.start();
    }

    public void start() {
        frame.setVisible( true );
    }
}
