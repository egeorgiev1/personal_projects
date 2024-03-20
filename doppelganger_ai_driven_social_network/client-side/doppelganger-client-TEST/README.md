# doppelganger-client
Group 17's client-side code of Doppelganger

## Initial Setup
These instructions walk you through setting up Android Studio with this repository.

TODO: include instructions for setting up the config file of android studio

1. If a project is currently open in Android Studio, close it using `File->Close Project`.
2. After you arrive to the welcome window, click `Check out project from Version Control -> GitHub`.
3. If it prompts you to login, **don't use your username and password**! Generate an access token instead.
4. When you get to the "Clone Repository" window, use https://github.com/emilxp/doppelganger-client.git as the repository URL. You may be able to find it in the dropdown.
5. Click the clone button.
6. You'll get a "You have checked out a Studio project" message. Click yes.
7. You're going to get an error stating "Cannot load modules". Don't worry, that's because .iml files are missing. They're going to be generated after Gradle compiles the project. Source: [What are iml files in Android Studio?
](https://stackoverflow.com/questions/30737082/what-are-iml-files-in-android-studio) .
8. After the build is done, go to `Tools->Android->Sync Project with Gradle Files`. This will trigger another compilation. It's necessary, because for some reason Android Studio doesn't know your Android SDK version specified in the Gradle files and maybe other important information.
9. You're almost ready to code! Checkout the next sections to see how to setup your own branch.
