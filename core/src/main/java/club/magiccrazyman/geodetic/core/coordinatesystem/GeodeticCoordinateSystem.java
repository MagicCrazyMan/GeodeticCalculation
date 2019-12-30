package club.magiccrazyman.geodetic.core.coordinatesystem;

import club.magiccrazyman.geodetic.core.tools.CalculationTools;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.*;

/**
 * 大地坐标系
 * <br>
 * 大地经度：正数位于东半球，负数位于西半球
 * 大地纬度：正数位于北半球，负数位于南半球
 * <br>
 * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第四章
 *
 * @author Magic Crazy Man
 */
public class GeodeticCoordinateSystem {

    private static HashMap<Integer, GeodeticCoordinateSystem> systems;

    public static GeodeticCoordinateSystem getSystemById(int id) {
        if (systems == null) {
            initSystemsFromXML();
        }
        return systems.get(id);
    }

    public static Collection<GeodeticCoordinateSystem> getSystems(){
        if(systems == null){
            initSystemsFromXML();
        }
        return systems.values();
    }

    private static void initSystemsFromXML() {
        try {
            systems = new HashMap<>();

            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(GeodeticCoordinateSystem.class.getResource("/resource/goordinateSystems/geodetic/systems.xml"));
            Element systemsXML = document.getRootElement();
            for (Iterator<Element> it = systemsXML.elementIterator("system"); it.hasNext(); ) {
                Element systemXML = it.next();
                int id = Integer.parseInt(systemXML.element("id").getText());
                String referenceEllipsoidName = systemXML.element("referenceEllipsoidName").getText();
                String name = systemXML.element("names").element("zh_cn").getText();
                double primeMerdian = Double.parseDouble(systemXML.element("primeMeridian").getText());
                double semimajorAxis = Double.parseDouble(systemXML.element("semimajorAxis").getText());
                double semiminorAxis = Double.parseDouble(systemXML.element("semiminorAxis").getText());

                GeodeticCoordinateSystem system = new GeodeticCoordinateSystem(semimajorAxis, semiminorAxis, primeMerdian, name,id,referenceEllipsoidName);
                systems.put(id, system);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 椭圆长半轴
     */
    private final double semimajorAxis;

    /**
     * 椭圆短半轴
     */
    private final double semiminorAxis;

    /**
     * 极点处的子午线曲率半径
     */
    private final double poleCurvatureRadius;

    /**
     * 椭球扁率α
     */
    private final double flattening;

    /**
     * 椭圆第一偏心率
     */
    private final double firstEccentricity;

    /**
     * 椭球第二偏心率
     */
    private final double secondEccentricity;

    /**
     * 大地坐标系起始子午线
     */
    private final double primeMeridian;

    /**
     * 大地坐标系名称
     */
    private final String name;

    /**
     * 大地坐标系ID
     */
    private final int id;

    /**
     * 大地坐标系椭球体名称
     */
    private final String referenceEllipsoidName;

    /**
     * 大地坐标系构造器，通过输入椭圆长半轴a及短半轴b创建一个新的大地坐标系
     *
     * @param semimajorAxis          椭圆长半轴
     * @param semiminorAxis          椭圆短半轴
     * @param primeMeridian          大地坐标系起始子午线
     * @param name                   大地坐标系名称
     * @param id                     大地坐标系ID
     * @param referenceEllipsoidName 大地坐标系椭球体名称
     */
    public GeodeticCoordinateSystem(double semimajorAxis, double semiminorAxis, double primeMeridian, String name, int id, String referenceEllipsoidName) {
        this.semimajorAxis = semimajorAxis;
        this.semiminorAxis = semiminorAxis;
        this.poleCurvatureRadius = Math.pow(semimajorAxis, 2) / semiminorAxis;
        this.flattening = (semiminorAxis - semiminorAxis) / semimajorAxis;
        double v = Math.pow(semimajorAxis, 2) - Math.pow(semiminorAxis, 2);
        this.firstEccentricity = v / Math.pow(semimajorAxis, 2);
        this.secondEccentricity = v / Math.pow(semiminorAxis, 2);
        this.primeMeridian = primeMeridian;
        this.name = name;
        this.id = id;
        this.referenceEllipsoidName = referenceEllipsoidName;
    }

    /**
     * 大地坐标系构造器，通过输入椭圆长半轴a及短半轴b创建一个新的大地坐标系
     *
     * @param semimajorAxis 椭圆长半轴
     * @param semiminorAxis 椭圆短半轴
     * @param primeMeridian 大地坐标系起始子午线
     * @param name          大地坐标系名称
     * @see GeodeticCoordinateSystem#GeodeticCoordinateSystem(double, double, double, String, int, String)
     */
    public GeodeticCoordinateSystem(double semimajorAxis, double semiminorAxis, double primeMeridian, String name) {
        this(semimajorAxis, semiminorAxis, primeMeridian, name, 10000 + (int) (Math.random() * 10000), "Unknown");
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
     * @return 以L(大地经度), B(大地纬度), H(大地高)，count(迭代总次数)，...(每次迭代值)顺序排列ArrayList
     */
    public ArrayList<Double> transformToGeodeticCoordinateSystem(double X, double Y, double Z, double precision) {
        ArrayList<Double> list = new ArrayList<>();

        double N, L, B, H;

        //L = acos(X / sqrt(X^2 + Y^2))
        L = Math.acos(X / Math.hypot(X, Y));
        if (Y < 0) { //由于计算结果恒小于等于180度，故如果Y小于0，则需要取反
            L -= L;
        }

        //B迭代推算
        ArrayList<Double> Bi = calculateGeodeticLatitudeFromSpatialSystem(X, Y, Z, precision);
        B = Bi.get(Bi.size() - 1);

        //H = Z / sinB - N * (1 -e^2)
        N = semimajorAxis / (Math.sqrt(1 - firstEccentricity * Math.pow(Math.sin(B), 2)));
        H = Z / Math.sin(B) - N * (1 - firstEccentricity);

        list.add(L);
        list.add(B);
        list.add(H);
        list.add((double) Bi.size() - 1); //Bi中的第一个值为初值，不算在迭代次数中
        list.addAll(Bi);
        return list;
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
     * @return 包含每次迭代值的ArrayList，最后一个值即为最终计算值
     * @see GeodeticCoordinateSystem#calculateGeodeticLatitudeFromSpatialSystemIteration(double, double, double, double, double, ArrayList)
     */
    public ArrayList<Double> calculateGeodeticLatitudeFromSpatialSystem(double X, double Y, double Z, double precision) {
        double tanB1 = Z / Math.hypot(X, Y);
        double t0 = Z / Math.hypot(X, Y);
        double p = poleCurvatureRadius * firstEccentricity / Math.hypot(X, Y);
        double k = 1 + secondEccentricity;
        return this.calculateGeodeticLatitudeFromSpatialSystemIteration(precision, tanB1, t0, p, k, new ArrayList<>());
    }

    /**
     * 迭代法，从空间直角坐标系推算大地纬度B
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第105页
     *
     * @param precision 大地纬度B迭代精度，单位：弧度
     * @param tanB1     前一次迭代所得的tanB值
     * @param t0        递归计算使用参数
     * @param p         递归计算使用参数
     * @param k         递归计算使用参数
     * @param list      用于递归的ArrayList
     * @return 包含每次迭代值的ArrayList，最后一个值即为最终计算值
     */
    private ArrayList<Double> calculateGeodeticLatitudeFromSpatialSystemIteration(double precision, double tanB1, double t0, double p, double k, ArrayList<Double> list) {
        double tanB2, B1, B2;

        B1 = Math.atan(tanB1);
        list.add(B1);

        tanB2 = t0 + p * tanB1 / Math.sqrt(k + Math.pow(tanB1, 2));
        B2 = Math.atan(tanB2);

        if (Math.abs(B1 - B2) <= precision) {
            //检验精度要求，达到精度要求便中止迭代并返回大地纬度B
            list.add(B2);
            return list;
        } else {
            //否则继续进行迭代
            return this.calculateGeodeticLatitudeFromSpatialSystemIteration(precision, tanB2, t0, p, k, list);
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
     * @return 以X(X轴坐标值), Y(Y轴坐标值), Z(Z轴坐标值)顺序排列的ArrayList
     */
    public ArrayList<Double> transformToSpatialCoordinateSystem(double L, double B, double H) {
        ArrayList<Double> list = new ArrayList<>();

        double N, X, Y, Z;

        //X = (N + H) * cosB * cosL
        N = semimajorAxis / (Math.sqrt(1 - firstEccentricity * Math.pow(Math.sin(B), 2)));
        X = (N + H) * Math.cos(B) * Math.cos(L);

        //Y = (N + H) * cosB * sinL
        Y = (N + H) * Math.cos(B) * Math.sin(L);

        //Z = (H * (1 - e^2) + H) * sinB
        Z = (N * (1 - firstEccentricity) + H) * Math.sin(B);

        list.add(X);
        list.add(Y);
        list.add(Z);
        return list;
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
     * @return 以L2(大地线终点的经度L2)，B2(大地线终点的纬度B2)，A2(大地线终点的大地方位角A2)顺序排序的ArrayList
     */
    public ArrayList<Double> directSolutionOfGeodeticProblem(double L1, double B1, double A1, double S) {
        ArrayList<Double> list = new ArrayList<>();
        double W1, sinu1, cosu1, sinA0, cosA0, cotO1, sin2O1, cos2O1, sin2O0, cos2O0, sigma0, sigma, k2, A, B, C, alpha, beta, delta, sinu2, B2, lambda, sinA1, tanlambda, L2, A2, tanA2;
        //计算起点的归化纬度归化纬度
        W1 = Math.sqrt(1 - firstEccentricity * Math.pow(Math.sin(B1), 2));

        sinu1 = Math.sin(B1) * Math.sqrt(1 - firstEccentricity) / W1;
        cosu1 = Math.cos(B1) / W1;

        //计算辅助函数值
        sinA1 = Math.sin(A1);
        sinA0 = cosu1 * sinA1;
        cosA0 = Math.sqrt(1 - Math.pow(sinA0, 2));
        cotO1 = cosu1 * Math.cos(A1) / sinu1;
        sin2O1 = 2 * cotO1 / (Math.pow(cotO1, 2) + 1);
        cos2O1 = (Math.pow(cotO1, 2) - 1) / (Math.pow(cotO1, 2) + 1);

        //计算球面长度
        k2 = secondEccentricity * Math.pow(cosA0, 2);
        A = semiminorAxis * (1 + k2 / 4 - 3 * Math.pow(k2, 2) / 64 + 5 * Math.pow(k2, 3) / 256);
        B = semiminorAxis * (k2 / 8 - Math.pow(k2, 2) / 32 + 15 * Math.pow(k2, 3) / 1024);
        C = semiminorAxis * (Math.pow(k2, 2) / 128 - 3 * Math.pow(k2, 3) / 512);

        sigma0 = (S - (B + C * cos2O1) * sin2O1) / A;
        cos2O0 = Math.cos(2 * sigma0);
        sin2O0 = Math.sin(2 * sigma0);
        sigma = sigma0 + (B + 5 * C * (cos2O1 * cos2O0 - sin2O1 * sin2O0)) * (sin2O1 * cos2O0 + cos2O1 * sin2O0) / A;

        //计算经度改正数
        alpha = (firstEccentricity / 2 + Math.pow(firstEccentricity, 2) / 8 + Math.pow(firstEccentricity, 3) / 16) - (Math.pow(firstEccentricity, 2) / 16 + Math.pow(firstEccentricity, 3) / 16) * Math.pow(cosA0, 2) + (3 * Math.pow(firstEccentricity, 3) / 128) * Math.pow(cosA0, 4);
        beta = (Math.pow(firstEccentricity, 2) / 32 + Math.pow(firstEccentricity, 3) / 32) * Math.pow(cosA0, 2) - (Math.pow(firstEccentricity, 3) / 64) * Math.pow(cosA0, 4);
        delta = (alpha * sigma + beta * ((sin2O1 * cos2O0 + cos2O1 * sin2O0) - sin2O1)) * sinA0;

        //计算重点大地坐标及大地方位角
        sinu2 = sinu1 * Math.cos(sigma) + cosu1 * Math.cos(A1) * Math.sin(sigma);
        B2 = Math.atan(sinu2 / (Math.sqrt(1 - firstEccentricity) * Math.sqrt(1 - Math.pow(sinu2, 2))));
        lambda = Math.atan(sinA1 * Math.sin(sigma) / (cosu1 * Math.cos(sigma) - sinu1 * Math.sin(sigma) * Math.cos(A1)));

        //判断λ取值
        tanlambda = Math.tan(lambda);
        lambda = Math.abs(lambda);
        if (sinA1 > 0 && tanlambda < 0) {
            lambda = CalculationTools.degrees2Radians(180) - lambda;
        } else if (sinA1 < 0 && tanlambda < 0) {
            lambda = -lambda;
        } else if (sinA1 < 0 && tanlambda > 0) {
            lambda = lambda - CalculationTools.degrees2Radians(180);
        }

        L2 = L1 + lambda - delta;
        A2 = Math.atan(cosu1 * sinA1 / (cosu1 * Math.cos(sigma) * Math.cos(A1) - sinu1 * Math.sin(sigma)));
        tanA2 = Math.tan(A2);

        //判断A2取值
        A2 = Math.abs(A2);
        if (sinA1 < 0 && tanA2 < 0) {
            A2 = CalculationTools.degrees2Radians(180) - A2;
        } else if (sinA1 > 0 && tanA2 > 0) {
            A2 = CalculationTools.degrees2Radians(180) + A2;
        } else if (sinA1 > 0 && tanA2 < 0) {
            A2 = CalculationTools.degrees2Radians(360) - A2;
        }

        list.add(L2);
        list.add(B2);
        list.add(A2);
        return list;
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
     * @return 以A1(起点大地方位角)，A2(终点大地方位角)，S(大地线)，count(趋近次数)，...(每次趋近σ值)顺序排序的ArrayList
     */
    public ArrayList<Double> inverseSolutionOfGeodeticProblem(double L1, double B1, double L2, double B2, double precision) {
        ArrayList<Double> list = new ArrayList<>();
        double S, A1, A2, W1, W2, sinu1, sinu2, cosu1, cosu2, L, a1, a2, b1, b2, p, q, sinO, cosO, sigma, lambda, sinA0, cosA0_2, x, alpha, beta, delta1, delta2, y, k2, A, B, C, dB, dC;
        //计算两点的归化纬度
        W1 = Math.sqrt(1 - firstEccentricity * Math.pow(Math.sin(B1), 2));
        W2 = Math.sqrt(1 - firstEccentricity * Math.pow(Math.sin(B2), 2));

        //辅助计算
        sinu1 = Math.sin(B1) * Math.sqrt(1 - firstEccentricity) / W1;
        sinu2 = Math.sin(B2) * Math.sqrt(1 - firstEccentricity) / W2;
        cosu1 = Math.cos(B1) / W1;
        cosu2 = Math.cos(B2) / W2;
        L = L2 - L1;
        a1 = sinu1 * sinu2;
        a2 = cosu1 * cosu2;
        b1 = cosu1 * sinu2;
        b2 = sinu1 * cosu2;

        //逐次趋近法计算σ，实在不想用迭代了，就用for循环好了
        ArrayList<Double> list1 = new ArrayList<>();
        delta2 = 0;
        do {
            delta1 = delta2;
            lambda = L + delta1;

            p = cosu2 * Math.sin(lambda);
            q = b1 - b2 * Math.cos(lambda);
            A1 = Math.atan(p / q);

            A1 = Math.abs(A1);
            if (p > 0 && q < 0) {
                A1 = CalculationTools.degrees2Radians(180) - A1;
            } else if (p < 0 && q < 0) {
                A1 = CalculationTools.degrees2Radians(180) + A1;
            } else if (p < 0 && q > 0) {
                A1 = CalculationTools.degrees2Radians(360) - A1;
            }

            sinO = p * Math.sin(A1) + q * Math.cos(A1);
            cosO = a1 + a2 * Math.cos(lambda);
            sigma = Math.atan(sinO / cosO);

            sigma = Math.abs(sigma);
            if (cosO < 0) {
                sigma = CalculationTools.degrees2Radians(180) - sigma;
            }

            sinA0 = cosu1 * Math.sin(A1);
            cosA0_2 = 1 - Math.pow(sinA0, 2);
            x = 2 * a1 - cosA0_2 * cosO;

            alpha = (firstEccentricity / 2 + Math.pow(firstEccentricity, 2) / 8 + Math.pow(firstEccentricity, 3) / 16) - (Math.pow(firstEccentricity, 2) / 16 + Math.pow(firstEccentricity, 3) / 16) * cosA0_2 + (3 * Math.pow(firstEccentricity, 3) / 128) * Math.pow(cosA0_2, 2);
            beta = (Math.pow(firstEccentricity, 2) / 32 + Math.pow(firstEccentricity, 3) / 32) - (Math.pow(firstEccentricity, 3) / 64) * cosA0_2;
            delta2 = (alpha * sigma - 2 * beta * x * sinO) * sinA0;
            list1.add(sigma);
        } while (Math.abs(delta2 - delta1) > precision);

        //计算系数A，B"，C"和大地线S
        k2 = secondEccentricity * cosA0_2;
        A = semiminorAxis * (1 + k2 / 4 - 3 * Math.pow(k2, 2) / 64 + 5 * Math.pow(k2, 3) / 256);
        B = semiminorAxis * (k2 / 8 - Math.pow(k2, 2) / 32 + 15 * Math.pow(k2, 3) / 1024);
        C = semiminorAxis * (Math.pow(k2, 2) / 128 - 3 * Math.pow(k2, 3) / 512);
        dB = 2 * B / cosA0_2;
        dC = 2 * C / Math.pow(cosA0_2, 2);

        y = (Math.pow(cosA0_2, 2) - 2 * Math.pow(x, 2)) * Math.cos(sigma);
        S = A * sigma + (dB * x + dC * y) * Math.sin(sigma);

        //计算反方位角
        A2 = Math.atan(cosu1 * Math.sin(lambda) / (b1 * Math.cos(lambda) - b2));
        if (A1 < CalculationTools.degrees2Radians(180)) {
            A2 += CalculationTools.degrees2Radians(180);
        }

        list.add(A1);
        list.add(A2);
        list.add(S);
        list.add((double) list1.size());
        list.addAll(list1);
        return list;
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
        return poleCurvatureRadius / Math.pow(1 + eit2, 1.5);
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
        return poleCurvatureRadius / Math.sqrt(1 + eit2);
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
        return poleCurvatureRadius / (1 + eit2);
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
        return calculatePrimeVerticalCurvatureRadius(secondEccentricity * Math.pow(Math.cos(B), 2)) * Math.cos(B) * l;
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
     * @return 包含每次迭代值的ArrayList，最后一个值即为最终计算值
     * @see GeodeticCoordinateSystem#calculateGeodeticLatitudeFromMeridianArcIteration(double, double, double, double[], ArrayList)
     */
    public ArrayList<Double> calculateGeodeticLatitudeFromMeridianArc(double X, double precision) {
        double[] parameters = calculateMeridianArcParameters();
        double start = X / parameters[0]; //迭代初始值 = X / parameters[0]

        return calculateGeodeticLatitudeFromMeridianArcIteration(start, X, precision, parameters, new ArrayList<>());
    }

    /**
     * 迭代法，根据子午线弧长推算大地纬度B
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第118页
     *
     * @param B1         前次迭代值
     * @param X          X轴真坐标值
     * @param precision  迭代精度
     * @param parameters 子午线弧长计算参数
     * @param list       用于递归的ArrayList
     * @return 包含每次迭代值的ArrayList，最后一个值即为最终计算值
     */
    private ArrayList<Double> calculateGeodeticLatitudeFromMeridianArcIteration(double B1, double X, double precision, double[] parameters, ArrayList<Double> list) {
        list.add(B1);
        double B2 = (X - (-1 * parameters[1] / 2 * Math.sin(2 * B1) + parameters[2] / 4 * Math.sin(4 * B1) - parameters[3] / 6 * Math.sin(6 * B1) + parameters[4] / 8 * Math.sin(8 * B1))) / parameters[0];
        if (Math.abs(B2 - B1) <= precision) {
            list.add(B2);
            return list;
        } else {
            return calculateGeodeticLatitudeFromMeridianArcIteration(B2, X, precision, parameters, list);
        }
    }

    private double[] meridianArcParameters;

    /**
     * 计算子午线弧长计算参数
     * <br>
     * 详细请参考《大地测量学基础》（第二版），武汉大学出版社。第111页
     *
     * @return 以a0，a2，a4，a6，a8顺序排列的double类型数组
     */
    public double[] calculateMeridianArcParameters() {
        if (meridianArcParameters == null) {
            double m0, m2, m4, m6, m8, a0, a2, a4, a6, a8;
            m0 = semimajorAxis * (1 - firstEccentricity);
            m2 = 3.0 / 2 * firstEccentricity * m0;
            m4 = 5.0 / 4 * firstEccentricity * m2;
            m6 = 7.0 / 6 * firstEccentricity * m4;
            m8 = 9.0 / 8 * firstEccentricity * m6;

            a0 = m0 + m2 / 2 + 3.0 / 8 * m4 + 5.0 / 16 * m6 + 35.0 / 128 * m8;
            a2 = m2 / 2 + m4 / 2 + 15.0 / 32 * m6 + 7.0 / 16 * m8;
            a4 = m4 / 8 + 3.0 / 16 * m6 + 7.0 / 32 * m8;
            a6 = m6 / 32 + m8 / 16;
            a8 = m8 / 128;

            meridianArcParameters = new double[]{a0, a2, a4, a6, a8};
        }
        return meridianArcParameters;
    }

    /**
     * 获取椭圆长半轴a
     *
     * @return 椭圆长半轴a
     */
    public final double getSemimajorAxis() {
        return semimajorAxis;
    }

    /**
     * 获取椭圆短半轴b
     *
     * @return 椭圆短半轴b
     */
    public final double getSemiminorAxis() {
        return semiminorAxis;
    }

    /**
     * 获取椭圆第一偏心率e^2
     *
     * @return 椭圆第一偏心率e^2
     */
    public final double getFirstEccentricity() {
        return firstEccentricity;
    }

    /**
     * 获取椭圆第二偏心率e'^2
     *
     * @return 椭圆第二偏心率e'^2
     */
    public final double getSecondEccentricity() {
        return secondEccentricity;
    }

    /**
     * 获取椭圆扁率α
     *
     * @return 椭圆扁率α
     */
    public final double getFlattening() {
        return flattening;
    }

    /**
     * 获取椭圆扁率倒数 1/α
     *
     * @return 椭圆扁率倒数 1/α
     */
    public final double getInverseFlattening() {
        return 1 / flattening;
    }

    /**
     * 获取椭圆极点处的子午线曲率半径c
     *
     * @return 椭圆极点处的子午线曲率半径c
     */
    public final double getPoleCurvatureRadius() {
        return poleCurvatureRadius;
    }

    /**
     * 获取大地坐标系名称
     *
     * @return 大地坐标系名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取大地坐标系ID
     *
     * @return 大地坐标系ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取大地坐标系椭球体名称
     *
     * @return 大地坐标系椭球体名称
     */
    public String getReferenceEllipsoidName() {
        return referenceEllipsoidName;
    }

    /**
     * 获取大地坐标系起始子午线
     *
     * @return 大地坐标系起始子午线
     */
    public double getPrimeMeridian() {
        return primeMeridian;
    }

    @Override
    public String toString() {
        return "GeodeticCoordinateSystem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reference ellipsoid name=" + referenceEllipsoidName+
                ", prime meridian=" + primeMeridian +
                ", semi major axis=" + semimajorAxis +
                ", semi minor axis=" + semiminorAxis +
                ", pole curvature radius=" + poleCurvatureRadius +
                ", first eccentricity=" + firstEccentricity +
                ", second eccentricity=" + secondEccentricity +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GeodeticCoordinateSystem) {
            GeodeticCoordinateSystem system = (GeodeticCoordinateSystem) obj;
            return this.semimajorAxis == system.semimajorAxis && this.semiminorAxis == system.semiminorAxis;
        } else {
            return super.equals(obj);
        }
    }
}
