package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Nov 29, 2006
 * Time: 11:09:06 AM
 * Copyright (c) Nov 29, 2006 by Sam Reid
 */

public class PathRecordContol extends HorizontalLayoutPanel {
    private JButton recordPath;
    private JButton clearHistory;
    private JButton pausePath;
    private EnergySkateParkModule module;

    public PathRecordContol( final EnergySkateParkModule module ) {
        this.module = module;
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "path" ) ) );
        recordPath = new JButton( EnergySkateParkStrings.getString( "record" ) );

        recordPath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordPath( true );
                updateButtons();
                clearHistory.setEnabled( true );
            }
        } );
        pausePath = new JButton( EnergySkateParkStrings.getString( "pause_path" ) );
        pausePath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordPath( false );
                updateButtons();
            }
        } );
        clearHistory = new JButton( EnergySkateParkStrings.getString( "clear" ) );
        clearHistory.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearPaths();
            }
        } );
        clearHistory.setEnabled( false );
        add( recordPath );
        add( pausePath );
        add( clearHistory );
        updateButtons();
    }

    private void updateButtons() {
        recordPath.setEnabled( !module.getEnergySkateParkModel().isRecordPath() );
        pausePath.setEnabled( module.getEnergySkateParkModel().isRecordPath() );
    }
}
