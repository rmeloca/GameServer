/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.game;

import java.io.Serializable;

/**
 *
 * @author romulo
 */
public final class Coordinate implements Serializable {

    private int x;
    private int y;

    public Coordinate(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return x;
    }

    private void setX(int x) {
        if (x >= 0) {
            this.x = x;
        } else {
            this.x = 0;
        }
    }

    public int getY() {
        return y;
    }

    private void setY(int y) {
        if (y >= 0) {
            this.y = y;
        } else {
            this.y = 0;
        }
    }

    public Coordinate north() {
        return new Coordinate(x + 1, y);
    }

    public Coordinate northwest() {
        return new Coordinate(x + 1, y - 1);
    }

    public Coordinate northeast() {
        return new Coordinate(x + 1, y + 1);
    }

    public Coordinate south() {
        return new Coordinate(x - 1, y);
    }

    public Coordinate southwest() {
        return new Coordinate(x - 1, y - 1);
    }

    public Coordinate southeast() {
        return new Coordinate(x - 1, y + 1);
    }

    public Coordinate east() {
        return new Coordinate(x, y + 1);
    }

    public Coordinate west() {
        return new Coordinate(x, y - 1);
    }

    /**
     * Retorna igualdade entre coordenadas
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return ((Coordinate) obj).getX() == x && ((Coordinate) obj).getY() == y;
    }

}
