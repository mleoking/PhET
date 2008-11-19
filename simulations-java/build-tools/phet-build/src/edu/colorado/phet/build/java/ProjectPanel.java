package edu.colorado.phet.build.java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Locale;

import javax.swing.*;

import org.jfree.ui.tabbedui.VerticalLayout;

import edu.colorado.phet.build.PhetProject;

public class ProjectPanel extends JPanel {
    private File basedir;
    private PhetProject project;
    private JLabel titleLabel;
    private JTextArea changesTextArea;
    private JList flavorList;
    private JList localeList;
    private JScrollPane changesScrollPane;

    public ProjectPanel( File basedir, PhetProject project ) {
        this.basedir = basedir;
        this.project = project;
        setLayout( new VerticalLayout() );

        titleLabel = new JLabel( project.getName() );
        add( titleLabel );

        changesTextArea = new JTextArea( 10, 30 );
        changesTextArea.setEditable( false );
        changesScrollPane = new JScrollPane( changesTextArea );
        changesScrollPane.setPreferredSize( new Dimension( 600, 250 ) );
        add( changesScrollPane );

        flavorList = new JList( project.getFlavorNames() );
        JScrollPane flavorScrollPane = new JScrollPane( flavorList );
        flavorScrollPane.setBorder( BorderFactory.createTitledBorder( "Simulations" ) );
        flavorScrollPane.setPreferredSize( new Dimension( 300, 100 ) );
        add( flavorScrollPane );

        localeList = new JList( project.getLocales() );
        JScrollPane localeScroll = new JScrollPane( localeList );
        localeScroll.setBorder( BorderFactory.createTitledBorder( "Locales" ) );
        localeScroll.setPreferredSize( new Dimension( 300, 200 ) );
        add( localeScroll );

        JButton launch = new JButton( "Launch" );
        launch.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                launch();
            }
        } );
        add( launch );
        setProject( project );
    }

    private void launch() {
        BuildScript buildScript = new BuildScript( basedir, project, null, null );
        buildScript.runSim(getSelectedLocale(),getFlavor());
    }

    private String getFlavor() {
        return (String) flavorList.getSelectedValue();
    }

    private Locale getSelectedLocale() {
        return (Locale) localeList.getSelectedValue();
    }

    public void setProject( PhetProject project ) {
        this.project = project;
        titleLabel.setText( project.getName() + " (" + project.getVersionString() + ")" );
        flavorList.setListData( project.getFlavorNames() );
        flavorList.setSelectedIndex( 0 );
        localeList.setListData( project.getLocales() );

        changesTextArea.setText( loadChangedText() );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                changesTextArea.scrollRectToVisible( new Rectangle( 0, 0, 1, 1 ) );
                localeList.scrollRectToVisible( new Rectangle( 0, 0, 1, 1 ) );
                localeList.setSelectedValue( new Locale( "en" ), true );
            }
        } );
    }

    private String loadChangedText() {
        File changesFile = project.getChangesFile();
        if ( !changesFile.exists() ) {
            return "";
        }
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( changesFile ) );
            StringBuffer s = new StringBuffer();
            String line = bufferedReader.readLine();
            while ( line != null ) {
                s.append( line ).append( "\n" );
                line = bufferedReader.readLine();
            }
            return s.toString();
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
