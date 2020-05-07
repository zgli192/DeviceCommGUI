package com.ford.vcc.cme.devicecomm.gui;

/**
 * TODO Insert class description here.  <br/><br/>
 *
 * <pre>
 *
 *       Change log:
 *       ------ --------------- ------------ --------------------------------------------
 *       Ver    By              Date         Description
 *       ------ --------------- ------------ --------------------------------------------
 *       1.0    Patrik Lycke    2006 okt 24  Created.
 * </pre>
 */
public class ClientIDProvider {
    private static int currentClientID = 0;

    public static int provideClientId(){
        return currentClientID++;

    }
}
