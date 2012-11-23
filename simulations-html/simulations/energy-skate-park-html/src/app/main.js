require( ['websocket-refresh',
             'tab',
             "tpl!view/massSlider.html",
             "tpl!view/frictionControls.html",
             '../../../../common/common-html/src/app/analytics/Analytics'
         ], function ( WebsocketRefresh, Tab, massSlider, frictionControls, Analytics ) {

    //Workaround for IE9 console: http://stackoverflow.com/questions/5472938/does-ie9-support-console-log-and-is-it-a-real-function
    if ( !window.console ) {
        window.console = {};
    }
    if ( !window.console.log ) {
        window.console.log = function () { };
    }

    WebsocketRefresh.listenForRefresh();
    var analytics = new Analytics();

    //TODO: move page creation to here, so it reads like $("body").append($(createTab("tab1")))
    new Tab( "tab1", true, massSlider( {id: "tab1"} ), analytics );
    new Tab( "tab2", false, frictionControls( {id: "tab2"} ), analytics );
    new Tab( "tab3", false, frictionControls( {id: "tab3"} ), analytics );
} );