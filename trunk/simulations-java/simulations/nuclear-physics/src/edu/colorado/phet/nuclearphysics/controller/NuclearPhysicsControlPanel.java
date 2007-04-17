/**
 * Class: NuclearPhysicsControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.nuclearphysics.view.*;
import edu.colorado.phet.coreadditions_nuclearphysics.GridBagUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Constructor
 */
public class NuclearPhysicsControlPanel extends JPanel {
    private NuclearPhysicsModule module;
    private int rowIdx = 0;
    private JPanel mainPanel;

    public NuclearPhysicsControlPanel( final NuclearPhysicsModule module, java.util.List modelClasses ) {
        this.module = module;
        JPanel panel = new JPanel(  );
        panel.setLayout( new BorderLayout() );
        panel.add( new LegendPanel(modelClasses), BorderLayout.NORTH );
        mainPanel = new JPanel( new GridBagLayout() );
        panel.add( mainPanel, BorderLayout.CENTER );
        this.add( panel );
    }

    protected NuclearPhysicsModule getModule() {
        return module;
    }

    public void addPanelElement( JPanel panel ) {
        try {
            GridBagUtil.addGridBagComponent( mainPanel, panel,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }
}