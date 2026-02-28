package com.dashomi.actionregulator.utils;

import java.util.List;

public class FilterUtils {
    // Normal  (invert=false): empty list → pass all; list has entries → only pass if contained.
    // Inverted (invert=true): empty list → block all; list has entries → pass all EXCEPT contained.
    public static boolean matchesFilter(List<String> list, boolean invert, String id) {
        if (!invert) {
            return list.isEmpty() || list.contains(id);
        } else {
            return !list.isEmpty() && !list.contains(id);
        }
    }
}

