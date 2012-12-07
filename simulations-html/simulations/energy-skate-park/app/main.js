require( ['util/WebsocketRefresh',
             '../../../common/phet/app/analytics/Analytics',
             'i18n!../nls/energy-skate-park-strings',
             'view/Tab',
             'easel', 'tpl!../tab.html'
         ], function ( WebsocketRefresh, Analytics, Strings, Tab, Easel, tabTemplate ) {

    WebsocketRefresh.listenForRefresh();
    var analytics = new Analytics();

    var $container = $( '#container' );
    $container.append( tabTemplate() );

    new Tab( $( "#tab1" ), Easel, Strings, analytics ).render();

    //TODO: move page creation to here, so it reads like $("body").append($(createTab("tab1")))
//    window.tab1 = new Tab( "tab1", true, massSlider( {id: "tab1"} ), analytics );
//    new Tab( "tab2", false, frictionControls( {id: "tab2"} ), analytics );
//    new Tab( "tab3", false, frictionControls( {id: "tab3"} ), analytics );
} );
