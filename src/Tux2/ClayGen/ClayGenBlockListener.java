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
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
    	Block thewaterblock = event.getBlock();
    	plugin.convertBlocks(thewaterblock);
    }
    
    public void onBlockPlace (BlockPlaceEvent event) {
    	Block thegravelblock = event.getBlock();
    	if(thegravelblock.getTypeId() == GRAVEL) {
    		plugin.gravelPlaced(thegravelblock);
    	}
    }

    //put all Block related code here
}
