/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:56:11 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class ParticleSetGraphic extends CompositeInteractiveGraphic {
    CCK3Module module;
    private BufferedImage image;
    Hashtable table = new Hashtable();

    public ParticleSetGraphic( CCK3Module module ) throws IOException {
        this.module = module;
        this.image = module.getImageSuite().getParticleImage();
    }

    public void addGraphic( Electron e ) {
        ElectronGraphic eg = new ElectronGraphic( e, module.getTransform(), image, module.getApparatusPanel(), module );
        addGraphic( eg, 3 );
        table.put( e, eg );
    }

    public boolean contains( int x, int y ) {
        return false;
    }

    public void removeGraphics( Electron[] electrons ) {
        for( int i = 0; i < electrons.length; i++ ) {
            Electron electron = electrons[i];
            ElectronGraphic eg = (ElectronGraphic)table.get( electron );
            super.removeGraphic( eg );
        }
    }
}
