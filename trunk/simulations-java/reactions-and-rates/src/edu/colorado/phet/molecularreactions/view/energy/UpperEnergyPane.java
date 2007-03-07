/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.awt.geom.Rectangle2D;
import java.awt.*;

public class UpperEnergyPane extends PPath {
    private final Color moleculePaneBackgroundColor = MRConfig.MOLECULE_PANE_BACKGROUND;
    private final Dimension upperPaneSize;

    public UpperEnergyPane(Dimension upperPaneSize) {
        super( new Rectangle2D.Double( 0, 0,
               upperPaneSize.getWidth(),
               upperPaneSize.getHeight() ) );

        this.upperPaneSize = upperPaneSize;

        setWidth( upperPaneSize.getWidth() );
        setHeight( upperPaneSize.getHeight() );
        setPaint( moleculePaneBackgroundColor );
        setStroke( null );
        setVisible( false );
    }
    
    public Dimension getSize() {
        return upperPaneSize;
    }
}
