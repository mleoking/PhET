package edu.colorado.phet.forces1d;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.view.FreeBodyDiagramSuite;

/**
 * User: Sam Reid
 * Date: Nov 22, 2004
 * Time: 11:11:57 AM
 */
public class Forces1DControlPanel extends IForceControl {
    private Forces1DModule module;
    private Force1DModel model;

    private FreeBodyDiagramSuite freeBodyDiagramSuite;
    private JComboBox comboBox;

    private BarrierCheckBox barriers;


    public Forces1DControlPanel( final Forces1DModule module ) {
        super( module );
        this.module = module;
        model = module.getForceModel();
        freeBodyDiagramSuite = new FreeBodyDiagramSuite( module );

        JButton lessControls = new JButton( Force1DResources.get( "Force1dControlPanel.lessControls" ) );
        lessControls.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSimpleControlPanel();
            }
        } );

        freeBodyDiagramSuite.setControlPanel( this );
        addControl( new FBDButton( freeBodyDiagramSuite ) );
        addControl( freeBodyDiagramSuite.getFreeBodyDiagramPanel() );

        addControl( new ShowComponentForcesCheckBox( module ) );
        addControl( new ShowTotalForceCheckBox( module ) );

        comboBox = new ObjectComboBox( module, module.getImageElements(), this );
        addControl( comboBox );


        barriers = new BarrierCheckBox( module );

        module.setObject( module.imageElementAt( 0 ) );
        super.setHelpPanelEnabled( true );
        if ( Toolkit.getDefaultToolkit().getScreenSize().width >= 1280 ) {

        }
        else {
            super.removeTitle();
        }


        JPanel smallPanel = new JPanel();
        smallPanel.add( barriers );

        addControlFullWidth( smallPanel );

//        gravity.setPaintTicks( false );

        addControl( lessControls );
    }

    public void reset() {
        freeBodyDiagramSuite.reset();
    }

    public void handleUserInput() {
        freeBodyDiagramSuite.handleUserInput();
    }

    public void updateGraphics() {
        freeBodyDiagramSuite.updateGraphics();
    }

    public FreeBodyDiagramSuite getFreeBodyDiagramSuite() {
        return freeBodyDiagramSuite;

    }

}

