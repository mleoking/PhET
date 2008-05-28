<?php

// This is based largely on the example on the website:
// http://pear.php.net/manual/en/package.mail.mail-queue.php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include(SITE_ROOT."admin/newsletter-config.php");

// How many mails could we send each time the script is called
// For now, just do it all at once
// $max_amount_mails = 5;

// we use the news_db_options and news_mail_options from the config again
$mail_queue =& new Mail_Queue($news_db_options, $news_mail_options);

// Really sending the messages
$mail_queue->sendMailsInQueue();

?>
