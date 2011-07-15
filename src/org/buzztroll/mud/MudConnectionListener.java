package org.buzztroll.mud;

import java.lang.*;

public interface
MudConnectionListener
{
    public void
    disconnected();

    public void
    messageReceived(
        String                          message);

    public void
    connectTic();
}
