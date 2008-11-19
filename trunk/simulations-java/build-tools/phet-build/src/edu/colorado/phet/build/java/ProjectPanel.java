package edu.colorado.phet.build.java;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import edu.colorado.phet.build.PhetProject;

public class ProjectPanel extends JPanel {
    private File basedir;
    private PhetProject project;
    private JLabel titleLabel;
    private JTextArea changesTextArea;
    private JList flavorList;
    private JList localeList;

    public ProjectPanel( File basedir, PhetProject project ) {
        this.basedir = basedir;
        this.project = project;
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        titleLabel = new JLabel( project.getName() );
        add( titleLabel );

        changesTextArea = new JTextArea( 10, 30 );
        changesTextArea.setPreferredSize( new Dimension( 400, 400 ) );
        changesTextArea.setEditable( false );
        add( changesTextArea );

        flavorList = new JList( project.getFlavorNames() );
        flavorList.setPreferredSize( new Dimension( 200, 200 ) );
        flavorList.setBorder( BorderFactory.createTitledBorder( "Simulations" ) );
        add( flavorList );

        localeList = new JList( project.getLocales() );
        localeList.setBorder( BorderFactory.createTitledBorder( "Locales" ) );
        localeList.setPreferredSize( new Dimension( 200, 200 ) );
        localeList.setMinimumSize( new Dimension( 200, 200 ) );
        add( localeList );

        setProject( project );
    }

    public void setProject( PhetProject project ) {
        this.project = project;
        titleLabel.setText( project.getName() + " (" + project.getVersionString() + ")" );
        flavorList.setListData( project.getFlavorNames() );
        flavorList.setSelectedIndex( 0 );
        localeList.setListData( project.getLocales() );
        changesTextArea.setText( loadChangedText() );
    }

    private String loadChangedText() {
        File changesFile = project.getChangesFile();
        if ( !changesFile.exists() ) {
            return "";
        }
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( changesFile ) );
            String s = "";
            String line = bufferedReader.readLine();
            while ( line != null ) {
                s += line + "\n";
                line = bufferedReader.readLine();
            }
            return s;
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
