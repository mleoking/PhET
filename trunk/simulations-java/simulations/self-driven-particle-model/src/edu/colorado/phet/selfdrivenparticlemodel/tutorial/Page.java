// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import java.awt.geom.Point2D;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.util.JSAudioPlayer;
import edu.colorado.phet.selfdrivenparticlemodel.view.UniverseGraphic;
import edu.umd.cs.piccolo.PNode;

public abstract class Page {
    private double dy = 0;
    private String text = "";//includes activity
    private boolean noActivity = false;
    private BasicTutorialCanvas basicPage;
    private String finishText;
    private boolean didShowFinishText = false;
    private boolean advanced = false;

    public Page( BasicTutorialCanvas basicPage ) {
        this.basicPage = basicPage;
    }

    public void setFinishText( String finishText ) {
        this.finishText = finishText;
    }

    protected void artificialAdvance() {
        advanced = true;
    }

    public void init() {
        basicPage.setText( text );
        if ( noActivity ) {
            System.out.println( "No activity: showing next button immediately." );
            basicPage.showNextButton();
            artificialAdvance();
        }
        basicPage.requestFocus();
    }

    public void setText( String text ) {
        this.text = text;
    }

    public BasicTutorialCanvas getBasePage() {
        return basicPage;
    }

    public void playHarp() {
        getBasePage().playHarp();
    }

    protected void advance() {
        if ( !advanced ) {
            if ( getFinishText() != null ) {
                showFinishText();
            }
            playHarp();
            showNextButton();
            advanced = true;
        }
    }

    protected void showFinishText() {
        if ( !didShowFinishText ) {
            append( getFinishText() );
            didShowFinishText = true;
        }
    }

    protected void append( String finishText ) {
        getBasePage().append( finishText );
    }

    private String getFinishText() {
        return finishText;
    }

    public void teardown() {
    }

    protected void showNextButton() {
        getBasePage().showNextButton();
    }

    public void addChild( PNode node ) {
        if ( !getBasePage().getLayer().getChildrenReference().contains( node ) ) {
            getBasePage().addChild( node );
        }
    }

    public void removeChild( PNode node ) {
        while ( getBasePage().getLayer().getChildrenReference().contains( node ) ) {
            getBasePage().removeChild( node );
        }
    }

    public void pauseModel() {
        getBasePage().stopModel();
    }

    public void startModel() {
        getBasePage().startModel();
    }

    public void clearParticles() {
        getBasePage().clearParticles();
    }

    public ParticleModel getParticleModel() {
        return getBasePage().getParticleModel();
    }

    public UniverseGraphic getUniverseGraphic() {
        return getBasePage().getUniverseGraphic();
    }

    public String getFullText() {
        return text + ( finishText == null ? "" : finishText );
    }

    public String getPartialText() {
        return text;
    }

    public String getVisibleText() {
        if ( advanced ) {
            return getFullText();
        }
        else {
            return getPartialText();
        }
    }

    public boolean getAdvanced() {
        return advanced;
    }

    public void playApplause() {
        JSAudioPlayer.playNoBlock( BasicTutorialCanvas.class.getClassLoader().getResource( "self-driven-particle-model/audio/claps.wav" ) );
    }

    public void relayoutChildren() {

    }

    public Point2D getLocationBeneath( PNode node ) {
        return new Point2D.Double( node.getFullBounds().getX(), node.getFullBounds().getMaxY() + getDy() );
    }

    public double getDy() {
        return dy;
    }
}
