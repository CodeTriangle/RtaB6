package tel.discord.rtab.bombs;

import tel.discord.rtab.Achievement;
import tel.discord.rtab.GameController;

public class BankruptBomb implements Bomb
{
	public void explode(GameController game, int victim, int penalty)
	{
		int amountLost = game.players.get(victim).resetRoundDelta();
		//If they didn't have any money to lose, just drop back to the basic bomb code
		if(amountLost == 0)
			Bomb.super.explode(game, victim, penalty);
		else
		{
			game.channel.sendMessage("It goes **BOOM**...").queue();
			try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			game.channel.sendMessage("It also goes **BANKRUPT**. _\\*whoosh*_").queue();
			try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			if(amountLost < 0)
			{
				game.channel.sendMessage(String.format("**$%1$,d** *returned*, plus $%2$,d penalty.",
						Math.abs(amountLost),Math.abs(penalty))).queue();
			}
			else
				game.channel.sendMessage(String.format("**$%1$,d** lost, plus $%2$,d penalty.",
						amountLost,Math.abs(penalty))).queue();
			StringBuilder extraResult = game.players.get(victim).blowUp(penalty,false);
			if(extraResult != null)
				game.channel.sendMessage(extraResult).queue();
			if(amountLost < 2*game.players.get(victim).getRoundDelta())
				Achievement.UNBANKRUPT.check(game.players.get(victim));
		}
	}
}
