package br.AtomGamers.WRP;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PluginService implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void run(PlayerMoveEvent e) {
        Player sender = e.getPlayer();
        if (SimpleRegionManager.inAnyArea(sender)) {
            if (CommandSetup.hasConfigArea(SimpleRegionManager.getAreaName(sender))) {
                if (CommandSetup.isCorrectRegionWorld(sender, SimpleRegionManager.getAreaName(sender))) {
                    if (CommandSetup.enabled()) {
                        sender.sendMessage(CommandSetup.format(sender));
                    }
                    sender.teleport(CommandSetup.getAreaLocation(SimpleRegionManager.getAreaName(sender)));
                }
            }
        }
    }
}