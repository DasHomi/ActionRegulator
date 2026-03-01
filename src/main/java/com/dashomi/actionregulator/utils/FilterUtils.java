package com.dashomi.actionregulator.utils;

import java.util.List;

public class FilterUtils {
    public static boolean matchesFilter(List<String> list, boolean invert, String id) {
        if (!invert) {
            return list.isEmpty() || list.contains(id);
        } else {
            return !list.isEmpty() && !list.contains(id);
        }
    }
}

