package com.betterLead;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeashListener implements Listener {

    private final Plugin plugin;
    private final Map<UUID, BukkitRunnable> leashTasks;

    public LeashListener(Plugin plugin) {
        this.plugin = plugin;
        this.leashTasks = new HashMap<>();
    }

    @EventHandler
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        entity.setInvulnerable(true);
        // 启动定时任务
        BukkitRunnable leashTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isValid() || !player.isValid()) {
                    this.cancel();
                    leashTasks.remove(entity.getUniqueId());
                    return;
                }

                double distance = player.getLocation().distance(entity.getLocation());
                if (distance > 6) {
                    entity.teleport(player);
                }
            }
        };
        leashTask.runTaskTimer(plugin, 0, 1);
        leashTasks.put(entity.getUniqueId(), leashTask);
    }

    @EventHandler
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
        Entity entity = event.getEntity();
        entity.setInvulnerable(false);
        cancelLeashTask(entity);
    }

    @EventHandler
    public void onEntityUnleash(EntityUnleashEvent event) {
        Entity entity = event.getEntity();
        cancelLeashTask(entity);
        if (event.getReason() == EntityUnleashEvent.UnleashReason.DISTANCE) {
            entity.teleport(event.getEntity().getWorld().getPlayers().iterator().next());
        }
        entity.setInvulnerable(false);
    }

    private void cancelLeashTask(Entity entity) {
        UUID entityId = entity.getUniqueId();
        if (leashTasks.containsKey(entityId)) {
            BukkitRunnable task = leashTasks.get(entityId);
            task.cancel();
            leashTasks.remove(entityId);
        }
    }
}
