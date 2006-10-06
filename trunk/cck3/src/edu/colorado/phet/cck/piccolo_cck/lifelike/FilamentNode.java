package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.model.CurrentVoltListener;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Filament;
import edu.colorado.phet.cck.piccolo_cck.PhetPPath;
import edu.colorado.phet.cck.piccolo_cck.TotalBulbComponentNode;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.Area;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:50:04 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class FilamentNode extends PhetPNode {
    private Filament fil;
    private TotalBulbComponentNode totalBulbComponentNode;
    private Stroke stroke = new BasicStroke( 8 / 80.0f );

    public FilamentNode( Filament fil, TotalBulbComponentNode totalBulbComponentNode ) {
        this.fil = fil;
        this.totalBulbComponentNode = totalBulbComponentNode;
        totalBulbComponentNode.getBulb().addCurrentVoltListener( new CurrentVoltListener() {
            public void currentOrVoltageChanged( Branch branch ) {
                update();
            }
        } );
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
    }

    protected void paintAfterChildren( PPaintContext paintContext ) {
        paintContext.popClip( null );
    }

    private void update() {
        double intensity = totalBulbComponentNode.getBulb().getIntensity();
        PPath path = new PhetPPath( fil.getPath(), stroke, new Color( (float)( intensity ), (float)( intensity * .4 ), (float)( intensity * .5 ) ) );
        removeAllChildren();
        addChild( path );
    }

    public Filament getFilament() {
        return fil;
    }

}
