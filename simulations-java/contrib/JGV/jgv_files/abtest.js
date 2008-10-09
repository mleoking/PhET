abtest_conversion = 0;
function clickGoal(clickElement, testid, uacct) {
	if (jQuery(clickElement)) jQuery(clickElement).mousedown(function(){
		if (!abtest_conversion) {
			_uacct = uacct;
			urchinTracker("/"+testid+"/goal");
			abtest_conversion = 1;
		}
	});
}
