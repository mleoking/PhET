package edu.colorado.phet.website.test;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.response.StringResponse;

import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public class CacheTestPanel extends PhetPanel {

    private static final Logger logger = Logger.getLogger( CacheTestPanel.class.getName() );

    private static StringResponse fakeResponse;

    public CacheTestPanel( String id, PageContext context ) {
        super( id, context );
        //logger.debug( "*(after) public CacheTestPanel( String id, PageContext context )" );

        add( HeaderContributor.forCss( CSS.WARNING ) );

        add( new Label( "locale", getLocale().toString() ) );
    }

/*
    @Override
    public Locale getMyLocale() {
        logger.debug( "* public Locale getMyLocale()" );
        return super.getMyLocale();
    }

    @Override
    public Session getHibernateSession() {
        logger.debug( "* public Session getHibernateSession()" );
        return super.getHibernateSession();
    }

    @Override
    public NavMenu getNavMenu() {
        logger.debug( "* public NavMenu getNavMenu()" );
        return super.getNavMenu();
    }

    @Override
    public Locale getLocale() {
        Locale ret = super.getLocale();
        logger.debug( "* public Locale getLocale(): " + ret.toString() );
        return ret;
    }

    @Override
    public PhetRequestCycle getPhetCycle() {
        logger.debug( "* public PhetRequestCycle getPhetCycle()" );
        return super.getPhetCycle();
    }

    @Override
    public PhetLocalizer getPhetLocalizer() {
        logger.debug( "* public PhetLocalizer getPhetLocalizer()" );
        return super.getPhetLocalizer();
    }

    @Override
    public ServletContext getServletContext() {
        logger.debug( "* public ServletContext getServletContext()" );
        return super.getServletContext();
    }

    @Override
    protected void onComponentTag( ComponentTag tag ) {
        logger.debug( "* protected void onComponentTag( ComponentTag tag )" );
        super.onComponentTag( tag );
    }

    @Override
    protected void onComponentTagBody( MarkupStream markupStream, ComponentTag openTag ) {
        logger.debug( "* protected void onComponentTagBody( MarkupStream markupStream, ComponentTag openTag )" );
        super.onComponentTagBody( markupStream, openTag );
    }

    @Override
    public void renderHead( HtmlHeaderContainer container ) {
        logger.debug( "* public void renderHead( HtmlHeaderContainer container )" );
        //HtmlHeaderContainerWrapper wrapper = new HtmlHeaderContainerWrapper( container );
        //super.renderHead( wrapper );

        StringResponse fakeResponse = new StringResponse();
        RequestCycle cycle = getRequestCycle();
        Response response = cycle.getResponse();

        cycle.setResponse( fakeResponse );
        super.renderHead( container );
        cycle.setResponse( response );

        logger.debug( "***\n" + fakeResponse.getBuffer() );

        response.write( fakeResponse.getBuffer() );


        //    @Override
//    protected void onRender( MarkupStream markupStream ) {
//        if ( isCacheable() ) {
//            StringResponse fakeResponse;
//
//            RequestCycle cycle = getRequestCycle();
//            Response response = cycle.getResponse();
//            synchronized( this ) {
//                if ( cachedVersion == null ) {
//                    logger.debug( "not cached" );
//                    fakeResponse = new StringResponse();
//
//                    cycle.setResponse( fakeResponse );
//                    super.onRender( markupStream );
//                    cycle.setResponse( response );
//
//                    cachedVersion =
//                }
//                else {
//                    logger.debug( "cached" );
//                    markupStream.skipComponent();
//                }
//            }
//
//            logger.debug( "fakeResponse: " + fakeResponse.getBuffer() );
//
//            response.write( fakeResponse.getBuffer() );
//        }
//        else {
//            super.onRender( markupStream );
//        }
//    }
    }

    @Override
    public HeaderPartContainer newHeaderPartContainer( String id, String scope ) {
        logger.debug( "* public HeaderPartContainer newHeaderPartContainer( String id, String scope )" );
        return super.newHeaderPartContainer( id, scope );
    }

    @Override
    public String getMarkupType() {
        String ret = super.getMarkupType();
        logger.debug( "* public String getMarkupType(): " + ret );
        return ret;
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
    public boolean isTransparentResolver() {
        logger.debug( "* public boolean isTransparentResolver()" );
        return super.isTransparentResolver();
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

        RequestCycle cycle = getRequestCycle();
        Response response = cycle.getResponse();
        synchronized( this ) {
            if ( fakeResponse == null ) {
                logger.debug( "not cached" );
                fakeResponse = new StringResponse();

                cycle.setResponse( fakeResponse );
                super.onRender( markupStream );
                cycle.setResponse( response );

            }
            else {
                logger.debug( "cached" );
                markupStream.skipComponent();
            }
        }

        logger.debug( "fakeResponse: " + fakeResponse.getBuffer() );

        response.write( fakeResponse.getBuffer() );
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
        //logger.debug( "* public String getId()" );
        return super.getId();
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
    public org.apache.wicket.Session getSession() {
        logger.debug( "* public org.apache.wicket.Session getSession()" );
        return super.getSession();
    }

    @Override
    public long getSizeInBytes() {
        logger.debug( "* public long getSizeInBytes()" );
        return super.getSizeInBytes();
    }

    @Override
    public String getVariation() {
        String ret = super.getVariation();
        logger.debug( "* public String getVariation():" + ret );
        return ret;
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
        boolean ret = super.isVisible();
        logger.debug( "* public boolean isVisible(): " + ret );
        return ret;
    }

    @Override
    public Component remove( IBehavior behavior ) {
        logger.debug( "* public Component remove( IBehavior behavior )" );
        return super.remove( behavior );
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
        List ret = super.getBehaviors( type );
        logger.debug( "* protected List getBehaviors( Class type ): size " + ret.size() );
        for ( Object o : ret ) {
            logger.debug( "  " + o );
        }
        return ret;
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
    protected void onDetach() {
        logger.debug( "* protected void onDetach()" );
        super.onDetach();
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
*/

}
