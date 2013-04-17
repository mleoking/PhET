// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.Photon;
import edu.colorado.phet.lasers.model.LaserModel;

/*
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AbstractLegend extends JPanel {
    private GridBagConstraints iconGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                 1, 1, 1, 1,
                                                                 GridBagConstraints.CENTER,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 60, 5, 10 ),
                                                                 0, 0 );
    private GridBagConstraints labelGbc = new GridBagConstraints( 1, GridBagConstraints.RELATIVE,
                                                                  1, 1, 1, 1,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 0, 0, 5, 10 ),
                                                                  0, 0 );

    public AbstractLegend( String title ) {
        super( new GridBagLayout() );
        setBorder( new TitledBorder( title ) );
    }

    protected BufferedImage getPhotonImage( double wavelength ) {
        Photon photon = new Photon( wavelength, new Point2D.Double(), new MutableVector2D() );
        return PhotonGraphic.getInstance( this, photon ).getImage();
    }

    public void addLegendItem( Image image, String text ) {
        add( new JLabel( new ImageIcon( image ) ), iconGbc );
        add( new JLabel( text ), labelGbc );
    }

    /*
     * Gets a legend image for photon.
     */
    protected BufferedImage createPhotonLegendImage() {
        return append( new BufferedImage[] { getPhotonImage( 680 ), getPhotonImage( 470 ), getPhotonImage( 800 ) } );
    }

    private BufferedImage append( BufferedImage[] images ) {
        int width = 0;
        int height = 0;
        for ( int i = 0; i < images.length; i++ ) {
            BufferedImage image = images[i];
            width += image.getWidth();
            height = Math.max( height, image.getHeight() );
        }
        BufferedImage bufferedImage = new BufferedImage( width, height, images[0].getType() );
        Graphics2D g2 = bufferedImage.createGraphics();
        int x = 0;


        for ( int i = 0; i < images.length; i++ ) {
            g2.drawRenderedImage( images[i], AffineTransform.getTranslateInstance( x, 0 ) );
            x += images[i].getWidth();
        }
        return bufferedImage;
    }

    protected BufferedImage getAtomImage() {
        LaserModel quantumModel = new LaserModel( 1 );
        Atom atom = new Atom( quantumModel, 3 );
        atom.setStates( new AtomicState[] {
                quantumModel.getGroundState(),
                quantumModel.getMiddleEnergyState(),
                quantumModel.getHighEnergyState()
        } );
        atom.setCurrState( quantumModel.getGroundState() );
        AnnotatedAtomGraphic atomGraphic = new AnnotatedAtomGraphic( this, atom );
        BufferedImage atomBI = new BufferedImage( atomGraphic.getWidth(), atomGraphic.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2BI = (Graphics2D) atomBI.getGraphics();
        g2BI.translate( atomGraphic.getWidth() / 2, atomGraphic.getHeight() / 2 );
        g2BI.scale( .8, .8 );
        atomGraphic.paint( g2BI );
        return atomBI;
    }
}