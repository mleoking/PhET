// Copyright 2002-2012, University of Colorado
define( [ ], function () {

    function Bucket( xPos, yPos, width, labelText ) {
        this.x = xPos;
        this.y = yPos;
        this.width = width;
        this.labelText = labelText;
    }

    return Bucket;
} );
