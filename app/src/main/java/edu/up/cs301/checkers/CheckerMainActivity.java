package edu.up.cs301.checkers;

import android.content.pm.ActivityInfo;

import java.util.ArrayList;

import edu.up.cs301.checkers.InfoMessage.CheckerState;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.gameConfiguration.GamePlayerType;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.utilities.Saving;
import edu.up.cs301.game.R;
import edu.up.cs301.checkers.CheckerPlayers.CheckerComputerPlayer1;
import edu.up.cs301.checkers.CheckerPlayers.CheckerComputerPlayer2;
import edu.up.cs301.checkers.CheckerPlayers.CheckerHumanPlayer1;

public class CheckerMainActivity extends GameMainActivity {

    // Tag for logging
    private static final String TAG = "CheckerMainActivity";
    public static final int PORT_NUMBER = 5213;

    /**
     * a checkers game is for two players. The default is human vs. computer
     */
    @Override
    public GameConfig createDefaultConfig() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // yellow-on-blue GUI
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new CheckerHumanPlayer1(name, R.layout.activity_main, (CheckerState) getGameState());
            }
        });

        // dumb computer player
        playerTypes.add(new GamePlayerType("Computer Player (dumb)") {
            public GamePlayer createPlayer(String name) {
                return new CheckerComputerPlayer1(name);
            }
        });

        // smarter computer player
        playerTypes.add(new GamePlayerType("Computer Player (smart)") {
            public GamePlayer createPlayer(String name) {
                return new CheckerComputerPlayer2(name);
            }
        });

        // Create a game configuration class for Chess
        GameConfig defaultConfig = new GameConfig(playerTypes, 2,2, "Chess", PORT_NUMBER);

        // Add the default players
        defaultConfig.addPlayer("Human", 0); // human player GUI
        defaultConfig.addPlayer("Computer", 1); // dumb computer player

        // Set the initial information for the remote player
        defaultConfig.setRemoteData("Remote Player", "", 1); // remote player GUI

        //done!
        return defaultConfig;

    }//createDefaultConfig

    /**
     * createLocalGame
     *
     * Creates a new game that runs on the server tablet,
     * @param gameState
     * 				the gameState for this game or null for a new game
     *
     * @return a new, game-specific instance of a sub-class of the LocalGame
     *         class.
     */
    @Override
    public LocalGame createLocalGame(GameState gameState) {
        if(gameState == null)
            return new CheckerLocalGame();
        return new CheckerLocalGame((CheckerState) gameState);
    }

    /**
     * saveGame, adds this games prepend to the filename
     *
     * @param gameName
     * 				Desired save name
     * @return String representation of the save
     */
    @Override
    public GameState saveGame(String gameName) {
        return super.saveGame(getGameString(gameName));
    }

    /**
     * loadGame, adds this games prepend to the desire file to open and creates the game specific state
     * @param gameName
     * 				The file to open
     * @return The loaded GameState
     */
    @Override
    public GameState loadGame(String gameName){
        String appName = getGameString(gameName);
        super.loadGame(appName);
        return (GameState) new CheckerState((CheckerState) Saving.readFromFile(appName, this.getApplicationContext()));
    }
}