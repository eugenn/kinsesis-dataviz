package com.kinesis.datavis.kcl.persistence;

import com.jdbc.dao.MappingDAO;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 01/09/16.
 */
public class Cleaner  {
    private MappingDAO mappingDAO;

    public Cleaner(MappingDAO mappingDAO) {
        this.mappingDAO = mappingDAO;

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        ses.scheduleAtFixedRate((Runnable) this::doClean, 0, 1, TimeUnit.HOURS);
    }

    private void doClean() {
        System.out.println("======Cleaning=========");
        System.out.println(mappingDAO.count());

        mappingDAO.deleteAll();
    }
}
