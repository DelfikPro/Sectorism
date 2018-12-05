package implario.loader;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Loader extends JavaPlugin {
    private List<String> plugins;
    private final File config = new File("plugins.txt");

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("loader");
        if (!config.exists()) {
            plugins = new ArrayList<>();
            try {
                config.createNewFile();
            }catch (IOException ex){}
            return;
        }
        {
            StringBuffer buffer = new StringBuffer((int) config.length());
            {
                try {
                    FileReader reader = new FileReader(config);
                    int buf;
                    for (buf = reader.read(); buf != -1; buffer.append((char)buf), buf = reader.read()) ;
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("Crush, can't load plugins");
                    Bukkit.shutdown();
                }
            }
            String split[] = buffer.toString().split("\n");
            plugins = new ArrayList<>(split.length);
            Collections.addAll(plugins, split);
        }
        for(String pl : plugins)
            try {
                Plugin plugin = Bukkit.getPluginManager().loadPlugin(new File("../_GLOBAL/" + pl + ".jar"));
                plugin.onLoad();
                Bukkit.getPluginManager().enablePlugin(plugin);
            }catch (Exception ex){
                ex.printStackTrace();
                System.out.println("can't load");
            }
    }

    @Override
    public void onDisable() {
        StringBuffer buffer = new StringBuffer();
        for(String pl : plugins)
            buffer.append(pl).append('\n');
        try{
            FileWriter writer = new FileWriter(config);
            writer.append(buffer);
            writer.flush();
            writer.close();
        }catch (IOException ex){
            ex.printStackTrace();
            System.out.println("can't write");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp())return false;
        if(args.length == 2){
            plugins.remove(args[0]);
            sender.sendMessage("remove");
        }else {
            plugins.add(args[0]);
            sender.sendMessage("add");
        }
        return true;
    }
}
