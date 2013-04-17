This project is responsible for sending out fine-grained email notifications about ticket comments (and some ticket changes) for PhET's Unfuddle Service.

It runs on phet-server under this startup script:
/etc/init.d/local

See ticket #2689: Configure phet-server to automatically launch services upon a restart
https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/2689

The proguard file is customized for running on phet-server under Ubuntu, so to build the dist/unfuddle-email-pro.jar which is used on startup, you must run 'ant' from trunk/util/unfuddle.
