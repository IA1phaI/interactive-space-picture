package ru.vsu.cs.course2.a1pha;

public record Point(int x, int y) {
    public double distanceTo(int x, int y) {
        return Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
    }
}
