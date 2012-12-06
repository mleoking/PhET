//File for delivering and collecting events for sim event data analysis
define( [], function () {
    function Analytics() {
    }

    Analytics.prototype.log = function ( component, componentType, action, parameters, messageType ) {
        var time = new Date().getTime();
        var m = (messageType === undefined) ? "user" : messageType;

        //Create the standard formatted line for logging, matches the java format output
        var line = this.messageToString( component, componentType, action, parameters, m, time );

        //Log it to the console
        console.log( line );

        //Log it to the DOM
        var analyticsElement = document.getElementById( "analyticsLog" );
        if ( analyticsElement != null && typeof analyticsElement != 'undefined' ) {
            analyticsElement.innerHTML += line + "<br>";
        }

        this.logToSimian( component, componentType, action, parameters, m, time );
        //TODO: echo a copy to Google analytics event tracking for recording, visualization and other Google Analytics benefits?
    };

    Analytics.prototype.messageToString = function ( component, componentType, action, parameters, messageType, time ) {
        var array = [time, messageType, component, componentType, action];
        if( parameters ){

          for ( var i = 0; i < parameters.length; i++ ) {
              var obj = parameters[i];

              //See http://stackoverflow.com/questions/1078118/how-to-iterate-over-a-json-structure
              for ( var key in obj ) {
                  array.push( key + " = " + obj[key] );
              }
          }

        }
        return array.join( "\t" );
    };

    /**
     * This function starts with required parameters
     * @param {String} component
     * @param {String} componentType
     * @param {String} action
     * @param {Array} parameters optional parameters
     * @param {String} messageType optional
     * @param {Number} time
     * */
    Analytics.prototype.logToSimian = function ( component, componentType, action, parameters, messageType, time ) {
        var img = new Image();
        var message = "http://simian.colorado.edu/__utm.gif?" +
                      "time=" + time +
                      "&messageType=" + messageType +
                      "&component=" + component +
                      "&componentType=" + componentType +
                      "&action=" + action;
        if ( parameters !== undefined ) {
            for ( var i = 0; i < parameters.length; i++ ) {
                var obj = parameters[i];

                //http://stackoverflow.com/questions/1078118/how-to-iterate-over-a-json-structure
                for ( var key in obj ) {
                    var attributeName = key;
                    var attributeValue = obj[key];
                    message = message + "&parameter_" + attributeName + "=" + attributeValue;
                }
            }
        }
        img.src = message;
    };

    return Analytics;
} );
