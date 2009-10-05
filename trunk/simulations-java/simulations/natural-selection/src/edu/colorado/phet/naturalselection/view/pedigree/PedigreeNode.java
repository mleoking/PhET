package edu.colorado.phet.naturalselection.view.pedigree;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.GenerationBunnyNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class PedigreeNode extends PNode {

    private PBoundedNode child;

    private NaturalSelectionModel model;

    private static final double BUNNY_WIDTH = 60.0;

    private static final int MAX_LEVEL = 4;

    private static final double LINE_PAD_HORIZ = 2.0;
    private static final double LINE_PAD_VERT = 2.0;

    public PedigreeNode( NaturalSelectionModel model ) {
        this.model = model;

        child = new PBoundedNode();
        PText bunnyText = new PText( NaturalSelectionStrings.PEDIGREE_START_MESSAGE );
        bunnyText.setFont( new PhetFont( 24 ) );
        child.addChild( bunnyText );
        addChild( child );
        System.out.println( "P: " + bunnyText.getHeight() );
        child.setOffset( -bunnyText.getWidth() / 2, 10 );
    }

    public void reset() {
        if ( child != null ) {
            removeChild( child );
            child = null;
        }
    }

    public void displayBunny( Bunny bunny ) {
        System.out.println( "displayBunny (in PedigreeNode)" );
        reset();

        child = getBlock( bunny, 0, getBunnyMaxLevel( bunny ) );
        addChild( child );
    }

    private static double desiredBunnyWidth( int level ) {
        if ( level == 0 ) {
            return 50.0;
        }
        else if ( level == 1 ) {
            return 40.0;
        }
        else if ( level == 2 ) {
            return 30.0;
        }
        else if ( level == 3 ) {
            return 25.0;
        }
        else if ( level == 4 ) {
            return 15.0;
        }

        return 15.0;
    }

    private static double getVPad( int level ) {
        return 10;
    }

    private static double getHPad( int level ) {
        if ( level == 0 ) {
            return 40.0;
        }
        else if ( level == 1 ) {
            return 30.0;
        }
        else if ( level == 2 ) {
            return 20.0;
        }
        else if ( level == 3 ) {
            return 15.0;
        }
        else if ( level == 4 ) {
            return 10.0;
        }

        return 10;
    }

    private static double getBunnyScale( GenerationBunnyNode node, int level ) {
        return desiredBunnyWidth( level ) / node.getBunnyWidth();
    }

    private static PPath line( double x1, double y1, double x2, double y2 ) {
        PPath ret = PPath.createLine( (float) x1, (float) y1, (float) x2, (float) y2 );
        ret.setStroke( new BasicStroke( 1.0f ) );
        ret.setPaint( new Color( 0, 0, 0, 0 ) );
        ret.setStrokePaint( Color.BLACK );
        return ret;
    }

    private static PBoundedNode getBlock( Bunny bunny, int level, int maxLevel ) {
        PBoundedNode ret = new PBoundedNode();

        GenerationBunnyNode bunnyNode = new GenerationBunnyNode( bunny );
        bunnyNode.setSelected( bunny.isSelected() );
        double bunnyScale = getBunnyScale( bunnyNode, level );
        double bunnyWidth = bunnyScale * bunnyNode.getBunnyWidth();
        double bunnyHeight = bunnyScale * bunnyNode.getBunnyHeight();
        bunnyNode.scale( bunnyScale );
        ret.addChild( bunnyNode );

        ret.setBunnyWidth( bunnyWidth );
        ret.setBunnyHeight( bunnyHeight );

        if ( level == maxLevel ) {
            bunnyNode.setOffset( -bunnyWidth / 2, 0 );
            ret.setBoundWidth( bunnyWidth );
            ret.setBoundHeight( bunnyHeight );
        }
        else {
            PBoundedNode fatherNode = getBlock( bunny.getFather(), level + 1, maxLevel );
            PBoundedNode motherNode = getBlock( bunny.getMother(), level + 1, maxLevel );
            double fatherWidth = fatherNode.getBoundWidth();
            double fatherHeight = fatherNode.getBoundHeight();
            double motherWidth = motherNode.getBoundWidth();
            double motherHeight = motherNode.getBoundHeight();
            double fatherOffset = -getHPad( maxLevel ) / 2 - fatherWidth / 2;
            fatherNode.setOffset( fatherOffset, 0 );
            double motherOffset = getHPad( maxLevel ) / 2 + motherWidth / 2;
            motherNode.setOffset( motherOffset, 0 );
            ret.setBoundWidth( fatherWidth + motherWidth + getHPad( maxLevel ) );
            double maxChildHeight = fatherHeight > motherHeight ? fatherHeight : motherHeight;
            double maxChildBunnyHeight = fatherNode.getBunnyHeight() > motherNode.getBunnyHeight() ? fatherNode.getBunnyHeight() : motherNode.getBunnyHeight();
            bunnyNode.setOffset( -bunnyWidth / 2, maxChildHeight + getVPad( level ) );
            ret.setBoundHeight( maxChildHeight + getVPad( level ) + bunnyHeight );
            ret.addChild( fatherNode );
            ret.addChild( motherNode );
            double crossHeight = maxChildHeight - maxChildBunnyHeight / 2;
            ret.addChild( line( fatherOffset + fatherNode.getBunnyWidth() / 2 + LINE_PAD_HORIZ, crossHeight, motherOffset - motherNode.getBunnyWidth() / 2 - LINE_PAD_HORIZ, crossHeight ) );
            ret.addChild( line( 0, crossHeight, 0, maxChildHeight + getVPad( level ) - LINE_PAD_VERT ) );
        }

        /*
        PPath path = PPath.createRectangle( -(float) ret.getBoundWidth() / 2, 0, (float) ret.getBoundWidth(), (float) ret.getBoundHeight() );
        path.setStroke( new BasicStroke( 2.0f ) );
        path.setPaint( new Color( 0, 0, 0, 0 ) );
        path.setStrokePaint( Color.BLUE );
        ret.addChild( path );
        */

        return ret;
    }

    private static int getBunnyMaxLevel( Bunny bunny ) {
        return getBunnyMaxLevel( bunny, 0 );
    }

    private static int getBunnyMaxLevel( Bunny bunny, int depth ) {
        if ( depth == MAX_LEVEL ) {
            return depth;
        }

        Bunny father = bunny.getFather();
        Bunny mother = bunny.getMother();

        if ( father == null || mother == null ) {
            return depth;
        }

        int fatherLevel = getBunnyMaxLevel( father, depth + 1 );
        int motherLevel = getBunnyMaxLevel( mother, depth + 1 );

        // return min
        return fatherLevel > motherLevel ? motherLevel : fatherLevel;
    }

    private static class PBoundedNode extends PNode {
        private double boundWidth;
        private double boundHeight;
        private double bunnyWidth;
        private double bunnyHeight;

        public double getBoundWidth() {
            return boundWidth;
        }

        public void setBoundWidth( double boundWidth ) {
            this.boundWidth = boundWidth;
        }

        public double getBoundHeight() {
            return boundHeight;
        }

        public void setBoundHeight( double boundHeight ) {
            this.boundHeight = boundHeight;
        }

        public double getBunnyWidth() {
            return bunnyWidth;
        }

        public void setBunnyWidth( double bunnyWidth ) {
            this.bunnyWidth = bunnyWidth;
        }

        public double getBunnyHeight() {
            return bunnyHeight;
        }

        public void setBunnyHeight( double bunnyHeight ) {
            this.bunnyHeight = bunnyHeight;
        }
    }
}
