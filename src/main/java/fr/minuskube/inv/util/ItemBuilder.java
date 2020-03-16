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

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private static java.util.regex.Pattern newlinePattern = java.util.regex.Pattern.compile("\n");

    private ItemStack stack;
    private ItemMeta meta;

    public static ItemBuilder builder(ItemStack stack) {
        return new ItemBuilder(stack);
    }

    public static ItemBuilder builder(Material material, int amount) {
        return builder(new ItemStack(material, amount));
    }

    public static ItemBuilder builder(Material material) {
        return builder(material, 1);
    }

    private ItemBuilder(ItemStack stack) {
        stack = new ItemStack(stack);
        meta = stack.getItemMeta();
    }

    public ItemBuilder name(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder lore(String[] lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder lore(String lore) {
        return lore(newlinePattern.split(lore));
    }

    public ItemBuilder appendLore(String line) {
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(line);
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder removeLore(String line) {
        List<String> lore = meta.getLore();
        if (lore != null) {
            lore.remove(line);
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder color(Color color) {
        switch (stack.getType()) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                lam.setColor(color);
                meta = lam;
                break;
            default:
                throw new IllegalStateException("To be able to use ItemBuilder#color(c) the material MUST be either LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS or LEATHER_BOOTS");
        }
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        return enchantment(enchantment, 1);
    }

    public ItemBuilder metaEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder metaEnchantment(Enchantment enchantment) {
        return metaEnchantment(enchantment, 1);
    }

    public ItemStack build() {
        this.stack.setItemMeta(this.meta);
        return this.stack;
    }
}
