package com.junglee.task.entity;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface Game
{
    /**
     * @return Returns the unique id associated with this game object.
     */
    public Object getId();


    /**
     * Get the name of the game. Preferably should be a unique name.
     *
     * @return Returns the name of the game.
     */
    public String getGameName();



    /**
     * Unloads the current game, by closing all sessions. This will delegate
     * to {@link GameChannel#close()}
     *
     * @return In case of Netty Implementation it would return a collection of
     *         {@link ChannelFuture} object.
     */
    public Object unload();
}
