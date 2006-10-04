package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.components.Filament;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.BulbComponentGraphic;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:50:04 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class FilamentNode extends PhetPNode {
    private Filament fil;
    private TotalBulbComponentNode totalBulbComponentNode;
    private Color color = Color.black;
    private Stroke stroke = new BasicStroke( 8 / 80.0f );
    private BulbComponentGraphic.IntensityChangeListener intensityListener;

    public FilamentNode( Filament fil, TotalBulbComponentNode totalBulbComponentNode ) {
        this.fil = fil;
        this.totalBulbComponentNode = totalBulbComponentNode;
        intensityListener = new BulbComponentGraphic.IntensityChangeListener() {
            public void intensityChanged( BulbComponentGraphic bulbComponentGraphic, double intensity ) {
                color = new Color( (float)( intensity ), (float)( intensity * .4 ), (float)( intensity * .5 ) );
            }
        };

        fil.addObserver( new SimpleObserver() {
            public void update() {
                FilamentNode.this.update();
            }
        } );
        update();
        setPickable( false );
        setChildrenPickable( false );
    }

    protected void paint( PPaintContext paintContext ) {
        Shape origClip = paintContext.getGraphics().getClip();
        Area area = new Area( origClip );
        area.subtract( new Area( totalBulbComponentNode.getClipShape( this ) ) );
        paintContext.pushClip( area );
        paintContext.pushClip( area );
    }

    protected void paintAfterChildren( PPaintContext paintContext ) {
        paintContext.popClip( null );
    }

    private void update() {
        GeneralPath p = fil.getPath();
//        Shape x = AffineTransform.getTranslateInstance( -p.getBounds().x, -p.getBounds().y ).createTransformedShape( p );
        PPath path = new PhetPPath( p, stroke, color );
        removeAllChildren();
        addChild( path );
    }

    public Filament getFilament() {
        return fil;
    }

}
