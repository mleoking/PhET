/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeBC;
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ReactionSelectorIcons
 * <p>
 * A static class whose sole purpose in life is to cough up the proper
 * icons for controls used to select reactions.
 * <p>
 * Everything in this class is static. There is one public method. It returns
 * an image icon used to represent a specific reaction. Everything else
 * happens when the class is loaded.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionSelectorIcons {

    private static ImageIcon DEFAULT_ICON;
    private static ImageIcon R1_ICON;
    private static ImageIcon R2_ICON;
    private static ImageIcon R3_ICON;

    private static PImage aNode = new PImage( );
    private static PImage bcNode = new PImage( );

    private static Map reactionToIconMap = new HashMap( );

    static {
        createImages();
        reactionToIconMap.put( Profiles.DEFAULT, DEFAULT_ICON );
        reactionToIconMap.put( Profiles.R1, R1_ICON );
        reactionToIconMap.put( Profiles.R2, R2_ICON  );
        reactionToIconMap.put( Profiles.R3, R3_ICON  );
    }


    /**
     * Returns an ImageIcon that represents the reaction for a specified energy profile
     * @param profile
     * @return
     */
    public static ImageIcon getIcon( EnergyProfile profile ) {
        ImageIcon icon = (ImageIcon)reactionToIconMap.get( profile );
        if( icon == null ) {
            throw new RuntimeException( "internal error");
        }
        return icon;
    }

    private static void createImages() {
        DEFAULT_ICON = new ImageIcon( createImage( Profiles.DEFAULT ) );
        R1_ICON = new ImageIcon( createImage( Profiles.R1));
        R2_ICON = new ImageIcon( createImage( Profiles.R2));
        R3_ICON = new ImageIcon( createImage( Profiles.R3));
    }

    private static Image createImage( EnergyProfile profile ) {
        setMoleculeImages( profile );
        Font font = new Font( "Lucida sans", Font.BOLD, 18);
        PText plusA = new PText( "+" );
        plusA.setTextPaint( Color.black );
        plusA.setFont( font );
        PNode pNode = new PNode();
        pNode.addChild( aNode );
        pNode.addChild( plusA );
        pNode.addChild( bcNode );
        plusA.setOffset( aNode.getFullBounds().getWidth() + 3 ,
                         (aNode.getFullBounds().getHeight() - plusA.getFullBounds().getHeight()) / 2 );
        bcNode.setOffset( plusA.getOffset().getX() + plusA.getFullBounds().getWidth() + 3,
                          (aNode.getFullBounds().getHeight() - bcNode.getFullBounds().getHeight()) / 2 );
        return pNode.toImage( );
    }

    private static void setMoleculeImages( EnergyProfile profile ) {
        aNode.setImage( new MoleculeIcon( MoleculeA.class, profile ).getImage() );
        bcNode.setImage( new MoleculeIcon( MoleculeBC.class, profile ).getImage() );
    }

}
