require( ['websocket-refresh',
             'introduction-tab',
             'i18n!nls/energy-skate-park-strings'
         ], function ( WebsocketRefresh, IntroductionTab, Strings ) {
    console.log( Strings.large );
    console.log( Strings["plots.position.meters"] );
    console.log( Strings["plots.energy-vs-position"] );
    console.log( Strings["energy-skate-park.description"] );
    //$( "#tab2" ).append( $( '<canvas id="c2" width=400 height=300></canvas>' ) );
//    $( "#tab3" ).append( $( '<canvas id="c" width=400 height=300></canvas>' ) );

//    $( '#tab1' ).css( 'width', '100%' ).css( 'height', '100%' );

    $( "#theMainBody" ).append( $( '<div data-role="navbar" id="navBar"><ul>' +
                                   '<li><a href="#tab1" id="introNavBarButton" data-transition="none" class="ui-btn-active ui-state-persist">' + Strings["tab.introduction"] + '</a></li>' +
                                   '<li><a href="#tab2" id="frictionNavBarButton" data-transition="none" >' + Strings["tab.friction"] + '</a></li>' +
                                   '<li><a href="#tab3" id="playgroundNavBarButton" data-transition="none" >' + Strings["tab.trackPlayground"] + '</a></li></ul></div>' ) ).trigger( "create" );

    WebsocketRefresh.listenForRefresh();
    var tab1 = new IntroductionTab( "tab1" );
    var tab2 = new IntroductionTab( "tab2" );
} );