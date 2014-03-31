# Bomberman Testing Plan

## Functional Testing

- Client join game ( test.integration.TestClient.testConnect() )
- Client disconnect from game (test.integration.TestClient.testDisconnect() )
- Game ends:
	- all players disconnect from game ( TestClient.testDisconnectWithMultiplePlayers() )
- Win game (player finds door) (TestDriver:playerWin.txt)
- Correct game state (test.integration.SystemTest)
- players moving/touching (TestDriver:playerCollision.txt)
- player finds powerup (TestDriver:testFlamePass.txt, TestDriver.testBombRangePlusOne.txt)
- deploy bombs (TestDriver:breakWallWithBomb.txt)
- bomb chain reaction (TestDriver:testUpgradedBombsChainReaction.txt)

## Concurrency Testing

- Players colliding ( test.integration.SystemTest.testPlayerCollision() )
- Player dies from bomb ( test.integration.TestBombs.testDestroyObjects() )
- Player collides with enemy (TestDriver:testPlayerEnemyCollision.txt)
- Player attempts to drop two bombs (test.integration.TestBombs.testChainReaction())
- Player moves after death (TestDriver:playerCollision.txt), as part of this test, player 2 attempts to move after player 1 has collided into him and killed them both.

## Scalability Testing (To be done in milestone 3)

- Test baseline average latency with 1 player across real networks (not just localhost)
- Test how much latency is added for each player that is added
- Test how much latency is added by increasing map size
- Test how much latency is added by increasing number of map components (enemies, boxes, etc)


## Creating a Test Case

In order to create a Test Case, you need to create a text file with the following format

Example Test Case:
	
	Players = 2 ; Location = [1->0,0 : 2->3,0] ; Grid = testGrid2
	1:RIGHT
	1:RIGHT
	1:RIGHT
	2:DOWN:400

The first line of the TestCase should contain the following, separated by semicolons

-Players = Amount of players in the test case

-Location = Starting locations of all the players, a starting location must be specified for each player. The format is as follows:
	[ player1 -> coordinates of player 1 : player2 -> coordinates of player 2 ]

-Grid = Name of the Grid to be used, the grid file must be placed in the grid.tests section of the resource folder


Every other line contains 2 or 3 elements separated by a colon:

-First element is the Player number

-Second element is the Action (either UP, DOWN, LEFT, RIGHT or SPACE)
	UP: move the player up
	DOWN: move the player down
	LEFT: move the player left
	RIGHT: move the player right
	SPACE: player places a bomb at its current location

-Third Element is the optional time to wait in milliseconds before performing the action

Essentially, each line contains the player, the command it's going to run, and the time to wait before performing the action.

Note: Whether you order all your commands by player or not doesn't matter. For example, the two following test cases will be the same:

1:RIGHT								1:RIGHT
1:UP									2:UP
1:DOWN				and				1:UP
2:UP									2:LEFT
2:LEFT								1:DOWN
2:DOWN								2:DOWN


## Creating a Grid File (for testing)

1. Create a json file in res/grids/test  (e.g. testFooBar.json)
2. The json follows the schema:
```
{
	"size": {"width": <width>, "height": <height>},
	"squares": [
		{"point": {"x": <x>, "y": <y>}, "entities": ["<entityName>", ...]},
		...
	]
}
```
3. The list of updated entity names supported by the GridLoader can be found in server.content.GridLoader.loadEntity()

