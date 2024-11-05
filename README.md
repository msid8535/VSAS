# Virtual Scroll Access System (VSAS)

# Background
The goal of this assignment is to work as a team to develop a software product using the Scrum methodology and Agile development tools and practices.

# Prologue
In the enchanting realm of Edstemus, a mystical guild of computer science elves known as the 'Whiskers' sought to explore the ancient scrolls of wisdom stored within the Library of Agility. These scrolls, though not made of parchment and ink, were binary files, each containing a trove of digital knowledge. The revered wizard H of the realm of T has tasked some of the best Whiskers with creating a Virtual Scroll Access System (VSAS) to facilitate the retrieval and sharing of these digital scrolls. Your group has been selected for this noble quest.

# Features
- User: Adding Scrolls, viewing scrolls, editing scrolls, removing scrolls, previewing scrolls, filtered search of scrolls by date, content, name, etc.
- Admin: Adding users, viewing users, removing users, viewing download and upload Stats

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
