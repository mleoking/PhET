/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.particles;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * User: Sam Reid
 * Date: Sep 5, 2003
 * Time: 2:10:37 AM
 * Copyright (c) Sep 5, 2003 by Sam Reid
 */
public class ParticleSetGraphic implements Graphic {
//    ArrayList particleGraphics = new ArrayList();
    ParticleSet particleSet;
    private ModelViewTransform2d transform;
    private CCK2Module module;

    Hashtable table = new Hashtable();//key=branch particle, value= graphic

    public BranchParticleGraphic getGraphic(BranchParticle bp) {
        return (BranchParticleGraphic) table.get(bp);
    }

    public ParticleSetGraphic(ParticleSet particleSet, final ModelViewTransform2d transform, final CCK2Module module) {
        this.particleSet = particleSet;
        this.transform = transform;
        this.module = module;
        particleSet.addParticleSetObserver(new ParticleSetObserver() {
            public void particleRemoved(BranchParticle bp) {
                table.remove(bp);
            }

            public void particleAdded(BranchParticle bp) {
                BranchParticleGraphic bgp = new BranchParticleGraphic(bp, transform, module, module.getImageSuite().getParticleImage(), module.getApparatusPanel());
                if (table.containsKey(bp))
                    throw new RuntimeException("Graphic for particle already exists.");
                table.put(bp, bgp);
            }
        });
    }

    public void paint(Graphics2D g) {
        Collection graphics = table.values();
        for (Iterator iterator = graphics.iterator(); iterator.hasNext();) {
            BranchParticleGraphic branchParticleGraphic = (BranchParticleGraphic) iterator.next();
            branchParticleGraphic.paint(g);
        }
    }

    public void paint(Graphics2D g, BranchParticle particle) {
        BranchParticleGraphic bpg = getGraphic(particle);
        bpg.paint(g);
    }

    public void paint(Graphics2D g, Branch branch) {
        Enumeration k = table.keys();
        while (k.hasMoreElements()) {
            BranchParticle branchParticle = (BranchParticle) k.nextElement();
            if (branchParticle.getBranch() == branch) {
                paint(g, branchParticle);
            }
        }
    }
}
