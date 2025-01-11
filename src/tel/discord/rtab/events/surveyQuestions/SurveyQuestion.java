package tel.discord.rtab.events.surveyQuestions;

import tel.discord.rtab.GameController;
import tel.discord.rtab.Player;

public abstract class SurveyQuestion {
	static class InvalidAnswerException extends Exception {
	}

	GameController game;
	int playerNumber;
	Player player;

	String initialMessage;

	public SurveyQuestion(GameController game, int player) {
		this.game = game;
		this.playerNumber = player;
		this.player = game.players.get(playerNumber);
	}

	/** Ask the question
	 * @param questionNumber The number of the question, starting from 1
	 */
	public void askQuestion(int questionNumber) {
		if (this.initialMessage == null) {
			throw new AbstractMethodError("No initial message provided. Please override askQuestion or set initialMessage to a valid string");
		}

		game.channel.sendMessage(String.format("**Question %d**: %s", this.initialMessage, questionNumber));
	}

	/** Set the response to the question
	 * @param response The user's response to the question
	 * @return Whether the response was accepted
	 */
	public abstract boolean setResponse(String response);

	/** Deal with the answer
	 * @return The next question to ask, or null if the survey is complete.
	 */
	public abstract SurveyQuestion processAnswer();

	/** Ask the question, wait for response, then process answer, and return the next question to ask. */
	public SurveyQuestion run() {
	}
}
