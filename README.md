# cordova-plugin-camerapicturebackground
Automatically take Picture from Android Smartphones without any User Interactions.

###Supported Platform

* Android

###Installation

Plugin can be installed using the [Command Line Interface](http://cordova.apache.org/docs/en/4.0.0/guide_cli_index.md.html#The%20Command-Line%20Interface):

````
cordova plugin add https://github.com/an-rahulpandey/cordova-plugin-camerapicturebackground.git
````

###Plugin Usage

````
var options = {
    name: "Image", //image suffix
    dirName: "CameraPictureBackground", //foldername
    orientation: "landscape", //or portrait
    type: "back" //or front
};

window.plugins.CameraPictureBackground.takePicture(success, error, options);

function success(imgurl) {
    console.log("Imgurl = " + imgurl);
}
````

#####Here options are

**name** = Suffix to be added while saving the image for.e.g Image-yyyymmddhhmmss

**dirName** = Folder will be created with this name in Pictures directory of external storage.

**orientation** = while taking the picture, camera should be in landscape or portrait mode.

**type** = Either to use front or bank camera.


###Credits

Stackoverflow user Sam - http://stackoverflow.com/a/27083867/1584921
