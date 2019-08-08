# Unity Android Live Wallpaper

Run your Unity game as live wallpaper on Android. Used in production by the app [*Fluid Simulation*](https://play.google.com/store/apps/details?id=games.paveldogreat.fluidsimfree).

<img src="screenshots/app_1.jpg?raw=true" width="200"> <img src="screenshots/app_2.jpg?raw=true" width="200">

The main reason I open source it is because this implementation has several major bugs that I can't fix and I need help with:

- When you launch lwp preview, press home button and then go back to preview. Whoops... it crashes.
- On many launchers instead of fullscreen, it runs only in small portion of the screen.
- Also not rendering in fullscreen when in landscape mode.

So my goal is to fix these problems and to make this library the best Unity's lwp Android implementation ever, that is also the simplest and free.

# Usage

Export your game as Android Studio project. Then you need to make next steps:

1) Copy content from [AndroidManifest.xml](AndroidManifest.xml) to the project's AndroidManifest.xml, after main activity tag. Should look like this:
 <img src="screenshots/manifest.png?raw=true" width="800">

2) Add [WallpaperActivity.java](WallpaperActivity.java) script into the project. You probably would need to change the package name at the top of the script.

3) Make `xml` folder in `res` and add [wallpaper.xml](wallpaper.xml) file there.
 <img src="screenshots/xml.png?raw=true" width="400">

Now you can build and run. Go into wallpaper settings of your phone to see it. If you are using Samsung device then you need to install Google Wallpapers app to set it as a wallpaper.
