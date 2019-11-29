package club.magiccrazyman.geodetic.core.coordinatesystem;

import club.magiccrazyman.geodetic.core.tools.CalculationTools;

/**
 * 大地坐标系
 * <br>
 * 大地经度：正数位于东半球，负数位于西半球
 * 大地纬度：正数位于北半球，负数位于南半球
 * <br>
 * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第四章
 *
 * @author Magic Crazy Man
 * @version 0.1.0
 */
public class GeodeticCoordinateSystem {

    /**
     * 椭圆长半轴
     */
    private final double a;

    /**
     * 椭圆短半轴
     */
    private final double b;

    /**
     * 极点处的子午线曲率半径
     */
    private final double c;

    /**
     * 椭圆第一偏心率
     */
    private final double e2;

    /**
     * 椭球第二偏心率
     */
    private final double de2;

    /**
     * 大地坐标系名称
     */
    private final String name;

    /**
     * 大地坐标系构造器，通过输入椭圆长半轴a及短半轴b创建一个新的大地坐标系
     *
     * @param a    椭圆长半轴
     * @param b    椭圆短半轴
     * @param name 大地坐标系名称
     */
    public GeodeticCoordinateSystem(double a, double b, String name) {
        this.a = a;
        this.b = b;
        this.c = Math.pow(a, 2) / b;
        double v = Math.pow(a, 2) - Math.pow(b, 2);
        this.e2 = v / Math.pow(a, 2);
        this.de2 = v / Math.pow(b, 2);
        this.name = name;
    }

    /**
     * 基于当前大地坐标系，将空间直角坐标系坐标转换为大地坐标系坐标
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第105页
     *
     * @param X         空间直角坐标系X轴值，单位：米
     * @param Y         空间直角坐标系Y轴值，单位：米
     * @param Z         空间直角坐标系Z轴值，单位：米
     * @param precision 大地纬度B迭代精度，单位：弧度
     * @return 以L(大地经度), B(大地纬度), H(大地高)，count(迭代总次数)顺序排列的double类型数组
     */
    public double[] transformToGeodeticCoordinateSystem(double X, double Y, double Z, double precision) {
        double N, L, B, H;
        int count;

        //L = acos(X / sqrt(X^2 + Y^2))
        L = Math.acos(X / Math.hypot(X, Y));
        if (Y < 0) { //由于计算结果恒小于等于180度，故如果Y小于0，则需要取反
            L = -L;
        }

        //B迭代推算
        double[] tmp = calculateGeodeticLatitudeFromSpatialSystem(X, Y, Z, precision);
        B = tmp[0];
        count = (int) tmp[1];

        //H = Z / sinB - N * (1 -e^2)
        N = a / (Math.sqrt(1 - e2 * Math.pow(Math.sin(B), 2)));
        H = Z / Math.sin(B) - N * (1 - e2);
        return new double[]{L, B, H, count};
    }

    /**
     * 迭代法，从空间直角坐标系推算大地纬度B
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第106页
     *
     * @param X         空间直角坐标系X轴值，单位：米
     * @param Y         空间直角坐标系Y轴值，单位：米
     * @param Z         空间直角坐标系Z轴值，单位：米
     * @param precision 大地纬度B迭代精度，单位：弧度
     * @return 以B(大地纬度)，count(迭代次数)顺序排列的double类型数组
     * @see GeodeticCoordinateSystem#calculateGeodeticLatitudeFromSpatialSystemIteration(double, double, double, double, double, int)
     */
    public double[] calculateGeodeticLatitudeFromSpatialSystem(double X, double Y, double Z, double precision) {
        double tanB1 = Z / Math.hypot(X, Y);
        return this.calculateGeodeticLatitudeFromSpatialSystemIteration(X, Y, Z, precision, tanB1, 1);
    }

    /**
     * 迭代法，从空间直角坐标系推算大地纬度B
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第105页
     *
     * @param X         空间直角坐标系X轴值，单位：米
     * @param Y         空间直角坐标系Y轴值，单位：米
     * @param Z         空间直角坐标系Z轴值，单位：米
     * @param precision 大地纬度B迭代精度，单位：弧度
     * @param tanB1     前一次迭代所得的tanB值
     * @param count     迭代次数，由1开始
     * @return 以B(大地纬度)，count(迭代次数)顺序排列的double类型数组
     */
    private double[] calculateGeodeticLatitudeFromSpatialSystemIteration(double X, double Y, double Z, double precision, double tanB1, int count) {
        final double t0, p, k;

        t0 = Z / Math.hypot(X, Y);
        p = c * e2 / Math.hypot(X, Y);
        k = 1 + de2;

        double tanB2, B1, B2;
        tanB2 = t0 + p * tanB1 / Math.sqrt(k + Math.pow(tanB1, 2));
        B1 = Math.atan(tanB1);
        B2 = Math.atan(tanB2);

        if (Math.abs(B1 - B2) <= precision) {
            //检验精度要求，达到精度要求便中止迭代并返回大地纬度B
            return new double[]{B2, count};
        } else {
            //否则继续进行迭代
            return this.calculateGeodeticLatitudeFromSpatialSystemIteration(X, Y, Z, precision, tanB2, ++count);
        }
    }

    /**
     * 基于当前大地坐标系，将大地坐标系坐标转换为空间直角坐标系坐标
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第105页
     *
     * @param L 大地经度，单位：弧度
     * @param B 大地纬度，单位：弧度
     * @param H 大地高，单位：米
     * @return 以X(X轴坐标值), Y(Y轴坐标值), Z(Z轴坐标值)顺序排列的double类型数组
     */
    public double[] transformToSpatialCoordinateSystem(double L, double B, double H) {
        double N, X, Y, Z;

        //X = (N + H) * cosB * cosL
        N = a / (Math.sqrt(1 - e2 * Math.pow(Math.sin(B), 2)));
        X = (N + H) * Math.cos(B) * Math.cos(L);

        //Y = (N + H) * cosB * sinL
        Y = (N + H) * Math.cos(B) * Math.sin(L);

        //Z = (H * (1 - e^2) + H) * sinB
        Z = (N * (1 - e2) + H) * Math.sin(B);

        return new double[]{X, Y, Z};
    }

    /**
     * 根据大地线起点的纬度B1，经度L1，大地方位角A1以及大地线长度S计算大地线终点的纬度B2，经度L2，大地方位角A2
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第148页
     *
     * @param L1 大地线起点的经度L1，单位：弧度
     * @param B1 大地线起点的纬度B1，单位：弧度
     * @param A1 大地线起点的大地方位角A1，单位：弧度
     * @param S  大地线长度S，单位：米
     * @return 以L2(大地线终点的经度L2)，B2(大地线终点的纬度B2)，A2(大地线终点的大地方位角A2)顺序排序的double类型数组
     */
    public double[] directSolutionOfGeodeticProblem(double L1, double B1, double A1, double S) {
        double W1, sinu1, cosu1, sinA0, cosA0, cotO1, sin2O1, cos2O1, sin2O0, cos2O0, sigma0, sigma, k2, A, B, C, alpha, beta, delta, sinu2, B2, lambda, sinA1, tanlambda, L2, A2, tanA2;
        //计算起点的归化纬度归化纬度
        W1 = Math.sqrt(1 - e2 * Math.pow(Math.sin(B1), 2));

        sinu1 = Math.sin(B1) * Math.sqrt(1 - e2) / W1;
        cosu1 = Math.cos(B1) / W1;

        //计算辅助函数值
        sinA1 = Math.sin(A1);
        sinA0 = cosu1 * sinA1;
        cosA0 = Math.sqrt(1 - Math.pow(sinA0, 2));
        cotO1 = cosu1 * Math.cos(A1) / sinu1;
        sin2O1 = 2 * cotO1 / (Math.pow(cotO1, 2) + 1);
        cos2O1 = (Math.pow(cotO1, 2) - 1) / (Math.pow(cotO1, 2) + 1);

        //计算球面长度
        k2 = de2 * Math.pow(cosA0, 2);
        A = b * (1 + k2 / 4 - 3 * Math.pow(k2, 2) / 64 + 5 * Math.pow(k2, 3) / 256);
        B = b * (k2 / 8 - Math.pow(k2, 2) / 32 + 15 * Math.pow(k2, 3) / 1024);
        C = b * (Math.pow(k2, 2) / 128 - 3 * Math.pow(k2, 3) / 512);

        sigma0 = (S - (B + C * cos2O1) * sin2O1) / A;
        cos2O0 = Math.cos(2 * sigma0);
        sin2O0 = Math.sin(2 * sigma0);
        sigma = sigma0 + (B + 5 * C * (cos2O1 * cos2O0 - sin2O1 * sin2O0)) * (sin2O1 * cos2O0 + cos2O1 * sin2O0) / A;

        //计算经度改正数
        alpha = (e2 / 2 + Math.pow(e2, 2) / 8 + Math.pow(e2, 3) / 16) - (Math.pow(e2, 2) / 16 + Math.pow(e2, 3) / 16) * Math.pow(cosA0, 2) + (3 * Math.pow(e2, 3) / 128) * Math.pow(cosA0, 4);
        beta = (Math.pow(e2, 2) / 32 + Math.pow(e2, 3) / 32) * Math.pow(cosA0, 2) - (Math.pow(e2, 3) / 64) * Math.pow(cosA0, 4);
        delta = (alpha * sigma + beta * ((sin2O1 * cos2O0 + cos2O1 * sin2O0) - sin2O1)) * sinA0;

        //计算重点大地坐标及大地方位角
        sinu2 = sinu1 * Math.cos(sigma) + cosu1 * Math.cos(A1) * Math.sin(sigma);
        B2 = Math.atan(sinu2 / (Math.sqrt(1 - e2) * Math.sqrt(1 - Math.pow(sinu2, 2))));
        lambda = Math.atan(sinA1 * Math.sin(sigma) / (cosu1 * Math.cos(sigma) - sinu1 * Math.sin(sigma) * Math.cos(A1)));

        //判断λ取值
        tanlambda = Math.tan(lambda);
        lambda = Math.abs(lambda);
        if (sinA1 > 0 && tanlambda < 0) {
            lambda = CalculationTools.degree2Rad(180) - lambda;
        } else if (sinA1 < 0 && tanlambda < 0) {
            lambda = -lambda;
        } else if (sinA1 < 0 && tanlambda > 0) {
            lambda = lambda - CalculationTools.degree2Rad(180);
        }

        L2 = L1 + lambda - delta;
        A2 = Math.atan(cosu1 * sinA1 / (cosu1 * Math.cos(sigma) * Math.cos(A1) - sinu1 * Math.sin(sigma)));
        tanA2 = Math.tan(A2);

        //判断A2取值
        A2 = Math.abs(A2);
        if (sinA1 < 0 && tanA2 < 0) {
            A2 = CalculationTools.degree2Rad(180) - A2;
        } else if (sinA1 > 0 && tanA2 > 0) {
            A2 = CalculationTools.degree2Rad(180) + A2;
        } else if (sinA1 > 0 && tanA2 < 0) {
            A2 = CalculationTools.degree2Rad(360) - A2;
        }

        return new double[]{L2, B2, A2};
    }

    /**
     * 根据两个大地坐标点L1，B1，L2，B2计算大地线S及其大地方位角A1，A2
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第149页
     *
     * @param L1        起点大地坐标的大地经度L1，单位：弧度
     * @param B1        起点大地坐标的大地纬度B1，单位：弧度
     * @param L2        终点大地坐标的大地经度L1，单位：弧度
     * @param B2        终点大地坐标的大地纬度B1，单位：弧度
     * @param precision 迭代推算经度
     * @return 以A1(起点大地方位角)，A2(终点大地方位角)，S(大地线)顺序排序的double类型数组
     */
    public double[] inverseSolutionOfGeodeticProblem(double L1, double B1, double L2, double B2, double precision) {
        double S, A1, A2, W1, W2, sinu1, sinu2, cosu1, cosu2, L, a1, a2, b1, b2, p, q, sinO, cosO, sigma, lambda, sinA0, cosA0_2, x, alpha, beta, delta1, delta2, y, k2, A, B, C, dB, dC;
        //计算两点的归化纬度
        W1 = Math.sqrt(1 - e2 * Math.pow(Math.sin(B1), 2));
        W2 = Math.sqrt(1 - e2 * Math.pow(Math.sin(B2), 2));

        //辅助计算
        sinu1 = Math.sin(B1) * Math.sqrt(1 - e2) / W1;
        sinu2 = Math.sin(B2) * Math.sqrt(1 - e2) / W2;
        cosu1 = Math.cos(B1) / W1;
        cosu2 = Math.cos(B2) / W2;
        L = L2 - L1;
        a1 = sinu1 * sinu2;
        a2 = cosu1 * cosu2;
        b1 = cosu1 * sinu2;
        b2 = sinu1 * cosu2;

        //逐次趋近法计算σ，实在不想用迭代了，就用for循环好了
        delta2 = 0;
        do {
            delta1 = delta2;
            lambda = L + delta1;

            p = cosu2 * Math.sin(lambda);
            q = b1 - b2 * Math.cos(lambda);
            A1 = Math.atan(p / q);

            A1 = Math.abs(A1);
            if (p > 0 && q < 0) {
                A1 = CalculationTools.degree2Rad(180) - A1;
            } else if (p < 0 && q < 0) {
                A1 = CalculationTools.degree2Rad(180) + A1;
            } else if (p < 0 && q > 0) {
                A1 = CalculationTools.degree2Rad(360) - A1;
            }

            sinO = p * Math.sin(A1) + q * Math.cos(A1);
            cosO = a1 + a2 * Math.cos(lambda);
            sigma = Math.atan(sinO / cosO);

            sigma = Math.abs(sigma);
            if (cosO < 0) {
                sigma = CalculationTools.degree2Rad(180) - sigma;
            }

            sinA0 = cosu1 * Math.sin(A1);
            cosA0_2 = 1 - Math.pow(sinA0, 2);
            x = 2 * a1 - cosA0_2 * cosO;

            alpha = (e2 / 2 + Math.pow(e2, 2) / 8 + Math.pow(e2, 3) / 16) - (Math.pow(e2, 2) / 16 + Math.pow(e2, 3) / 16) * cosA0_2 + (3 * Math.pow(e2, 3) / 128) * Math.pow(cosA0_2, 2);
            beta = (Math.pow(e2, 2) / 32 + Math.pow(e2, 3) / 32) - (Math.pow(e2, 3) / 64) * cosA0_2;
            delta2 = (alpha * sigma - 2 * beta * x * sinO) * sinA0;
        } while (Math.abs(delta2 - delta1) > precision);

        //计算系数A，B"，C"和大地线S
        k2 = de2 * cosA0_2;
        A = b * (1 + k2 / 4 - 3 * Math.pow(k2, 2) / 64 + 5 * Math.pow(k2, 3) / 256);
        B = b * (k2 / 8 - Math.pow(k2, 2) / 32 + 15 * Math.pow(k2, 3) / 1024);
        C = b * (Math.pow(k2, 2) / 128 - 3 * Math.pow(k2, 3) / 512);
        dB = 2 * B / cosA0_2;
        dC = 2 * C / Math.pow(cosA0_2, 2);

        y = (Math.pow(cosA0_2, 2) - 2 * Math.pow(x, 2)) * Math.cos(sigma);
        S = A * sigma + (dB * x + dC * y) * Math.sin(sigma);

        //计算反方位角
        A2 = Math.atan(cosu1 * Math.sin(lambda) / (b1 * Math.cos(lambda) - b2));
        if (A1 < CalculationTools.degree2Rad(180)) {
            A2 += CalculationTools.degree2Rad(180);
        }
        return new double[]{A1, A2, S};
    }

    /**
     * 计算子午圈曲率半径，单位：米
     * <br>
     * M = c / sqrt(1 + η^2)^3 = c / (1 + η^2)^(1/2)
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第108页
     *
     * @param eit2 η^2 = e'^2 * sin(B)^2
     * @return 子午圈曲率半径，单位：米
     */
    public double calculateMeridianCurvatureRadius(double eit2) {
        return c / Math.pow(1 + eit2, 1.5);
    }

    /**
     * 计算卯酉圈曲率半径，单位：米
     * <br>
     * N = c / sqrt(1 + η^2)
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第109页
     *
     * @param eit2 η^2 = e'^2 * sin(B)^2
     * @return 卯酉圈曲率半径，单位：米
     */
    public double calculatePrimeVerticalCurvatureRadius(double eit2) {
        return c / Math.sqrt(1 + eit2);
    }

    /**
     * 计算平均曲率半径，单位：米
     * <br>
     * N = c / sqrt(1 + η^2)
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第109页
     *
     * @param eit2 η^2 = e'^2 * sin(B)^2
     * @return 平均曲率半径，单位：米
     */
    public double calculateAverageCurvatureRadius(double eit2) {
        return c / (1 + eit2);
    }

    /**
     * 根据大地纬度计算子午线弧长，单位：米
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第115页
     *
     * @param B 大地纬度B
     * @return 子午线弧长，单位：米
     */
    public double calculateMeridianArc(double B) {
        double[] a = this.calculateMeridianArcParameters(); //获取计算参数
        return a[0] * B - a[1] / 2 * Math.sin(2 * B) + a[2] / 4 * Math.sin(4 * B) - a[3] / 6 * Math.sin(6 * B) + a[4] / 8 * Math.sin(8 * B);
    }

    /**
     * 根据大地经差计算平行圈弧长，单位：米
     * <br>
     * * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第119页
     *
     * @param l 大地经差
     * @param B 大地纬度
     * @return 平行圈弧长，单位：米
     */
    public double calculateParallelCircleArc(double l, double B) {
        return calculatePrimeVerticalCurvatureRadius(de2 * Math.pow(Math.cos(B), 2)) * Math.cos(B) * l;
    }

    /**
     * 迭代法，根据子午线弧长推算大地纬度B
     * <br>
     * 此处X轴真坐标值 == 子午线弧长
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第118页
     *
     * @param X         X轴真坐标值，单位：米
     * @param precision 推算精度，单位：弧度
     * @return 以B(大地纬度)，count(迭代次数)顺序排列的double类型数组
     * @see GeodeticCoordinateSystem#calculateGeodeticLatitudeFromMeridianArcIteration(double[], double, double, double, int)
     */
    public double[] calculateGeodeticLatitudeFromMeridianArc(double X, double precision) {
        double[] parameters = calculateMeridianArcParameters();
        double start = X / parameters[0]; //迭代初始值 = X / parameters[0]

        return calculateGeodeticLatitudeFromMeridianArcIteration(parameters, start, X, precision, 1);
    }

    /**
     * 迭代法，根据子午线弧长推算大地纬度B
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第118页
     *
     * @param parameters 子午线弧长计算参数
     * @param B1         前次迭代值
     * @param X          X轴真坐标值
     * @param precision  迭代精度
     * @param count      迭代次数
     * @return 以B(大地纬度)，count(迭代次数)顺序排列的double类型数组
     */
    private double[] calculateGeodeticLatitudeFromMeridianArcIteration(double[] parameters, double B1, double X, double precision, int count) {
        double B2 = (X - (-1 * parameters[1] / 2 * Math.sin(2 * B1) + parameters[2] / 4 * Math.sin(4 * B1) - parameters[3] / 6 * Math.sin(6 * B1) + parameters[4] / 8 * Math.sin(8 * B1))) / parameters[0];
        if (Math.abs(B2 - B1) <= precision) {
            return new double[]{B2, count};
        } else {
            return calculateGeodeticLatitudeFromMeridianArcIteration(parameters, B2, X, precision, ++count);
        }
    }

    /**
     * 计算子午线弧长计算参数
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第111页
     *
     * @return 以a0，a2，a4，a6，a8顺序排列的double类型数组
     */
    public double[] calculateMeridianArcParameters() {
        double m0, m2, m4, m6, m8, a0, a2, a4, a6, a8;
        m0 = a * (1 - e2);
        m2 = 3.0 / 2 * e2 * m0;
        m4 = 5.0 / 4 * e2 * m2;
        m6 = 7.0 / 6 * e2 * m4;
        m8 = 9.0 / 8 * e2 * m6;

        a0 = m0 + m2 / 2 + 3.0 / 8 * m4 + 5.0 / 16 * m6 + 35.0 / 128 * m8;
        a2 = m2 / 2 + m4 / 2 + 15.0 / 32 * m6 + 7.0 / 16 * m8;
        a4 = m4 / 8 + 3.0 / 16 * m6 + 7.0 / 32 * m8;
        a6 = m6 / 32 + m8 / 16;
        a8 = m8 / 128;
        return new double[]{a0, a2, a4, a6, a8};
    }

    /**
     * 获取椭圆长半轴a
     *
     * @return 椭圆长半轴a
     */
    public final double getA() {
        return a;
    }

    /**
     * 获取椭圆短半轴b
     *
     * @return 椭圆短半轴b
     */
    public final double getB() {
        return b;
    }

    /**
     * 获取椭圆第一偏心率e^2
     *
     * @return 椭圆第一偏心率e^2
     */
    public final double getE2() {
        return e2;
    }

    /**
     * 获取椭圆第二偏心率e'^2
     *
     * @return 椭圆第二偏心率e'^2
     */
    public final double getDE2() {
        return de2;
    }

    /**
     * 获取椭圆扁率α
     *
     * @return 椭圆扁率α
     */
    public final double getAlpha() {
        return (a - b) / a;
    }

    /**
     * 获取椭圆极点处的子午线曲率半径c
     *
     * @return 椭圆极点处的子午线曲率半径c
     */
    public final double getC() {
        return c;
    }

    /**
     * 获取大地坐标系名称
     *
     * @return 大地坐标系名称
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GeodeticCoordinateSystem{" +
                "name='" + name + '\'' +
                ", a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", e2=" + e2 +
                ", de2=" + de2 +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GeodeticCoordinateSystem) {
            GeodeticCoordinateSystem system = (GeodeticCoordinateSystem) obj;
            return this.a == system.a && this.b == system.b;
        } else {
            return super.equals(obj);
        }
    }

    /**
     * 储存已知大地坐标系的枚举类型
     */
    public enum RecordedSystem {

        /**
         * 2000国家大地坐标系实例
         */
        CGCS2000(new GeodeticCoordinateSystem(6378137, 6356752.31414, "2000国家大地坐标系")),

        /**
         * 1980年西安大地坐标系实例
         */
        Xian_1980(new GeodeticCoordinateSystem(6378140, 6356755.2882, "1980年西安大地坐标系")),

        /**
         * 1954年北京大地坐标系实例
         */
        Beijing_1954(new GeodeticCoordinateSystem(6378245, 6356863.0188, "1954年北京大地坐标系")),

        /**
         * WGS 1984大地坐标系实例
         */
        WGS_1984(new GeodeticCoordinateSystem(6378137.0, 6356752.314245179, "WGS 1984大地坐标系"));

        /**
         * 大地坐标系实例
         */
        private final GeodeticCoordinateSystem system;

        /**
         * 枚举构造器
         *
         * @param system {@link GeodeticCoordinateSystem} 大地坐标系实例
         */
        RecordedSystem(GeodeticCoordinateSystem system) {
            this.system = system;
        }

        /**
         * 获取大地坐标系的 {@link GeodeticCoordinateSystem} 实例
         *
         * @return GeodeticCoordinateSystem 实例
         */
        public GeodeticCoordinateSystem getSystem() {
            return system;
        }
    }
}
