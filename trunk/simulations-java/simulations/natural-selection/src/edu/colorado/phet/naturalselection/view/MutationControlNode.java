package edu.colorado.phet.naturalselection.view;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public abstract class MutationControlNode extends PNode {
    
    private JButton addMutationButton;
    private PSwing addMutationButtonHolder;

    public MutationControlNode( BufferedImage iconImage ) {
        addMutationButton = new JButton( "Add Mutation", new ImageIcon( iconImage ) );
        addMutationButtonHolder = new PSwing( addMutationButton );
        addChild( addMutationButtonHolder );
    }

    

    public Point2D getCenter() {
        return new Point2D.Double( getOffset().getX() + addMutationButton.getWidth() / 2, getOffset().getY() + addMutationButton.getHeight() / 2 );
    }

    public abstract Point2D getBunnyLocation( BigVanillaBunny bunny );

}
