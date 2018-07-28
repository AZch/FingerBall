package constants;

/**
 * константы для параметров игры
 */
public class paramConst {
    public final static int SPEED_BALL = 5;

    public final static int startCountFig = 3;
    public final static int startCountMoveFig = 3;

    public final static int maxSpeedLifeFig = 15;

    public final static int START_SPEED_LIFE_FIG = 5;

    public final static int maxDrawWall = 2;

    public final static int MAX_FIGURE = 10;

    public final static int MAX_MOVE_FIGURE = 10;

    // получение параметров из настроек
    public static final String PREFS_FILE = "account";

    // Получение значения текущего рекорда
    public static final String RECORD = "RECORD";

    // получение значения радуги
    public static final String RAINBOW = "RAINBOW";

    // максимальное значение рекорда, до которого увеличивается скорость шара
    public static final int MAX_SPEED_RECORD = 45;

    // количество спасительных квадратов
    public static final int COUNT_SAVE_SQUARE = 5;

    // желательный фпс игры
    public final static int MAX_FPS = 45;
    // макс. кол-во кадров которые можно пропустить
    public final static int MAX_FRAME_SKIPS = 5;
    // период, которые занимает кадр (одноления и рисование)
    public final static int FRAME_PERIOD = 1000 / MAX_FPS;

    public final static int COUNT_FIVE_SECOND = 5 * MAX_FPS; // 5 секунд для обновления чего либо

    // фпс анимации объектов на карте
    public final static int FPS_ANIMATION_OBJECTS = 10;

    // квадрат
    public final static int  SQUARE_FIG = 0;

    // 4-х конечная звезда
    public final static int FOUR_POINTER_STAR = 1;

    // 5-ти конечная звезда
    public final static int FIVE_POINTER_STAR = 2;

    // огонь
    public final static int FIRE_FIG = 5;

    // ромб
    public final static int PENTAGON = 3;

    // треугольник
    public final static int TRIANGLE = 4;

    // прямоугольник
    public final static int RECTANGLE_FIG = 6;
}
