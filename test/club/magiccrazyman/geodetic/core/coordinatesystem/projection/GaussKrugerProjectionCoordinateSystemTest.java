package club.magiccrazyman.geodetic.core.coordinatesystem.projection;

import club.magiccrazyman.geodetic.core.tools.CalculationTools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GaussKrugerProjectionCoordinateSystemTest {
    private  GaussKrugerProjectionCoordinateSystem beijing54_3_20N = GaussKrugerProjectionCoordinateSystem.RecordedSystem.Beijing_1954_3_Degree_GK_CM_117E.getSystem();
    private  GaussKrugerProjectionCoordinateSystem beijing54_3_21N = GaussKrugerProjectionCoordinateSystem.RecordedSystem.Beijing_1954_3_Degree_GK_CM_120E.getSystem();

    private GaussKrugerProjectionCoordinateSystem cgcs2000_3_111E = GaussKrugerProjectionCoordinateSystem.RecordedSystem.CGCS2000_3_Degree_GK_CM_111E.getSystem();
    private GaussKrugerProjectionCoordinateSystem utm = GaussKrugerProjectionCoordinateSystem.RecordedSystem.WGS_1984_UTM_Zone_49N.getSystem();

    @Test
    void forwardCalculation() {
        double L = CalculationTools.degree2Rad(111 + 17.0 / 60 + 58.3596 / 3600);
        double B = CalculationTools.degree2Rad(30 + 45.0 / 60 + 25.4425 / 3600);
        double[] xy = cgcs2000_3_111E.forwardCalculation(L, B);
        String xz = String.format("%.4f", xy[0]);
        String yz = String.format("%.4f", xy[1]);
        String x = String.format("%.4f", xy[2]);
        String y = String.format("%.4f", xy[3]);
        assertEquals("3404079.3582", xz, String.format("xz：%f", xy[0]));
        assertEquals("528680.0889", yz, String.format("yz：%f", xy[1]));
        assertEquals("3404079.3582", x, String.format("x：%f", xy[2]));
        assertEquals("28680.0889", y, String.format("y：%f", xy[3]));
    }

    @Test
    void backwardCalculation() {
        double x = 3404762.7444;
        double y = 624430.6672;
        double precision = 0.0000000000001;
        double[] lb = cgcs2000_3_111E.backwardCalculation(x, y, precision, true);
        String L = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(lb[0]));
        String B = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(lb[1]));
        assertEquals("112°17'58.3596\"", L, String.format("L：%f", lb[0]));
        assertEquals("30°45'25.4425\"", B, String.format("B：%f", lb[1]));
    }

    @Test
    void projectionTransform() {
        double oldX = 1944359.6070;
        double oldY = 240455.4563;
        double precision = 0.0000000000001;

        double[] newXY = beijing54_3_20N.projectionTransform(beijing54_3_21N, oldX, oldY, precision, false);

        String xz = String.format("%.4f", newXY[0]);
        String yz = String.format("%.4f", newXY[1]);
        String x = String.format("%.4f", newXY[2]);
        String y = String.format("%.4f", newXY[3]);
        assertEquals("1943076.2990", xz, String.format("xz：%f", newXY[0]));
        assertEquals("421912.7778", yz, String.format("yz：%f", newXY[1]));
        assertEquals("1943076.2990", x, String.format("x：%f", newXY[2]));
        assertEquals("-78087.2222", y, String.format("y：%f", newXY[3]));
    }

    @Test
    void utmCalculation() {
        double L = CalculationTools.degree2Rad(112 + 17.0 / 60 + 58.3596 / 3600);
        double B = CalculationTools.degree2Rad(30 + 45.0 / 60 + 25.4425 / 3600);
        double[] xy = utm.forwardCalculation(L, B);

        double precision = 0.00000000000000001;
        double[] lb = utm.backwardCalculation(xy[0], xy[1], precision, true);

        String Ld = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(lb[0]));
        String Bd = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(lb[1]));

        assertEquals("112°17'58.3596\"", Ld, String.format("L：%f", lb[0]));
        assertEquals("30°45'25.4425\"", Bd, String.format("B：%f", lb[1]));
    }
}