//Listen for file changes for auto-refresh
define( [], function () {
    return {
        listenForRefresh:function () {
            if ( "WebSocket" in window ) {
                // Let us open a web socket
                var ws = new WebSocket( "ws://localhost:8887/echo" );
                ws.onmessage = function () { document.location.reload( true ); };
                ws.onclose = function () { };
                console.log( "opened websocket" );
            }
            else {
                // The browser doesn't support WebSocket
                //            alert( "WebSocket NOT supported by your Browser!" );
                console.log( "WebSocket NOT supported by your Browser!" );
            }
        }
    }
} );