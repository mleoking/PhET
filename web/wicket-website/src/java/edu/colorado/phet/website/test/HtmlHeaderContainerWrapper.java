package edu.colorado.phet.website.test;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.util.convert.IConverter;

public class HtmlHeaderContainerWrapper extends HtmlHeaderContainer {

    private HtmlHeaderContainer host;

    private static final Logger logger = Logger.getLogger( HtmlHeaderContainerWrapper.class.getName() );

    public HtmlHeaderContainerWrapper( HtmlHeaderContainer host ) {
        super( host.getId() );

        this.host = host;
    }

    @Override
    protected boolean renderOpenAndCloseTags() {
        logger.debug( "* protected boolean renderOpenAndCloseTags()" );
        return super.renderOpenAndCloseTags();
    }

    @Override
    public boolean isTransparentResolver() {
        logger.debug( "* public boolean isTransparentResolver()" );
        return super.isTransparentResolver();
    }

    @Override
    protected void onDetach() {
        logger.debug( "* protected void onDetach()" );
        super.onDetach();
    }

    @Override
    protected IHeaderResponse newHeaderResponse() {
        logger.debug( "* protected IHeaderResponse newHeaderResponse()" );
        return super.newHeaderResponse();
    }

    @Override
    public IHeaderResponse getHeaderResponse() {
        logger.debug( "* public IHeaderResponse getHeaderResponse()" );
        return super.getHeaderResponse();
    }

    @Override
    public String getMarkupType() {
        logger.debug( "* public String getMarkupType()" );
        return super.getMarkupType();
    }

    @Override
    public MarkupStream getAssociatedMarkupStream( boolean throwException ) {
        logger.debug( "* public MarkupStream getAssociatedMarkupStream( boolean throwException )" );
        return super.getAssociatedMarkupStream( throwException );
    }

    @Override
    public void internalAdd( Component child ) {
        logger.debug( "* public void internalAdd( Component child )" );
        super.internalAdd( child );
    }

    @Override
    public void remove( Component component ) {
        logger.debug( "* public void remove( Component component )" );
        super.remove( component );
    }

    @Override
    public Component setModel( IModel model ) {
        logger.debug( "* public Component setModel( IModel model )" );
        return super.setModel( model );
    }

    @Override
    public String toString() {
        logger.debug( "* public String toString()" );
        return super.toString();
    }

    @Override
    public String toString( boolean detailed ) {
        logger.debug( "* public String toString( boolean detailed )" );
        return super.toString( detailed );
    }

    @Override
    protected void onRender( MarkupStream markupStream ) {
        logger.debug( "* protected void onRender( MarkupStream markupStream )" );
        super.onRender( markupStream );
    }

    @Override
    protected void renderAll( MarkupStream markupStream ) {
        logger.debug( "* protected void renderAll( MarkupStream markupStream )" );
        super.renderAll( markupStream );
    }

    @Override
    public boolean hasAssociatedMarkup() {
        logger.debug( "* public boolean hasAssociatedMarkup()" );
        return super.hasAssociatedMarkup();
    }

    @Override
    public Component add( IBehavior behavior ) {
        logger.debug( "* public Component add( IBehavior behavior )" );
        return super.add( behavior );
    }

    @Override
    public void detachModels() {
        logger.debug( "* public void detachModels()" );
        super.detachModels();
    }

    @Override
    public IConverter getConverter( Class type ) {
        logger.debug( "* public IConverter getConverter( Class type )" );
        return super.getConverter( type );
    }

    @Override
    public String getId() {
        logger.debug( "* public String getId()" );
        return super.getId();
    }

    @Override
    public Locale getLocale() {
        logger.debug( "* public Locale getLocale()" );
        return super.getLocale();
    }

    @Override
    public String getMarkupId( boolean createIfDoesNotExist ) {
        logger.debug( "* public String getMarkupId( boolean createIfDoesNotExist )" );
        return super.getMarkupId( createIfDoesNotExist );
    }

    @Override
    public String getMarkupId() {
        logger.debug( "* public String getMarkupId()" );
        return super.getMarkupId();
    }

    @Override
    public Session getSession() {
        logger.debug( "* public Session getSession()" );
        return super.getSession();
    }

    @Override
    public long getSizeInBytes() {
        logger.debug( "* public long getSizeInBytes()" );
        return super.getSizeInBytes();
    }

    @Override
    public String getVariation() {
        logger.debug( "* public String getVariation()" );
        return super.getVariation();
    }

    @Override
    public boolean isEnabled() {
        logger.debug( "* public boolean isEnabled()" );
        return super.isEnabled();
    }

    @Override
    public boolean isVersioned() {
        logger.debug( "* public boolean isVersioned()" );
        return super.isVersioned();
    }

    @Override
    public boolean isVisible() {
        logger.debug( "* public boolean isVisible()" );
        return super.isVisible();
    }

    @Override
    public Component remove( IBehavior behavior ) {
        logger.debug( "* public Component remove( IBehavior behavior )" );
        return super.remove( behavior );
    }

    @Override
    public void renderHead( HtmlHeaderContainer container ) {
        logger.debug( "* public void renderHead( HtmlHeaderContainer container )" );
        super.renderHead( container );
    }

    @Override
    public void replaceWith( Component replacement ) {
        logger.debug( "* public void replaceWith( Component replacement )" );
        super.replaceWith( replacement );
    }

    @Override
    public void setMarkupId( String markupId ) {
        logger.debug( "* public void setMarkupId( String markupId )" );
        super.setMarkupId( markupId );
    }

    @Override
    public Component setVersioned( boolean versioned ) {
        logger.debug( "* public Component setVersioned( boolean versioned )" );
        return super.setVersioned( versioned );
    }

    @Override
    protected void checkHierarchyChange( Component component ) {
        logger.debug( "* protected void checkHierarchyChange( Component component )" );
        super.checkHierarchyChange( component );
    }

    @Override
    protected void detachModel() {
        logger.debug( "* protected void detachModel()" );
        super.detachModel();
    }

    @Override
    protected List getBehaviors( Class type ) {
        logger.debug( "* protected List getBehaviors( Class type )" );
        return super.getBehaviors( type );
    }

    @Override
    protected IModelComparator getModelComparator() {
        logger.debug( "* protected IModelComparator getModelComparator()" );
        return super.getModelComparator();
    }

    @Override
    protected boolean getStatelessHint() {
        logger.debug( "* protected boolean getStatelessHint()" );
        return super.getStatelessHint();
    }

    @Override
    protected IModel initModel() {
        logger.debug( "* protected IModel initModel()" );
        return super.initModel();
    }

    @Override
    protected void internalOnModelChanged() {
        logger.debug( "* protected void internalOnModelChanged()" );
        super.internalOnModelChanged();
    }

    @Override
    protected boolean isBehaviorAccepted( IBehavior behavior ) {
        logger.debug( "* protected boolean isBehaviorAccepted( IBehavior behavior )" );
        return super.isBehaviorAccepted( behavior );
    }

    @Override
    protected MarkupStream locateMarkupStream() {
        logger.debug( "* protected MarkupStream locateMarkupStream()" );
        return super.locateMarkupStream();
    }

    @Override
    protected void onAfterRender() {
        logger.debug( "* protected void onAfterRender()" );
        super.onAfterRender();
    }

    @Override
    protected void onBeforeRender() {
        logger.debug( "* protected void onBeforeRender()" );
        super.onBeforeRender();
    }

    @Override
    protected boolean callOnBeforeRenderIfNotVisible() {
        logger.debug( "* protected boolean callOnBeforeRenderIfNotVisible()" );
        return super.callOnBeforeRenderIfNotVisible();
    }

    @Override
    protected void onComponentTag( ComponentTag tag ) {
        logger.debug( "* protected void onComponentTag( ComponentTag tag )" );
        super.onComponentTag( tag );
    }

    @Override
    protected void onModelChanged() {
        logger.debug( "* protected void onModelChanged()" );
        super.onModelChanged();
    }

    @Override
    protected void onModelChanging() {
        logger.debug( "* protected void onModelChanging()" );
        super.onModelChanging();
    }

    @Override
    public int hashCode() {
        logger.debug( "* public int hashCode()" );
        return super.hashCode();
    }

    @Override
    public boolean equals( Object o ) {
        logger.debug( "* public boolean equals( Object o )" );
        return super.equals( o );
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        logger.debug( "* protected Object clone() throws CloneNotSupportedException" );
        return super.clone();
    }

    @Override
    protected void finalize() throws Throwable {
        logger.debug( "* protected void finalize() throws Throwable" );
        super.finalize();
    }

}
