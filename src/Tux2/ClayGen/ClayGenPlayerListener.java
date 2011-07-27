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

import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author Tux2
 */
public class ClayGenPlayerListener extends PlayerListener {
    private final ClayGen plugin;

    public ClayGenPlayerListener(ClayGen instance) {
        plugin = instance;
    }

    //Insert Player related code here
}

