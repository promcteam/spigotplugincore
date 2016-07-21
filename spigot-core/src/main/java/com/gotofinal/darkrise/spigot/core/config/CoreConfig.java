package com.gotofinal.darkrise.spigot.core.config;

import java.util.Collections;
import java.util.List;

import com.gotofinal.darkrise.spigot.core.utils.cmds.CommandType;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;

import org.diorite.cfg.annotations.CfgClass;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;

@CfgClass(name = "CoreConfig")
public class CoreConfig
{
    @CfgIntDefault(20)
    @CfgComment("Amount of health that will be visible for player. (real amount will be scaled to this amount)")
    @CfgComment("Values below 0 will disable this option.")
    private double scaleHealth;

    @CfgComment("Commands executed for player on login. This placeholders are supported: ")
    @CfgComment("{player} - name of player")
    private List<DelayedCommand> onJoin = Collections.singletonList(new DelayedCommand(CommandType.OP, "give {player} cookie 1", 100));

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
}
