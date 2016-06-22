package com.gotofinal.darkrise.core.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.Material;

import org.diorite.cfg.annotations.defaults.CfgCustomDefault;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CfgCustomDefault(Material.class)
public @interface CfgMaterialDefault
{
    Material value();
}