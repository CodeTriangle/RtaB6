package tel.discord.rtab.bombs;

import tel.discord.rtab.GameController;
import tel.discord.rtab.PlayerStatus;

public class BoostBlast implements Bomb
{
	public void explode(GameController game, int victim, int penalty)
	{
		game.channel.sendMessage("It goes **BOOM**...").queue();
		try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
		if (game.playersAlive > 1 && game.players.get(victim).booster > 100)
		{
			int excessBoost = game.players.get(victim).booster - 100;
			int livingPlayers = game.playersAlive - 1;
			int boostPerPlayer = excessBoost / livingPlayers;
			if (boostPerPlayer < 1)
			{
				boostPerPlayer = 1; //give a minimum if there is /some/ boost
			}		
			game.channel.sendMessage("And blasts their boost between the players! "
					+ String.format("%,d%% boost awarded to living players!",boostPerPlayer)).queue();
			for(int i=0; i<game.players.size(); i++)
			{
				if(game.players.get(i).status == PlayerStatus.ALIVE && i != victim)
				{
					game.players.get(i).addBooster(boostPerPlayer);
				}
			}	
		}
		game.channel.sendMessage(String.format("$%,d lost as penalty.",Math.abs(penalty))).queue();
		StringBuilder extraResult = game.players.get(victim).blowUp(penalty,false);
		if(extraResult != null)
			game.channel.sendMessage(extraResult).queue();
	}
}
