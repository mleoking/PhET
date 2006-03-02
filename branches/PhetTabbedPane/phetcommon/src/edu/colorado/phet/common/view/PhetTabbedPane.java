/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.uitest.TestApp;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * PhetTabbedPane
 * <p>
 * A JTabbedPane that has customizable background and foreground colors for the selected
 * tab.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetTabbedPane extends JTabbedPane {

    private FontUIResource font = new FontUIResource( "Sans", Font.BOLD, 14 );
    private InsetsUIResource insets = new InsetsUIResource( 6, 10, 6, 10 );

    private Color unselectedForeground = Color.black;
    private Color selectedForeground;

    /**
     *
     * @param selectedBackground
     * @param selectedForeground
     */
    public PhetTabbedPane( Color selectedBackground, Color selectedForeground ) {
        setColors( selectedBackground, selectedForeground );
        setListener();
    }

    /**
     *
     * @param tabPlacement
     * @param selectedBackground
     * @param selectedForeground
     */
    public PhetTabbedPane( int tabPlacement, Color selectedBackground, Color selectedForeground ) {
        super( tabPlacement );
        setColors( selectedBackground, selectedForeground );
        setListener();
    }

    /**
     *
     * @param tabPlacement
     * @param tabLayoutPolicy
     * @param selectedBackground
     * @param selectedForeground
     */
    public PhetTabbedPane( int tabPlacement, int tabLayoutPolicy, Color selectedBackground, Color selectedForeground ) {
        super( tabPlacement, tabLayoutPolicy );
        setColors( selectedBackground, selectedForeground );
        setListener();
    }

    /**
     *
     * @param selectedBackground
     * @param selectedForeground
     */
    public void setColors( Color selectedBackground, Color selectedForeground ) {
        this.selectedForeground = selectedForeground;
        UIManager.put( "TabbedPane.selected", new ColorUIResource( selectedBackground ) );
        UIManager.put( "TabbedPane.tabInsets", insets );
        UIManager.put( "TabbedPane.font", font );
        UIManager.put( "TabbedPane.tabAreaInsets", new InsetsUIResource( 10,20,0,20) );
        // This is the outline around the selected tab
//        UIManager.put( "TabbedPane.selectHighlight", new ColorUIResource( Color.red ) );
        // Doesn't seem to do anything
//        UIManager.put( "TabbedPane.highlight", new ColorUIResource( Color.red ) );
//        UIManager.put( "TabbedPane.shadow", new ColorUIResource( Color.red ) );
//        UIManager.put( "TabbedPane.tabDarkShadow", new ColorUIResource( Color.red ) );
//        UIManager.put( "TabbedPane.tabHighlight", new ColorUIResource( Color.red ) );
//        UIManager.put( "TabbedPane.tabShadow", new ColorUIResource( Color.red ) );
        SwingUtilities.updateComponentTreeUI( this );

        setTabForegrounds();
    }

    /**
     * Attaches a change listener that sets the color of the text on the tabs
     */
    private void setListener() {
        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setTabForegrounds();
            }
        } );
    }

    private void setTabForegrounds() {
        int selectedTabIdx = getSelectedIndex();
        int tabCnt = getTabCount();
        for( int i = 0; i < tabCnt; i++ ) {
            if( i == selectedTabIdx ) {
                setForegroundAt( i, selectedForeground );
            }
            else {
                setForegroundAt( i, unselectedForeground );
            }
        }
    }


    /**
     * A test driver for PhetTabbedPane
     */
    static public class TestApp extends PhetApplication{

        static FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 500, 400 );

        public TestApp( String[] args ) {
            super( args, "Test", "Test", "0.01.00", frameSetup );

            PiccoloModule module1 = new TestModule( "Module A");
            PiccoloModule module2 = new TestModule( "Module B");

            addModule( module1 );
            addModule( module2 );

            setSelectedTabBackgroundColor( new Color( 200, 160, 0) );
            setSelectedTabForegroundColor( Color.white );
        }


        class TestModule extends PiccoloModule {
            public TestModule( String name ) {
                super( name, new SwingClock( 40, 1 ));
                setModel( new BaseModel() );
                setSimulationPanel( new JPanel() );
            }
        }


        public static void main( String[] args ) {
            edu.colorado.phet.uitest.TestApp app = new edu.colorado.phet.uitest.TestApp( args );
            app.startApplication();
        }
    }
}
