package com.qunhe.util.nest.util;

import com.qunhe.util.nest.data.NestPath;
import com.qunhe.util.nest.data.Placement;
import com.qunhe.util.nest.data.Segment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SvgUtil {

    public static List<String> svgGenerator(List<NestPath> list, List<List<Placement>> applied, double binwidth, double binHeight) throws Exception {
        List<String> strings = new ArrayList<String>();
        int x = 10;
        int y = 0;
        for (List<Placement> binlist : applied) {
            String s = " <g transform=\"translate(" + x + "  " + y + ")\">" + "\n";
            s += "    <rect x=\"0\" y=\"0\" width=\"" + binwidth + "\" height=\"" + binHeight + "\"  fill=\"none\" stroke=\"#010101\" stroke-width=\"1\" />\n";
            for (Placement placement : binlist) {
                int bid = placement.bid;
                NestPath nestPath = getNestPathByBid(bid, list);
                double ox = placement.translate.x;
                double oy = placement.translate.y;
                double rotate = placement.rotate;
                s += "<g transform=\"translate(" + ox + x + " " + oy + y + ") rotate(" + rotate + ")\"> \n";
                s += "<path d=\"";
                for (int i = 0; i < nestPath.getSegments().size(); i++) {
                    if (i == 0) {
                        s += "M";
                    } else {
                        s += "L";
                    }
                    Segment segment = nestPath.get(i);
                    s += segment.x + " " + segment.y + " ";
                }
                s += "Z\" fill=\"#8498d1\" stroke=\"#010101\" stroke-width=\"1\" />" + " \n";
                s += "</g> \n";
            }
            s += "</g> \n";
            y += binHeight + 50;
            strings.add(s);
        }
        return strings;
    }

    private static NestPath getNestPathByBid(int bid, List<NestPath> list) {
        for (NestPath nestPath : list) {
            if (nestPath.bid == bid) {
                return nestPath;
            }
        }
        return null;
    }

}
