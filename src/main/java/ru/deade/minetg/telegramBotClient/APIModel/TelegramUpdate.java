package ru.deade.minetg.telegramBotClient.APIModel;

public class TelegramUpdate {
    public long update_id;
    public TelegramMessage message;
    public TelegramMessage edited_message;
    public TelegramMessage channel_post;
    public TelegramMessage edited_channel_post;
}