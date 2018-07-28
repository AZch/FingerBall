package maker;

import java.util.Random;

import wordcreators.fingerball.GameView;

/**
 * Created by anton on 24.12.2017.
 */

public class Geometric_Calculate {

    public static float[] findPlaceFig(float sizeFig, GameView gameView) {
        Random random = new Random();
        float XY[] = new float[2];
        XY[0] = random.nextInt(gameView.getWidthMap());
        XY[1] = random.nextInt(gameView.getHeightMap());
        return XY;
    }


    public static boolean interesectionLinesBool(float ax1, float ay1, float ax2, float ay2, float bx1, float by1, float bx2, float by2) {
        float v1 = (bx2 - bx1) * (ay1 - by1) - (by2 - by1) * (ax1 - bx1),
                v2 = (bx2 - bx1) * (ay2 - by1) - (by2 - by1) * (ax2 - bx1),
                v3 = (ax2 - ax1) * (by1 - ay1) - (ay2 - ay1) * (bx1 - ax1),
                v4 = (ax2 - ax1) * (by2 - ay1) - (ay2 - ay1) * (bx2 - ax1);
        return  (v1 * v2 < 0) && (v3 * v4 < 0);
    }

    /**
     * Определение пересечения линий и получение точки пересечения
     * @param x11 Координата X начала первой линии
     * @param y11 Координата Y начала первой линии
     * @param x12 Координата X конца перовой линии
     * @param y12 Координата Y конца перовй линии
     * @param x21 Координата X начала второй линии
     * @param y21 Координата Y начала второй линии
     * @param x22 Координата X конца второй линии
     * @param y22 Координата Y конца второй линии
     * @return точка пересечения линий, возврщяет -1 -1, если линии не пересекаются (так как карта (по ширине и высоте) не содержит отрицательных координат)
     */
    public static float[] intersectionLines(float x11, float y11, float x12, float y12, float x21, float y21, float x22, float y22) {
        float[] xy = new float[2];
        xy[0] = -1;
        xy[1] = -1;

        float a1 = y11 - y12, a2 = y21 - y22;
        float b1 = x12 - x11, b2 = x22 - x21;

        float denomin = a1 * b2 - a2 * b1;

        if (denomin == 0)
            return xy;

        float c1 = x11 * y12 - x12 * y11, c2 = x21 * y22 - x22 * y21;
        xy[0] = (-1) * ((c1 * b2 - c2 * b1) / (a1 * b2 - a2 * b1));
        xy[1] = (-1) * ((a1 * c2 - a2 * c1) / (a1 * b2 - a2 * b1));

        return xy;
    }

    // через косинусы
    public static double angleLines(float a1, float b1, float a2, float b2) {
        double denom = (Math.sqrt(a1*a1 + b1*b1) * Math.sqrt(a2*a2 + b2*b2));
        if (denom == 0)
            return 0;

        double x = Math.abs(a1 * a2 + b1 * b2) / denom;
        if (x > 1 || x < -1)
            return 0;

        double angle = Math.acos(x);
        if (angle > Math.PI)
            return Math.PI * 2 - angle;
        else
            return angle;
    }

    /**
     * расстояние между точками
     * @param x1 координата X первой точки
     * @param y1 координата Y первой точки
     * @param x2 координата X второй точки
     * @param y2 координата Y второй точки
     * @return расстояние между точками
     */
    public static float destination(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt( (double) ((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2)));
    }

    // вычисление порядка точек линии
    public static float[] getTrueLineFire(float x, float y, float endX, float endY, float centerX, float centerY) {
        float returnLine[] = new float[4];

        float r = destination(x, y, centerX, centerY);
        float angle = (float) Math.asin(destination(x, y, endX, endY) / (2 * r)) * 2;
        float TrueLineSize = r * angle;
        float reversLineSize = r * (360 - angle);
        if (TrueLineSize > reversLineSize) {
            returnLine[0] = endX;
            returnLine[1] = endY;
            returnLine[2] = x;
            returnLine[3] = y;
            return returnLine;
        } else {
            returnLine[0] = x;
            returnLine[1] = y;
            returnLine[2] = endX;
            returnLine[3] = endY;
            return returnLine;
        }
    }

    // угол поворота точки
    public static float getAnglePoint(float x, float y, float centerX, float centerY) {
        return (float) Math.atan2(centerX - y, x - centerX);
    }

    /**
     * Расстояние от точки до прямой
     * @param x1 координата X начала прямой
     * @param y1 координата Y начала прямой
     * @param x2 координата X конца прямой
     * @param y2 координата Y конца прямой
     * @param x координата X точки
     * @param y координата Y точки
     * @return расстояние от точки до прямой
     */
    public static double destLinePoint(float x1, float y1, float x2, float y2, float x, float y) {

        float a = y1 - y2, b = x2 - x1, c = x1 * y2 - x2 * y1; // коэфициенты прямой

        return Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b);
    }
}
