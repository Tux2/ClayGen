package Tux2.ClayGen;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ClayBreak implements Listener {
	
	ClayGen plugin;
	public static final int CLAY = 82;
	
	public ClayBreak(ClayGen plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak (BlockBreakEvent event) {
		if(event.getBlock().getTypeId() == CLAY) {
			Block clayblock = event.getBlock();
			int claycount = 0;
			if(plugin.customdrops) {
				if(plugin.clayblocks.containsKey(plugin.compileBlockString(clayblock))) {
	    			ClayDelay theblock = plugin.clayblocks.get(plugin.compileBlockString(clayblock));
	    			claycount = plugin.getNumberOfDrops(theblock);
	    			plugin.clayblocks.remove(plugin.compileBlockString(clayblock));
	    			plugin.saveClayBlocks();
	    		}else {
	        		claycount = clayblock.getData();
	    		}
			}else {
				claycount = plugin.defaultclaydrop;
			}
			
			//If the clay count is zero, this must be a world generated block.
			//Set it to the default amount of clay.
			if(claycount == 0) {
				claycount = plugin.defaultclaydrop;
			}
			//drop the items
			for(int i = 0; i < claycount; i++) {
	    		clayblock.getWorld().dropItemNaturally(clayblock.getLocation(), new ItemStack(337, 1));
			}
			//turn the block to air, disabling further drops.
			clayblock.setTypeId(0);
		}
	}

}
