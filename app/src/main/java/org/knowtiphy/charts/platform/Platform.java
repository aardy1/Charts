/*
 * Copyright Knowtiphy
 * All rights reserved.
 */

package org.knowtiphy.charts.platform;

import org.apache.commons.lang3.SystemUtils;

/** Get the platform the app is running on. */
public class Platform {
    public static IPlatform getPlatform() {
        // if (com.gluonhq.attach.util.Platform.isIOS())
        // return new IOS();
        // else if (com.gluonhq.attach.util.Platform.isAndroid())
        // // TODO
        // return null;
        // else
        if (SystemUtils.IS_OS_MAC_OSX) {
            return new MacOSX();
        }
        //    else if(SystemUtils.IS_OS_WINDOWS)
        //    // TODO
        //    {
        //      return null;
        //    }
        else if (SystemUtils.IS_OS_LINUX) {
            return new Linux();
        } else {
            return new IOS();
        }
    }
}