/*
 * Copyright (C) 2011  Joshua Reetz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package Tux2.ClayGen;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * ClayGen block listener
 * @author Tux2
 */
public class ClayGenBlockListener extends BlockListener {
    public static final int CLAY = 82;
	private final ClayGen plugin;
    public static final int GRAVEL = 13;
    boolean debug = false;

    public ClayGenBlockListener(final ClayGen plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockFromTo(BlockFromToEvent event) {
    	//Block thewaterblock = event.getBlock();
    	//plugin.convertBlocks(thewaterblock);
    	Block thewaterblock2 = event.getToBlock();
    	plugin.convertBlocks(thewaterblock2);
    }
    
    public void onBlockPlace (BlockPlaceEvent event) {
    	Block thegravelblock = event.getBlock();
    	if(thegravelblock.getTypeId() == GRAVEL) {
    		plugin.gravelPlaced(thegravelblock);
    	}
    }
    
    public void onBlockPhysics (BlockPhysicsEvent event) {
    	if(event.getBlock().getTypeId() == CLAY) {
    		if(plugin.waterenabled && plugin.lavaenabled) {
        		if(!plugin.hasBlockNextTo(event.getBlock(), 8, 9, 10, 11)) {
        			plugin.clayWaterRemoved(event.getBlock());
        		}
    		}else if(plugin.waterenabled) {
    			if(!plugin.hasBlockNextTo(event.getBlock(), 8, 9)) {
        			plugin.clayWaterRemoved(event.getBlock());
        		}
    		}else if(plugin.lavaenabled) {
    			if(!plugin.hasBlockNextTo(event.getBlock(), 10, 11)) {
        			plugin.clayWaterRemoved(event.getBlock());
        		}
    		}
    	}
    }
    
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

    //put all Block related code here
}
