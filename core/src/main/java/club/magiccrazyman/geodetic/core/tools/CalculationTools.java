package club.magiccrazyman.geodetic.core.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算辅助工具
 *
 * @author Magic Crazy Man
 */
public class CalculationTools {
    /**
     * 弧度转换角度工具
     *
     * @param radians 弧度
     * @return 角度，单位：十进制度
     */
    public static double radians2Degrees(double radians) {
        return unitConversion(radians, 0);
    }

    /**
     * 角度转换弧度工具
     *
     * @param degrees 角度值，单位：十进制度
     * @return 弧度
     */
    public static double degrees2Radians(double degrees) {
        return unitConversion(degrees, 1);
    }

    /**
     * 角度值字符串格式化工具
     *
     * @param degrees 角度值，单位：十进制度
     * @return 格式化字符串
     */
    public static String degreesFormatter(double degrees) {
        BigDecimal bg = new BigDecimal(degrees);
        degrees = bg.setScale(8, RoundingMode.HALF_UP).doubleValue();
        boolean negative = false;
        if (degrees < 0) {
            negative = true;
            degrees = Math.abs(degrees);
        }
        int d = (int) degrees;
        int m = (int) ((degrees - d) * 60);
        double s = (degrees - d) * 3600 - m * 60;

        return String.format("%s°%d'%.4f\"", negative ? "-" + d : d, m, s);
    }

    /**
     * 单位转换二合一方法
     *
     * @param input 输入参数
     * @param mode  选择模式；0：弧度转角度；1：角度转弧度
     * @return 输出结果
     */
    private static double unitConversion(double input, int mode) {
        if (mode == 1) {
            return input * Math.PI / 180;
        }
        return input * 180 / Math.PI;
    }
}
