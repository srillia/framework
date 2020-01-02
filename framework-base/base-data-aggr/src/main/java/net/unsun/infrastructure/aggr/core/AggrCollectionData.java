package net.unsun.infrastructure.aggr.core;


import net.unsun.infrastructure.aggr.util.AggrUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * aggregate collection data
 * @author tokey
 * @param <T>
 */
public class AggrCollectionData<T> extends Aggr<T> {


    /**
     * aggregate collection data
     * @return
     */
    public Object aggregate() {
        List<T> mainList = null;
        try {
            checkAggrParams();
            mainList = (List) stack.get().get(MAIN);
            String mainIdFiledName = (String) stack.get().get(MAIN_ID_FIELD_NAME);
            Set<Object> slaveSet = (Set) stack.get().get(SLAVE);
            Iterator<Object> iter = slaveSet.iterator();
            while (iter.hasNext()) {
                Map<String, Object> map = (Map) iter.next();
                Object slaveList = map.get(SLAVE_ITEM);
                String slaveIdFileName = (String) map.get(SLAVE_ID_FIELD_NAME);
                String slaveFileName = (String) map.get(SLAVE_ITEM_NAME);
                if (slaveList instanceof Collection) {
                    mainList = AggrUtil.copy(mainList, mainIdFiledName, (Collection<?>) slaveList, slaveIdFileName, slaveFileName);
                    continue;
                }
                throw new RuntimeException("Aggr: list copy list ,slave object is not list type");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return mainList;
    }





}

