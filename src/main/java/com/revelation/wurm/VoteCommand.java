package com.revelation.wurm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.economy.MonetaryConstants;
import java.util.logging.Logger;

final class VoteCommand {

	public static boolean onPlayerMessage(Communicator communicator, String message) throws Exception {
		Player performer = communicator.getPlayer();
		if (message.startsWith("/vote")) {
			communicator.sendNormalServerMessage("Attempting to claim vote");
			try {
				HttpsURLConnection connection = (HttpsURLConnection) new URL(
						"https://wurm-unlimited.com/api/?action=post&object=votes&element=claim&key=" + VoteReward.APIKEY + "&steamid="+performer.getSteamId()
				).openConnection();
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				connection.setSSLSocketFactory(createTrustAllSocketFactory());

				int responseCode = connection.getResponseCode();
				if (responseCode != 200) throw new IOException(performer.getName() + " " + performer.getSteamId() + " attempted to vote and could not connect."+ responseCode);
				VoteReward.logInfo("https://wurm-unlimited.com/api/?action=post&object=votes&element=claim&key=" + VoteReward.APIKEY + "&steamid="+performer.getSteamId());

				InputStream in = connection.getInputStream();

				ByteArrayOutputStream stringBuffer = new ByteArrayOutputStream();
				byte[] buffer = new byte[256];
				int len = 0;
				while (len != -1) {
					stringBuffer.write(buffer, 0, len);
					len = in.read(buffer);
				}

				String result = new String(stringBuffer.toByteArray(), StandardCharsets.UTF_8);
				if (result.equals("1")) {
					try {
						performer.addMoney(MonetaryConstants.COIN_IRON * VoteReward.rewardAmt);
						performer.getCommunicator().sendAlertServerMessage(VoteReward.rewardMsg);
						VoteReward.logInfo(performer.getName() + " " + performer.getSteamId() + " claimed their vote reward successfully.");
						VoteReward.logInfo("https://wurm-unlimited.com/api/?action=post&object=votes&element=claim&key=" + VoteReward.APIKEY + "&steamid="+performer.getSteamId());
					} catch (IOException ex) {
						performer.getCommunicator().sendAlertServerMessage("Failed to add reward. Please submit a support ticket.");
						ex.printStackTrace();
					}
				}
				else {
					performer.getCommunicator().sendAlertServerMessage(VoteReward.claimedMsg);
					VoteReward.logInfo(performer.getName() + " " + performer.getSteamId() + " attempted to claim vote but was unsuccessful.");
					VoteReward.logInfo("https://wurm-unlimited.com/api/?action=post&object=votes&element=claim&key=" + VoteReward.APIKEY + "&steamid="+performer.getSteamId());
				}
				in.close();
			} catch (IOException ex) {
				performer.getCommunicator().sendAlertServerMessage("Unable to check vote at this time. Please try again later.");
				ex.printStackTrace();
			}
		}
			return true;
		}

			private static SSLSocketFactory createTrustAllSocketFactory() throws IOException {
				try {
					SSLContext ctx = SSLContext.getInstance("TLS");
					TrustManager[] trustManagers = {
							new X509TrustManager() {
								@Override
								public void checkClientTrusted(X509Certificate[] chain, String authType) {
									/* empty */
								}

								@Override
								public void checkServerTrusted(X509Certificate[] chain, String authType) {
									/* empty */
								}

								@Override
								public X509Certificate[] getAcceptedIssuers() {
									return new X509Certificate[0];
								}
							}
					};
					ctx.init(null, trustManagers, null);
					return ctx.getSocketFactory();
				} catch (GeneralSecurityException ex) {
					throw new IOException(ex);
				}
			}
	public Logger logger = Logger.getLogger(this.getClass().getName());
		}
