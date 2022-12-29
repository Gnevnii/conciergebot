package ru.gnev.conciergebot.utils;

import org.springframework.stereotype.Service;

@Service
public class FlatHelper {

    public SectionFloor calculateSectionFloor(int flatN) { //100
        int x = flatN / 8;  // 12
        int y = flatN % 8;  // 4
        int floorN;
        int sectionN;
        if (y == 0) {
            floorN = x + 1;
            sectionN = 8;
        }
        else {
            floorN = x + 2; //14
            sectionN = y;   //4
        }
        return new SectionFloor(sectionN, floorN);
    }
}
