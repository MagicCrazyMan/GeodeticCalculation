package club.magiccrazyman.geodetic.core.coordinatesystem.projection;

import club.magiccrazyman.geodetic.core.tools.CalculationTools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GaussKrugerProjectionCoordinateSystemTest {

    private GaussKrugerProjectionCoordinateSystem system = GaussKrugerProjectionCoordinateSystem.RecordedSystem.CGCS2000_3_Degree_GK_CM_111E.getSystem();

    @Test
    void forwardCalculation() {
        double L = CalculationTools.degree2Rad(111 + 17.0 / 60 + 58.3596 / 3600);
        double B = CalculationTools.degree2Rad(30 + 45.0 / 60 + 25.4425 / 3600);
        double[] xy = system.forwardCalculation(L, B);
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
        double x = 3404079.3582;
        double y = 528680.0889;
        double precision = 0.0000000000001;
        boolean hasFalse = true;
        double[] lb = system.backwardCalculation(x, y, precision, hasFalse);
        String L = String.format("%.16f", lb[0]);
        String B = String.format("%.16f", lb[1]);
        assertEquals("1.9425435045825241", L, String.format("L：%f", lb[0]));
        assertEquals("0.5368120937090425", B, String.format("B：%f", lb[1]));
    }
}