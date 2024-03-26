package jpabook.dashdine.geo;


import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryUtil {

    public static Point calculatePoint(Double longitude, Double latitude) throws ParseException {
        return latitude != null && longitude != null ?
                (Point) new WKTReader().read(String.format("POINT(%s %s)", longitude, latitude))
                : null;
    }
}
