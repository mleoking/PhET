/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit.particles;

import edu.colorado.phet.cck.model.Electron;
import edu.colorado.phet.cck.model.ParticleSet;
import edu.colorado.phet.cck.phetgraphics_cck.CCKModule;
import edu.colorado.phet.common_cck.view.CompositeGraphic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:56:11 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class ParticleSetGraphic extends CompositeGraphic {
    private CCKModule module;
    private BufferedImage image;
    private Hashtable table = new Hashtable();

    public ParticleSetGraphic( CCKModule module ) throws IOException {
        this.module = module;
        this.image = module.getImageSuite().getParticleImage();
        this.module.getParticleSet().addListener( new ParticleSet.Listener() {
            public void particlesRemoved( Electron[]electrons ) {
                removeGraphics( electrons );
            }

            public void particleAdded( Electron e ) {
                addGraphic( e );
            }
        } );
    }

    public void addGraphic( Electron e ) {
        ElectronGraphic eg = new ElectronGraphic( e, module.getTransform(), image, module.getApparatusPanel(), module );
        addGraphic( eg, 3 );
        table.put( e, eg );
//        System.out.println( "Added Electron, table.size() = " + table.size() );
    }

    public boolean contains( int x, int y ) {
        return false;
    }

    public String toString() {
        String s = "";
        s += table.toString();
        s += "\n" + Arrays.asList( super.getGraphics() ).toString();
        return s;
    }

    public void removeGraphics( Electron[] electrons ) {
        for( int i = 0; i < electrons.length; i++ ) {
            Electron electron = electrons[i];
            ElectronGraphic eg = (ElectronGraphic)table.remove( electron );
            if( eg == null ) {
                System.out.println( "No graphic for electron: " + electron );
            }
            else {
                super.removeGraphic( eg );
                eg.delete();
            }
        }
//        System.out.println( "Removed electrons: table.size() = " + table.size() );
    }
}
