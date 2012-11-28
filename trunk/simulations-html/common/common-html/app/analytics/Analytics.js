//File for delivering and collecting events for sim event data analysis
define( [], function () {
    function Analytics() {

    }

    /**
     * This function starts with required parameters
     * @param {String} component
     * @param {String} componentType
     * @param {String} action
     * @param {Array} parameters optional parameters
     * @param {String} messageType optional
     * */
    Analytics.prototype.log = function ( component, componentType, action, parameters, messageType ) {
        //args: time, messageType, component, componentType, action, parameters
        var time = new Date().getTime();
        var m = (messageType === undefined) ? "user" : messageType;

        var img = new Image();
        var message = "http://simian.colorado.edu/__utm.gif?" +
                      "time=" + time +
                      "&messageType=" + m +
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

        //TODO: echo a copy to Google analytics event tracking for recording, visualization and other Google Analytics benefits?
    };

    return Analytics;
} );