package club.magiccrazyman.geodetic.core.logger;

import club.magiccrazyman.geodetic.core.coordinatesystem.GeodeticCoordinateSystem;
import club.magiccrazyman.geodetic.core.coordinatesystem.projection.GaussKrugerProjectionCoordinateSystem;

import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 自定义简易logger
 * <br>
 * <strong>此类不提供Javadoc</strong>
 *
 * @author Magic Crazy Man
 * @version 0.1.0
 */
public class SimpleLogger extends Logger {

    public static final String geodeticCoordinateSystemIntroduction = "大地坐标系：%s" + System.lineSeparator() +
            "   椭圆长半轴：%.4f 米" + System.lineSeparator() +
            "   椭圆短半轴：%.4f 米" + System.lineSeparator() +
            "   椭圆扁率：1/%.8f" + System.lineSeparator() +
            "   椭圆第一偏心率：%.15f" + System.lineSeparator() +
            "   椭圆第二偏心率：%.15f" + System.lineSeparator() +
            "   椭圆极点处的子午线曲率半径：%.4f 米" + System.lineSeparator();

    public static final String toGeoInputParametersIntroduction = "输入参数" + System.lineSeparator() +
            "   X轴坐标值：%.10f 米" + System.lineSeparator() +
            "   Y轴坐标值：%.10f 米" + System.lineSeparator() +
            "   Z轴坐标值：%.10f 米" + System.lineSeparator() +
            "   迭代经度：%s" + System.lineSeparator();

    public static final String toSpaInputParametersIntroduction = "输入参数" + System.lineSeparator() +
            "   大地经度L：%.10f 弧度" + System.lineSeparator() +
            "   大地纬度B：%.10f 弧度" + System.lineSeparator() +
            "   大地高H：%.10f  米" + System.lineSeparator();

    public static final String outputResultIntroduction = "计算结果" + System.lineSeparator();

    public static final String geodeticLongitudeResult = "   大地经度L：%.10f 弧度" + System.lineSeparator();

    public static final String geodeticLatitudeResult = "   大地纬度B：%.10f 弧度" + System.lineSeparator() +
            "       总迭代次数 %d" + System.lineSeparator();

    public static final String geodeticHeightResult = "   大地高 H：%.10f 米" + System.lineSeparator();

    public static final String geodeticIterationStart = "   迭代法，从空间直角坐标系推算大地纬度B" + System.lineSeparator() +
            "       迭代初始值：%.15f 弧度" + System.lineSeparator();

    public static final String geodeticLatitudeIterationCount = "       迭代 %d 次 B 值：%.15f 弧度" + System.lineSeparator();

    public static final String spatialXResult = "  X轴坐标值：%.10f 米" + System.lineSeparator();

    public static final String spatialYResult = "  Y轴坐标值：%.10f 米" + System.lineSeparator();

    public static final String spatialZResult = "  Z轴坐标值：%.10f 米" + System.lineSeparator();

    public static final String gaussProjectionCoordinateSystemIntroduction = "高斯投影坐标系：%s" + System.lineSeparator() +
            "   分度带：%d °" + System.lineSeparator() +
            "   中央子午线：%.1f 米" + System.lineSeparator() +
            "   东伪偏移值：%.1f 米" + System.lineSeparator() +
            "   北伪偏移值：%.1f 米" + System.lineSeparator() +
            "   缩放因子：%.1f" + System.lineSeparator() +
            "   纬度原点：%.1f" + System.lineSeparator() +
            "   线性单位：%s" + System.lineSeparator();

    public static final String gaussProjectionForwardInputParameters = "输入参数" + System.lineSeparator() +
            "   大地经度L：%.10f 弧度" + System.lineSeparator() +
            "   大地纬度B：%.10f 弧度" + System.lineSeparator();

    public static final String gaussProjectionBackwardInputParameters = "输入参数" + System.lineSeparator() +
            "   X轴坐标值：%.10f 米" + System.lineSeparator() +
            "   Y轴坐标值：%.10f 米" + System.lineSeparator() +
            "   是否有偏移量：%s" + System.lineSeparator();

    public static final String gaussProjectionForwardResult = "   经差：%.4f 弧度" + System.lineSeparator() +
            "   中央子午线弧长：%.4f 米" + System.lineSeparator() +
            "   卯酉圈曲率半径：%.4f 弧度" + System.lineSeparator() +
            "   X轴真坐标值：%.4f 弧度" + System.lineSeparator() +
            "   Y轴真坐标值：%.4f 弧度" + System.lineSeparator() +
            "   X轴偏移坐标值：%.4f 弧度" + System.lineSeparator() +
            "   Y轴偏移坐标值：%.4f 弧度" + System.lineSeparator();

    public static final String gaussProjectionIterationStart = "   迭代法，子午线弧长计算大地纬度Bf" + System.lineSeparator() +
            "       迭代初始值：%.15f 弧度" + System.lineSeparator();

    public static final String gaussProjectionLatitudeIterationCount = "       迭代 %d 次 Bf 值：%.15f 弧度" + System.lineSeparator();

    public static final String gaussProjectionBackwardResult = "   子午圈曲率半径：%.4f 米" + System.lineSeparator() +
            "   卯酉圈曲率半径：%.4f 米" + System.lineSeparator() +
            "   大地纬度B：%.4f 弧度" + System.lineSeparator() +
            "       迭代次数：%d" + System.lineSeparator() +
            "   大地经度L：%.4f 弧度" + System.lineSeparator() +
            "       经差l：%.4f 弧度" + System.lineSeparator();

    public static SimpleLogger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        return new SimpleLogger(logger.getName(), logger.getResourceBundleName());
    }


    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * @throws MissingResourceException if the resourceBundleName is non-null and
     *                                  no corresponding resource can be found.
     */
    protected SimpleLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    public void simpleLog(String msg) {
        this.log(Level.INFO, msg);
    }

    public void simpleFormatLog(String formattedString, Object... param) {
        this.log(Level.INFO, String.format(formattedString, param));
    }

    public void logGeodeticCoordinateSystemIntroduction(GeodeticCoordinateSystem system) {
        this.log(Level.INFO, String.format(geodeticCoordinateSystemIntroduction,
                system.getName(),
                system.getA(),
                system.getB(),
                1 / system.getAlpha(),
                system.getE2(),
                system.getDE2(),
                system.getC()));
    }

    public void logToGeoInputParametersIntroduction(double X, double Y, double Z, double precision) {
        this.log(Level.INFO, String.format(toGeoInputParametersIntroduction, X, Y, Z, String.valueOf(precision)));
    }

    public void logToSpaInputParametersIntroduction(double L, double B, double H) {
        this.log(Level.INFO, String.format(toSpaInputParametersIntroduction, L, B, H));
    }

    public void logOutputResultIntroduction() {
        this.log(Level.INFO, String.format(outputResultIntroduction));
    }

    public void logGeodeticLongitudeResult(double L) {
        this.log(Level.INFO, String.format(geodeticLongitudeResult, L));
    }

    public void logGeodeticLatitudeResult(double B, int count) {
        this.log(Level.INFO, String.format(geodeticLatitudeResult, B, count));
    }

    public void logGeodeticHeightResult(double H) {
        this.log(Level.INFO, String.format(geodeticHeightResult, H));
    }

    public void logGeodeticIterationStart(double B) {
        this.log(Level.INFO, String.format(geodeticIterationStart, B));
    }

    public void logGeodeticLatitudeIterationCount(double B, int count) {
        this.log(Level.INFO, String.format(geodeticLatitudeIterationCount, count, B));
    }

    public void logSpatialXResult(double X) {
        this.log(Level.INFO, String.format(spatialXResult, X));
    }

    public void logSpatialYResult(double Y) {
        this.log(Level.INFO, String.format(spatialYResult, Y));
    }

    public void logSpatialZResult(double Z) {
        this.log(Level.INFO, String.format(spatialZResult, Z));
    }

    public void logGaussProjectionCoordinateSystem(GaussKrugerProjectionCoordinateSystem system) {
        this.log(Level.INFO, String.format(gaussProjectionCoordinateSystemIntroduction,
                system.getName(),
                system.getDegree(),
                system.getCentralMeridian(),
                system.getFalseEasting(),
                system.getFalseNorthing(),
                system.getScaleFactor(),
                system.getLatitudeOfOrigin(),
                system.getLinerUnit()));
        this.logGeodeticCoordinateSystemIntroduction(system.getGeodeticCoordinateSystem());
    }

    public void logGaussProjectionForwardInputParameters(double L, double B) {
        this.log(Level.INFO, String.format(gaussProjectionForwardInputParameters, L, B));
    }

    public void logGaussProjectionBackwardInputParameters(double x, double y, boolean hasFalse) {
        this.log(Level.INFO, String.format(gaussProjectionBackwardInputParameters, x, y, hasFalse ? "有" : "无"));
    }

    public void logGaussProjectionForwardResult(double l, double X, double N, double x, double y, double xz, double yz) {
        this.log(Level.INFO, String.format(gaussProjectionForwardResult, l, X, N, x, y, xz, yz));
    }

    public void logGaussProjectionIterationStart(double B1) {
        this.log(Level.INFO, String.format(gaussProjectionIterationStart, B1));
    }

    public void logGaussProjectionIterationCount(double B2, int count) {
        this.log(Level.INFO, String.format(gaussProjectionLatitudeIterationCount, count, B2));
    }

    public void logGaussProjectionBackwardResult(double Mf, double Nf, double B, int count, double L, double l) {
        this.log(Level.INFO, String.format(gaussProjectionBackwardResult, Mf, Nf, B, count, L, l));
    }
}
