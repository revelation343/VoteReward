package com.genesis.wurm.server;

import com.wurmonline.server.creatures.Communicator;
import org.gotti.wurmunlimited.modloader.interfaces.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VoteReward implements WurmServerMod, Initable, PreInitable, PlayerMessageListener {
	private static final Logger logger = Logger.getLogger("VoteReward");

	public static void logException(String msg, Throwable e) {
		if (logger != null)
			logger.log(Level.SEVERE, msg, e);
	}

	public static void logWarning(String msg) {
		if (logger != null)
			logger.log(Level.WARNING, msg);
	}

	public static void logInfo(String msg) {
		if (logger != null)
			logger.log(Level.INFO, msg);
	}


	@Override
	public void init() {
	}

	@Override
	public void preInit() {
	}

	@Override
	public MessagePolicy onPlayerMessage(Communicator communicator, String message, String title) {
		if (message.startsWith("/vote")) {
			try {
				return VoteCommand.onPlayerMessage(communicator, message) ? MessagePolicy.DISCARD : MessagePolicy.PASS;
			}
			catch (Exception e) {
				communicator.sendAlertServerMessage("Error: " + e.toString());
			}
				}
		return MessagePolicy.PASS;
	}

	@Deprecated
	@Override
	public boolean onPlayerMessage(Communicator communicator, String msg) {
		return false;
	}
}
