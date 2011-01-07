// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Nov 29, 2006
 * Time: 11:09:06 AM
 */

public class PathRecordContol extends HorizontalLayoutPanel {
    private JButton recordPath;
    private JButton clearHistory;
    private EnergySkateParkModule module;
    private boolean recording = false;

    public PathRecordContol( final EnergySkateParkModule module ) {
        this.module = module;
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "controls.path" ) ) );
        recordPath = new JButton( EnergySkateParkStrings.getString( "controls.record-path" ) );
        recordPath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setRecording( !recording );
            }
        } );
        clearHistory = new JButton( EnergySkateParkStrings.getString( "time.clear" ) );
        clearHistory.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearPaths();
                if( !recording ) {
                    clearHistory.setEnabled( false );
                }
            }
        } );
        clearHistory.setEnabled( false );
        setFill( GridBagConstraints.NONE );
        setAnchor( GridBagConstraints.WEST );
        add( recordPath );
        setAnchor( GridBagConstraints.EAST );
        add( clearHistory );
        updateButtons();
    }

    private void updateButtons() {
        recordPath.setText( recording ? EnergySkateParkStrings.getString( "controls.stop-recording-path" ) : EnergySkateParkStrings.getString( "controls.record-path" ) );
        clearHistory.setEnabled( module.getEnergySkateParkModel().getNumHistoryPoints() > 0 );
    }

    public void reset() {
        setRecording( false );
    }

    private void setRecording( boolean b ) {
        this.recording = b;
        module.setRecordPath( recording );
        updateButtons();
    }
}
