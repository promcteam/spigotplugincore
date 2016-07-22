package com.gotofinal.darkrise.spigot.core.config.elements;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.yaml.snakeyaml.nodes.MappingNode;

import org.diorite.cfg.system.TemplateYamlConstructor.TemplateConstructMapping;
import org.diorite.cfg.system.deserializers.TemplateDeserializer;
import org.diorite.utils.reflections.DioriteReflectionUtils;

@SuppressWarnings("unchecked")
public class ConfigurationSerializableTemplateDeserializer<T extends ConfigurationSerializable> extends TemplateDeserializer<T>
{
    public ConfigurationSerializableTemplateDeserializer(final Class<T> fieldType)
    {
        super(fieldType);
    }

    @Override
    public T construct(final TemplateConstructMapping constructMapping, final MappingNode node)
    {
        Map<Object, Object> objectMap = constructMapping.getTemplateYamlConstructor().constructMapping(node);
        Map<String, Object> resultMap = new LinkedHashMap<>(objectMap.size());
        for (final Entry<Object, Object> entry : objectMap.entrySet())
        {
            resultMap.put(entry.getKey().toString(), entry.getValue());
        }
        node.getValue().clear();
        return (T) DioriteReflectionUtils.getConstructor(node.getType(), Map.class).invoke(resultMap);
    }
}
