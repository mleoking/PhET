package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Sam Reid
 */
public class SpecialFeaturesMenu extends JMenu {
    public SpecialFeaturesMenu(final MovingManApplication movingManApplication) {//Pass the movingManApplication so we can access the active module
        super("Special Features");//TODO: IL8N should re-use as many strings from the previous version as possible

        add(new JMenuItem(new AbstractAction("Expression Evaluator") {
            public void actionPerformed(ActionEvent e) {
                MovingManModule module = (MovingManModule) movingManApplication.getActiveModule();
                if (!module.getEvaluateExpressionDialogVisible()) {
                    module.setEvaluateExpressionDialogVisible(true);
                    //First time showing the dialog should pause the system instead of switching to expression mode immediately while playing
                    module.setPaused(true);
                }
            }
        }));

        final JCheckBoxMenuItem reverseXAxis = new JCheckBoxMenuItem("Reverse x-axis", !movingManApplication.getPositiveToTheRight().getValue());
        reverseXAxis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                movingManApplication.getPositiveToTheRight().setValue(!reverseXAxis.isSelected());
            }
        });
        movingManApplication.getPositiveToTheRight().addObserver(new SimpleObserver() {
            public void update() {
                reverseXAxis.setSelected(!movingManApplication.getPositiveToTheRight().getValue());
            }
        });
        add(reverseXAxis);

//        final JRadioButtonMenuItem positiveToTheRightMenuItem = new JRadioButtonMenuItem( new AbstractAction( "Positive to the Right" ) {
//            public void actionPerformed( ActionEvent e ) {
//                movingManApplication.getPositiveToTheRight().setValue( true );
//            }
//        } );
//        add( positiveToTheRightMenuItem );
//        final JRadioButtonMenuItem positiveToTheLeftMenuItem = new JRadioButtonMenuItem( new AbstractAction( "Positive to the Left" ) {
//            public void actionPerformed( ActionEvent e ) {
//                movingManApplication.getPositiveToTheRight().setValue( false );
//            }
//        } );
//        add( positiveToTheLeftMenuItem );
//
//        SimpleObserver updateButtonStates = new SimpleObserver() {
//            public void update() {
//                positiveToTheRightMenuItem.setSelected( movingManApplication.getPositiveToTheRight().getValue() );
//                positiveToTheLeftMenuItem.setSelected( !movingManApplication.getPositiveToTheRight().getValue() );
//            }
//        };
//        movingManApplication.getPositiveToTheRight().addObserver( updateButtonStates );
//        updateButtonStates.update();
//
//        addSeparator();
    }
}
