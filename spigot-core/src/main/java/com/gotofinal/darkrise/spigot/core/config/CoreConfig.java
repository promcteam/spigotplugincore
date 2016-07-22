package com.gotofinal.darkrise.spigot.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.gotofinal.darkrise.spigot.core.utils.cmds.CommandType;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;

import org.bukkit.Material;

import org.diorite.cfg.annotations.CfgClass;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;

@CfgClass(name = "CoreConfig")
public class CoreConfig
{
    @CfgIntDefault(20)
    @CfgComment("Amount of health that will be visible for player. (real amount will be scaled to this amount)")
    @CfgComment("Values below 0 will disable this option.")
    private double scaleHealth;

    @CfgComment("Commands executed for player on login. This placeholders are supported: ")
    @CfgComment("{player} - name of player")
    private List<DelayedCommand> onJoin     = Collections.singletonList(new DelayedCommand(CommandType.CONSOLE, "give {player} cookie 1", 100));
    @CfgComment("Commands executed when player click on block, use type of -1 to ignore type.")
    @CfgComment("Players with 'core.oninteract.bypass' permission will be ignored.")
    private List<CommandBlock>   onInteract = Collections.singletonList(new CommandBlock(Material.WORKBENCH, - 1, null, true, new DelayedCommand(CommandType.CONSOLE, "give {player} cookie 1", 0)));

    private transient Map<Material, Byte2ObjectMap<CommandBlock>> onInteractMap = new EnumMap<>(Material.class);

    public double getScaleHealth()
    {
        return this.scaleHealth;
    }

    public void setScaleHealth(final double scaleHealth)
    {
        this.scaleHealth = scaleHealth;
    }

    public List<DelayedCommand> getOnJoin()
    {
        return this.onJoin;
    }

    public void setOnJoin(final List<DelayedCommand> onJoin)
    {
        this.onJoin = onJoin;
    }

    public List<CommandBlock> getOnInteract()
    {
        return this.onInteract;
    }

    public Collection<CommandBlock> getOnInteract(Material material, int type)
    {
        Byte2ObjectMap<CommandBlock> map = this.onInteractMap.get(material);
        if (map == null)
        {
            return Collections.emptyList();
        }
        Collection<CommandBlock> result = new ArrayList<>(0);
        CommandBlock commandBlock = map.get((byte) (- 1));
        if (commandBlock != null)
        {
            result.add(commandBlock);
        }
        commandBlock = map.get((byte) type);
        if (commandBlock != null)
        {
            result.add(commandBlock);
        }
        return result;
    }

    public void setOnInteract(final List<CommandBlock> onInteract)
    {
        this.onInteract = onInteract;
        for (final CommandBlock commandBlock : onInteract)
        {
            Byte2ObjectMap<CommandBlock> map = this.onInteractMap.get(commandBlock.getMaterial());
            if (map == null)
            {
                map = new Byte2ObjectOpenHashMap<>(5);
                this.onInteractMap.put(commandBlock.getMaterial(), map);
            }
            map.put((byte) commandBlock.getType(), commandBlock);
        }
    }
}
