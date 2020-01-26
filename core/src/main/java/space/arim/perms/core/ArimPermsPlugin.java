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

import space.arim.api.config.SimpleConfigFramework;
import space.arim.api.uuid.UUIDResolver;

import space.arim.perms.api.ArimPerms;

public class ArimPermsPlugin implements ArimPerms {

	static final int MAX_RECURSION_DEPTH = 32;
	
	private final PluginEnvOptions options;
	private final Groups groups;
	private final Users users;
	private final Logs logs;
	private final Commands commands;
	private final Data data;
	private final Config config;
	private final Messages messages;
	
	public ArimPermsPlugin(PluginEnvOptions options) {
		this.options = options;
		groups = new Groups(this);
		users = new Users(this);
		logs = new Logs(this, options.logger);
		commands = new Commands(this);
		data = new Data(this);
		config = new Config(this);
		messages = new Messages(this);
	}
	
	@Override
	public File getFolder() {
		return options.folder;
	}
	
	@Override
	public boolean isOnlineMode() {
		return options.onlineMode;
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
	public UUIDResolver resolver() {
		return getRegistry().getRegistration(UUIDResolver.class);
	}

	@Override
	public Config config() {
		return config;
	}
	
	@Override
	public SimpleConfigFramework messages() {
		return messages;
	}
	
	static boolean matches(String permission, String checkPerm) {
		return checkPerm.equalsIgnoreCase(permission) || checkPerm.contains("*") && (checkPerm.equals("*") || checkPerm.endsWith(".*") && permission.startsWith(checkPerm.substring(0, checkPerm.lastIndexOf(".*"))));
	}
	
}
