# UEK-335

## Author

This project is developed by Jonathan Russ.

## GitHub Repository

<https://github.com/JonathanXDR/UEK-335>

## Table of Contents

- [UEK-335](#uek-335)
  - [Author](#author)
  - [GitHub Repository](#github-repository)
  - [Table of Contents](#table-of-contents)
  - [Setup in Android Studio](#setup-in-android-studio)
    - [Prerequisites](#prerequisites)
    - [Cloning the Repository](#cloning-the-repository)
    - [Configuring the Project](#configuring-the-project)
    - [Running the App](#running-the-app)
    - [Troubleshooting](#troubleshooting)
    - [Notes](#notes)
  - [License](#license)

## Setup in Android Studio

### Prerequisites

- Android Studio (latest version recommended)
- Git (for cloning the repository)

### Cloning the Repository

1. Open Android Studio.
2. Select `File` > `New` > `Project from Version Control`.
3. In the URL field, enter `https://github.com/JonathanXDR/UEK-335`.
4. Choose your preferred directory and click `Clone`.

### Configuring the Project

1. Once the project is opened in Android Studio, wait for the Gradle sync to complete. This process may take a few minutes.
2. Ensure that the Kotlin plugin is updated to the latest version (check via `File` > `Settings` > `Languages & Frameworks` > `Kotlin`).

### Running the App

1. Connect an Android device or set up an Android Emulator:
   - To setup an Emulator, go to `Tools` > `AVD Manager`, and create a new Android Virtual Device.
2. Select the device or emulator from the dropdown menu in the toolbar.
3. Click on the green `Run` button (▶) in the toolbar to build and run the app.
4. The app should now launch on your selected device or emulator.

### Troubleshooting

- If you encounter any build errors, try cleaning the project (`Build` > `Clean Project`) and then rebuilding it (`Build` > `Rebuild Project`).
- For any issues related to dependencies, make sure to have an active internet connection and try syncing the project with Gradle files (`File` > `Sync Project with Gradle Files`).

### Notes

- Make sure your Android device or emulator runs on a compatible API level as specified in the app's `build.gradle` file.
- For optimal performance, use a physical device or configure the emulator to use adequate system resources.

## License

[MIT](LICENSE.txt)
