package com.revelation.wurm;

import com.wurmonline.server.creatures.Communicator;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class VoteReward implements WurmServerMod, Configurable, ServerStartedListener, Initable, PreInitable, PlayerMessageListener {
	private static final Logger logger = Logger.getLogger("VoteReward");
	public static String APIKEY;
	public static int rewardAmt;
	public static String rewardMsg;
	public static String claimedMsg;


	static {
		VoteReward.APIKEY = "";
		VoteReward.rewardAmt = 0;
		VoteReward.rewardMsg = "";
		VoteReward.claimedMsg = "";
	}

	public void configure(Properties properties){
		VoteReward.APIKEY = properties.getProperty("APIKEY");
		VoteReward.rewardAmt = Integer.valueOf(properties.getProperty("rewardamt"));
		VoteReward.rewardMsg = properties.getProperty("rewardmsg");
		VoteReward.claimedMsg = properties.getProperty("claimedmsg");
		VoteReward.logger.log(Level.INFO, "API KEY = " + VoteReward.APIKEY);
		VoteReward.logger.log(Level.INFO, "Reward Amount = " + VoteReward.rewardAmt);
		VoteReward.logger.log(Level.INFO, "Reward Message = " + VoteReward.rewardMsg);
		VoteReward.logger.log(Level.INFO, "Claimed Message= " + VoteReward.claimedMsg);
	}

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
	public void onServerStarted() {
		VoteReward.logger.info("Vote Rewards Enabled");
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
