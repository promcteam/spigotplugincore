package com.gotofinal.messages.bukkit;

import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.ReceiverConverter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.command.CommandSender;

public class BukkitReceiverConverter implements ReceiverConverter<CommandSender>
{
    private final BukkitMessagesAPI api;

    public BukkitReceiverConverter(final BukkitMessagesAPI api)
    {
        this.api = api;
    }

    @Override
    public MessageReceiver apply(final CommandSender commandSender)
    {
        return new BukkitMessageReceiver(this.api, commandSender);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("api", this.api).toString();
    }
}
