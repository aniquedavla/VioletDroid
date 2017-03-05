# VioletDroid

# Introduction
VioletDroid is an Android application for creating simple UML Class and Sequence diagrams quickly on your phone or tablet. VioletDroid provides simple features for a user’s convenience such as creating and exporting diagrams. It is not intended to be used as an industrial strength tool, so features such as code generation are not available. VioletDroid is simply an easy way for users to create UML diagrams.

## Important installs ##

Android Platform Tools:
>easily with [homebrew](http://brew.sh/)
```
$ brew install android-platform-tools
```

Set your ANDROID_HOME environment variable (e.g. `/Users/yourname/Library/Android/sdk`)
```
$ export ANDROID_HOME /PATH/TO/ANDROID/SDK
```

## Running the Android Application from Terminal ##

>To start an emulator:
```
$ emulator -adb NAME_OF_EMULATOR
```

To compile and run the application on the emulator or a plugged in device

1. Open a new Terminal instance at /VioletDroidApplication/
2. Run the following commands 
```
$ ./gradlew assembledebug
$ adb install -r app/build/outputs/apk/app-debug.apk 
$ adb shell am start -n "com.example.violetdroidapplication/.MainActivity"
```
>To run checkstyle:
```
$ ./gradlew checkstyle

## Using VioletDroid ##
### Creating and Editing a Diagram ###
1. Choose which style of diagram you want to create 
2. Tap the “Class” button to create a class item, then enter its name, attributes, and methods
3. The item is highlighted to show it is selected
4. You can drag your finger to drag the selected item around
5. Tap the "Class" button again while a class item is selected to edit its properties
6. To deselect, tap the selected item once
7. Tap the “Arrow” button to create an arrow, then select the arrow properties 
8. To draw the arrow, tap the item from which the arrow will start, then tap the item to which the arrow will point
9. Tap the "Arrow" button while an arrow is selected to edit its style
10. Tap the "Note" button to create a note, then enter its text
11. Tap the "Note" button again while a note is selected to edit its text
12. Tap the "Delete" button to remove the currently selected item

### Saving your Diagram ###
Once you are finished editing your diagram, you have the option to save your UML diagram as a JSON file that the app can later reopen if necessary. 

1. To save a new diagram, press the "File" button and then press "Save As"
2. Enter a name and press "Done"
3. If you choose a name that is already taken, the old file will be overwritten
4. To write changes to a previously saved diagram, press the File button and then press "Save"

### Loading an Existing Diagram ###
If you want to continue work on a diagram you were previously working on in VioletDroid, you can simply load the JSON file from your SD card.

1. Press the "File" button, then press “Load”
2. Choose the diagram from the list that is shown 

### Exporting your Diagram ###
VioletDroid provides you with the option to export your UML diagram as a PNG image that will be saved on the device.

1. To export your diagram as an image, press the "File" button, then press “Export”
2. Enter a name for the image and press "OK"
3. The diagram will be saved to the device's "Pictures" folder
