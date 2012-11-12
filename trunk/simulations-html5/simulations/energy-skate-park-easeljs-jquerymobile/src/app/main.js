require( ['websocket-refresh',
             'introduction-tab'
         ], function ( WebsocketRefresh, IntroductionTab ) {
    WebsocketRefresh.listenForRefresh();
    var tab1 = new IntroductionTab();
//    var tab2 = new IntroductionTab();
//    console.log( tab );

    $( "#frictionNavBarButton" )
} );