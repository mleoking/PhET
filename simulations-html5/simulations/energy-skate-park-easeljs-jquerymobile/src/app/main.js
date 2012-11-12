require( ['websocket-refresh',
             'introduction-tab',
             'i18n!nls/colors'
         ], function ( WebsocketRefresh, IntroductionTab, Strings ) {
    console.log( Strings.red );
    WebsocketRefresh.listenForRefresh();
    var tab1 = new IntroductionTab();
} );