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

/**
 * This is a simple class that simplifies the creation of ItemStacks
 */
public class ItemBuilder {

    private static java.util.regex.Pattern newlinePattern = java.util.regex.Pattern.compile("\n");

    private ItemStack stack;
    private ItemMeta meta;

    /**
     * Create a new ItemBuilder instance based on an existing stack.
     * <br><br>
     * The stack is copied using {@link ItemStack#ItemStack(ItemStack)}
     *
     * @param stack The stack to use as a base
     * @return A new ItemBuilder instance
     */
    public static ItemBuilder builder(ItemStack stack) {
        return new ItemBuilder(stack);
    }

    /**
     * Create a new ItemBuilder instance creating an ItemStack with the given material and amount
     *
     * @param material The material to use for the ItemStack
     * @param amount The amount of the item that should be used
     * @return A new ItemBuilder instance
     */
    public static ItemBuilder builder(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    /**
     * Create a new ItemBuilder instance creating an ItemStack with the given material
     *
     * @param material The material to use for the ItemStack
     * @return A new ItemBuilder instance
     */
    public static ItemBuilder builder(Material material) {
        return builder(material, 1);
    }


    private ItemBuilder(ItemStack stack) {
        this.stack = new ItemStack(stack);
        meta = stack.getItemMeta();
    }

    private ItemBuilder(Material material, int amount) {
        stack = new ItemStack(material, amount);
        meta = stack.getItemMeta();
    }


    /**
     * Sets the displayName of the item to the given input
     *
     * @param displayName The new display name
     * @return <code>this</code> instance
     */
    public ItemBuilder name(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }


    /**
     * Sets the lore for the item based on the string array passed to it
     *
     * @param lore The new lore, where each element is a new line
     * @return <code>this</code> instance
     */
    public ItemBuilder lore(String[] lore) {
        return lore(Arrays.asList(lore));
    }

    /**
     * Sets the lore for the item based on the string array passed to it
     *
     * @param lore The new lore, where each element is a new line
     * @return <code>this</code> instance
     */
    public ItemBuilder lore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    /**
     * Sets the lore to the given string. If a newline character (<code>\n</code>) is found, the lore will be split into an additional line
     *
     * @param lore The new lore, each line separated by a <code>\n</code>
     * @return <code>this</code> instance
     */
    public ItemBuilder lore(String lore) {
        return lore(newlinePattern.split(lore));
    }

    /**
     * Appends the given line to the end of the lore. Newline characters (<code>\n</code>) will be ignored
     *
     * @param line The line of lore, that should be appended to the existing lines
     * @return <code>this</code> instance
     */
    public ItemBuilder appendLore(String line) {
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(line);
        meta.setLore(lore);
        return this;
    }

    /**
     * Removes a line from the current lore using {@link List#remove(Object)}
     *
     * @param line The line of lore, that should be removed
     * @return <code>this</code> instance
     */
    public ItemBuilder removeLore(String line) {
        List<String> lore = meta.getLore();
        if (lore != null) {
            lore.remove(line);
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * Sets a new amount for this item stack
     *
     * @param amount The new amount
     * @return <code>this</code> instance
     */
    public ItemBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    /**
     * Applies the given color to the item if the item is leather armor, so it MUST be one of the following
     * <ul>
     *     <li>LEATHER_HELMET</li>
     *     <li>LEATHER_CHESTPLATE</li>
     *     <li>LEATHER_LEGGINGS</li>
     *     <li>LEATHER_BOOTS</li>
     * </ul>
     *
     * @param color The new color that should be applied
     * @return <code>this</code> instance
     * @throws IllegalStateException This exception is thrown, if the material is NOT one of the listed types
     */
    public ItemBuilder leatherColor(Color color) {
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

    /**
     * Adds an enchantment directly to the item stack without level restrictions
     *
     * @param enchantment The enchantment to add
     * @param level The level of the enchantment
     * @return <code>this</code> instance
     */
    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Adds an enchantment with the level 1 to the item stack
     *
     * @param enchantment The enchantment to add
     * @return <code>this</code> instance
     */
    public ItemBuilder enchantment(Enchantment enchantment) {
        return enchantment(enchantment, 1);
    }

    /**
     * Adds an enchantment to the item meta without level restrictions
     *
     * @param enchantment The enchantment to add
     * @param level The level of the enchantment
     * @return <code>this</code> instance
     */
    public ItemBuilder metaEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Adds an enchantment with the level 1 to the item meta
     *
     * @param enchantment The enchantment to add
     * @return <code>this</code> instance
     */
    public ItemBuilder metaEnchantment(Enchantment enchantment) {
        return metaEnchantment(enchantment, 1);
    }

    /**
     * Applies all the modifications to the item meta to the item stack and returns the item stack
     *
     * @return The modified item stack
     */
    public ItemStack build() {
        this.stack.setItemMeta(this.meta);
        return this.stack;
    }
}
