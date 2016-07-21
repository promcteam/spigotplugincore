package com.gotofinal.messages.bukkit;

import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Logger;

import com.gotofinal.messages.BaseMessagesAPI;
import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.ReceiverConverter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.chat.BaseComponent;

public class BukkitMessagesAPI extends BaseMessagesAPI
{
    protected ReceiverConverter<CommandSender> receiverConverter = new BukkitReceiverConverter(this);
    protected final Plugin plugin;

    public BukkitMessagesAPI(final Plugin plugin, final Locale... locales)
    {
        super(locales);
        this.plugin = plugin;
    }

    @Override
    public ReceiverConverter<CommandSender> getReceiverConverter()
    {
        return this.receiverConverter;
    }

    /**
     * Wraps given sender instance into MessageReceiver.
     *
     * @param sender
     *         sender instance to wrap.
     *
     * @return created MessageReceiver for given sender.
     */
    public MessageReceiver wrap(final CommandSender sender)
    {
        return this.receiverConverter.apply(sender);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setReceiverConverter(final ReceiverConverter<?> receiverConverter)
    {
        this.receiverConverter = (ReceiverConverter<CommandSender>) receiverConverter;
    }

    @Override
    public void broadcastMessage(final BaseComponent msg)
    {
        Bukkit.spigot().broadcast(msg);
    }

    @Override
    public void broadcastMessage(final BaseComponent... msg)
    {
        Bukkit.spigot().broadcast(msg);
    }

    @Override
    public Iterable<MessageReceiver> getReceivers()
    {
        return new PlayerIterable(this.receiverConverter, Bukkit.getOnlinePlayers());
    }

    @Override
    public Logger getLogger()
    {
        return this.plugin.getLogger();
    }

    private static final class PlayerIterable implements Iterable<MessageReceiver>
    {
        private final Function<CommandSender, MessageReceiver> func;
        private final Iterable<? extends CommandSender>        senders;

        private PlayerIterable(final Function<CommandSender, MessageReceiver> func, final Iterable<? extends CommandSender> senders)
        {
            this.func = func;
            this.senders = senders;
        }

        @Override
        public Iterator<MessageReceiver> iterator()
        {
            return new PlayerIterator(this.func, this.senders.iterator());
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("func", this.func).append("senders", this.senders).toString();
        }
    }

    private static final class PlayerIterator implements Iterator<MessageReceiver>
    {
        private final Function<CommandSender, MessageReceiver> func;
        private final Iterator<? extends CommandSender>        playerIterator;

        private PlayerIterator(final Function<CommandSender, MessageReceiver> func, final Iterator<? extends CommandSender> playerIterator)
        {
            this.func = func;
            this.playerIterator = playerIterator;
        }

        @Override
        public boolean hasNext()
        {
            return this.playerIterator.hasNext();
        }

        @Override
        public MessageReceiver next()
        {
            return this.func.apply(this.playerIterator.next());
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("func", this.func).append("playerIterator", this.playerIterator).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("receiverConverter", this.receiverConverter).append("plugin", this.plugin).toString();
    }
}
