var exec = require('cordova/exec');

exports.takePicture = function(success, error, options) {
    var defaults = {
			name : "Image",
			dirName : "CameraPictureBackground",
			orientation : "landscape",
			type : "back"
	};

	if(options){
		for (var key in defaults){
			if(options[key] != undefined){
				defaults[key] = options[key];
			}
		}
	}
    exec(success, error, "CameraPictureBackground", "takePicture", [defaults]);
};
