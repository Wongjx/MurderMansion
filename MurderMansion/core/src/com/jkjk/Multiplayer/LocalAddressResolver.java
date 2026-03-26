package com.jkjk.Multiplayer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public final class LocalAddressResolver {
	private LocalAddressResolver() {
	}

	public static String getLocalIpv4() {
		String fallback = null;
		try {
			for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
					.hasMoreElements();) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()
						|| networkInterface.isPointToPoint()) {
					continue;
				}
				for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses
						.hasMoreElements();) {
					InetAddress address = addresses.nextElement();
					if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
						if (address.isSiteLocalAddress()) {
							return address.getHostAddress();
						}
						if (fallback == null) {
							fallback = address.getHostAddress();
						}
					}
				}
			}
		} catch (SocketException e) {
			return null;
		}
		return fallback;
	}
}
