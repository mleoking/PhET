/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.bulb.BulbGraphic;
import edu.colorado.phet.cck.elements.branch.components.AmmeterBranch;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.Bulb;
import edu.colorado.phet.cck.elements.branch.components.Switch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:38:25 PM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public class LifelikeGraphicFactory implements BranchGraphicFactory {
    public static Stroke branchStroke = new BasicStroke( 22 );
    private static Color highlightColor = Color.yellow;
    public static Stroke WIRE_STROKE = new BasicStroke( 16 );
    private static Stroke bulbHighlightStroke = new BasicStroke( 3 );

    CCK2Module module;
    private BufferedImage switchImage;
    private ModelViewTransform2D transform;
    private Circuit circuit;
    private BufferedImage bulbImage;
    private BufferedImage resistorImage;
    private BufferedImage batteryImage;
    private Color wireColor = DefaultCompositeBranchGraphic.JUNCTION_COLOR;

    public LifelikeGraphicFactory( CCK2Module module, BufferedImage switchImage ) throws IOException {
        this.module = module;
        this.switchImage = switchImage;
        this.transform = module.getTransform();
        this.batteryImage = module.getLifelikeImageSuite().getBatteryImage();
        this.circuit = module.getCircuit();
        bulbImage = module.getImageSuite().getBulbImage();
        this.resistorImage = module.getLifelikeImageSuite().getResistorImage();
    }

    public DefaultCompositeBranchGraphic getSwitchGraphic( Switch branch ) {
        SwitchGraphic sg = null;
        try {
            sg = new SwitchGraphic( circuit, transform, branch, wireColor, WIRE_STROKE, module, switchImage, module.getImageSuite().getImageHandle(), branchStroke, highlightColor );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        DefaultCompositeBranchGraphic dcogs = new DefaultCompositeBranchGraphic( transform, branch, module, sg );
        return dcogs;
    }

    public AbstractBranchGraphic getBulbGraphic( Bulb bulb ) {
        BulbGraphic bg = new BulbGraphic( circuit, transform, bulb, module, bulbImage, bulbHighlightStroke, highlightColor );
        return bg;
    }

    public AbstractBranchGraphic getResistorGraphic( Branch resistor ) {
        ImageBranchGraphic ibg = new ImageBranchGraphic( circuit, module.getTransform(), resistor, wireColor, WIRE_STROKE, module, resistorImage, branchStroke, highlightColor );
        ColorBands cb = new ColorBands( true, ibg );
        ibg.addGraphicAfterImage( cb );
        DefaultCompositeBranchGraphic gr = new DefaultCompositeBranchGraphic( transform, resistor, module, ibg );
        return gr;
    }

    public DefaultCompositeBranchGraphic getWireGraphic( Branch wire ) {
        BranchGraphic beegy = new BranchGraphic( circuit, module.getTransform(), wire, wireColor, WIRE_STROKE, module, highlightColor, branchStroke );
        DefaultCompositeBranchGraphic bg = new DefaultCompositeBranchGraphic( transform, wire, module, beegy );
        wire.addSelectionListener( beegy );
        return bg;
    }

    public AbstractBranchGraphic getBatteryGraphic( Battery branch ) {
        if( branch.getDX() == 0 ) {
//            System.out.println("branch = " + branch + ", dx=0");
            return getImageGraphic( circuit, transform, branch, module, batteryImage );
        }
        BatteryGraphic bg = new BatteryGraphic( circuit, transform, branch, module, batteryImage, DefaultCompositeBranchGraphic.JUNCTION_STROKE, highlightColor );
        return bg;
    }

    public DefaultCompositeBranchGraphic getImageGraphic( Circuit circuit, ModelViewTransform2D transform, Branch branch, CCK2Module module, BufferedImage image ) {
        ImageBranchGraphic ibg = new ImageBranchGraphic( circuit, module.getTransform(), branch, wireColor, WIRE_STROKE, module, image, branchStroke, highlightColor );
        DefaultCompositeBranchGraphic gr = new DefaultCompositeBranchGraphic( transform, branch, module, ibg );
        return gr;
    }

    public void apply( CCK2Module cck2Module ) {
    }


    public AbstractBranchGraphic getAmmeterBranchGraphic( AmmeterBranch resistor ) {
        BufferedImage ammeterImage = null;
        try {
            ammeterImage = module.getImageSuite().getAmmeterImage();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        ImageBranchGraphic ibg = new ImageBranchGraphic( circuit, module.getTransform(), resistor, wireColor, WIRE_STROKE, module, ammeterImage, branchStroke, highlightColor );
        CurrentReadout cb = new CurrentReadout( true, ibg );
        ibg.addGraphicAfterImage( cb );
        DefaultCompositeBranchGraphic gr = new DefaultCompositeBranchGraphic( transform, resistor, module, ibg );
        return gr;
    }

    public void setWireColor( Color color ) {
        this.wireColor = color;
    }
}
