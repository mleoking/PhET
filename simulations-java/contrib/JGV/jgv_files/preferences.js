if (typeof(SFX) == "undefined") {
    var SFX = {}; //load the SFX var if we don't already have one.
}

SFX.preference = {'pref':null};

SFX.set_preference = function(pref, value){
    $.ajax({type: 'POST', url: '/preference.php', data: 'pref='+pref+'&value='+value, async: false});
    return false;
}

SFX.get_preference = function(prefname){
    $.ajax({
        url: '/preference.php',
        data: 'pref='+prefname,
        async: false,
        success:function(data){
            SFX.preference.pref = data;
        }
    });
    return SFX.preference.pref;
}