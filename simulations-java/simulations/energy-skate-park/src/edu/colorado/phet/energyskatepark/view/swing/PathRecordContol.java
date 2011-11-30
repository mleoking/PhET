// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

/**
 * User: Sam Reid
 * Date: Nov 29, 2006
 * Time: 11:09:06 AM
 */

public class PathRecordContol extends HorizontalLayoutPanel {
    private final JButton recordPath;
    private final JButton clearHistory;
    private final AbstractEnergySkateParkModule module;
    private boolean recording = false;

    public PathRecordContol( final AbstractEnergySkateParkModule module ) {
        this.module = module;
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkResources.getString( "controls.path" ) ) );
        recordPath = new JButton( EnergySkateParkResources.getString( "controls.record-path" ) );
        recordPath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setRecording( !recording );
            }
        } );
        clearHistory = new JButton( EnergySkateParkResources.getString( "time.clear" ) );
        clearHistory.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearPaths();
                if ( !recording ) {
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
        recordPath.setText( recording ? EnergySkateParkResources.getString( "controls.stop-recording-path" ) : EnergySkateParkResources.getString( "controls.record-path" ) );
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
