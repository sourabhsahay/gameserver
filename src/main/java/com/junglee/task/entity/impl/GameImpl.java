package com.junglee.task.entity.impl;

import com.junglee.task.entity.Game;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public class GameImpl implements Game {

    private final Object id;
    private final String gameName;

    public GameImpl(Object id, String gameName) {
        super();
        this.id = id;
        this.gameName = gameName;
    }

    /**
     * Meant as a database access key.
     *
     * @return The unique identifier of this Game.
     */
    public Object getId() {
        return id;
    }


    public String getGameName() {
        return gameName;
    }


    public synchronized Object unload() {
        return null;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(gameName).append(id).build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameImpl other = (GameImpl) obj;
        return new EqualsBuilder().append(this.gameName,other.gameName).append(this.id,other.id).build();
    }

}
