package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import javax.swing.*;
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
    private JButton pausePath;
    private EnergySkateParkModule module;

    public PathRecordContol( final EnergySkateParkModule module ) {
        this.module = module;
        setBorder( BorderFactory.createTitledBorder( EnergySkateParkStrings.getString( "controls.path" ) ) );
        recordPath = new JButton( EnergySkateParkStrings.getString( "controls.record-path" ) );

        recordPath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordPath( true );
                updateButtons();
                clearHistory.setEnabled( true );
            }
        } );
        pausePath = new JButton( EnergySkateParkStrings.getString( "controls.stop-recording-path" ) );
        pausePath.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordPath( false );
                updateButtons();
            }
        } );
        clearHistory = new JButton( EnergySkateParkStrings.getString( "time.clear" ) );
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
