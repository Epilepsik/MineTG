package ru.deade.minetg;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class EventsListener implements Listener {
	
	Long expChId = MineTG.getInstance().explChatId;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		e.setJoinMessage(player.getName() + "§a зашел на сервер!");
		if (player.isOp() || player.hasPermission("group.admin")) {
			for (Player pp : Bukkit.getOnlinePlayers()) {
				pp.playSound(pp.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 100.0F, 100.0F);
			}
			e.setJoinMessage("§cАдминистратор §e" + player.getName() + "§c зашел на сервер!");
		}
		else {
			player.sendMessage("Добро пожаловать!");
		}
		
		List<String> onlinePlayers = new ArrayList<String>();
        for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
            onlinePlayers.add(pl.getName());
        }
        updatePlayersOnlineInTelegram(onlinePlayers.toArray(new String[onlinePlayers.size()]));
		
		String name = e.getPlayer().getName();
        String messageToTelegram = String.format("%s joined the game", name);
        MineTG.getInstance().telegram.sendMessage(expChId, messageToTelegram);
		
	}
	
	@EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String name = event.getPlayer().getName();
        String message = event.getMessage();
        String messageToTelegram = String.format("<%s> %s", name, message);
        MineTG.getInstance().telegram.sendMessage(expChId, messageToTelegram);
    }
	
	@EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String messageToTelegram = event.getDeathMessage();
        MineTG.getInstance().telegram.sendMessage(expChId, messageToTelegram);
    }
	
	@EventHandler
    public void onQuit(PlayerQuitEvent event) {
        List<String> onlinePlayers = new ArrayList<String>();
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!player.equals(event.getPlayer())) {
                onlinePlayers.add(player.getName());
            }
        }
        updatePlayersOnlineInTelegram(onlinePlayers.toArray(new String[onlinePlayers.size()]));

        String name = event.getPlayer().getName();
        String messageToTelegram = String.format("%s left the game", name);
        MineTG.getInstance().telegram.sendMessage(expChId, messageToTelegram);
    }
	
	public void updatePlayersOnlineInTelegram(String[] players) {
        String message = "";
        if (players.length == 0) {
            message = "Nobody online in Minecraft";
        } else {
            message = "Online in Minecraft: " + String.join(", ", players);
        }
        MineTG.getInstance().telegram.setChatDescription(expChId, message);
    }
	
	

}
