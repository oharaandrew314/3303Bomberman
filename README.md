3303Bomberman
=============

Bomberman game for the SYSC 3303 Project.

https://github.com/oharaandrew314/3303Bomberman

###Usage Instructions

1. Run the JAR to open the Graphical Launcher Application
2. Create a Server (if you wish to host the server)
3. Create a Client
    1. Enter the <IP>:<PORT> of the host to connect to (initially localhost)
    2. The client will launch and attempt to connect to the server on the host
    3. Press the 'Enter' key to start the game
3. The server and clients include help menus for applicable topics

It is not reccomended to attempt to use command-line arguments to launch the server and client, they are deprecated and unmaintained

### Loading Project into Eclipse
1. Open the project in Eclipse
2. Ensure that JUnit 4 is added to the build path
3. Ensure that lib/gson is added to the build path
4. Ensure the "res" folder is added to the build path

### Testing Instructions

Test Driver Application
1. Run the JAR to open the Launcher Application
2. Create a Server
3. Run the TestDriver
4. Wait for completion
5. Check console output and log file output next to JAR

Unit and Integration Tests
1. Follow "Loading Project into Eclipse" instructions
2. Run "test.UnitTestSuite with JUnit"

###Extras
- Full Graphical View
- Graphical Launcher Application (Server, Client, Spectator, TestDriver)
- Complately data-driven help text
- Multiple servers running at once

###Deadlines
- Milestone 1: March 3
- Milestone 2: March 17
- Milestone 3: March 31

###Contributors
- Andrew O'Hara  - 100815259
- Caleb Simpson  - 100819001
- Dennis Dionne  - 100822373
- Darrell Penner - 100817231

###Contribution Rules

Feel free to challenge my rules, or add your own.
It may look like there is a lot, but they're mostly just steps

- I don't really care about style nitpicking (as long as it's readable and using Windows/CRLF line-endings)
- Create feature branches for each feature (no personal development branches!)
  1. Create feature branch off dev (if it's related to a Github issue, put the issue # in branch name)
  2. Make commits
  3. Ensure tests pass
  4. Make a pull request to merge into dev
  5. Modify your branch to be mergeable if necessary
  6. If you modified someone else's code, have them review and merge
  7. If you didn't modify someone else's code, feel free to merge yourself
  8. Delete the feature branch, and pull dev
- Submitting milestones
  1. Wait until dev is stable and meets milestone requirements
  2. Make a pull request to merge dev into master
  3. Once everyone has reviewed, merge it
  4. Create a "Milestone X" branch off of master
  5. Merge master into dev
