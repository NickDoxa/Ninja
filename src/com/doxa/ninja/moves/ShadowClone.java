package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ShadowClone extends MoveBase implements Listener {
	
	Main plugin;
	public ShadowClone(Main main) {
		plugin = main;
	}
	
	public void createItemSC() {
		setName("Shadow Clones", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Shadow Clones");
		setItem(Material.NETHER_STAR);
		setMoveType(MoveType.CLONE);
	}
	
	public void createSCItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	Map<String, Long> sc_cd = new HashMap<String, Long>();
	List<Zombie> zombie_list = new ArrayList<Zombie>();
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			if (sc_cd.containsKey(player.getName())) {
				if (sc_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (sc_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use for " + timeleft + " seconds"));
					return;
				}
			}
			sc_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.CLONE) * 1000));
			plugin.useChakra(0.2, player);
			int zombie_amt = 0;
			while (zombie_amt < plugin.getCloneAmount()) {
				createClones(player);
				zombie_amt++;
			}
			zombie_check.put(player, zombie_list);
		}
	}
	
	Map<Player, List<Zombie>> zombie_check = new HashMap<Player, List<Zombie>>();
	
	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Zombie) {
			Entity p = event.getTarget();
			if (p instanceof Player) {
				Player player = (Player) p;
				if (zombie_check.containsKey(player)) {
					for (Zombie z : zombie_check.get(player)) {
						if (entity == z) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getPlayerHead(String player) {
		
		boolean isNewVersion = Arrays.stream(Material.values())
				.map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		
		Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
		ItemStack item = new ItemStack(type, 1);
		
		if (!isNewVersion)
			item.setDurability((short) 3);
		
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(player);
		
		item.setItemMeta(meta);
		
		return item;
	}
		
	@SuppressWarnings("deprecation")
	public void createClones(Player player) {
		if (plugin.useInvisibility()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3 * 20, 1));
		}
		List<Zombie> del_zombie_list = new ArrayList<Zombie>();
		Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
		zombie.setAdult();
		zombie.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
		final Location location = zombie.getLocation();
		zombie.getWorld().spawnParticle(Particle.SPIT, location.getX(), location.getY(), location.getZ(),
    			10);
		zombie.getEquipment().setHelmet(getPlayerHead(player.getName()));
		zombie.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		zombie.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		zombie.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
		zombie.setCustomNameVisible(true);
		zombie.setCustomName(player.getName());
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2), true);
		del_zombie_list.add(zombie);
		zombie_list.add(zombie);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Zombie z : del_zombie_list) {
					Location location = z.getLocation();
					z.getWorld().spawnParticle(Particle.SPIT, location.getX(), location.getY(), location.getZ(),
	            			10);
					z.remove();
				}
			}
		}, 5 * 20);
	}
	
	@EventHandler
	public void onZombieDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Zombie) {
			Zombie zombie = (Zombie) event.getEntity();
			if (zombie_list.contains(zombie)) {
				final Location location = zombie.getLocation();
				zombie.getWorld().spawnParticle(Particle.SPIT, location.getX(), location.getY(), location.getZ(),
            			10);
				zombie.remove();
			}
		}
	}
}
