// Copyright 2002-2012, University of Colorado

define( [
            "./Person"
        ],
        function ( Person ) {

            function Contact( firstName, lastName ) {
                this.person = new Person( firstName, lastName );
                console.log( "Do we have a Person in Contact? : " + ( this.person instanceof Person ) );
            }

            return Contact;
        } );