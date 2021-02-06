package com.doxa.ninja.moves;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Agility extends MoveBase implements Listener {

	Main plugin;
	public Agility(Main main) {
		plugin = main;
	}
	
	public void createItemAgility() {
		setName("Agility", ChatColor.YELLOW + "" + ChatColor.BOLD + "Agility");
		setItem(Material.FEATHER);
		setMoveType(MoveType.AGILITY);
	}
	
	public void createAgItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJump(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand() != null && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && 
				player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName())) {
			double post = event.getTo().getY();
			double pre = event.getFrom().getY();
			boolean block_check = post > pre  ? true : false;
			PotionEffect ninja_speed = new PotionEffect(PotionEffectType.SPEED, 1000000, 2, true, false, false);
			player.addPotionEffect(ninja_speed, true);
			if (block_check && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
				Vector vec = new Vector(0, 1, 0);
				player.setVelocity(vec);
			}
			if (player.isSneaking() && player.isOnGround()) {
				Vector vec = player.getEyeLocation().getDirection();
				player.setVelocity(new Vector(vec.getX() * 0.6, vec.getY() * 0, vec.getZ() * 0.6));
			}
		}
	}
	
	Map<String, Long> ag_cd = new HashMap<String, Long>();
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			
			if (ag_cd.containsKey(player.getName())) {
				if (ag_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (ag_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use for " + timeleft + " seconds"));
					return;
				}
			}
			ag_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.AGILITY) * 1000));
			Vector vec = player.getEyeLocation().getDirection();
			player.setVelocity(new Vector(vec.getX() * 2, vec.getY(), vec.getZ() * 2));
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DOLPHIN_JUMP, 1, 1);
		}
	}
	
	@EventHandler
	public void eventItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		try {
		if (!item.getItemMeta().getDisplayName().equals(getColorName()))
			player.removePotionEffect(PotionEffectType.SPEED);
		} catch (NullPointerException e) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
	}
}
