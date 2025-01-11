package tel.discord.rtab.events.surveyQuestions;

import tel.discord.rtab.GameController;

public abstract class MultipleChoiceQuestion extends SurveyQuestion {
	int answer = 0;
	boolean responseValid = false;
	final String[] choices;

	protected MultipleChoiceQuestion(GameController game, int player, String[] choices) {
		super(game, player);
		this.choices = choices;
	}

	@Override
	public boolean setResponse(String response) {
		response = response.toLowerCase().trim();
		if (response.length() > 1) {
			return false;
		}
		char answer = response.charAt(0);
		answer -= 'a';

		if (answer >= choices.length) {
			return false;
		}

		this.answer = answer;

		return responseValid = true;
	}
}
