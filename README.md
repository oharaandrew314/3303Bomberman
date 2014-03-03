3303Bomberman
=============

Bomberman game for the SYSC 3303 Project

###Usage Instructions

By JAR:
Running Jar will open a DemoRunner

1. Click Server
2. Optionally click Spectator
3. Click TestDriver to run tests
4. Check output log file next to JAR

With Eclipse:

Run client.controllers.TestDriver with no arguments

###Deadlines
- Milestone 1: March 3rd
- Milestone 2: March 17
- Milestone 3: March 31

###Contributors
- Andrew O'Hara - 100815259  - Controllers, Grid Generation, Integration tests
- Caleb Simpson - 100819001  - Networking, Events
- Dennis Dionne - 100822373  - Test Client and loader
- Darrell Penner - 100817231 - Client, Grid Models

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
