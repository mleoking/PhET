require( ['websocket-refresh',
             'tab',
             "tpl!view/massSlider.html",
             "tpl!view/frictionControls.html"
         ], function ( WebsocketRefresh, Tab, massSlider, frictionControls ) {

    WebsocketRefresh.listenForRefresh();

    //TODO: move page creation to here, so it reads like $("body").append($(createTab("tab1")))
    new Tab( "tab1", true, massSlider( {id: "tab1"} ) );
    new Tab( "tab2", false, frictionControls( {id: "tab2"} ) );
    new Tab( "tab3", false, frictionControls( {id: "tab3"} ) );
} );