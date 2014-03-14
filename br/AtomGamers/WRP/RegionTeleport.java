package br.AtomGamers.WRP;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionTeleport extends JavaPlugin {

    @Override
    public void onEnable() {
        checkDepends();
        loadConfiguration();
        startRegistration();
        getCommand("regiontp").setExecutor(new CommandSetup(this));
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        reloadConfig();
        sender.sendMessage("§1[RegionTeleport] §fInicializando Listeners..");
        sender.sendMessage("§1[RegionTeleport] §fPreparando Configurações..");
        sender.sendMessage("§1[RegionTeleport] §fPreparando sql('area_statement')..");
        sender.sendMessage("§1[RegionTeleport] §fPlugin inicializado. (Autor=AtomGamers)");
    }

    @Override
    public void onDisable() {
        saveConfig();
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("§1[RegionTeleport] §fFinalizando Listeners..");
        sender.sendMessage("§1[RegionTeleport] §fSalvando as configurações..");
        sender.sendMessage("§1[RegionTeleport] §fSalvando/Finalizando sql('area_statement')..");
        sender.sendMessage("§1[RegionTeleport] §fPlugin finalizado. (Autor=AtomGamers)");
    }

    protected static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    protected void loadConfiguration() {
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            try {
                saveResource("config_template.yml", false);
                File config2 = new File(getDataFolder(), "config_template.yml");
                config2.renameTo(new File(getDataFolder(), "config.yml"));
            } catch (Exception e) {
                /* create new data folder. */
            }
        }
        reloadConfig();
    }

    protected void startRegistration() {
        getServer().getPluginManager().registerEvents(new PluginService(), this);
    }

    protected void checkDepends() {
        Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null) {
            ConsoleCommandSender sender = Bukkit.getConsoleSender();
            sender.sendMessage("§4[RegionTeleport] §f'WorldGuard' nao encontrado.");
            sender.sendMessage("§4[RegionTeleport] §fFinalizando sql('area_statement')..");
            sender.sendMessage("§4[RegionTeleport] §fPlugin DESATIVADO! (Autor=AtomGamers)");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}
