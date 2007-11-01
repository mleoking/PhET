package edu.colorado.phet.rotation.torque;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.rotation.controls.ResetButton;
import edu.colorado.phet.rotation.controls.ShowVectorsControl;

/**
 * Created by: Sam
 * Oct 31, 2007 at 11:34:53 PM
 */
public class IntroSimulationControlPanel extends JPanel {
    public IntroSimulationControlPanel( final IntroModule torqueModule ) {

        JPanel checkBoxPanel = new VerticalLayoutPanel();
//        final JCheckBox showNonTangentialForces = new JCheckBox( "Allow non-tangential forces", torqueModule.getTorqueModel().isAllowNonTangentialForces() );
//        showNonTangentialForces.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                torqueModule.getTorqueModel().setAllowNonTangentialForces( showNonTangentialForces.isSelected() );
//            }
//        } );
//        checkBoxPanel.add( showNonTangentialForces );

//        final JCheckBox showComponents = new JCheckBox( "Show Components", torqueModule.getTorqueModel().isShowComponents() );
//        showComponents.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                torqueModule.getTorqueModel().setShowComponents( showComponents.isSelected() );
//            }
//        } );
//        checkBoxPanel.add( showComponents );
        checkBoxPanel.add( new ResetButton( torqueModule ) );
//        checkBoxPanel.add( new RulerButton( rulerNode ) );
        checkBoxPanel.add( new ShowVectorsControl( torqueModule.getVectorViewModel() ) );
//        add( checkBoxPanel, getConstraints( 0, 1, 1 ) );
        add( checkBoxPanel );
    }
}
