package com.doxa.ninja.moves;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class MoveBase {
	
	Main plugin;
	
	private ItemStack base_item;
	private String base_name;
	private String color_name;
	private String desc;
	private MoveType move;
	
	public void setMoveType(MoveType m) {
		move = m;
	}
	
	public MoveType getMove() {
		return move;
	}
	
	public void setName(String s, String s2) {
		base_name = s;
		color_name = s2;
		System.out.println("[Ninja] Enabling " + s);
	}
	
	public String getName() {
		return base_name;
	}
	
	public String getColorName() {
		return color_name;
	}
	
	public void setItem(Material m) {
		base_item = new ItemStack(m);
	}
	
	public ItemStack getItem() {
		return base_item;
	}
	
	public void setDescription(String s) {
		desc = s;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public void createItem(Player player, String prefix) {
		PlayerInventory inv = player.getInventory();
		ItemMeta item_meta = getItem().getItemMeta();
		item_meta.setDisplayName(getColorName());
		item_meta.addEnchant(Enchantment.DURABILITY, 1, true);
		item_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item_meta.setUnbreakable(true);
		getItem().setItemMeta(item_meta);
		if (inv.contains(getItem())) {
			player.sendMessage(prefix + ChatColor.RED + getName() + " already bound!");
			return;
		}
		if (inv.getItemInMainHand().getType() == null || inv.getItemInMainHand().getType() == Material.AIR) {
			inv.setItem(inv.getHeldItemSlot(), getItem());
		} else {
			ItemStack item_move = inv.getItemInMainHand();
			inv.setItem(inv.firstEmpty(), item_move);
			inv.setItem(inv.getHeldItemSlot(), getItem());
		}
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
				new TextComponent(ChatColor.GREEN + getName() + " Received"));
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		ItemStack item2 = event.getCursor();
		try {
			if (item.getItemMeta().getDisplayName().equals(getColorName()) || 
					item2.getItemMeta().getDisplayName().equals(getColorName())) {
				event.setCancelled(true);
				Player player = (Player) event.getInventory().getHolder();
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.RED + "You can't change the " + getName() + " position!"));
			}
		} catch (NullPointerException e) {
			return;
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		try {
			if (item.getItemStack().getItemMeta().getDisplayName().equals(getColorName())) {
				event.setCancelled(true);
			}
		} catch (NullPointerException e) {
			return;
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		int i = 0;
		try {
			while (i < 10) {
				ItemStack item = event.getDrops().get(i);
				if (item != null)
				if (item.hasItemMeta())
				if (item.getItemMeta().hasDisplayName())
				if (item.getItemMeta().getDisplayName().equals(getColorName())) {
					event.getDrops().remove(i);
					return;
				}
				i++;
			}
		} catch (IndexOutOfBoundsException e) {
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(PlayerInteractEvent event) {
		try {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || 
				event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			ItemStack item = event.getPlayer().getItemInHand();
			if (item != null)
			if (item.hasItemMeta())
			if (item.getItemMeta().hasDisplayName())
			if (item.getItemMeta().getDisplayName().equals(getColorName())) {
				event.setCancelled(true);
			}
		}
		} catch (NullPointerException e) {
			event.setCancelled(true);
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFall(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1), true);
			}
		}
	}
	
	public void clear(Player player) {
		Inventory inv = player.getInventory();
		ItemStack[] items = inv.getContents();
		for (ItemStack i : items) {
			if (i != null) 
			if (i.hasItemMeta())
			if (i.getItemMeta().hasDisplayName())
			if (i.getItemMeta().getDisplayName().equals(getColorName()))
				inv.remove(i);
		}
	}
}
