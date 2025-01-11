package tel.discord.rtab.events.surveyQuestions;

import tel.discord.rtab.GameController;

public abstract class ScaleQuestion extends SurveyQuestion {
	int answer;
	boolean responseValid = false;
	int rangeStart;
	int rangeEnd;

	protected ScaleQuestion(GameController game, int player, int rangeStart, int rangeEnd) {
		super(game, player);
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}

	@Override
	public boolean setResponse(String response) {
		response = response.trim();
		try {
			this.answer = Integer.parseInt(response);
			responseValid = true;
		} catch (NumberFormatException e) {
			responseValid = false;
		}
		return responseValid;
	}
}
