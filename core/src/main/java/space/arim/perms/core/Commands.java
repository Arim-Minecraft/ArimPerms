/* 
 * ArimPerms-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimPerms-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimPerms-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimPerms-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.perms.core;

import space.arim.perms.api.ArimPerms;
import space.arim.perms.api.CmdSender;
import space.arim.perms.api.CommandManager;

public class Commands implements CommandManager {

	private final ArimPerms core;
	
	public Commands(ArimPerms core) {
		this.core = core;
	}
	
	@Override
	public void runCommand(CmdSender sender, String[] args) {
		
	}

}
