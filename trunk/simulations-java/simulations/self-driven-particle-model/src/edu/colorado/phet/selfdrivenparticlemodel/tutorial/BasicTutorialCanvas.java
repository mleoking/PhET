// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.model.Particle;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolox.pswing.PSwing;

public class BasicTutorialCanvas extends TutorialCanvas implements IParticleApp {

    private UniverseGraphic universeGraphic;
    private ParticleModel particleModel;
    private TutorialTextArea textArea;
    private PButton nextSwing;
    private PActivity modelActivity;
    private ArrayList particleGraphics = new ArrayList();
    private ArrayList influenceGraphics = new ArrayList();
    private double modelDT = 1.0;
    private PageMaker[] pageMakers;
    private ArrayList pages = new ArrayList();
    private int advancedToPageIndex = 0;
    private boolean halosVisible;
    private PButton prevSwing;
    private boolean showedPrevButton = false;
    private int viewingPageIndex = 0;
    private SelfDrivenParticleModelApplication tutorialApplication;
    private MyPSwing textAreaPSwing;

    static class MyPSwing extends PSwing {

        public MyPSwing( JComponent component ) {
            super( component );
        }

        public void doReshape() {
            getComponent().setBounds( 0, 0, getComponent().getPreferredSize().width, getComponent().getPreferredSize().height );
            double height = Math.max( getComponent().getPreferredSize().height, 150 );
            setBounds( 0, 0, getComponent().getPreferredSize().width, height );
        }
    }

    public BasicTutorialCanvas( SelfDrivenParticleModelApplication tutorialApplication, AbstractUnit unit ) {
        this.tutorialApplication = tutorialApplication;
        unit.setBasePage( this );
        particleModel = new ParticleModel( 550, 550 );
        particleModel.setRandomness( 0.0 );
        universeGraphic = new UniverseGraphic( particleModel );
        if ( SelfDrivenParticleModelApplication.isLowResolution() ) {
            universeGraphic.scale( 0.82 );
        }
        textArea = new TutorialTextArea();
        setLayout( null );
        textAreaPSwing = new MyPSwing( textArea );
        addChild( textAreaPSwing );
        nextSwing = new PButton( this, "Next" );

        nextSwing.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                removeChild( nextSwing );
                nextSection();
            }
        } );

        prevSwing = new PButton( this, "Previous" );
        prevSwing.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                previousSection();
            }
        } );

        relayoutChildren();
        modelActivity = new PActivity( -1, 30 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                particleModel.step( modelDT );
            }
        };

        this.pageMakers = unit.getPageMakers();
        this.advancedToPageIndex = 0;
        Page currentPage = pageMakers[advancedToPageIndex].createPage();
        pages.add( currentPage );
        addKeyListener( tutorialApplication.getKeyHandler() );
        getKeyListeners();
        relayoutChildren();
    }

    public void setText( String text ) {
        textArea.setText( text );
        relayoutChildren();
    }

    public void createUniverse() {
        addChild( universeGraphic );
        topLeft( universeGraphic );
        relayoutChildren();
    }

    public UniverseGraphic getUniverseGraphic() {
        return universeGraphic;
    }

    public void topLeft( PNode node ) {
        int inset = 2;
        node.setOffset( inset, textAreaPSwing.getFullBounds().getMaxY() + inset );
    }


    public void append( String finishText ) {
        textArea.setText( textArea.getText() + finishText );
        relayoutChildren();
    }

    public void clearParticles() {
        while ( getParticleModel().numParticles() > 0 ) {
            removeParticle( getParticleModel().lastParticle() );
        }
    }

    public ParticleModel getParticleModel() {
        return particleModel;
    }

    static final Random random = new Random();

    public void setNumberParticles( int numParticles ) {
        boolean changed = false;
        while ( numParticles < particleModel.numParticles() ) {
            removeParticle( particleModel.lastParticle() );
            changed = true;
        }
        while ( numParticles > particleModel.numParticles() ) {
            Color color = ParticleGraphic.newRandomColor();
            addParticle( random.nextDouble() * getParticleModel().getBoxWidth(), random.nextDouble() * particleModel.getBoxHeight(), random.nextDouble() * 2 * Math.PI, color );
            changed = false;
        }
    }

    private void removeParticle( Particle particle ) {
        for ( int i = 0; i < particleGraphics.size(); i++ ) {
            ParticleGraphicWithTail particleGraphicWithTail = (ParticleGraphicWithTail) particleGraphics.get( i );
            if ( particleGraphicWithTail.getParticle() == particle ) {
                getUniverseGraphic().removeParticleGraphic( particleGraphicWithTail );
                particleGraphics.remove( particleGraphicWithTail );
                i--;
            }
        }
        for ( int i = 0; i < influenceGraphics.size(); i++ ) {
            ParticleInfluenceGraphic particleInfluenceGraphic = (ParticleInfluenceGraphic) influenceGraphics.get( i );
            if ( particleInfluenceGraphic.getParticle() == particle ) {
                getUniverseGraphic().removeInfluenceGraphic( particleInfluenceGraphic );
                influenceGraphics.remove( particleInfluenceGraphic );
                i--;
            }
        }
        getParticleModel().removeParticle( particle );
    }

    public void addHalos() {
        for ( int i = 0; i < particleGraphics.size(); i++ ) {
            ParticleGraphicWithTail particleGraphicWithTail = (ParticleGraphicWithTail) particleGraphics.get( i );
            if ( !isHaloShowing( particleGraphicWithTail.getParticle() ) ) {
                ParticleInfluenceGraphic particleInfluenceGraphic = new ParticleInfluenceGraphic( getParticleModel(), particleGraphicWithTail.getParticle() );
                universeGraphic.addInfluenceGraphic( particleInfluenceGraphic );
                influenceGraphics.add( particleInfluenceGraphic );
            }
        }
    }

    private boolean isHaloShowing( Particle particle ) {
        for ( int i = 0; i < influenceGraphics.size(); i++ ) {
            ParticleInfluenceGraphic particleInfluenceGraphic = (ParticleInfluenceGraphic) influenceGraphics.get( i );
            if ( particleInfluenceGraphic.getParticle() == particle ) {
                return true;
            }
        }
        return false;
    }

    public Point2D getNextButtonLocation() {
        return new Point2D.Double( textAreaPSwing.getFullBounds().getWidth() - nextSwing.getFullBounds().getWidth() - 5, textAreaPSwing.getFullBounds().getHeight() + 5 );
    }

    public void showNextButton() {
        if ( !getLayer().getChildrenReference().contains( nextSwing ) ) {
            addChild( nextSwing );
            nextSwing.setOffset( getNextButtonLocation() );
        }
        relayoutChildren();
    }

    public void hideNextButton() {
        while ( getLayer().getChildrenReference().contains( nextSwing ) ) {
            removeChild( nextSwing );
        }
        relayoutChildren();
    }

    private void previousSection() {
        if ( viewingPageIndex == 0 && tutorialApplication.isFirstUnit() ) {
            return;
        }
        else if ( viewingPageIndex == 0 ) {
            sectionAt( viewingPageIndex ).teardown();
            tutorialApplication.previousUnit();
            return;
        }
        sectionAt( viewingPageIndex ).teardown();
        viewingPageIndex--;
        sectionAt( viewingPageIndex ).init();

        synchronizeFullText();
        showNextButton();
        relayoutChildren();
    }

    private void synchronizeFullText() {
        String fullText = pageMakers[viewingPageIndex].createPage().getFullText();
        textArea.setText( fullText );
        showNextButton();
    }

    Page sectionAt( int i ) {
        return (Page) pages.get( i );
    }

    private void synchronizeLastPage() {
        String visibleText = sectionAt( viewingPageIndex ).getVisibleText();
        textArea.setText( visibleText );
        if ( sectionAt( viewingPageIndex ).getAdvanced() ) {
            showNextButton();
        }
    }

    private void nextSection() {
        if ( viewingPageIndex < advancedToPageIndex ) {
            sectionAt( viewingPageIndex ).teardown();
            viewingPageIndex++;
            sectionAt( viewingPageIndex ).init();
            if ( viewingPageIndex == advancedToPageIndex ) {
                synchronizeLastPage();
            }
            else {
                synchronizeFullText();
            }
        }
        else {
            if ( advancedToPageIndex + 1 < pageMakers.length ) {
                sectionAt( viewingPageIndex ).teardown();
                //todo check for available in array
                advancedToPageIndex++;
                viewingPageIndex++;
                Page next = pageMakers[advancedToPageIndex].createPage();
                pages.add( next );

                sectionAt( viewingPageIndex ).init();
            }
        }
        if ( !showedPrevButton ) {
            showedPrevButton = true;
            showPrevButton();
        }
    }

    private void showPrevButton() {
        if ( !getLayer().getChildrenReference().contains( prevSwing ) ) {
            addChild( prevSwing );
            relayoutChildren();
        }
    }

    public void addParticle( double x, double y, double angle, Color color ) {
        Particle particle = new Particle( x, y, angle );
        ParticleGraphicWithTail particleGraphic = new ParticleGraphicWithTail( particle );
        particleGraphic.setColor( color );
        particleModel.addParticle( particle );
        particle.setLocation( x, y );//redundant?
        universeGraphic.addParticleGraphic( particleGraphic );
        particleGraphics.add( particleGraphic );
        if ( halosVisible ) {
            addHalos();
        }
    }

    public void startModel() {
        getRoot().addActivity( modelActivity );
    }

    public void stopModel() {
        getRoot().getActivityScheduler().removeActivity( modelActivity );
    }

    public void start( SelfDrivenParticleModelApplication tutorialApplication ) {
        super.start( tutorialApplication );
        sectionAt( viewingPageIndex ).init();
    }

    protected void relayoutChildren() {

        super.relayoutChildren();
        if ( textArea != null ) {
            int textBoundsHeight = 150;
            textArea.setBounds( 0, 0, getWidth(), textBoundsHeight );
            textAreaPSwing.doReshape();
        }
        testTopLeft( universeGraphic );

        nextSwing.setOffset( getNextButtonLocation() );
        prevSwing.setOffset( 2 + universeGraphic.getFullBounds().getMaxX(), nextSwing.getFullBounds().getY() );

        if ( getCurrentPage() != null ) {
            getCurrentPage().relayoutChildren();
        }
    }

    private Page getCurrentPage() {
        if ( viewingPageIndex >= 0 && viewingPageIndex < pages.size() ) {
            return sectionAt( viewingPageIndex );
        }
        else {
            return null;
        }
    }

    private void testTopLeft( PNode node ) {
        if ( node != null ) {
            topLeft( node );
        }
    }

    public void setHalosVisible( boolean halosVisible ) {
        this.halosVisible = halosVisible;
        if ( halosVisible ) {
            addHalos();
        }
        else {
            removeHalos();
        }
    }

    private void removeHalos() {
        for ( int i = 0; i < influenceGraphics.size(); i++ ) {
            ParticleInfluenceGraphic particleInfluenceGraphic = (ParticleInfluenceGraphic) influenceGraphics.get( i );
            influenceGraphics.remove( particleInfluenceGraphic );
            getUniverseGraphic().removeInfluenceGraphic( particleInfluenceGraphic );
            i--;
        }
    }

    public boolean isHalosVisible() {
        return halosVisible;
    }

    public PButton getPreviousButton() {
        return prevSwing;
    }

    public SelfDrivenParticleModelApplication getTutorialApplication() {
        return tutorialApplication;
    }

    public void moveRight() {
        nextSection();
    }

    public void moveLeft() {
        previousSection();
    }


}
