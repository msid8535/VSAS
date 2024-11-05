# Virtual Scroll Access System (VSAS)
  Features
- User: Adding Scrolls, Viewing, Editing, Removing, Previewing
- Admin: Adding users, Removing users, Viewing Download and Upload Stats

# Version Information
- Java version: 17
- Gradle version: 8.10

# Installation and Execute Instructions
### Method 1: Using Java executable
- Install the .jar file from the latest release.
- Place the installed .jar file into any folder of your choice.
- Navigate to the folder in the command line by using `cd`.
- Run `java -jar [executable filename]`

### Method 2: Using source code
- Install all source code files as a zip, then extract to a folder of your choice.
- Navigate to the folder in the command line by using `cd`.
- Run `gradle clean build`
- Run `gradle run`
  - To run the program without the gradle output, run `gradle run -q --console=plain` instead.

# Test Instructions
#### Performing tests requires you to have the source code and Gradle installed.
- Tests should automatically be done when `gradle build` is run.
- Nevertheless, tests can be run again by running `gradle test`
- Coverage report can be found in `build/reports/jacoco/test/html/index.html`
