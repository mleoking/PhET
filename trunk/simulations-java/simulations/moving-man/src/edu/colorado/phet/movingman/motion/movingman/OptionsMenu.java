package edu.colorado.phet.movingman.motion.movingman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;

/**
 * Created by: Sam
 * Dec 5, 2007 at 8:37:03 PM
 */
public class OptionsMenu extends JMenu {
    public OptionsMenu( JFrame frame, final MovingManOptions module ) {
        super( SimStrings.get( "controls.special-features" ) );
        setMnemonic( SimStrings.get( "controls.special-features.mnemonic" ).charAt( 0 ) );
        JMenuItem jep = new JMenuItem( SimStrings.get( "expressions.title" ) );
        add( jep );
        final MotionExpressionFrame mef = new MotionExpressionFrame( frame, module );
        jep.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mef.setVisible( true );
            }
        } );

        final JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem( SimStrings.get( "controls.reverse-x-axis" ), false );
        jcbmi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean ok = module.confirmClear();
                if ( ok ) {
                    module.setRightDirPositive( !jcbmi.isSelected() );
                }
            }
        } );
        add( jcbmi );
        addSeparator();
        JRadioButtonMenuItem closed = new JRadioButtonMenuItem( SimStrings.get( "options.walls" ), true );
        JRadioButtonMenuItem open = new JRadioButtonMenuItem( SimStrings.get( "options.free-range" ), false );
        open.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setBoundaryOpen( true );
            }
        } );
        closed.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setBoundaryOpen( false );
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( closed );
        buttonGroup.add( open );
        add( closed );
        add( open );
    }

    public static interface MovingManOptions {
        boolean confirmClear();

        void setRightDirPositive( boolean b );

        void setBoundaryOpen( boolean b );

        TimeSeriesModel getTimeSeriesModel();

        void setExpressionUpdate( String text );
    }
}
