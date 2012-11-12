require( ['websocket-refresh',
             'introduction-tab'
         ], function ( WebsocketRefresh, IntroductionTab ) {
    WebsocketRefresh.listenForRefresh();
    var tab = new IntroductionTab();
//    console.log( tab );
} );