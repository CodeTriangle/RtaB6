package tel.discord.rtab.events.surveyQuestions;

import tel.discord.rtab.GameController;

public abstract class YesNoQuestion extends SurveyQuestion {
	boolean answer = false;
	boolean responseValid = false;

	protected YesNoQuestion(GameController game, int player) {
		super(game, player);
	}

	@Override
	public boolean setResponse(String response) {
		response = response.toLowerCase().trim();
		if (response == "yes" || response == "y") {
			answer = true;
			responseValid = true;
		}
		if (response == "no" || response == "n") {
			answer = false;
			responseValid = true;
		}
		return responseValid;
	}
}
