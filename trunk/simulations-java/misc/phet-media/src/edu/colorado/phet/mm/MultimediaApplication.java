package edu.colorado.phet.mm;

import edu.colorado.phet.mm.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Aug 11, 2006
 * Time: 9:02:20 AM
 * Copyright (c) Aug 11, 2006 by Sam Reid
 */

public class MultimediaApplication {
    private JFrame frame;
    private JPanel controlPanel;
    private final JTextArea textArea = new JTextArea();
    private ImageEntry[] imageEntries;

    public MultimediaApplication() {
        frame = new JFrame( "PhET Multimedia Browser" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        controlPanel = new JPanel();
        controlPanel.add( new JButton( new AbstractAction( "Scan Sims" ) {
            public void actionPerformed( ActionEvent e ) {
                scanImages();
            }
        } ) );
        controlPanel.add( new JButton( new AbstractAction( "Show Annotations" ) {
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


        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( controlPanel, BorderLayout.CENTER );
        textArea.setAutoscrolls( true );
        textArea.setRows( 10 );
        mainPanel.add( new JScrollPane( textArea ), BorderLayout.SOUTH );
        frame.setContentPane( mainPanel );
        frame.setSize( 1000, 1000 );
    }

    //Searches through phet simulations for images not in the annotation repository
    private void scanImages() {
        File[] imageFiles = ImageFinder.getImageFiles();
        for( int i = 0; i < imageFiles.length; i++ ) {
            System.out.println( "scanning i=" + i + "/" + imageFiles.length );
            File imageFile = imageFiles[i];
            File repositoryCopy = getRepositoryCopy( imageFile );
            if( repositoryCopy == null ) {
                System.out.println( "Found an unannotated file: " + imageFile.getAbsolutePath() );
                addToRepository( imageFile );
            }
        }
    }

    private void addToRepository( File imageFile ) {
        File file = ConvertAnnotatedRepository.createNewRepositoryFile( imageFile );
        try {
            FileUtils.copy( imageFile, file );
            ImageEntry imageEntry = ImageEntry.createNewEntry( file.getAbsolutePath() );
            ConvertAnnotatedRepository.storeProperties( imageEntry, file );
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
        for( int i = 0; i < repositoryFiles.length; i++ ) {
            File repositoryFile = repositoryFiles[i];
            try {
                if( FileUtils.contentEquals( imageFile, repositoryFile ) ) {
                    return repositoryFile;
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void appendLine() {
        appendLine( "" );
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
        for( int i = 0; i < imageEntries.length; i++ ) {
            ImageEntry imageEntry = imageEntries[i];
            total++;
            if( imageEntry.isDone() ) {
                numDone++;
            }
            if( imageEntry.isNonPhet() ) {
                numNonPhet++;
            }
            if( imageEntry.getSource() != null && imageEntry.getSource().trim().length() != 0 ) {
                String source = imageEntry.getSource();
                if( sourceTable.containsKey( source ) ) {
                    Integer value = (Integer)sourceTable.get( source );
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

    public static ImageEntry[] getAllImageEntries() {
        ArrayList list = new ArrayList();
        File root = getDataDirectory();
        File[] children = root.listFiles();
        for( int i = 0; i < children.length; i++ ) {
            File simDir = children[i];
            ImageEntry[] e = getImageEntries( simDir );
            for( int j = 0; j < e.length; j++ ) {
                ImageEntry imageEntry = e[j];
                if( !list.contains( imageEntry ) ) {
                    list.add( imageEntry );
                }
            }
        }
        return (ImageEntry[])list.toArray( new ImageEntry[0] );
    }

    public void setImageEntries( ImageEntry[] imageEntries ) {
        this.imageEntries = imageEntries;
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        MultimediaTable table = new MultimediaTable();
        for( int i = 0; i < imageEntries.length; i++ ) {
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

    static String[] suffixes = new String[]{"png", "gif", "jpg", "tif", "tiff"};

    private static ImageEntry[] getImageEntries( File simDir ) {

        ArrayList all = new ArrayList();
        File[] children = simDir.listFiles();
        for( int i = 0; children != null && i < children.length; i++ ) {
            File child = children[i];
            if( child.isFile() ) {
                if( hasSuffix( child.getName(), suffixes ) ) {
                    String path = getPath( getDataDirectory().getParentFile(), child );
                    ImageEntry imageEntry = new ImageEntry( path );
//                    decorate( imageEntry );
                    all.add( imageEntry );
                }
            }
            else {
                ImageEntry[] entries = getImageEntries( child );
                all.addAll( Arrays.asList( entries ) );
            }
        }
        return (ImageEntry[])all.toArray( new ImageEntry[0] );
    }

    public static String getPath( File parent, File child ) {
        String parentAbs = parent.getAbsolutePath();
        String childAbs = child.getAbsolutePath();
        return childAbs.substring( parentAbs.length() + 1 );
    }

    private static boolean hasSuffix( String zipEntryName, String[] suffixes ) {
        boolean image = false;
        for( int i = 0; i < suffixes.length; i++ ) {
            String suffix = suffixes[i];
            if( zipEntryName.toLowerCase().endsWith( suffix ) ) {
                image = true;
                break;
            }
        }
        return image;
    }

    public static void main( String[] args ) {
        MultimediaApplication multimediaApplication = new MultimediaApplication();
        multimediaApplication.start();
    }

    public void start() {
        frame.setVisible( true );
    }
}
