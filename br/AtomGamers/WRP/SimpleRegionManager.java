package br.AtomGamers.WRP;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SimpleRegionManager {

    protected static boolean inAnyArea(Player sender) {
        RegionManager rm = RegionTeleport.getWorldGuard().getRegionManager(sender.getWorld());
        ApplicableRegionSet set = rm.getApplicableRegions(sender.getLocation());
        return set.size() > 0;
    }

    protected static String getAreaName(Player sender) {
        RegionManager rm = RegionTeleport.getWorldGuard().getRegionManager(sender.getWorld());
        ApplicableRegionSet set = rm.getApplicableRegions(sender.getLocation());
        String id = set.iterator().next().getId();
        return id;
    }

    protected static boolean existArea(String area, World w) {
        RegionManager rg = RegionTeleport.getWorldGuard().getRegionManager(w);
        if (rg.getRegion(area) != null) {
            return true;
        } else {
            return false;
        }
    }
}
