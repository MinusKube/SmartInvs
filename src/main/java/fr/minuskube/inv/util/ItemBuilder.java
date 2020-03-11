/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.minuskube.inv.util;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

public final class ItemBuilder {

    @NotNull
    private final ItemStack itemStack;

    private ItemBuilder(@NotNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @NotNull
    public static ItemBuilder of(@NotNull final XMaterial xMaterial) {
        return ItemBuilder.of(
            Optional.ofNullable(xMaterial.parseMaterial()).orElseThrow(() ->
                new IllegalStateException("Material of the " + xMaterial.name() + " cannot be null!")
            )
        );
    }

    @NotNull
    public static ItemBuilder of(@NotNull final Material material) {
        return ItemBuilder.of(new ItemStack(material));
    }

    @NotNull
    public static ItemBuilder of(@NotNull final ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    @NotNull
    public ItemBuilder name(@NotNull final String displayName) {
        return this.name(displayName, true);
    }

    @NotNull
    public ItemBuilder name(@NotNull final String displayName, final boolean colored) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        if (colored) {
            itemMeta.setDisplayName(this.colored(displayName));
        } else {
            itemMeta.setDisplayName(displayName);
        }
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    @NotNull
    private String colored(@NotNull final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @NotNull
    public ItemBuilder data(final int data) {
        return this.data((byte) data);
    }

    @NotNull
    public ItemBuilder data(final byte data) {
        final MaterialData materialData = this.itemStack.getData();
        materialData.setData(data);
        this.itemStack.setData(materialData);
        return this;
    }

    @NotNull
    public ItemBuilder lore(@NotNull final String... lore) {
        return this.lore(Arrays.asList(lore), true);
    }

    @NotNull
    public ItemBuilder lore(@NotNull final List<String> lore, final boolean colored) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        if (colored) {
            itemMeta.setLore(this.colored(lore));
        } else {
            itemMeta.setLore(
                lore
            );
        }
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    @NotNull
    private List<String> colored(@NotNull final List<String> list) {
        return list.stream().map(this::colored).collect(Collectors.toList());
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull final String... enchantments) {
        for (final String s : enchantments) {
            final String[] split = s.split(":");
            final String enchantment;
            final int level;
            if (split.length == 1) {
                enchantment = split[0];
                level = 1;
            } else {
                enchantment = split[0];
                level = this.getInt(split[1]);
            }
            XEnchantment.matchXEnchantment(enchantment).ifPresent(xEnchantment -> this.enchantments(xEnchantment, level));
        }
        return this;
    }

    private int getInt(@NotNull final String string) {
        try {
            return Integer.parseInt(string);
        } catch (final Exception ignored) {
            // ignored
        }
        return 0;
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull final XEnchantment enchantment, final int level) {
        final Optional<Enchantment> enchantmentOptional = Optional.ofNullable(enchantment.parseEnchantment());
        if (enchantmentOptional.isPresent()) {
            return this.enchantments(enchantmentOptional.get(), level);
        }
        return this;
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull final Enchantment enchantment, final int level) {
        final Map<Enchantment, Integer> map = new HashMap<>();
        map.put(enchantment, level);
        return this.enchantments(map);
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull final Map<Enchantment, Integer> enchantments) {
        this.itemStack.addUnsafeEnchantments(enchantments);
        return this;
    }

    @NotNull
    public ItemStack build() {
        return this.itemStack;
    }

}
