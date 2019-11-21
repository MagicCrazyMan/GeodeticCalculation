package club.magiccrazyman.geodetic.core.tools;

/**
 * 计算辅助工具
 *
 * @author Magic Crazy Man
 * @version 0.1.0
 */
public class CalculationTools {
    /**
     * 弧度转换角度工具
     *
     * @param arc 弧度
     * @return 角度，单位：十进制度
     */
    public static double rad2Degree(double arc) {
        return unitConversion(arc, 0);
    }

    /**
     * 角度转换弧度工具
     *
     * @param degree 角度值，单位：十进制度
     * @return 弧度
     */
    public static double degree2Rad(double degree) {
        return unitConversion(degree, 1);
    }

    /**
     * 角度值字符串格式化工具
     *
     * @param degree 角度值，单位：十进制度
     * @return 格式化字符串
     */
    public static String degreeFormatter(double degree) {
        boolean negative = false;
        if(degree < 0){
            negative = true;
            degree = Math.abs(degree);
        }
        int d = (int) degree;
        int m = (int) ((degree - d) * 60);
        double s = (degree - d) * 3600 - m * 60;

        String result = String.format("%d°%d\'%.4f\"", d, m, s);
        if(negative){
            result = "-" + result;
        }
        return result;
    }

    /**
     * 单位转换二合一方法
     *
     * @param input 输入参数
     * @param mode 选择模式；0：弧度转角度；1：角度转弧度
     * @return 输出结果
     */
    private static double unitConversion(double input, int mode) {
        if (mode == 1) {
            return input * Math.PI / 180;
        }
        return input * 180 / Math.PI;
    }
}
