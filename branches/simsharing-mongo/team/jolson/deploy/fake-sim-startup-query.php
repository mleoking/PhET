<?php
	print <<<BOO
<?xml version="1.0"?>
<sim_startup_query_response>
<sim_version_response
project="charges-and-fields"
sim="charges-and-fields"
version_major="1"
version_minor="07"
version_dev="00"
version_revision="288400"
version_timestamp="1231567890"
ask_me_later_duration_days="1" />

<phet_installer_update_response recommend_update="true" timestamp_seconds="1234167890" ask_me_later_duration_days="30" />
BOO;
	
	print "</sim_startup_query_response>";
?>