package ru.deade.minetg;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ru.deade.telegramBotClient.Formatting;
import ru.deade.telegramBotClient.TelegramBotClient;
import ru.deade.telegramBotClient.APIModel.TelegramMessage;
import ru.deade.telegramBotClient.APIModel.TelegramUpdate;
import net.md_5.bungee.api.chat.TextComponent;

public class MineTG extends JavaPlugin {
	
	private static MineTG instance;
	
	public MineTG() {
		if (MineTG.instance != null) {
			throw new Error("Plugin already initialized!");
		}
		
		MineTG.instance = this;
	}
	
	public static MineTG getInstance() {
		return instance;
	}
	
	public long adminChatId;
	public long explChatId;
	public String telegramBotToken;
	public String telegramBotName;
	
	public TelegramBotClient telegram;
	
	@Override
	public void onEnable()	{
		Logger logger = getServer().getLogger();
				
		this.saveDefaultConfig();
		adminChatId = this.getConfig().getLong("telegram-bot.admin-chat-id");
		explChatId = this.getConfig().getLong("telegram-bot.expl-chat-id");
		telegramBotToken = this.getConfig().getString("telegram-bot.token");
		telegramBotName = this.getConfig().getString("telegram-bot.name");
		//this.getConfig().set("telegram-bot.chat-id", 234234234);
		//this.getConfig().set("telegram-bot.token", "afsfaskdfaskdjfoasdfjsldfkjasdf");
		telegram = new TelegramBotClient(telegramBotToken);
		//getCommand("cmd1").setExecutor(new CmdExecutor());
		//getCommand("cmd2").setExecutor(new CmdExecutor());
			
		getServer().getPluginManager().registerEvents(new EventsListener(), this);
		
		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			TelegramUpdate[] updates = telegram.getNextUpdates();
            for (TelegramUpdate update : updates) {
                if (update.message != null) {
                    TelegramMessage message = update.message;
                    
                    logger.info(message.from.first_name + " ввел: " + message.text);
                    
                    long chatId = message.chat.id;
                    
                    //String chid = "chatId" + chatId;
                    //logger.warning(chid+", adminChatId="+adminChatId);
                                        
                    if (chatId == explChatId) {
                    	String s = Character.toString(message.text.charAt(0));
                    	if (s.equalsIgnoreCase("/")) {
                    		
                    		Bukkit.getScheduler().runTask(this, () -> {
                    		
                    			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message.text);
                    		});
                    	                    		
                    	}
                    	else {
                    		
                    	TextComponent formatted = Formatting.formatTelegramMessageToMinecraft(message);
                        Bukkit.getServer().spigot().broadcast(formatted);
                        }
                    }
                    else {
                    // Avoid infinite loops
                    if (message.from.is_bot) return;

                    String info = String.format("Set `telegram-chat-id` to `%d` in `plugins/MineTG/config.yml` " +
                    		"if you want to integrate this chat with the Minecraft chat", chatId);
                    telegram.sendMarkdownMessage(chatId, info);
                    }
                }
            }
		}, 10, 10);
	}
	
	
	@Override
	public void onDisable() {
		this.saveConfig();
	}
	
	
	

}
