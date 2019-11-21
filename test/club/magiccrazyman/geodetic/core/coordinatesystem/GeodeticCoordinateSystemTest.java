package club.magiccrazyman.geodetic.core.coordinatesystem;

import club.magiccrazyman.geodetic.core.tools.CalculationTools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeodeticCoordinateSystemTest {

    private GeodeticCoordinateSystem CGCS2000 = GeodeticCoordinateSystem.RecordedSystem.CGCS2000.getSystem();
    private GeodeticCoordinateSystem Beijing54 = GeodeticCoordinateSystem.RecordedSystem.Beijing_1954.getSystem();

    @Test
    void transformToGeodeticCoordinateSystem() {
        double X = 3694419.14443691;
        double Y = 3694419.14443691;
        double Z = 5194455.18902173;
        double precision = 0.0000000000001;
        double[] LBHC = CGCS2000.transformToGeodeticCoordinateSystem(X, Y, Z, precision);
        String L = String.format("%.16f", LBHC[0]);
        String B = String.format("%.16f", LBHC[1]);
        String H = String.format("%.4f", LBHC[2]);
        int C = (int) LBHC[3];
        assertEquals("0.7853981633974484", L, String.format("L：%s", L));
        assertEquals("0.7853981633974484", B, String.format("B：%s", B));
        assertEquals("999999.9987", H, String.format("H：%s", H));
        assertEquals(6, C, String.format("count：%d", C));
    }

    @Test
    void transformToSpatialCoordinateSystem() {
        double L = 0.7853981633974483;
        double B = 0.7853981633974483;
        double H = 999999.9987;
        double[] XYZ = CGCS2000.transformToSpatialCoordinateSystem(L, B, H);
        String X = String.format("%.8f", XYZ[0]);
        String Y = String.format("%.8f", XYZ[1]);
        String Z = String.format("%.8f", XYZ[2]);
        assertEquals("3694419.14443691", X, String.format("X：%s", X));
        assertEquals("3694419.14443691", Y, String.format("Y：%s", Y));
        assertEquals("5194455.18902173", Z, String.format("Z：%s", Z));
    }

    @Test
    void directSolutionOfGeodeticProblem() {
        double L1 = CalculationTools.degree2Rad(114 + 20 / 60.0 + 0 / 3600.0);
        double B1 = CalculationTools.degree2Rad(30 + 30 / 60.0 + 0 / 3600.0);
        double A1 = CalculationTools.degree2Rad(225);
        double S = 10000000;
        double[] LBA = Beijing54.directSolutionOfGeodeticProblem(L1, B1, A1, S);
        String L2 = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBA[0]));
        String B2 = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBA[1]));
        String A2 = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(LBA[2]));
        assertEquals("51°16'32.4976\"", L2, String.format("L2：%s", L2));
        assertEquals("-37°43'44.1353\"", B2, String.format("B2：%s", B2));
        assertEquals("50°21'22.4896\"", A2, String.format("A2：%s", A2));
    }

    @Test
    void inverseSolutionOfGeodeticProblem() {
        double L1 = CalculationTools.degree2Rad(114 + 20 / 60.0 + 0 / 3600.0);
        double B1 = CalculationTools.degree2Rad(30 + 30 / 60.0 + 0 / 3600.0);
        double L2 = CalculationTools.degree2Rad(51 + 16 / 60.0 + 32.5 / 3600.0);
        double B2 = CalculationTools.degree2Rad(-(37 + 43 / 60.0 + 44.1351 / 3600.0));
        double precision = 0.000000000000000001;
        double[] AAS = Beijing54.inverseSolutionOfGeodeticProblem(L1, B1, L2, B2, precision);
        String A1 = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(AAS[0]));
        String A2 = CalculationTools.degreeFormatter(CalculationTools.rad2Degree(AAS[1]));
        String S = String.format("%.4f", AAS[2]);
        assertEquals("224°59'59.9989\"", A1, String.format("A1：%s", A1));
        assertEquals("50°21'22.4881\"", A2, String.format("A2：%s", A2));
        assertEquals("9999999.9535", S, S);
    }
}