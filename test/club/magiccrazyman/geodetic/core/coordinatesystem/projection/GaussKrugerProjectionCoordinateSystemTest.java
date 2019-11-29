package club.magiccrazyman.geodetic.core.coordinatesystem.projection;

import club.magiccrazyman.geodetic.core.tools.CalculationTools;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GaussKrugerProjectionCoordinateSystemTest {
    private GaussKrugerProjectionCoordinateSystem beijing54_3_20N = GaussKrugerProjectionCoordinateSystem.RecordedSystem.Beijing_1954_3_Degree_GK_CM_117E.getSystem();
    private GaussKrugerProjectionCoordinateSystem beijing54_3_21N = GaussKrugerProjectionCoordinateSystem.RecordedSystem.Beijing_1954_3_Degree_GK_CM_120E.getSystem();

    private GaussKrugerProjectionCoordinateSystem cgcs2000_3_111E = GaussKrugerProjectionCoordinateSystem.RecordedSystem.CGCS2000_3_Degree_GK_CM_111E.getSystem();
    private GaussKrugerProjectionCoordinateSystem utm = GaussKrugerProjectionCoordinateSystem.RecordedSystem.WGS_1984_UTM_Zone_49N.getSystem();

    @Test
    void forwardCalculation() {
        double L = CalculationTools.degree2Rad(111 + 17.0 / 60 + 58.3596 / 3600);
        double B = CalculationTools.degree2Rad(30 + 45.0 / 60 + 25.4425 / 3600);

        ArrayList<Double> xy = cgcs2000_3_111E.forwardCalculation(L, B);

        String xz = String.format("%.4f", xy.get(0));
        String yz = String.format("%.4f", xy.get(1));
        String x = String.format("%.4f", xy.get(2));
        String y = String.format("%.4f", xy.get(3));

        assertEquals("3404079.3582", xz, String.format("xz：%f", xy.get(0)));
        assertEquals("528680.0889", yz, String.format("yz：%f", xy.get(1)));
        assertEquals("3404079.3582", x, String.format("x：%f", xy.get(2)));
        assertEquals("28680.0889", y, String.format("y：%f", xy.get(3)));
    }

    @Test
    void backwardCalculation() {
        double x = 3404762.7444;
        double y = 624430.6672;
        double precision = 0.0000000000001;

        ArrayList<Double> LBlMNC = cgcs2000_3_111E.backwardCalculation(x, y, precision, true);

        String L = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBlMNC.get(0)));
        String B = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBlMNC.get(1)));
        int count = (int) (double) LBlMNC.get(5);

        assertEquals("112°17'58.3596\"", L, String.format("L：%f", LBlMNC.get(0)));
        assertEquals("30°45'25.4425\"", B, String.format("B：%f", LBlMNC.get(1)));
        assertEquals(5, count, String.format("B：%f", LBlMNC.get(5)));
    }

    @Test
    void projectionTransform() {
        double oldX = 1944359.6070;
        double oldY = 240455.4563;
        double precision = 0.0000000000001;

        ArrayList<Double> newXY = beijing54_3_20N.projectionTransform(beijing54_3_21N, oldX, oldY, precision, false);

        String xz = String.format("%.4f", newXY.get(0));
        String yz = String.format("%.4f", newXY.get(1));
        String x = String.format("%.4f", newXY.get(2));
        String y = String.format("%.4f", newXY.get(3));

        assertEquals("1943076.2990", xz, String.format("xz：%f", newXY.get(0)));
        assertEquals("421912.7778", yz, String.format("yz：%f", newXY.get(1)));
        assertEquals("1943076.2990", x, String.format("x：%f", newXY.get(2)));
        assertEquals("-78087.2222", y, String.format("y：%f", newXY.get(3)));
    }

    @Test
    void utmCalculation() {
        double L = CalculationTools.degree2Rad(112 + 17.0 / 60 + 58.3596 / 3600);
        double B = CalculationTools.degree2Rad(30 + 45.0 / 60 + 25.4425 / 3600);
        ArrayList<Double> xy = utm.forwardCalculation(L, B);

        double precision = 0.00000000000000001;
        ArrayList<Double> lb = utm.backwardCalculation(xy.get(0), xy.get(1), precision, true);

        String Ld = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(lb.get(0)));
        String Bd = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(lb.get(1)));

        assertEquals("112°17'58.3596\"", Ld, String.format("L：%f", lb.get(0)));
        assertEquals("30°45'25.4425\"", Bd, String.format("B：%f", lb.get(1)));
    }
}