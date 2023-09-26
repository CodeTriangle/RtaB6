package tel.discord.rtab.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

public class RankCommand extends ParsingCommand {
    public RankCommand()
    {
        this.name = "rank";
		this.aliases = new String[]{"money"};
        this.help = "view the rank of a player by name, or by rank with #[number]";
        this.guildOnly = false;
    }
	@Override
	protected void execute(CommandEvent event) {
		try
		{
			List<String> list = Files.readAllLines(Paths.get("scores","scores"+event.getChannel().getId()+".csv"));
			String name = event.getArgs();
			int index;
			//Search for own ID if no name given (to ensure match even if name changed)
			if(name.isEmpty())
				index = findUserInList(list,event.getAuthor().getId(),false);
			//Or search by rank if they gave a rank
			else if(name.startsWith("#"))
			{
				try
				{
					index = Integer.parseInt(name.substring(1))-1;
				}
				catch(NumberFormatException e1)
				{
					index = findUserInList(list,name,true); //This will never find anything because "#" is an illegal character lmao (but we tried)
				}
			}
			//Or search by ID if they gave a mention
			else if(event.getArgs().contains("<@"))
				index = findUserInList(list,parseMention(name),false);
			//Or just search by the name given
			else
				index = findUserInList(list,name,true);
			if(index < 0 || index >= list.size())
			{
				if(name.isEmpty())
					event.reply("You haven't played the game yet.");
				else
					event.reply("User not found.");
			}
			else
			{
				String[] record = list.get(index).split("#");
				int money = Integer.parseInt(record[2]);
				int booster = Integer.parseInt(record[3]);
				int winstreak = Integer.parseInt(record[4]);
				StringBuilder response = new StringBuilder();
				response.append(record[1]).append(": ");
				if(money<0)
					response.append("-");
				response.append(String.format("$%1$,d [%2$d%%x%3$d.%4$d]",Math.abs(money),booster,winstreak/10,winstreak%10));
				response.append(" - Rank #").append(index + 1).append("/").append(list.size());
				event.reply(response.toString());
			}
		} catch (IOException e)
		{
			event.reply("This command must be used in a game channel.");
		}
	}

}
