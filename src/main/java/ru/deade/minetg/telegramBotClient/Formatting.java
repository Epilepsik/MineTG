package ru.deade.minetg.telegramBotClient;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import ru.deade.minetg.MineTG;
import ru.deade.minetg.telegramBotClient.APIModel.TelegramMessage;
import ru.deade.minetg.telegramBotClient.APIModel.TelegramMessageEntity;
import ru.deade.minetg.telegramBotClient.APIModel.TelegramUser;

public class Formatting {

    public static TextComponent formatTelegramMessageToMinecraft(TelegramMessage message) {
        TextComponent msg = new TextComponent();
        boolean action = false;
        TextComponent name = tgUserToTextComponent(message.from);

        if (message.forward_from != null) {
            TextComponent fwd_name = tgUserToTextComponent(message.forward_from);
            TextComponent fwdComponent = new TextComponent("[Fwd: ");
            fwdComponent.setColor(ChatColor.AQUA);
            fwdComponent.addExtra(fwd_name);
            fwdComponent.addExtra(new TextComponent("] "));
            msg.addExtra(fwdComponent);
        }

        if (message.text != null) {
            // TODO fix formatting
            String text = message.text;
            if (message.entities != null) {
                text = addBoldAndItalicFormatting(text, message.entities);
            }
            msg.addExtra(new TextComponent(convertEmojisToMinecraft(text)));

        } else if (message.caption != null) {
            // TODO fix formatting
            msg.addExtra(new TextComponent("§3[Photo]§r "));
            String text = message.caption;
            if (message.caption_entities != null) {
                text = addBoldAndItalicFormatting(text, message.caption_entities);
            }
            msg.addExtra(new TextComponent(convertEmojisToMinecraft(text)));

        } else if (message.photo != null) {
            // TODO fix formatting
            msg.addExtra(new TextComponent("§3[Photo]§r "));

        } else if (message.sticker != null) {
            // TODO fix formatting
            msg.addExtra(new TextComponent(String.format("§a[Sticker]§r %s from %s",
                    convertEmojisToMinecraft(message.sticker.emoji),
                    convertEmojisToMinecraft(message.sticker.set_name))));

        } else if (message.location != null) {
            // TODO fix formatting
        	msg.addExtra(new TextComponent(String.format("§3[Location: lat: %s, long: %s]§r",
        			message.location.latitude,
        			message.location.longitude)));

        } else if (message.new_chat_members != null) {
            msg.addExtra(new TextComponent("added "));

            for (int i = 0; i < message.new_chat_members.length; i++) {
                if (i > 0) {
                    msg.addExtra(new TextComponent(", "));
                }
                msg.addExtra(tgUserToTextComponent(message.new_chat_members[i]));
            }
            action = true;

        } else if (message.left_chat_member != null) {
            action = true;
            msg.addExtra(new TextComponent("removed "));
            msg.addExtra(tgUserToTextComponent(message.left_chat_member));

        } else {
            // TODO fix formatting
            msg.addExtra(new TextComponent("§7[An unrecognized message type]"));
        }

        TextComponent formatted = new TextComponent();
        if (action) {
            formatted.setColor(ChatColor.YELLOW);
            formatted.addExtra(name);
            formatted.addExtra(new TextComponent(" "));
            formatted.addExtra(msg);

        } else {
            formatted.addExtra(name);
            formatted.addExtra(new TextComponent(": "));
            formatted.addExtra(msg);
        }

        return formatted;
    }

    public static TextComponent tgUserToTextComponent(TelegramUser user) {
    	TextComponent userComponent;
    	BaseComponent[] usernameComponent;
    	if (user.id == MineTG.getInstance().adminChatId) {
    		//userComponent = new TextComponent(user.getName().replace("§", "⅋"));
    		userComponent = new TextComponent("§4deade");
        } else {
        	userComponent = new TextComponent(user.getName().replace("§", "⅋"));
        }
    	                
        if (user.username != null && user.id != MineTG.getInstance().adminChatId) {
            usernameComponent = new ComponentBuilder("@" + user.username).create();
        } else { 
        	if (user.id == MineTG.getInstance().adminChatId) {
        		usernameComponent = new ComponentBuilder("самый злой админ").create();
        		}
        	else
        		usernameComponent = new ComponentBuilder("No username").create();
        }
        
        userComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, usernameComponent));
        
        return userComponent;
    }

    public static String addBoldAndItalicFormatting(String text, TelegramMessageEntity[] entities) {
        boolean[] boldCharacters = new boolean[text.length()];
        boolean[] italicCharacters = new boolean[text.length()];

        for (int i = 0; i < text.length(); i++) {
            boldCharacters[i] = false;
            italicCharacters[i] = false;
        }

        for (TelegramMessageEntity entity : entities) {
            if (entity.type.equals("bold")) {
                for (int i = 0; i < entity.length; i++) {
                    boldCharacters[entity.offset + i] = true;
                }
            }
            if (entity.type.equals("italic")) {
                for (int i = 0; i < entity.length; i++) {
                    italicCharacters[entity.offset + i] = true;
                }
            }
        }

        String newText = "";
        boolean prevBold = false;
        boolean prevItalic = false;
        for (int i = 0; i < text.length(); i++) {
            boolean bold = boldCharacters[i];
            boolean italic = italicCharacters[i];

            if ((prevBold && !bold) || (prevItalic && !italic)) {
                newText += "§r";
                if (bold) {
                    newText += "§l";
                }
                if (italic) {
                    newText += "§o";
                }
            }
            if (bold && !prevBold) {
                newText += "§l";
            }
            if (italic && !prevItalic) {
                newText += "§o";
            }

            newText += text.substring(i, i+1);

            prevBold = bold;
            prevItalic = italic;
        }

        return newText;
    }

    public static String convertEmojisToMinecraft(String withEmojis) {
        return (withEmojis
                .replace("😃", "§6=D§r ")
                .replace("😄", "§6:D§r ")
                .replace("😟", "§6D:§r ")
                .replace("😂", "§3X§6D§r ")
                .replace("😆", "§6XD§r ")
                .replace("🤓", "§6:3§r ")
                .replace("😎", "§8B§6)§r ")
                .replace("🤩", "§e⁑§6D§r ")
                .replace("😘", "§6︰§c*§r ")
                .replace("😭", "§3π§6o§3π§r ")
                .replace("😢", "§6︰§3'§6(§r ")
                .replace("😑", "§6⚍§r ")
                .replace("🆘", "§csos§r ")
                .replace("🔥", "§c`§6Δ§c‘§r ")
                .replace("💯", "§4¹ºº§r ")
                .replace("👌", "§65/5§r ")
                .replace("👍", "§6+1§r ")
                .replace("👎", "§6-1§r ")
                .replace("🍑", "§6❦§r ")
                .replace("❤️", "§c‹3§r ")
                .replace("🧡", "§6‹3§r ")
                .replace("💛", "§e‹3§r ")
                .replace("💚", "§2‹3§r ")
                .replace("💙", "§9‹3§r ")
                .replace("💜", "§5‹3§r ")
                .replace("🖤", "§8‹3§r ")
                .replace("💕", "§d‹33§r ")
                .replace("💗", "§d‹‹3§r ")
                .replace("💔", "§c‹/3§r ")
                .replace("❣️", "§c❣️§r ")
        );
    }
}
