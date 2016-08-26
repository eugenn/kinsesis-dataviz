package com.amazonaws.services.kinesis.samples.datavis.model;

import java.util.Comparator;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class TypeCountComparator implements Comparator<TypeCount> {
    @Override
    public int compare(TypeCount c1, TypeCount c2) {
        if (c2.getCount() > c1.getCount()) {
            return 1;
        } else if (c1.getCount() == c2.getCount()) {
            return 0;
        } else {
            return -1;
        }
    }
}
