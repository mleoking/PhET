// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.dev;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.module.moleculeshapes.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.util.ColorPropertyControl;

import com.jme3.util.BufferUtils;

public class DeveloperOptions {
    public static void addDeveloperOptions( JMenu developerMenu, final JFrame frame, final MoleculeShapesModule module ) {
        developerMenu.add( new JSeparator() );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Allow drag movement behind the molecule center", MoleculeShapesProperties.allowDraggingBehind ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "\"Move\" mouse cursor on rotation", MoleculeShapesProperties.useRotationCursor ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Disable \"Show Bond Angles\" checkbox with less than 2 bonds", MoleculeShapesProperties.disableNAShowBondAngles ) );
        developerMenu.add( new JSeparator() );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show FPS", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                             public void update() {
                                 module.getApp().statistics.setDisplayFps( get() );
                             }
                         }, false );
        }} ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show Statistics", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                             public void update() {
                                 module.getApp().statistics.setDisplayStatView( get() );
                             }
                         }, false );
        }} ) );
        developerMenu.add( new JMenuItem( "Dump Direct Memory" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    StringBuilder builder = new StringBuilder();
                    BufferUtils.printCurrentDirectMemory( builder );
                    System.out.println( "----------\n" + builder.toString() );
                }
            } );
        }} );
        developerMenu.add( new JSeparator() );
        developerMenu.add( new JMenuItem( "Color Options" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new JDialog( frame ) {{
                        setTitle( "Color Options" );
                        setResizable( false );

                        setContentPane( new JPanel() {{
                            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
                            add( new ColorPropertyControl( frame, "Background: ", MoleculeShapesColor.BACKGROUND.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Control panel borders: ", MoleculeShapesColor.CONTROL_PANEL_BORDER.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Control panel titles: ", MoleculeShapesColor.CONTROL_PANEL_TITLE.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Central atom: ", MoleculeShapesColor.ATOM_CENTER.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Radial atom: ", MoleculeShapesColor.ATOM.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Bonds: ", MoleculeShapesColor.BOND.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Remove all foreground: ", MoleculeShapesColor.REMOVE_BUTTON_TEXT.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Remove all background: ", MoleculeShapesColor.REMOVE_BUTTON_BACKGROUND.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Molecular geometry: ", MoleculeShapesColor.MOLECULAR_GEOMETRY_NAME.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Electron geometry: ", MoleculeShapesColor.ELECTRON_GEOMETRY_NAME.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Bond angle readout: ", MoleculeShapesColor.BOND_ANGLE_READOUT.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Bond angle sweep: ", MoleculeShapesColor.BOND_ANGLE_SWEEP.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Bond angle arc: ", MoleculeShapesColor.BOND_ANGLE_ARC.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Real example formula: ", MoleculeShapesColor.REAL_EXAMPLE_FORMULA.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Real example border: ", MoleculeShapesColor.REAL_EXAMPLE_BORDER.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Light 1 color: ", MoleculeShapesColor.SUN.getProperty() ) );
                            add( new ColorPropertyControl( frame, "Light 2 color: ", MoleculeShapesColor.MOON.getProperty() ) );
                        }} );
                        pack();
                        SwingUtils.centerInParent( this );
                    }}.setVisible( true );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Performance Options" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new PerformanceFrame( module.getApp() );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Show Error Dialog" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getApp().showErrorDialog( new Throwable( "Diagnostic error for testing error dialog" ) );
                }
            } );
        }} );
    }
}