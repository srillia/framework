package net.unsun.infrastructure.aggr.core;

import net.unsun.infrastructure.aggr.util.DataAggrUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * aggregate standard data
 * @author tokey
 * @param <T>
 */
public class AggrStandardData<T> extends Aggr {

    /**
     * aggregate standard data
     * @return
     */
    public Object aggregate() {
        Object mainObject = stack.get().get(MAIN);
        Set<Object> slaveSet = (Set) stack.get().get(SLAVE);
        Iterator<Object> iter = slaveSet.iterator();
        while (iter.hasNext()) {
            Map<String, Object> map = (Map) iter.next();
            Object slaveObject = map.get(SLAVE_ITEM);
            String slaveFileName = (String) map.get(SLAVE_ITEM_NAME);
            mainObject = DataAggrUtil.dynamicAddCloumn(mainObject, slaveObject, slaveFileName);
        }
        return mainObject;

    }


}

