/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.colormaps.ColorGrid;
import edu.colorado.phet.qm.view.colormaps3.ComplexColorMapAdapter;
import edu.colorado.phet.qm.view.colormaps3.MagnitudeInGrayscale3;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 12:48:33 PM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class SimpleWavefunctionGraphic extends PNode {
    private PhetPCanvas phetPCanvas;
    private ColorGridNode colorGridNode;
    private PPath borderGraphic;
    private Wavefunction wavefunction;
    private ComplexColorMapAdapter complexColorMapAdapter;

    public SimpleWavefunctionGraphic( final PhetPCanvas canvas, Wavefunction wavefunction ) {
        this.phetPCanvas = canvas;
        this.wavefunction = wavefunction;
        ColorGrid colorGrid = new ColorGrid( 4, 4, wavefunction.getWidth(), wavefunction.getHeight() );//todo support for change of size
        colorGridNode = new ColorGridNode( colorGrid );
        complexColorMapAdapter = new ComplexColorMapAdapter( wavefunction, new MagnitudeInGrayscale3() );

        addChild( colorGridNode );
        update();
        borderGraphic = new PPath( colorGridNode.getFullBounds() );
        borderGraphic.setStroke( new BasicStroke( 2 ) );
        borderGraphic.setStrokePaint( Color.white );
        addChild( borderGraphic );

        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                borderGraphic.setPathTo( colorGridNode.getFullBounds() );
            }
        };
        colorGridNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, pcl );
        colorGridNode.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, pcl );
    }

    public void fullPaint( PPaintContext paintContext ) {
        Graphics2D g = paintContext.getGraphics();

        Object origAnt = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        Object origInt = g.getRenderingHint( RenderingHints.KEY_INTERPOLATION );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        super.fullPaint( paintContext );
        if( origAnt == null ) {
            origAnt = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
        }
        if( origInt == null ) {
            origInt = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        }
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, origAnt );
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, origInt );
    }

    public void update() {
        colorGridNode.setGridDimensions( wavefunction.getWidth(), wavefunction.getHeight() );
        colorGridNode.paint( complexColorMapAdapter );
    }

}
