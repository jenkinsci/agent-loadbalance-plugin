package com.surenpi.jenkins.loadbalance;

import java.util.*;

/**
 * @author suren
 */
public enum Unit
{
    K(1024), M(1048576), G(1073741824);

    private long origin;
    Unit(long origin)
    {
        this.origin = origin;
    }

    public static List<String> list()
    {
        return list(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                long diff = Unit.valueOf(o1).getOrigin() - Unit.valueOf(o2).getOrigin();
                return diff > 0 ? 1 : (diff == 0 ? 0 : -1);
            }
        });
    }

    public static List<String> list(Comparator<String> comparator)
    {
        List<String> items = new ArrayList<>();
        for(Unit unit : Unit.values())
        {
            items.add(unit.name());
        }

        if(comparator != null)
        {
            items.sort(comparator);
        }

        return items;
    }

    public static Map<String, String> map()
    {
        Unit[] units = Unit.values();

        Map<String, String> map = new TreeMap<>();
        for(Unit unit : Unit.values())
        {
            map.put(unit.name(), unit.name());
        }

        return map;
    }

    public long getOrigin()
    {
        return origin;
    }

    public void setOrigin(long origin)
    {
        this.origin = origin;
    }
}