package edu.colorado.phet.movingman.motion.movingman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.movingman.MovingManResources;

/**
 * Created by: Sam
 * Dec 5, 2007 at 8:37:03 PM
 */
public class OptionsMenu extends JMenu {
    public OptionsMenu( JFrame frame, final MovingManOptions module ) {
        super( MovingManResources.getString( "controls.special-features" ) );
        setMnemonic( MovingManResources.getString( "controls.special-features.mnemonic" ).charAt( 0 ) );
        JMenuItem jep = new JMenuItem( MovingManResources.getString( "expressions.title" ) );
        add( jep );
        final MotionExpressionFrame mef = new MotionExpressionFrame( frame, module );
        jep.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mef.setVisible( true );
            }
        } );

        final JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem( MovingManResources.getString( "controls.reverse-x-axis" ), false );
        jcbmi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRightDirPositive( !jcbmi.isSelected() );
            }
        } );
        add( jcbmi );
        addSeparator();
        JRadioButtonMenuItem closed = new JRadioButtonMenuItem( MovingManResources.getString( "options.walls" ), true );
        JRadioButtonMenuItem open = new JRadioButtonMenuItem( MovingManResources.getString( "options.free-range" ), false );
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
