<?php

// Configuration options for the newsletter bulk mailer.
// This will be included by a web page that will queue the
// newsletter for delivery, and for a cron script that will
// do the actual delivery.

// This is based largely on the example on the website:
// http://pear.php.net/manual/en/package.mail.mail-queue.php

// Include the PEAR Mail_Queue library
include_once("Mail/Queue.php");

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/login-info.php");

// The options below specif the database access for the
// Mail_Queue library.  It will use the database to queue
// the messages, which will be dispatched later via a cron
// script.

// Type is the database container used, currently there are 'creole', 'db', 'mdb' and 'mdb2' available
$news_db_options['type']       = 'mdb2';

// How to access the database
$news_db_options['dsn']        = 'mysql://'.DB_USERNAME.':'.DB_PASSWORD.'@'.DB_HOSTNAME.'/'.DB_NAME;

// Name of the table used for the mail queue
$news_db_options['mail_table'] = 'newsletter_mail_queue';

// Here are the options for sending the messages themselves
// These are the options needed for the Mail-Class, especially used for Mail::factory()
$news_mail_options['driver']    = 'smtp';
$news_mail_options['host']      = 'localhost';
$news_mail_options['port']      = 25;
$news_mail_options['localhost'] = 'localhost'; //optional Mail_smtp parameter
$news_mail_options['auth']      = false;
$news_mail_options['username']  = '';
$news_mail_options['password']  = '';

?>