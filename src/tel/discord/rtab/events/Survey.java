package tel.discord.rtab.events;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tel.discord.rtab.GameController;
import tel.discord.rtab.MoneyMultipliersToUse;
import tel.discord.rtab.Player;
import tel.discord.rtab.RaceToABillionBot;
import tel.discord.rtab.events.surveyQuestions.*;

class Survey implements EventSpace {
	public static final int COMPLETION_REWARD = 10_000;
	public static final int BAIL_FINE = 100_000;

	@Override
	public String getName() {
		return "RtaB Survey";
	}

	@Override
	public void execute(GameController game, int playerNumber) {
		Player player = game.players.get(playerNumber);
		AtomicReference<SurveyQuestion> nextQuestion = new AtomicReference<>();
		AtomicBoolean goodEnding = new AtomicBoolean();

		while (nextQuestion != null) {
			CompletableFuture<SurveyQuestion> response = new CompletableFuture<>();
			RaceToABillionBot.waiter.waitForEvent(
				MessageReceivedEvent.class,
				e -> (
					e.getChannel().getId().equals(game.channel.getId()) &&
					e.getAuthor().equals(player.user) &&
					nextQuestion.get().setResponse((e.getMessage().toString()))
				),
				e -> {
					response.complete(nextQuestion.get().processAnswer());
				},
				30, TimeUnit.SECONDS,
				() -> {
					goodEnding.set(false);
					response.complete(null);
				}
			);

			nextQuestion.set(response.join());
		}

		try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

		StringBuilder extraResult = null;

		if (goodEnding.get()) {
			game.channel.sendMessage("Thank you for completing our survey!").queue();
			try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			game.channel.sendMessage(String.format("As compensation, you've earned **$%,d**.", COMPLETION_REWARD));
			extraResult = player.addMoney(COMPLETION_REWARD, MoneyMultipliersToUse.BONUS_ONLY);
		} else {
			game.channel.sendMessage("We're sorry you didn't like our survey.");
			try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			game.channel.sendMessage(String.format("Since you didn't finish, we have to fine you **$%,d**.", BAIL_FINE));
			extraResult = player.addMoney(COMPLETION_REWARD, MoneyMultipliersToUse.BONUS_ONLY);
		}

		if (extraResult != null) {
			game.channel.sendMessage(extraResult.toString());
		}
	}
}
