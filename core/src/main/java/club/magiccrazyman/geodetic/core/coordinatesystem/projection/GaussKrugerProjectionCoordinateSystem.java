package club.magiccrazyman.geodetic.core.coordinatesystem.projection;

import club.magiccrazyman.geodetic.core.coordinatesystem.GeodeticCoordinateSystem;
import club.magiccrazyman.geodetic.core.tools.CalculationTools;

import java.util.ArrayList;

/**
 * 高斯克吕格投影坐标系
 * <br>
 * 大地经度：正数位于东半球，负数位于西半球
 * 大地纬度：正数位于北半球，负数位于南半球
 * <br>
 * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第四章
 *
 * @author Magic Crazy Man
 */
public class GaussKrugerProjectionCoordinateSystem {

    /**
     * 东伪偏移值，单位：米
     */
    private final double falseEasting;

    /**
     * 北伪偏移值，单位：米
     */
    private final double falseNorthing;

    /**
     * 中央子午线，单位：十进制度
     */
    private final double centralMeridian;

    /**
     * 缩放因子
     */
    private final double scaleFactor;

    /**
     * 纬度原点
     */
    private final double latitudeOfOrigin;

    /**
     * 线性单位
     */
    private final String linerUnit;

    /**
     * 分度带
     */
    private final int degree;

    /**
     * Y轴坐标值是否会添加带号
     */
    private final boolean hasZoneNum;

    /**
     * 高斯克吕格投影坐标系名称
     */
    private final String name;

    /**
     * 大地坐标系实例
     *
     * @see GeodeticCoordinateSystem
     */
    private final GeodeticCoordinateSystem geodeticCoordinateSystem;

    /**
     * 高斯克吕格投影坐标系构造器
     *
     * @param geodeticCoordinateSystem 大地坐标系
     * @param degree                   分度带
     * @param hasZoneNum               Y轴坐标值是否会添加带号
     * @param falseEasting             东伪偏移值，单位：米
     * @param falseNorthing            北伪偏移值，单位：米
     * @param centralMeridian          中央子午线，单位：十进制度
     * @param scaleFactor              缩放因子
     * @param latitudeOfOrigin         纬度起点
     * @param linerUnit                线性单位
     * @param name                     坐标系名称
     */
    public GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem geodeticCoordinateSystem, int degree, boolean hasZoneNum, double falseEasting, double falseNorthing, double centralMeridian, double scaleFactor, double latitudeOfOrigin, String linerUnit, String name) {
        this.geodeticCoordinateSystem = geodeticCoordinateSystem;
        this.degree = degree;
        this.hasZoneNum = hasZoneNum;
        this.falseEasting = falseEasting;
        this.falseNorthing = falseNorthing;
        this.centralMeridian = centralMeridian;
        this.scaleFactor = scaleFactor;
        this.latitudeOfOrigin = latitudeOfOrigin;
        this.linerUnit = linerUnit;
        this.name = name;
    }

    /**
     * 高斯克吕格投影正算，将大地坐标系坐标点投影至高斯克吕格投影坐标系上
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第169页
     *
     * @param L 大地经度，单位：弧度
     * @param B 大地纬度，单位：弧度
     * @return 以xz(X轴偏移坐标值)，yz(Y轴偏移坐标值)，x(X轴真坐标值)，y(Y轴真坐标值)，l(经差)，S(中央子午线弧长)，N(卯酉圈曲率半径)顺序排列的ArrayList
     */
    public ArrayList<Double> forwardCalculation(double L, double B) {
        ArrayList<Double> list = new ArrayList<>();

        double X, l, N, t, eit2, x, y, xz, yz;
        l = L - CalculationTools.degrees2Radians(centralMeridian); //计算坐标点与中央子午线的经差
        X = geodeticCoordinateSystem.calculateMeridianArc(B); //根据大地纬度计算子午线弧长
        t = Math.tan(B); //t = atan(B)
        eit2 = geodeticCoordinateSystem.getSecondEccentricity() * Math.pow(Math.cos(B), 2); //η^2 = e'^2 * cos(B)^2
        N = geodeticCoordinateSystem.calculatePrimeVerticalCurvatureRadius(eit2); //计算卯酉圈曲率半径

        //x, y 为无偏移量真值
        x = X + N * Math.sin(B) * Math.cos(B) * Math.pow(l, 2) / 2 +
                N * Math.sin(B) * Math.pow(Math.cos(B), 3) * (5 - Math.pow(t, 2) + 9 * eit2 + 4 * Math.pow(eit2, 2)) * Math.pow(l, 4) / 24 +
                N * Math.sin(B) * Math.pow(Math.cos(B), 5) * (61 - 58 * Math.pow(t, 2) + Math.pow(t, 4)) * Math.pow(l, 6) / 720;
        y = N * Math.cos(B) * l +
                N * Math.pow(Math.cos(B), 3) * (1 - Math.pow(t, 2) + eit2) * Math.pow(l, 3) / 6 +
                N * Math.pow(Math.cos(B), 5) * (5 - 18 * Math.pow(t, 2) + Math.pow(t, 4) + 14 * eit2 - 58 * eit2 * Math.pow(t, 2)) * Math.pow(l, 5) / 120;

        //计算缩放因子
        x *= scaleFactor;
        y *= scaleFactor;

        //添加东伪偏移和北伪偏移，如果需要添加带号，此处会把带号一并带上
        yz = y + falseEasting;
        xz = x + falseNorthing;

        list.add(xz);
        list.add(yz);
        list.add(x);
        list.add(y);
        list.add(l);
        list.add(X);
        list.add(N);
        return list;
    }

    /**
     * 高斯克吕格投影反算，将高斯克吕格投影坐标系反算至大地坐标系上
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第171页
     *
     * @param x         X轴坐标值，单位：米
     * @param y         Y轴坐标值，单位：米
     * @param precision 大地纬度迭代精度，单位：弧度
     * @param hasFalse  坐标值是否含有伪偏移值，此偏移值包含带号
     * @return 以L(大地经度)，B(大地纬度)，l(经差)，Mf(子午圈曲率半径)，Nf(卯酉圈曲率半径)，count(迭代总次数)，...(Bf每次迭代值)顺序排列ArrayList
     */
    public ArrayList<Double> backwardCalculation(double x, double y, double precision, boolean hasFalse) {
        ArrayList<Double> list = new ArrayList<>();

        ArrayList<Double> Bi;
        double Bf, Mf, Nf, eit2f, tf, l, L, B;
        //修正伪偏移值
        if (hasFalse) {
            y = y - falseEasting;
            x = x - falseNorthing;
        }

        //复位缩放因子
        y /= scaleFactor;
        x /= scaleFactor;

        Bi = geodeticCoordinateSystem.calculateGeodeticLatitudeFromMeridianArc(x, precision); //迭代法，根据子午线弧长推算大地纬度B,此处X轴真坐标值即为子午线弧长
        Bf = Bi.get(Bi.size() - 1);
        eit2f = geodeticCoordinateSystem.getSecondEccentricity() * Math.pow(Math.cos(Bf), 2); //η^2 = e'^2 * cos(B)^2
        tf = Math.tan(Bf); //t = atan(B)
        Mf = geodeticCoordinateSystem.calculateMeridianCurvatureRadius(eit2f); //计算子午圈曲率半径
        Nf = geodeticCoordinateSystem.calculatePrimeVerticalCurvatureRadius(eit2f); //计算卯酉圈曲率半径

        B = Bf -
                tf * Math.pow(y, 2) / (2 * Mf * Nf) +
                tf * (5 + 3 * Math.pow(tf, 2) + eit2f - 9 * eit2f * Math.pow(tf, 2)) * Math.pow(y, 4) / (24 * Mf * Math.pow(Nf, 3)) -
                tf * (61 + 90 * Math.pow(tf, 2) + 45 * Math.pow(tf, 4)) * Math.pow(y, 6) / (720 * Mf * Math.pow(Nf, 5));
        //l为与中央子午线的经差
        l = y / (Nf * Math.cos(Bf)) -
                (1 + 2 * Math.pow(tf, 2) + eit2f) * Math.pow(y, 3) / (6 * Math.pow(Nf, 3) * Math.cos(Bf)) +
                (5 + 28 * Math.pow(tf, 2) + 24 * Math.pow(tf, 4) + 6 * eit2f + 8 * eit2f * Math.pow(tf, 2)) * Math.pow(y, 5) / (120 * Math.pow(Nf, 5) * Math.cos(Bf));
        L = l + CalculationTools.degrees2Radians(centralMeridian);

        list.add(L);
        list.add(B);
        list.add(l);
        list.add(Mf);
        list.add(Nf);
        list.add((double) Bi.size() - 1); //Bi第一个值为初值，不算在迭代次数中
        list.addAll(Bi);
        return list;
    }

    /**
     * 间接法坐标系投影转换，领带转换操作
     * <br>
     * 源高斯投影坐标 -> 大地坐标 -> 目标高斯投影坐标
     *
     * @param outputSystem 目标坐标系
     * @param x            x轴坐标值
     * @param y            y轴坐标值
     * @param precision    迭代精度
     * @param hasFalse     坐标值是否含有伪偏移值，此偏移值包含带号
     * @return 以xz(X轴偏移坐标值)，yz(Y轴偏移坐标值)，x(X轴真坐标值)，y(Y轴真坐标值)，L(大地经度)，B(大地纬度)，l(经差)，count(迭代总次数)，...(Bf每次迭代值)顺序排列的ArrayList
     * @throws UnsupportedOperationException 当两个投影坐标系的大地坐标系不一致时抛出此异常
     * @see GaussKrugerProjectionCoordinateSystem#forwardCalculation(double, double)
     * @see GaussKrugerProjectionCoordinateSystem#backwardCalculation(double, double, double, boolean)
     */
    public ArrayList<Double> projectionTransform(GaussKrugerProjectionCoordinateSystem outputSystem, double x, double y, double precision, boolean hasFalse) throws UnsupportedOperationException {
        if (!outputSystem.getGeodeticCoordinateSystem().equals(geodeticCoordinateSystem)) {
            throw new UnsupportedOperationException("两个投影坐标系的大地坐标系不一致");
        } else {
            ArrayList<Double> list = new ArrayList<>();

            ArrayList<Double> lb = backwardCalculation(x, y, precision, hasFalse);
            ArrayList<Double> newXY = outputSystem.forwardCalculation(lb.get(0), lb.get(1));

            list.addAll(newXY);
            list.addAll(lb);
            return list;
        }
    }

    /**
     * 获取此投影坐标系使用的大地坐标系
     *
     * @return GeodeticCoordinateSystem 大地坐标系实例
     * @see GeodeticCoordinateSystem
     */
    public GeodeticCoordinateSystem getGeodeticCoordinateSystem() {
        return geodeticCoordinateSystem;
    }

    /**
     * 获取东伪偏移值，单位：米
     *
     * @return 东伪偏移值
     */
    public double getFalseEasting() {
        return falseEasting;
    }

    /**
     * 获取北伪偏移值，单位：米
     *
     * @return 北伪偏移值
     */
    public double getFalseNorthing() {
        return falseNorthing;
    }

    /**
     * 获取中央子午线，单位：十进制度
     *
     * @return 中央子午线
     */
    public double getCentralMeridian() {
        return centralMeridian;
    }

    /**
     * 获取缩放因子
     *
     * @return 缩放因子
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * 获取纬度原点
     *
     * @return 纬度原点
     */
    public double getLatitudeOfOrigin() {
        return latitudeOfOrigin;
    }

    /**
     * 获取线性单位
     *
     * @return 线性单位
     */
    public String getLinerUnit() {
        return linerUnit;
    }

    /**
     * 获取分度带
     *
     * @return 分度带
     */
    public int getDegree() {
        return degree;
    }


    /**
     * 获取此高斯克吕格投影坐标系名称
     *
     * @return 高斯克吕格投影坐标系名称
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GaussKrugerProjectionCoordinateSystem{" +
                "falseEasting=" + falseEasting +
                ", falseNorthing=" + falseNorthing +
                ", centralMeridian=" + centralMeridian +
                ", scaleFactor=" + scaleFactor +
                ", latitudeOfOrigin=" + latitudeOfOrigin +
                ", linerUnit='" + linerUnit + '\'' +
                ", degree=" + degree +
                ", hasZoneNum=" + hasZoneNum +
                ", name='" + name + '\'' +
                ", geodeticCoordinateSystem=" + geodeticCoordinateSystem +
                '}';
    }

    /**
     * 储存已知高斯克吕格投影坐标系的枚举类型
     * <br>
     * 基准参数信息来自 ArcGIS
     */
    public enum RecordedSystem {

        /**
         * 大地坐标系：CGCS2000
         * <br>
         * 高斯投影坐标系：3度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        CGCS2000_3_Degree_GK_CM_111E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(1),
                3,
                false,
                500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "CGCS2000_3_Degree_GK_CM_111E")),

        /**
         * 大地坐标系：CGCS2000
         * <br>
         * 高斯投影坐标系：3度带；添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        CGCS2000_3_Degree_GK_Zone_37(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(1),
                3,
                true,
                37500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "CGCS2000_3_Degree_GK_Zone_37")),

        /**
         * 大地坐标系：CGCS2000
         * <br>
         * 高斯投影坐标系：6度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        CGCS2000_GK_CM_111E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(1),
                6,
                false,
                500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "CGCS2000_GK_CM_111E")),

        /**
         * 大地坐标系：CGCS2000
         * <br>
         * 高斯投影坐标系：6度带；不添加带号；123°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        CGCS2000_GK_CM_123E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(1),
                6,
                false,
                500000.0,
                0.0,
                123.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "CGCS2000_GK_CM_123E")),

        /**
         * 大地坐标系：CGCS2000
         * <br>
         * 高斯投影坐标系：6度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        CGCS2000_GK_Zone_19(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(1),
                6,
                true,
                19500000,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "CGCS2000_GK_Zone_19")),

        /**
         * 大地坐标系：Xian_1980
         * <br>
         * 高斯投影坐标系：3度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Xian_1980_3_Degree_GK_CM_111E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(2),
                3,
                false,
                500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Xian_1980_3_Degree_GK_CM_111E")),

        /**
         * 大地坐标系：Xian_1980
         * <br>
         * 高斯投影坐标系：3度带；添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Xian_1980_3_Degree_GK_Zone_37(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(2),
                3,
                true,
                37500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Xian_1980_3_Degree_GK_Zone_37")),

        /**
         * 大地坐标系：Xian_1980
         * <br>
         * 高斯投影坐标系：6度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Xian_1980_GK_CM_111E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(2),
                6,
                false,
                500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Xian_1980_GK_CM_111E")),

        /**
         * 大地坐标系：Xian_1980
         * <br>
         * 高斯投影坐标系：6度带；添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Xian_1980_GK_Zone_19(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(2),
                6,
                true,
                19500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Xian_1980_GK_Zone_19")),

        /**
         * 大地坐标系：Beijing_1954
         * <br>
         * 高斯投影坐标系：3度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Beijing_1954_3_Degree_GK_CM_111E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(3),
                3,
                false,
                500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Beijing_1954_3_Degree_GK_CM_111E")),

        /**
         * 大地坐标系：Beijing_1954
         * <br>
         * 高斯投影坐标系：3度带；不添加带号；117°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Beijing_1954_3_Degree_GK_CM_117E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(3),
                3,
                false,
                500000.0,
                0.0,
                117.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Beijing_1954_3_Degree_GK_CM_117E")),

        /**
         * 大地坐标系：Beijing_1954
         * <br>
         * 高斯投影坐标系：3度带；不添加带号；120°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Beijing_1954_3_Degree_GK_CM_120E(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(3),
                3,
                false,
                500000.0,
                0.0,
                120.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Beijing_1954_3_Degree_GK_CM_120E")),

        /**
         * 大地坐标系：Beijing_1954
         * <br>
         * 高斯投影坐标系：3度带；添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Beijing_1954_3_Degree_GK_Zone_37(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(3),
                3,
                true,
                37500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Beijing_1954_3_Degree_GK_Zone_37")),

        /**
         * 大地坐标系：Beijing_1954
         * <br>
         * 高斯投影坐标系：6度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Beijing_1954_GK_Zone_19N(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(3),
                6,
                false,
                500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Beijing_1954_GK_Zone_19N")),

        /**
         * 大地坐标系：Beijing_1954
         * <br>
         * 高斯投影坐标系：6度带；添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：1.0；纬度起点：0.0；线性单位：Meter(1.0)
         */
        Beijing_1954_GK_Zone_19(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(3),
                6,
                true,
                19500000.0,
                0.0,
                111.0,
                1.0,
                0.0,
                "Meter (1.0)",
                "Beijing_1954_GK_Zone_19")),

        /**
         * 大地坐标系：WGS 1984
         * <br>
         * 高斯投影坐标系：6度带；不添加带号；111°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：0.9996；纬度起点：0.0；线性单位：Meter(1.0)
         */
        WGS_1984_UTM_Zone_49N(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(4),
                6,
                false,
                500000.0,
                0.0,
                111.0,
                0.9996,
                0.0,
                "Meter (1.0)",
                "WGS_1984_UTM_Zone_49N")),

        /**
         * 大地坐标系：WGS 1984
         * <br>
         * 高斯投影坐标系：6度带；不添加带号；123°E中央子午线；东伪偏移500000.0米；北伪偏移：0.0米；缩放因子：0.9996；纬度起点：0.0；线性单位：Meter(1.0)
         */
        WGS_1984_UTM_Zone_51N(new GaussKrugerProjectionCoordinateSystem(GeodeticCoordinateSystem.getSystemById(4),
                6,
                false,
                500000.0,
                0.0,
                123.0,
                0.9996,
                0.0,
                "Meter (1.0)",
                "WGS_1984_UTM_Zone_51N"));

        /**
         * 高斯考虑投影坐标系实例
         */
        private GaussKrugerProjectionCoordinateSystem system;

        /**
         * 枚举构造器
         *
         * @param system {@link GaussKrugerProjectionCoordinateSystem} 高斯克吕格投影坐标系实例
         */
        RecordedSystem(GaussKrugerProjectionCoordinateSystem system) {
            this.system = system;
        }

        /**
         * 获取高斯克吕格投影坐标系的 {@link GaussKrugerProjectionCoordinateSystem} 实例
         *
         * @return GaussKrugerProjectionCoordinateSystem 实例
         */
        public GaussKrugerProjectionCoordinateSystem getSystem() {
            return system;
        }
    }
}
