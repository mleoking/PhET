/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 6, 2003
 * Time: 12:47:30 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.graphics.ApparatusPanel;
import edu.colorado.phet.graphics.GraphicFactory;
import edu.colorado.phet.graphics.PhetGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * This is a subclass of PhetMainPanel that uses a tabbed pane for apparatus panels.
 */
public abstract class TabbedMainPanel extends PhetMainPanel {

    private JTabbedPane apparatusTP = new JTabbedPane();
    private ArrayList apparatusPanels = new ArrayList();

    public TabbedMainPanel( PhetApplication application ) {
        super( application );
    }

    /**
     * This must be called by subclass constructors after they have added their
     * apparatus panels.
     */
    protected void initTabs() {
        this.add( apparatusTP, BorderLayout.CENTER );
        apparatusTP.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ApparatusPanel currentApparatusPanel = (ApparatusPanel)getApparatusTP().getSelectedComponent();
                setCurrentApparatusPanel( currentApparatusPanel );
            }
        } );
    }

    protected JTabbedPane getApparatusTP() {
        return apparatusTP;
    }

    protected ArrayList getApparatusPanels() {
        return apparatusPanels;
    }

    /**
     * Adds an apparatus panel to the internal list of apparatus panels.
     * @param apparatusPanel
     */
    protected void addApparatusPanel( ApparatusPanel apparatusPanel ) {
        apparatusPanels.add( apparatusPanel );
        apparatusTP.addTab( apparatusPanel.getName(), apparatusPanel );
    }

    protected void setCurrentApparatusPanel( ApparatusPanel apparatusPanel ) {
        super.setApparatusPanel( apparatusPanel );
    }

    /**
     * Selects the apparatus panel that is of a specified Class. This is used
     * to programatically switch apparatus panels. Specifically, it is used by
     * the guided inquiry framework.
     * @param apparatusPanelClass
     */
    public void selectApparatusPanelOfType( Class apparatusPanelClass ) {

        Component selection = null;
        for( int i = 0;
             selection == null && i < apparatusTP.getTabCount();
             i++ ) {
            if( apparatusTP.getComponentAt( i ).getClass() == apparatusPanelClass ) {
                selection = apparatusTP.getComponentAt( i );
            }
        }
        if( selection != null ) {
            apparatusTP.setSelectedComponent( selection );
        }
    }

    public void addBodyAllPanels( edu.colorado.phet.physics.body.Particle body ) {
        addBodyAllPanels( body, super.s_defaultGraphicLevel );
    }

    public void addBodyAllPanels( edu.colorado.phet.physics.body.Particle body, int level ) {
        GraphicFactory graphicFactory = PhetApplication.instance().getGraphicFactory();
        for( int i = 0; i < apparatusPanels.size(); i++ ) {
            ApparatusPanel apparatusPanel = (ApparatusPanel)apparatusPanels.get( i );
            PhetGraphic graphic = graphicFactory.createGraphic( body, apparatusPanel );
            apparatusPanel.addGraphic( graphic, level );
            apparatusPanel.repaint();
        }
    }


    /**
     *
     */
    public void clear() {
        for( int i = 0; i < apparatusPanels.size(); i++ ) {
            ApparatusPanel apparatusPanel = (ApparatusPanel)apparatusPanels.get( i );
            apparatusPanel.clear();
        }
        super.clear();
    }

}
