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

import java.io.File;
import java.util.logging.Logger;

import space.arim.perms.api.ArimPerms;

public class ArimPermsPlugin implements ArimPerms {

	static final int MAX_RECURSION_DEPTH = 32;
	
	private final File folder;
	private final Groups groups;
	private final Users users;
	private final Logs logs;
	private final Commands commands;
	private final Data data;
	private final Config config;
	
	public ArimPermsPlugin(File folder, Logger logger) {
		this.folder = folder;
		groups = new Groups(this);
		users = new Users(this);
		logs = new Logs(this, logger);
		commands = new Commands(this);
		data = new Data(this);
		config = new Config(this);
	}

	@Override
	public File getFolder() {
		return folder;
	}

	@Override
	public Groups groups() {
		return groups;
	}

	@Override
	public Users users() {
		return users;
	}

	@Override
	public Logs logs() {
		return logs;
	}
	
	@Override
	public Commands commands() {
		return commands;
	}
	
	@Override
	public Data data() {
		return data;
	}

	@Override
	public Config config() {
		return config;
	}
	
	static boolean matches(String permission, String checkPerm) {
		return checkPerm.equalsIgnoreCase(permission) || checkPerm.equals("*") || checkPerm.endsWith(".*") && permission.startsWith(checkPerm.substring(0, checkPerm.lastIndexOf(".*")));
	}
	
}
