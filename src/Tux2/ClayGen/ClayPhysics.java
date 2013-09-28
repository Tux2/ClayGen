package Tux2.ClayGen;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class ClayPhysics implements Listener {
	
	ClayGen plugin;
	public static final Material CLAY = Material.CLAY;
	
	public ClayPhysics(ClayGen plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPhysics (BlockPhysicsEvent event) {
		if(event.getBlock().getType() == CLAY) {
			if(plugin.waterenabled && plugin.lavaenabled) {
	    		if(!plugin.hasBlockNextTo(event.getBlock(), Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA,
	    				Material.LAVA)) {
	    			plugin.clayWaterRemoved(event.getBlock());
	    		}
			}else if(plugin.waterenabled) {
				if(!plugin.hasBlockNextTo(event.getBlock(), Material.STATIONARY_WATER, Material.WATER)) {
	    			plugin.clayWaterRemoved(event.getBlock());
	    		}
			}else if(plugin.lavaenabled) {
				if(!plugin.hasBlockNextTo(event.getBlock(), Material.STATIONARY_LAVA, Material.LAVA)) {
	    			plugin.clayWaterRemoved(event.getBlock());
	    		}
			}
		}
	}

}
