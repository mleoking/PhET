// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro;

import java.awt.*;
import java.io.IOException;
import java.util.Hashtable;

import edu.colorado.phet.common.conductivity.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.conductivity.view.graphics.Graphic;
import edu.colorado.phet.common.conductivity.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.conductivity.common.ClipGraphic;
import edu.colorado.phet.conductivity.common.TransformGraphic;
import edu.colorado.phet.conductivity.macro.bands.*;
import edu.colorado.phet.conductivity.macro.circuit.MacroCircuitGraphic;

public class BandSetGraphic extends TransformGraphic
        implements BandParticleObserver {

    public BandSetGraphic( ModelViewTransform2D modelviewtransform2d, Shape shape, DefaultBandSet defaultbandset ) {
        super( modelviewtransform2d );
        graphic = new CompositeInteractiveGraphic();
        bandParticleGraphicTable = new Hashtable();
        clipGraphic = new ClipGraphic( getTransform(), graphic, shape );
        BandGraphic bandgraphic = new BandGraphic( defaultbandset.getUpperBand(), modelviewtransform2d );
        graphic.addGraphic( bandgraphic, 1.0D );
        BandGraphic bandgraphic1 = new BandGraphic( defaultbandset.getLowerBand(), modelviewtransform2d );
        graphic.addGraphic( bandgraphic1, 1.0D );
    }

    public void particleRemoved( BandParticle bandparticle ) {
        Graphic graphic1 = (Graphic) bandParticleGraphicTable.get( bandparticle );
        graphic.remove( graphic1 );
        bandParticleGraphicTable.remove( bandparticle );
    }

    public void particleAdded( BandParticle bandparticle ) {
        try {
            BandParticleGraphic bandparticlegraphic = new BandParticleGraphic( bandparticle, getTransform(), MacroCircuitGraphic.getParticleImage() );
            bandParticleGraphicTable.put( bandparticle, bandparticlegraphic );
            graphic.addGraphic( bandparticlegraphic, 1.0D );
        }
        catch( IOException ioexception ) {
            ioexception.printStackTrace();
        }
    }

    public void update() {
    }

    public void paint( Graphics2D graphics2d ) {
        clipGraphic.paint( graphics2d );
    }

    CompositeInteractiveGraphic graphic;
    ClipGraphic clipGraphic;
    Hashtable bandParticleGraphicTable;
}
