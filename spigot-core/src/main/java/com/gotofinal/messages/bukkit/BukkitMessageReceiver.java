package com.gotofinal.messages.bukkit;

import com.gotofinal.messages.api.MessageReceiver;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

public class BukkitMessageReceiver implements MessageReceiver
{
    private final BukkitMessagesAPI api;
    private final CommandSender     sender;

    public BukkitMessageReceiver(final BukkitMessagesAPI api, final CommandSender sender)
    {
        this.api = api;
        this.sender = sender;
    }

    /**
     * Sends message to receiver.
     *
     * @param msg message to send.
     */
    @Override
    public void sendMessage(final BaseComponent msg)
    {
        if (this.sender instanceof Player)
        {
            ((Player) this.sender).spigot().sendMessage(msg);
        }
        else
        {
            this.sender.sendMessage(msg.toPlainText());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("api", this.api).append("sender", this.sender).toString();
    }
}
