package com.qunhe.util.nest.util;

import com.qunhe.util.nest.data.NestPath;

import java.util.List;

/**
 * 接收一组NestPath , 并且将其坐标变换到二维坐标平面上。
 */
public class PostionUtil {

    public static List<NestPath> positionTranslate4Path(double x, double y, List<NestPath> paths) {
        for (NestPath path : paths) {
            path.translate(x, y);
            y = path.getMaxY() + 10;
        }
        return paths;
    }
}
