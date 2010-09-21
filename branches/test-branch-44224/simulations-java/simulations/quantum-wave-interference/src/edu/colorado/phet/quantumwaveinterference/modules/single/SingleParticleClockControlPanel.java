package edu.colorado.phet.quantumwaveinterference.modules.single;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;

/**
 * User: Sam Reid
 * Date: Oct 31, 2006
 * Time: 9:28:16 AM
 */

public class SingleParticleClockControlPanel extends PiccoloClockControlPanel {
    public SingleParticleClockControlPanel( final SingleParticleModule singleParticleModule, IClock clock ) {
        super( clock );
        final JCheckBox rapid = new JCheckBox( "Rapid", false );
        rapid.setBorder( BorderFactory.createLineBorder( Color.blue ) );
        rapid.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                singleParticleModule.setRapid( rapid.isSelected() );
            }
        } );
        add( rapid );
    }
}
