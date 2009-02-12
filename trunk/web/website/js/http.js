/**
 * http.js: utilities for scripted HTTP requests
 *
 * From the book JavaScript: The Definitive Guide, 5th Edition,
 * by David Flanagan. Copyright 2006 O'Reilly Media, Inc. (ISBN: 0596101996)
 */

// Make sure we haven't already been loaded
var HTTP;
if (HTTP && (typeof HTTP != "object" || HTTP.NAME))
    throw new Error("Namespace 'HTTP' already exists");

// Create our namespace, and specify some meta-information
HTTP = {};
HTTP.NAME = "HTTP";    // The name of this namespace
HTTP.VERSION = 1.0;    // The version of this namespace

// This is a list of XMLHttpRequest creation factory functions to try
HTTP._factories = [
    function() { return new XMLHttpRequest(); },
    function() { return new ActiveXObject("Msxml2.XMLHTTP"); },
    function() { return new ActiveXObject("Microsoft.XMLHTTP"); }
];

// When we find a factory that works, store it here
HTTP._factory = null;

/**
 * Create and return a new XMLHttpRequest object.
 * 
 * The first time we're called, try the list of factory functions until
 * we find one that returns a nonnull value and does not throw an
 * exception.  Once we find a working factory, remember it for later use.
 */
HTTP.newRequest = function() {
    if (HTTP._factory != null) return HTTP._factory();

    for(var i = 0; i < HTTP._factories.length; i++) {
        try {
            var factory = HTTP._factories[i];
            var request = factory();
            if (request != null) {
                HTTP._factory = factory;
                return request;
            }
        }
        catch(e) {
            continue;
        }
    }

    // If we get here, none of the factory candidates succeeded,
    // so throw an exception now and for all future calls.
    HTTP._factory = function() {
        throw new Error("XMLHttpRequest not supported");
    }
    HTTP._factory(); // Throw an error
}

/**
 * Use XMLHttpRequest to fetch the contents of the specified URL using
 * an HTTP GET request.  When the response arrives, pass it (as plain
 * text) to the specified callback function.
 * 
 * This function does not block and has no return value.
 */
HTTP.getText = function(url, callback) {
    var request = HTTP.newRequest();
    request.onreadystatechange = function() {
        if (request.readyState == 4 && request.status == 200)
            callback(request.responseText);
    }
    request.open("GET", url);
    request.send(null);
};

/**
 * Use XMLHttpRequest to fetch the contents of the specified URL using
 * an HTTP GET request.  When the response arrives, pass it (as a parsed
 * XML Document object) to the specified callback function.
 * 
 * This function does not block and has no return value.
 */
HTTP.getXML = function(url, callback) {
    var request = HTTP.newRequest();
    request.onreadystatechange = function() {
        if (request.readyState == 4 && request.status == 200)
            callback(request.responseXML);
    }
    request.open("GET", url);
    request.send(null);
};

/**
 * Use an HTTP HEAD request to obtain the headers for the specified URL.
 * When the headers arrive, parse them with HTTP.parseHeaders() and pass the
 * resulting object to the specified callback function. If the server returns
 * an error code, invoke the specified errorHandler function instead.  If no
 * error handler is specified, pass null to the callback function.
 */
HTTP.getHeaders = function(url, callback, errorHandler) {
    var request = HTTP.newRequest();
    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            if (request.status == 200) {
                callback(HTTP.parseHeaders(request));
            }
            else {
                if (errorHandler) errorHandler(request.status,
                                               request.statusText);
                else callback(null);
            }
        }
    }
    request.open("HEAD", url);
    request.send(null);
};

/**
 * Parse the response headers from an XMLHttpRequest object and return
 * the header names and values as property names and values of a new object.
 */
HTTP.parseHeaders = function(request) {
    var headerText = request.getAllResponseHeaders();  // Text from the server
    var headers = {}; // This will be our return value
    var ls = /^\s*/;  // Leading space regular expression
    var ts = /\s*$/;  // Trailing space regular expression

    // Break the headers into lines
    var lines = headerText.split("\n");
    // Loop through the lines
    for(var i = 0; i < lines.length; i++) {
        var line = lines[i];
        if (line.length == 0) continue;  // Skip empty lines
        // Split each line at first colon, and trim whitespace away
        var pos = line.indexOf(':');     
        var name = line.substring(0, pos).replace(ls, "").replace(ts, "");
        var value = line.substring(pos+1).replace(ls, "").replace(ts, "");
        // Store the header name/value pair in a JavaScript object
        headers[name] = value;
    }
    return headers;
};

/**
 * Send an HTTP POST request to the specified URL, using the names and values
 * of the properties of the values object as the body of the request.
 * Parse the server's response according to its content type and pass
 * the resulting value to the callback function.  If an HTTP error occurs,
 * call the specified errorHandler function, or pass null to the callback
 * if no error handler is specified.
 **/
HTTP.post = function(url, values, callback, errorHandler) {
    var request = HTTP.newRequest();
    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            if (request.status == 200) {
                callback(HTTP._getResponse(request));
            }
            else {
                if (errorHandler) errorHandler(request.status,
                                               request.statusText);
                else callback(null);
            }
        }
    }

    request.open("POST", url);
    // This header tells the server how to interpret the body of the request
    request.setRequestHeader("Content-Type",
                             "application/x-www-form-urlencoded");
    // Encode the properties of the values object and send them as
    // the body of the request.
    request.send(HTTP.encodeFormData(values));
};

/**
 * Encode the property name/value pairs of an object as if they were from
 * an HTML form, using application/x-www-form-urlencoded format
 */
HTTP.encodeFormData = function(data) {
    var pairs = [];
    var regexp = /%20/g; // A regular expression to match an encoded space

    for(var name in data) {
	    print ("name " + name + ", value = " + data[name]);
	
        var value = data[name].toString();
        // Create a name/value pair, but encode name and value first
        // The global function encodeURIComponent does almost what we want,
        // but it encodes spaces as %20 instead of as "+". We have to
        // fix that with String.replace()
        var pair = encodeURIComponent(name).replace(regexp,"+") + '=' +
            encodeURIComponent(value).replace(regexp,"+");
        pairs.push(pair);
    }

    // Concatenate all the name/value pairs, separating them with &
    var joined = pairs.join('&');

	alert("Data = " + data + ", Joined parameters = " + joined);

	return joined;
};

/**
 * Parse an HTTP response based on its Content-Type header
 * and return the parsed object
 */
HTTP._getResponse = function(request) {
    // Check the content type returned by the server
    switch(request.getResponseHeader("Content-Type")) {
    case "text/xml":
        // If it is an XML document, use the parsed Document object
        return request.responseXML;

    case "text/json":
    case "application/json": 
    case "text/javascript":
    case "application/javascript":
    case "application/x-javascript":
        // If the response is JavaScript code, or a JSON-encoded value,
        // call eval() on the text to "parse" it to a JavaScript value.
        // Note: only do this if the JavaScript code is from a trusted server!
        return eval(request.responseText);

    default:
        // Otherwise, treat the response as plain text and return as a string
        return request.responseText;
    }
};

/**
 * Send an HTTP GET request for the specified URL.  If a successful
 * response is received, it is converted to an object based on the
 * Content-Type header and passed to the specified callback function.
 * Additional arguments may be specified as properties of the options object.
 *
 * If an error response is received (e.g., a 404 Not Found error),
 * the status code and message are passed to the options.errorHandler
 * function.  If no error handler is specified, the callback
 * function is called instead with a null argument.
 * 
 * If the options.parameters object is specified, its properties are
 * taken as the names and values of request parameters.  They are
 * converted to a URL-encoded string with HTTP.encodeFormData() and
 * are appended to the URL following a '?'.
 * 
 * If an options.progressHandler function is specified, it is
 * called each time the readyState property is set to some value less
 * than 4.  Each call to the progress handler function is passed an
 * integer that specifies how many times it has been called.
 *
 * If an options.timeout value is specified, the XMLHttpRequest
 * is aborted if it has not completed before the specified number
 * of milliseconds have elapsed.  If the timeout elapses and an
 * options.timeoutHandler is specified, that function is called with
 * the requested URL as its argument.
 **/
HTTP.get = function(url, callback, options) {
    var request = HTTP.newRequest();
    var n = 0;
    var timer;
    if (options.timeout)
        timer = setTimeout(function() {
                               request.abort();
                               if (options.timeoutHandler)
                                   options.timeoutHandler(url);
                           },
                           options.timeout);

    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            if (timer) clearTimeout(timer);
            if (request.status == 200) {
                callback(HTTP._getResponse(request));
            }
            else {
                if (options.errorHandler)
                    options.errorHandler(request.status,
                                         request.statusText);
                else callback(null);
            }
        }
        else if (options.progressHandler) {
            options.progressHandler(++n);
        }
    }

    var target = url;
    if (options.parameters)
        target += "?" + HTTP.encodeFormData(options.parameters)
    request.open("GET", target);
    request.send(null);
};

/**
 * This is a simple function to update the inner html of an element with the
 * GET result from a URL.
 */
HTTP.updateElementWithGet = function(url, parameters, elementId, code) {	
	element = document.getElementById(elementId);	
	
	var options = {};
	
	options.parameters = parameters;
	
	callback = function(result) {
		if (result != null) {
			element.innerHTML = result;
			
			if (code) {
				eval(code);
			}
		}
		else {
			element.innerHTML = "The server does not appear to be responding. Please try later.";
		}
	};
	
	HTTP.get(url, callback, options);
};

/**
 * This is a simple function to update the value of an element with the
 * GET result from a URL.
 */
HTTP.updateElementValueWithGet = function(url, parameters, elementId, code) {	
	element = document.getElementById(elementId);	
	
	var options = {};
	
	options.parameters = parameters;
	
	callback = function(result) {
		if (result != null) {
			if (result != '') {
				element.value = result;
			}			
			
			if (code) {
				eval(code);
			}
		}
	};
	
	HTTP.get(url, callback, options);
};