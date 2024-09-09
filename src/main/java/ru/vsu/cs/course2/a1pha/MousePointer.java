package ru.vsu.cs.course2.a1pha;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

public class MousePointer extends PaintableObject {
    private double scalingFactor;
    private final int positionsBufferSize;
    LinkedList<Point> positionsBuffer;

    private int noseHeight;
    private int bodyWidth;
    private int bodyHeight;
    private int nozzleInsideWidth;
    private int nozzleHeight;
    private int nozzleOutsideWidth;
    private int nozzleEdgeRadius;
    private int illuminatorDiameter;

    public MousePointer(double scalingFactor, int positionsBufferSize) {
        this.scalingFactor = scalingFactor;
        this.positionsBufferSize = positionsBufferSize;
        positionsBuffer = new LinkedList<>();
        move(-100, -100);

        noseHeight = (int) (15 * scalingFactor);
        bodyWidth = (int) (16 * scalingFactor);
        bodyHeight = (int) (40 * scalingFactor);
        nozzleInsideWidth = (int) (2 * scalingFactor);
        nozzleHeight = (int) (30 * scalingFactor);
        nozzleOutsideWidth = (int) (5 * scalingFactor);
        nozzleEdgeRadius = (int) (2 * scalingFactor);
        illuminatorDiameter = (int) (bodyWidth * 0.8);
    }

    @Override
    public void move(int x, int y) {
        super.move(x, y);
        assert positionsBuffer != null;
        if (positionsBuffer.isEmpty() || positionsBuffer.peekLast().x() != x || positionsBuffer.peekLast().y() != y) {
            if (positionsBuffer.size() == positionsBufferSize) {
                positionsBuffer.poll();
            }
            positionsBuffer.addLast(new Point(x, y));
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        double angle = getAngle();

        g2d.rotate(angle, getX(), getY());
        g2d.setColor(Color.gray);

        g2d.fillPolygon(
                new int[]{getX(), getX() - noseHeight, getX() - noseHeight},
                new int[]{getY(), getY() + (bodyWidth >> 1), getY() - (bodyWidth >> 1)},
                3);
        g2d.setColor(Color.gray.darker());
        g2d.fillRect(getX() - noseHeight - bodyHeight, getY() - (bodyWidth >> 1), bodyHeight, bodyWidth);
        g2d.setColor(Color.white);

        Polygon sideNozzle = new Polygon(
                new int[]{
                getX() - noseHeight - (bodyHeight >> 1),
                getX() - noseHeight - (bodyHeight >> 1) - nozzleHeight,
                getX() - noseHeight - (bodyHeight >> 1) - nozzleHeight,
                getX() - noseHeight - (bodyHeight >> 1)
        },
                new int[]{
                        getY() - (bodyWidth >> 1),
                        getY() - (bodyWidth >> 1) - nozzleOutsideWidth,
                        getY() - (bodyWidth >> 1) + (nozzleInsideWidth),
                        getY() - (bodyWidth >> 1) + (nozzleInsideWidth)
                },
                4);
        g2d.fillPolygon(sideNozzle);

        g2d.fill(mirrorShapeVertical(sideNozzle, getY()));

        g2d.fillRoundRect(
                getX() - noseHeight - (bodyHeight >> 1) - nozzleHeight,
                getY() - (nozzleInsideWidth >> 1),
                nozzleHeight,
                nozzleInsideWidth,
                nozzleEdgeRadius, nozzleEdgeRadius);
        g2d.fillOval(
                getX() - noseHeight - (bodyHeight >> 2) - (illuminatorDiameter >> 1),
                getY() - (illuminatorDiameter >> 1),
                illuminatorDiameter,
                illuminatorDiameter);
        g2d.rotate(-angle, getX(), getY());
    }

    private static Shape mirrorShapeVertical(Shape shape, int y) {
        AffineTransform at = new AffineTransform();
        at.translate(0, y);
        at.scale(1, -1);
        at.translate(0, -y);
        return at.createTransformedShape(shape);
    }
    public double getAngle() {
        Point pastPosition = positionsBuffer.peek();
        assert pastPosition != null;
        int dx = getX() - pastPosition.x();
        int dy = getY() - pastPosition.y();
        double angle;
        if (dx == 0) {
            if (dy < 0) {
                angle = Math.toRadians(-90);
            } else {
                angle = Math.toRadians(90);
            }
        } else {
            angle = Math.atan((dy) * 1.0 / (dx));
            if (dx < 0)
            angle += Math.toRadians(180);
        }
        return angle;
    }

    public Projectile createProjectile() {
        return new Projectile(
                getX(),
                getY(),
                (int) (20 * scalingFactor),
                (int) (20 * scalingFactor),
                (int) (3 * scalingFactor),
                getAngle());
    }
}
