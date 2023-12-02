/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import org.apache.commons.lang3.SystemUtils;

/**
 * @author graham
 */
public class Platform {

	//
	// public enum PlatformType {
	//
	// MAC, WINDOWS, LINUX, IOS, ANDROID
	//
	// }
	//
	// public static PlatformType PLATFORM;
	// static {
	// if (com.gluonhq.attach.util.Platform.isIOS())
	// PLATFORM = PlatformType.IOS;
	// else if (com.gluonhq.attach.util.Platform.isAndroid())
	// PLATFORM = PlatformType.ANDROID;
	// else if (SystemUtils.IS_OS_MAC_OSX)
	// PLATFORM = PlatformType.MAC;
	// else if (SystemUtils.IS_OS_WINDOWS)
	// PLATFORM = PlatformType.WINDOWS;
	// else if (SystemUtils.IS_OS_LINUX)
	// PLATFORM = PlatformType.LINUX;
	// }

	public static IPlatform getPlatform() {

		if (com.gluonhq.attach.util.Platform.isIOS())
			return new IOS();
		else if (com.gluonhq.attach.util.Platform.isAndroid())
			// TODO
			return null;
		else if (SystemUtils.IS_OS_MAC_OSX)
			return new MacOSX();
		else if (SystemUtils.IS_OS_WINDOWS)
			// TODO
			return null;
		else if (SystemUtils.IS_OS_LINUX)
			return new Linux();

		throw new IllegalArgumentException();
	}
	//
	// public static Path getAppRoot() {
	//
	// switch (PLATFORM) {
	// case IOS, ANDROID:
	// return
	// Services.get(StorageService.class).flatMap(StorageService::getPrivateStorage).get().toPath();
	// case MAC:
	// return Paths.get(System.getProperty("user.home"), "Documents", "KnowtiphyCharts");
	// default:
	// throw new IllegalArgumentException();
	// }
	// }
	//
	// public static Path getChartsPath() {
	//
	// var root = getAppRoot();
	// switch (PLATFORM) {
	// case IOS:
	// return root.resolve(Paths.get("Documents", "Region_08"));
	// case MAC:
	// return root.resolve(Paths.get("ENC", "08_REGION"));
	// case WINDOWS, LINUX:
	// // TODO
	// throw new IllegalArgumentException();
	// default:
	// throw new IllegalArgumentException();
	// }
	// }

	// public static Path getStylesPath() {
	//
	// var root = getAppRoot();
	// switch (PLATFORM) {
	// case IOS:
	// return root.resolve("Documents").resolve("styles");
	// case MAC:
	// return root.resolve("styles");
	// case WINDOWS, LINUX:
	// // TODO
	// throw new IllegalArgumentException();
	// default:
	// throw new IllegalArgumentException();
	// }
	// }

}