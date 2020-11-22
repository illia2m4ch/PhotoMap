package com.ilya.photomap.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    public interface Converter<I, O> {
        O map(I input);
    }

    public static <I, O> List<O> map(List<I> inputList, Converter<I, O> converter) {
        if (inputList == null) return null;

        List<O> outputList = new ArrayList<>(inputList.size());
        for (I item : inputList) {
            outputList.add(converter.map(item));
        }
        return outputList;
    }

    public static <T> List<T> innerJoin(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<>();
        for (T item : list1) {
            if (list2.contains(item)) list.add(item);
        }
        return list;
    }

}
