package com.doxa.ninja.moves;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Taijutsu extends MoveBase implements Listener {

	Main plugin;
	public Taijutsu(Main main) {
		plugin = main;
	}
	
	public void createItemTai() {
		setName("Taijutsu", ChatColor.GOLD + "" + ChatColor.BOLD + "Taijutsu");
		setItem(Material.RABBIT_FOOT);
		setMoveType(MoveType.TAIJUTSU);
		setDescription("Taijutsu is a martial art based upon traditional fighting styles. Using kicks and punches the attacker damages their opponent! To punch: left click an enemy."
				+ " To kick: hold shift while jumping and left click an enemy! This move does not require chakra.");
	}
	
	public void createTaiItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	Map<String, Long> tai_cd = new HashMap<String, Long>();
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			//KICK
			if (player.isSneaking() && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
				if (tai_cd.containsKey(player.getName())) {
					if (tai_cd.get(player.getName()) > System.currentTimeMillis()) {
						long timeleft = (tai_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
								new TextComponent(ChatColor.RED + "Cannot use for " + timeleft + " seconds"));
						return;
					}
				}
				tai_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.CHIDORI) * 1000));
				event.setDamage((plugin.getDamage(MoveType.TAIJUTSU) * 2.0));
				if (entity instanceof Player) {
					((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (5*20), 5, true, false, false));
					((Player) entity).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You were kicked in the " 
								+ ChatColor.DARK_RED + "" + ChatColor.BOLD + "head!"));
				}
			//PUNCH
			} else {
				event.setDamage(plugin.getDamage(MoveType.TAIJUTSU));
			}
			player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1, 1);
			player.getWorld().spawnParticle(Particle.CRIT, entity.getLocation(), 10);
		}
	}
	
}
