package tel.discord.rtab.games;

import java.util.List;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import tel.discord.rtab.Player;

public interface MiniGame {
	
	/**
	 * Initialises the game. It will run to completion, and interrupt the thread passed to it when it is finished.
	 * @param channel The channel to send messages to (if null, will use standard input)
	 * @param sendMessages Whether to send messages at all
	 * @param baseNumerator The numerator of the base multiplier
	 * @param baseDenominator The denominator of the base multiplier
	 * @param gameMultiplier Any other multiplier the game should take into account, for example the number of copies
	 * @param players The table of players in the game
	 * @param player The index of the one playing this minigame in the players table
	 * @param callWhenFinished The thread to call when the minigame is finished
	 * @param enhanced Whether the minigame has been enhanced
	 */
	void initialiseGame(MessageChannel channel, boolean sendMessages, int baseNumerator, int baseDenominator,
			int gameMultiplier, List<Player> players, int player, Thread callWhenFinished, boolean enhanced);
	
	/**
	 * Skips the messages currently being sent by the minigame, if they are skippable.
	 */
	void skipMessages();
	
	/**
	 * Gets the ID of the channel this minigame is running in.
	 */
	String getChannelID();
	
	/**
	 * Gets the ID of the player of the minigame.
	 */
	String getPlayerID();
	
	/**
	 * Gets the full name of the minigame.
	 * @return The minigame's full name
	 */
	String getName();
	
	/**
	 * Gets the short name of the minigame.
	 * @return The minigame's short name
	 */
	String getShortName();
	
	/**
	 * Gets a short description of what enhancing the minigame does.
	 * @return Description of minigame's enhancement effect
	 */
	default String getEnhanceText()
	{
		return "Enhancing this minigame has no effect.";
	}
	
	/**
	 * Gets whether or not the minigame is considered a bonus game.
	 * @return true if it is a bonus game, otherwise false
	 */
	boolean isBonus();
	
	/**
	 * Shuts down the minigame gracefully(?)
	 * The game will usually call this itself, but the shutdown command can do this too
	 */
	void gameOver();
	
	/**
	 * If true, designates the minigame as a "negative minigame", which won't be lost on bomb
	 * @return true if the minigame is here to hurt you
	 */
	default boolean isNegativeMinigame()
	{
		return false;
	}
}
