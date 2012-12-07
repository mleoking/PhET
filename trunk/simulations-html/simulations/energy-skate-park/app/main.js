require( ['websocket-refresh',
             'tab',
             "tpl!view/massSlider.html",
             "tpl!view/frictionControls.html",
             '../../../common/phet/app/analytics/Analytics',
             'i18n!../nls/energy-skate-park-strings',
             'view/Tab'
         ], function ( WebsocketRefresh, Tabold, massSlider, frictionControls, Analytics, Strings, Tab ) {

    WebsocketRefresh.listenForRefresh();
    var analytics = new Analytics();

    var tab1 = new Tab( Strings );

    //TODO: move page creation to here, so it reads like $("body").append($(createTab("tab1")))
//    window.tab1 = new Tab( "tab1", true, massSlider( {id: "tab1"} ), analytics );
//    new Tab( "tab2", false, frictionControls( {id: "tab2"} ), analytics );
//    new Tab( "tab3", false, frictionControls( {id: "tab3"} ), analytics );
} );
