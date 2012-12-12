define( [ "modelpath/Person" ],
        function ( Person ) {

            function Contact( name ) {
                this.person = new Person( name );
            }

            return Contact;
        } );