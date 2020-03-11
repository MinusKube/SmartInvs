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

    private ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @NotNull
    public static ItemBuilder of(@NotNull ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    @NotNull
    public static ItemBuilder of(@NotNull Material material) {
        return of(new ItemStack(material));
    }

    @NotNull
    public static ItemBuilder of(@NotNull XMaterial xMaterial) {
        return of(
            Optional.ofNullable(xMaterial.parseMaterial()).orElseThrow(() ->
                new IllegalStateException("Material of the " + xMaterial.name() + " cannot be null!")
            )
        );
    }

    @NotNull
    public ItemBuilder name(@NotNull String displayName) {
        return name(displayName, true);
    }

    @NotNull
    public ItemBuilder name(@NotNull String displayName, boolean colored) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        if (colored) {
            itemMeta.setDisplayName(colored(displayName));
        } else {
            itemMeta.setDisplayName(displayName);
        }
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    @NotNull
    public ItemBuilder data(int data) {
        return data((byte) data);
    }

    @NotNull
    public ItemBuilder data(byte data) {
        final MaterialData materialData = itemStack.getData();
        materialData.setData(data);
        itemStack.setData(materialData);
        return this;
    }

    @NotNull
    public ItemBuilder lore(@NotNull String... lore) {
        return lore(Arrays.asList(lore), true);
    }

    @NotNull
    public ItemBuilder lore(@NotNull List<String> lore, boolean colored) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        if (colored) {
            itemMeta.setLore(colored(lore));
        } else {
            itemMeta.setLore(
                lore
            );
        }
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull String... enchantments) {
        for (String s : enchantments) {
            final String[] split = s.split(":");
            final String enchantment;
            final int level;
            if (split.length == 1) {
                enchantment = split[0];
                level = 1;
            } else {
                enchantment = split[0];
                level = getInt(split[1]);
            }
            XEnchantment.matchXEnchantment(enchantment).ifPresent(xEnchantment -> enchantments(xEnchantment, level));
        }
        return this;
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull XEnchantment enchantment, int level) {
        final Optional<Enchantment> enchantmentOptional = Optional.ofNullable(enchantment.parseEnchantment());
        if (enchantmentOptional.isPresent()) {
            return enchantments(enchantmentOptional.get(), level);
        }
        return this;
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull Enchantment enchantment, int level) {
        final Map<Enchantment, Integer> map = new HashMap<>();
        map.put(enchantment, level);
        return enchantments(map);
    }

    @NotNull
    public ItemBuilder enchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        itemStack.addUnsafeEnchantments(enchantments);
        return this;
    }

    private int getInt(@NotNull String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception ignored) {
            // ignored
        }
        return 0;
    }

    @NotNull
    public ItemStack build() {
        return this.itemStack;
    }

    @NotNull
    private List<String> colored(@NotNull final List<String> list) {
        return list.stream().map(this::colored).collect(Collectors.toList());
    }

    @NotNull
    private String colored(@NotNull final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
